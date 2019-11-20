package com.xuecheng.ucenter.controller;

import com.xuecheng.api.ucenter.UcenterControllerApi;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.service.UcenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName UcenterController
 * @date 2019年07月08日 下午 4:38
 */
@RestController
@RequestMapping("/ucenter")
public class UcenterController implements UcenterControllerApi {

	@Autowired
	private UcenterService ucenterService;

	@Override
	@GetMapping("/getUserExt")
	public XcUserExt getUserExt(@RequestParam("username") String username) {
		return ucenterService.getUserExt(username);
	}
}
