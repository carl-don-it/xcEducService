package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "关于course的category接口", description = "course的category接口，提供category管理相关信息的查询")
public interface CategoryControllerApi {
	@ApiOperation("全部category")
	public CategoryNode findCategoryList();
}
