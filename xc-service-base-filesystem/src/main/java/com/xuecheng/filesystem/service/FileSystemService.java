package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName FileSystemService
 * @date 2019年06月24日 下午 10:19
 */
@Service
public class FileSystemService {
	@Autowired
	private FileSystemRepository fileSystemRepository;

	//注入fastDFs连接参数
	@Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
	private int connect_timeout_in_seconds;
	@Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
	private int network_timeout_in_seconds;
	@Value("${xuecheng.fastdfs.charset}")
	private String charset;
	@Value("${xuecheng.fastdfs.tracker_servers}")
	private String tracker_servers;

	public UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata) {
		//1.检验参数
		if (multipartFile == null) {
			ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
		}

		//2.请求fastDFS上传文件,返回FileId
		//2.1初始化fastdfs参数
		this.initFdfsConfig();
		//2.2上传文件,返回fileId
		String fileId = this.fdfs_upload(multipartFile);
		if (StringUtils.isEmpty(fileId)) {
			ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
		}

		//3.把文件信息传入MongoDB中
		FileSystem fileSystem = new FileSystem();
		//fileId和filePath是一样的
		fileSystem.setFileId(fileId);
		fileSystem.setFilePath(fileId);

		fileSystem.setFiletag(filetag);
		fileSystem.setBusinesskey(businesskey);
		fileSystem.setFileName(multipartFile.getOriginalFilename());
		fileSystem.setFileType(multipartFile.getContentType());
		if (StringUtils.isNotEmpty(metadata)) {
			try {
				Map map = JSON.parseObject(metadata, Map.class);
				fileSystem.setMetadata(map);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//4.成功返回结果
		fileSystemRepository.save(fileSystem);
		return new UploadFileResult(CommonCode.SUCCESS, fileSystem);
	}

	//上传文件,返回fileId
	private String fdfs_upload(MultipartFile multipartFile) {
		try {
			//获取trackerClient
			TrackerClient trackerClient = new TrackerClient();
			//获取trackerServer
			TrackerServer trackerServer = trackerClient.getConnection();
			//获取storageServer
			StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
			//获取storageClient
			StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
			//得到文件字节
			byte[] bytes = multipartFile.getBytes();
			//得到文件的原始名称
			String originalFilename = multipartFile.getOriginalFilename();
			//得到文件扩展名
			String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
			String fileId = storageClient1.upload_file1(bytes, ext, null);
			System.out.println("上传的文件id" + fileId);
			return fileId;
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
		}
		return null;
	}

	//初始化fastdfs参数
	private void initFdfsConfig() {
		try {
			ClientGlobal.initByTrackers(tracker_servers);
			ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
			ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
			ClientGlobal.setG_charset(charset);
		} catch (Exception e) {
			e.printStackTrace();
			//初始化文件系统出错
			ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
		}
	}
}
