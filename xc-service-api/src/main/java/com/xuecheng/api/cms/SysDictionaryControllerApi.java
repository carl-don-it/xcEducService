package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;

@Api(value = "数据字典接口", description = "提供数据字典接口的管理,查询功能")
public interface SysDictionaryControllerApi {
	public SysDictionary getByType(String type);
}
