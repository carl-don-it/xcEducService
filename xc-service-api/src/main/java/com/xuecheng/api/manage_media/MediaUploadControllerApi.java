package com.xuecheng.api.manage_media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "媒资管理接口", description = "媒资管理接口，提供文件上传，文件处理等接口")
public interface MediaUploadControllerApi {

	//文件上传前的准备工作
	@ApiOperation("文件上传注册")
	public ResponseResult register(String fileMd5,
	                               String fileName,
	                               Long fileSize,
	                               String mimetype,
	                               String fileExt);

	//每次上传分块前校验分块，如果已存在分块则不再上传，达到断点续传的目的
	@ApiOperation("分块检查")
	public CheckChunkResult checkchunk(String fileMd5,
	                                   Integer chunk,
	                                   Integer chunkSize);

	@ApiOperation("上传文件")
	public ResponseResult uploadchunk(MultipartFile multipartFile,
	                                  String fileMd5,
	                                  Integer chunk);

	@ApiOperation("合并文件")
	public ResponseResult mergechunks(String fileMd5,
	                                  String fileName,
	                                  Long fileSize,
	                                  String mimetype,
	                                  String fileExt);
}
