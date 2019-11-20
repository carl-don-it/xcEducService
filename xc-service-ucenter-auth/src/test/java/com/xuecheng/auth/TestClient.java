package com.xuecheng.auth;

import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {

	@Autowired   //不需要声明bean
			LoadBalancerClient loadBalancerClient;

	@Autowired   //需要声明bean对象
			RestTemplate restTemplate;

	//远程请求spring security获取令牌
	@Test
	public void testClient() {
		//1. 远程调用的地址
		//从eureka中获取认证服务的一个实例的地址
		ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
		//从实例中获取认证服务的地址（因为spring security在认证服务中）
		//此地址就是http://ip:port
		URI uri = serviceInstance.getUri();
		//令牌申请的地址 http://localhost:40400/auth/oauth/token
		String authUrl = uri + "/auth/oauth/token";

		//2. 远程调用的HttpEntity
		//2.1定义header,怎么知道header用什么类型
		LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
		String httpBasic = getHttpBasic("XcWebApp", "XcWebApp");
		header.add("Authorization", httpBasic);

		//2.2定义body
		LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "password");
		body.add("username", "itcast");
		body.add("password", "12322");

		//2.3 定义HttpEntity
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);

		//3. 设置restTemplate远程调用时候，对400和401不让报错，正确返回数据
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
					super.handleError(response);
				}
			}
		});

		//4. 发起http请求,远程调用spring security
		//String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables
		ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);

		//申请令牌信息
		Map bodyMap = exchange.getBody();
		System.out.println(bodyMap);
	}

	//获取httpbasic的串
	private String getHttpBasic(String clientId, String clientSecret) {
		String string = clientId + ":" + clientSecret;
		//将串进行base64编码
		byte[] encode = Base64Utils.encode(string.getBytes());
		return "Basic " + new String(encode);
	}

}
