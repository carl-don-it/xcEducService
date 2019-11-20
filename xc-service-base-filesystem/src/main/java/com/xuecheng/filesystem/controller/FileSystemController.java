package com.xuecheng.filesystem.controller;

import com.xuecheng.api.fileSystem.FileSystemControllerApi;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName FileSystemController
 * @date 2019年06月24日 下午 10:15
 */
@RestController
@RequestMapping("/filesystem")
public class FileSystemController implements FileSystemControllerApi {
	@Autowired
	private FileSystemService fileSystemService;

	@Override
	@PostMapping("/upload")
	public UploadFileResult upload(MultipartFile multipartFile,
	                               String filetag,
	                               String businesskey,
	                               String metadata) {
		return fileSystemService.upload(multipartFile, filetag, businesskey, metadata);
	}
}
