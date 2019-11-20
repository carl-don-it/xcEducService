package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 返回的都是json数据
 *
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName CmsPageController
 * @date 2019年06月09日 下午 10:00
 */
@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

	@Autowired
	private CmsPageService cmsPageService;

	@Override
	@GetMapping("/list/{page}/{size}")
	public QueryResponseResult<CmsPage> findList(
			@PathVariable("page") int page,
			@PathVariable("size") int size,
			QueryPageRequest queryPageRequest) {
		//直接调用service层
		return cmsPageService.findList(page, size, queryPageRequest);
	}

	/**
	 * 对象数据量比较大，使用json格式传输方便？，所以要使用@RequestBody
	 *
	 * @param cmsPage
	 * @return
	 */
	@Override
	@PostMapping("/add")
	public CmsPageResult add(@RequestBody CmsPage cmsPage) {
		return cmsPageService.add(cmsPage);
	}

	@Override
	@GetMapping("/find/{id}")
	public CmsPage findById(@PathVariable("id") String id) {
		return cmsPageService.findById(id);
	}

	@Override
	@PutMapping("/edit/{id}")
	public CmsPageResult edit(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
		return cmsPageService.update(id, cmsPage);
	}

	@Override
	@DeleteMapping("/delete/{id}")
	public ResponseResult delete(@PathVariable("id") String id) {
		return cmsPageService.delete(id);
	}

	@Override
	@GetMapping("/post/{id}")
	public ResponseResult post(@PathVariable("id") String id) {
		return cmsPageService.postPage(id);
	}

	@Override
	@PostMapping("/save")
	public CmsPageResult save(@RequestBody CmsPage cmsPage) {
		return cmsPageService.save(cmsPage);
	}

	@Override
	@PostMapping("/quickPost")
	public CmsPostPageResult quickPostPage(@RequestBody CmsPage cmsPage) {
		return cmsPageService.quickPost(cmsPage);
	}
}
