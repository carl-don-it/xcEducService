package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsSiteControllerApi;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.CmsSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName Cms
 * @date 2019年06月14日 上午 11:35
 */
@RestController
@RequestMapping("/cms/site")
public class CmsSiteController implements CmsSiteControllerApi {

	@Autowired
	private CmsSiteService cmsSiteService;

	@Override
	@GetMapping("/list")
	public QueryResponseResult<CmsSite> findList() {
		return cmsSiteService.findList();
	}
}
