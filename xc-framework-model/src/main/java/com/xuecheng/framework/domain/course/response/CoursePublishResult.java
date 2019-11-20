package com.xuecheng.framework.domain.course.response;

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
public class CoursePublishResult extends ResponseResult {
	//页面publishUrl= cmsSite.siteDomain+cmsSite.siteWebPath+ cmsPage.pageWebPath + cmsPage.pageName
	String publishUrl;

	public CoursePublishResult(ResultCode resultCode, String publishUrl) {
		super(resultCode);
		this.publishUrl = publishUrl;
	}
}
