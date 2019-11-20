package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.auth.dao.RedisDao;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName AuthService
 * @date 2019年07月08日 上午 11:22
 */
@Service
public class AuthService {
	@Value("${auth.tokenValiditySeconds}")
	private long tokenValiditySeconds;

	@Autowired
	private LoadBalancerClient loadBalancerClient;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RedisDao redisDao;

	/**
	 * 根据用户名和密码申请令牌，并且把令牌存储到redis中，并且传回来的token非null
	 *
	 * @param username
	 * @param password
	 * @param clientId
	 * @param clientSecret
	 * @return
	 */
	public AuthToken login(String username, String password, String clientId, String clientSecret) {
		//1. 检验参数，非空判断
		if (StringUtils.isEmpty(username)) {
			ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
		}
		if (StringUtils.isEmpty(password)) {
			ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
		}

		//2. 远程申请令牌
		AuthToken authToken = this.applyToken(username, password, clientId, clientSecret);
		//2.2令牌为空则登陆异常
		if (authToken == null) {
			ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
		}

		//3. 存储令牌到redis
		//3.1. key
		String key = "user_token:" + authToken.getAccess_token();
		//3.2. value
		String jsonValue = JSON.toJSONString(authToken);
		boolean result = redisDao.saveValue(key, jsonValue, tokenValiditySeconds);
		//3.2存储不成功，抛异常
		if (!result) {
			ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
		}
		//4.返回令牌对象
		return authToken;
	}

	/**
	 * 远程申请令牌，向本服务的spring security
	 *
	 * @param username
	 * @param password
	 * @param clientId
	 * @param clientSecret
	 * @return
	 */
	private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
		//1. 远程调用的url，从eureka
		ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
		URI uri = serviceInstance.getUri();
		//令牌申请的地址 http://localhost:40400/auth/oauth/token
		String authUrl = uri + "/auth/oauth/token";

		//2. 请求参数，包括head(http basic认证)，body
		//2.1 body
		LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "password");
		body.add("username", username);
		body.add("password", password);
		//2.2 header
		LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
		header.add("Authorization", this.getHttpBasic(clientId, clientSecret));
		//2.3 组装成httpEntity
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);

		//3. 发起远程请求
		//3.1 设置restTemplate远程调用时候，对400和401不让报错，正确返回数据
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
					super.handleError(response);
				}
			}
		});
		//3.2 发起远程请求
		ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);

		//4. 处理结果，返回
		//4.1 申请令牌信息
		Map bodyMap = exchange.getBody();
		//4.2 验证
		if (bodyMap == null ||
				bodyMap.get("access_token") == null ||
				bodyMap.get("refresh_token") == null ||
				bodyMap.get("jti") == null) {

			//解析spring security返回的错误信息
			if (bodyMap != null && bodyMap.get("error_description") != null) {
				String error_description = (String) bodyMap.get("error_description");
				if (error_description.indexOf("UserDetailsService returned null") >= 0) {
					ExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
				} else if (error_description.indexOf("坏的凭证") >= 0) {
					ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
				}
			}

			return null;
		}
		//4.3 封装返回
		AuthToken authToken = new AuthToken();
		authToken.setAccess_token((String) bodyMap.get("jti"));//用户身份令牌
		authToken.setRefresh_token((String) bodyMap.get("refresh_token"));//刷新令牌
		authToken.setJwt_token((String) bodyMap.get("access_token"));//jwt令牌
		return authToken;
	}

	/**
	 * 把密码账号进行base64编码
	 *
	 * @param clientId
	 * @param clientSecret
	 * @return
	 */
	private String getHttpBasic(String clientId, String clientSecret) {
		String string = clientId + ":" + clientSecret;
		//将串进行base64编码
		byte[] encode = Base64Utils.encode(string.getBytes());
		return "Basic " + new String(encode);

	}

	public void deleteToken(String access_token) {
		String key = "user_token:" + access_token;
		redisDao.deleteEntry(key);
	}

	public AuthToken getAuthToken(String access_token) {
		String key = "user_token:" + access_token;
		String value = redisDao.getValue(key);
		AuthToken authToken = null;
		try {
			authToken = JSON.parseObject(value, AuthToken.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return authToken;
	}
}
