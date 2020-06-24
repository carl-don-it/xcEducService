package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面查询接口
 */
@Api(value = "cms配置管理接口", description = "提供数据模型的管理、查询")
public interface CmsConfigControllerApi {

	@ApiOperation("根据id查询cms配置信息")
	@ApiImplicitParam(name = "id", value = "数据模型的配置id", required = true, paramType = "path", dataType = "String")
	@GetMapping("/find/{id}")
	CmsConfig findById(String id);

}
