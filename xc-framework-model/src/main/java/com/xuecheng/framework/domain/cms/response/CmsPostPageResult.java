package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Administrator
 * @version 1.0
 **/
@Data
@ToString
@NoArgsConstructor
public class CmsPostPageResult extends ResponseResult {
	//页面publishUrl= cmsSite.siteDomain+cmsSite.siteWebPath+ cmsPage.pageWebPath + cmsPage.pageName
	String pageUrl;

	public CmsPostPageResult(ResultCode resultCode, String pageUrl) {
		super(resultCode);
		this.pageUrl = pageUrl;
	}
}
