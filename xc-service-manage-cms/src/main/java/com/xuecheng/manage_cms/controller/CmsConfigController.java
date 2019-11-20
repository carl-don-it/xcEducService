package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.service.CmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName CmsConfigController
 * @date 2019年06月16日 下午 1:51
 */
@RestController
@RequestMapping("/cms/config")
public class CmsConfigController implements CmsConfigControllerApi {
	@Autowired
	private CmsConfigService cmsConfigService;

	@Override
	@GetMapping("/find/{id}")
	public CmsConfig findById(@PathVariable("id") String id) {
		return cmsConfigService.findById(id);
	}
}
