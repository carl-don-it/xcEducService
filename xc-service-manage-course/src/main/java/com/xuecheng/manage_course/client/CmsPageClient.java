package com.xuecheng.manage_course.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 远程调用cms微服务的客户端所使用的接口
 */
@FeignClient(value = XcServiceList.XC_SERVICE_MANAGE_CMS)
public interface CmsPageClient {

	/**
	 * 远程调用cms请求数据,根据页面id查询页面数据
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/cms/page/find/{id}")
	public CmsPage findById(@PathVariable("id") String id);

	/**
	 * 远程调用cms的保存页面
	 *
	 * @param cmsPage
	 * @return
	 */
	@PostMapping("/cms/page/save")
	public CmsPageResult save(CmsPage cmsPage);

	/**
	 * 远程调用一键添加和发布cmspage
	 *
	 * @param cmsPage
	 * @return
	 */
	@PostMapping("/cms/page/quickPost")
	public CmsPostPageResult quickPostPage(CmsPage cmsPage);
}
