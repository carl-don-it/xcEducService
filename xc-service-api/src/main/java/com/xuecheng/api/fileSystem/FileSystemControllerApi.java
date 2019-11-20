package com.xuecheng.api.fileSystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "FileSystem管理接口", description = "提供上传下载文件相关的操作")
public interface FileSystemControllerApi {

	/**
	 * 上传文件
	 *
	 * @param multipartFile 文件
	 * @param filetag       文件标签
	 * @param businesskey   业务key
	 * @param metadata      元信息,json格式
	 * @return
	 */
	@ApiOperation("上传文件")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "filetag", value = "文件标签", required = true, paramType = "form", dataType = "String"),
			@ApiImplicitParam(name = "businesskey", value = "业务key", required = true, paramType = "form", dataType = "String"),
			@ApiImplicitParam(name = "metadata", value = "元信息,json格式", required = true, paramType = "form", dataType = "json")

	})
	public UploadFileResult upload(MultipartFile multipartFile,
	                               String filetag,
	                               String businesskey,
	                               String metadata);
}
