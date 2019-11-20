package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsTemplateControllerApi;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.CmsTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName CmsTemplateController
 * @date 2019年06月14日 下午 8:08
 */
@RestController
@RequestMapping("/cms/template")
public class CmsTemplateController implements CmsTemplateControllerApi {

	@Autowired
	private CmsTemplateService cmsTemplateService;

	@Override
	@GetMapping("/list")
	public QueryResponseResult<CmsTemplate> findList() {
		return cmsTemplateService.findList();
	}
}
