package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName CmsPagePreviewController
 * @date 2019年06月17日 上午 8:47
 */

@Controller
public class CmsPagePreviewController extends BaseController {
	@Autowired
	private CmsPageService cmsPageService;

	//接收到页面id，生成静态化页面供预览
	@RequestMapping(value = "/cms/preview/{pageId}", method = RequestMethod.GET)
	public void preview(@PathVariable("pageId") String pageId) {
		String pageHtml = cmsPageService.getPageHtmlContent(pageId);
		if (StringUtils.isNotEmpty(pageHtml)) {
			try {
				//nginx 的ssi技术需要html类型,显式展示
				response.setHeader("Content-type", "text/html;charset=utf-8");
				ServletOutputStream outputStream = response.getOutputStream();
				outputStream.write(pageHtml.getBytes("utf-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
