package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "cms模板管理接口", description = "cms模板管理接口，提供模板的查询")
public interface CmsTemplateControllerApi {

	@ApiOperation("查询所有模板")
	public QueryResponseResult<CmsTemplate> findList();
}
