package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 页面查询接口
 */
@Api(value = "cms页面管理接口", description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {
	@ApiOperation("分页并按条件查询页面列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
			@ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path", dataType = "int")
	})
	public QueryResponseResult<CmsPage> findList(int page, int size, QueryPageRequest queryPageRequest);

	@ApiOperation("新增页面,只能新增")
	public CmsPageResult add(CmsPage cmsPage);

	@ApiOperation("根据id查询页面")
	@ApiImplicitParam(name = "id", value = "页面id", required = true, paramType = "path", dataType = "String")
	public CmsPage findById(String id);

	@ApiOperation("修改页面")
	@ApiImplicitParam(name = "id", value = "页面id", required = true, paramType = "path", dataType = "String")
	public CmsPageResult edit(String id, CmsPage cmsPage);

	@ApiOperation("通过id删除页面")
	@ApiImplicitParam(name = "id", value = "页面id", required = true, paramType = "path", dataType = "String")
	public ResponseResult delete(String id);

	@ApiOperation("发布已经存在的页面")
	@ApiImplicitParam(name = "id", value = "页面id", required = true, paramType = "path", dataType = "String")
	public ResponseResult post(String id);

	@ApiOperation("保存页面,可以新增,存在则修改")
	public CmsPageResult save(CmsPage cmsPage);

	@ApiOperation("一键添加和发布页面")
	@ApiImplicitParam(name = "cmsPage", value = "要添加和发布的cmsPage", required = true, paramType = "body",
			dataType = "com.xuecheng.framework.domain.cms.CmsPage")
	public CmsPostPageResult quickPostPage(CmsPage cmsPage);
}
