package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(value = "用户中心", description = "提供对用户信息的查询")
public interface UcenterControllerApi {

	@ApiOperation(value = "根据用户账号获取用户的信息，包括公司的扩展信息")
	@ApiImplicitParam(name = "username", value = "用户账号", paramType = "query", dataType = "String")
	public XcUserExt getUserExt(String username);
}
