package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName AuthController
 * @date 2019年07月08日 上午 11:09
 */

@RestController
@RequestMapping("/")
public class AuthController implements AuthControllerApi {

	@Value("${auth.clientId}")
	private String clientId;
	@Value("${auth.clientSecret}")
	private String clientSecret;
	@Value("${auth.cookieDomain}")
	private String cookieDomain;
	@Value("${auth.cookieMaxAge}")
	private int cookieMaxAge;

	@Autowired
	private AuthService authService;

	/**
	 * 间接向spring security申请令牌,不过要处理redis储存,cookie保存问题
	 *
	 * @param loginRequest
	 * @return
	 */
	@Override
	@PostMapping("/userlogin")
	public LoginResult login(LoginRequest loginRequest) {
		//参数判断
		if (loginRequest == null) {
			ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
		}
		//获取用户名和密码
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();
		//不在这里检验，因为这里不需要对这两个参数处理业务

		//1. 调用service申请令牌,保存令牌到redis,需要密码和用户名
		AuthToken authToken = authService.login(username, password, clientId, clientSecret);

		//2. 把身份令牌保存到cookie
		String access_token = authToken.getAccess_token();
		this.saveCookie(access_token);

		return new LoginResult(CommonCode.SUCCESS, access_token);
	}

	@Override
	@PostMapping("/userlogout")
	public ResponseResult logout() {
		//1. 获取cookie中的身份令牌
		String access_token = this.getAccessTokenFromCookie();
		if (access_token == null) {
			return new ResponseResult(CommonCode.SUCCESS);
		}
		//2. 删除redis中的token
		authService.deleteToken(access_token);

		//3. 删除cookies
		this.deleteCookie();
		return new ResponseResult(CommonCode.SUCCESS);

	}

	private void deleteCookie() {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		//HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
		CookieUtil.addCookie(response, cookieDomain, "/", "uid", "", 0, false);
	}

	/**
	 * 根据cookie中的身份令牌查询jwt令牌,并且返回
	 *
	 * @return
	 */
	@Override
	@GetMapping("/userjwt")
	public JwtResult userJwt() {
		//1. 获取cookie中的身份令牌
		String access_token = this.getAccessTokenFromCookie();
		if (access_token == null) {
			return new JwtResult(CommonCode.FAIL, null);
		}
		//2. 根据身份令牌从redis中获取AuthToken对象，
		AuthToken authToken = authService.getAuthToken(access_token);
		if (authToken == null) {
			return new JwtResult(CommonCode.FAIL, null);
		}
		//3. 获取jwt令牌
		return new JwtResult(CommonCode.SUCCESS, authToken.getJwt_token());
	}

	/**
	 * 从cookie中获取uid，也就是access_token
	 *
	 * @return
	 */
	private String getAccessTokenFromCookie() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		Map<String, String> map = CookieUtil.readCookie(request, "uid");
		if (map != null && map.get("uid") != null) {
			String uid = map.get("uid");
			return uid;
		}
		return null;
	}

	/**
	 * save 到cookie
	 *
	 * @param token
	 */
	private void saveCookie(String token) {

		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		//HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
		CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);

	}
}
