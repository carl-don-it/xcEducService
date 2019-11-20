package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName GridFsTest
 * @date 2019年06月16日 下午 9:37
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFsTest {
	@Autowired
	GridFsTemplate gridFsTemplate;
	@Autowired
	GridFSBucket gridFSBucket;

	/**
	 * 存文件，依赖gridFsTemplate
	 *
	 * @throws FileNotFoundException
	 */
	@Test
	public void testStore() throws FileNotFoundException {
		//要存储的文件
		File file = new File("E:\\BaiduNetdiskDownload\\0.笔记+讲义+资料\\7.在职加薪学成在线02\\73.会员版(2.0)-就业课(2.0)-课程预览Spring Cloud EurekaFeign\\100.课程预览Eureka Feign\\资料\\课程详情页面模板\\course.ftl");
		//定义输入流
		FileInputStream inputStram = new FileInputStream(file);
		//向GridFS存储文件
		ObjectId objectId = gridFsTemplate.store(inputStram, "course.ftl", "");
		//得到文件ID
		String fileId = objectId.toString();
		System.out.println(file);
	}

	@Test
	public void queryFile() throws IOException {
		String fileId = "5d064713b1a1ad3e6c3f57f8";
		//根据id查询文件
		GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
		//打开下载流对象
		GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
		//创建gridFsResource，用于获取流对象
		GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
		//获取流中的数据
		String s = IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
		System.out.println(s);
	}

	@Test
	public void testDelFile() throws IOException {
		//根据文件id删除fs.files和fs.chunks中的记录
		gridFsTemplate.delete(Query.query(Criteria.where("_id").is("5d064713b1a1ad3e6c3f57f8")));
	}
}
