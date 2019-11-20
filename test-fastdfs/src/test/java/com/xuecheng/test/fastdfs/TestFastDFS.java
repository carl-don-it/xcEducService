package com.xuecheng.test.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {

	@Test
	public void testUpload() {
		while (true) {
			//加载配置文件
			try {
				ClientGlobal.initByProperties("config/fastdfs-client.properties");
			} catch (IOException | MyException e) {
				e.printStackTrace();
			}
			//定义TrackerClient,用于请求TrackerServer
			TrackerClient trackerClient = new TrackerClient();
			//连接tracker
			TrackerServer trackerServer = null;
			try {
				trackerServer = trackerClient.getConnection();
			} catch (IOException e) {
				e.printStackTrace();

			}
			//获取storage
			StorageServer storageServer = null;
			try {
				storageServer = trackerClient.getStoreStorage(trackerServer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//创建storageClient
			StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);

			//向storage服务器上传文件
			String filePath = "C:\\Users\\TJR_S\\Pictures\\Camera Roll\\无脸男\\月夜.jpg";
			String file_ext_name = filePath.substring(filePath.lastIndexOf(".") + 1);
			String fileId = null;
			try {
				fileId = storageClient1.upload_file1(filePath, file_ext_name, null);
			} catch (IOException | MyException e) {
				e.printStackTrace();
			}
			//上传成功后拿到文件id
			if (trackerServer != null) {
				try {
					trackerServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (storageServer != null) {
				try {
					storageServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println(fileId);
		}
	}

	@Test
	public void testDownload() throws Exception {
		while (true) {
			//初始化配置
			ClientGlobal.initByProperties("config/fastdfs-client.properties");
			//获取trackerClient
			TrackerClient trackerClient = new TrackerClient();
			//获取trackerServer
			TrackerServer trackerServer = trackerClient.getConnection();
			//获取storageServer
			StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
			//获取storageClient
			StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
			//下载
			String filePath = "group3/M00/00/00/wKhIil0qjIyAdh0yAACRmDW9KG8615.jpg";
			byte[] bytes = storageClient1.download_file1(filePath);
			//写入文件,临时文件名字是时间戳，加上后缀

			if (bytes != null) {
				String file_ext_name = filePath.substring(filePath.lastIndexOf(".") + 1);
				String outputPath = "D:/" + new Date().getTime() + "." + file_ext_name;

				FileOutputStream fileOutputStream = new FileOutputStream(new File(outputPath));
				fileOutputStream.write(bytes);

				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (trackerServer != null) {
				try {
					trackerServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (storageServer != null) {
				try {
					storageServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

}
