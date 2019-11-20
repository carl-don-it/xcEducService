package com.xuecheng.manage_cms.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 在mongoDB中文件存取需要用到的类
 *
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName Mongodb
 * @date 2019年06月16日 下午 9:47
 */
@Configuration
public class MongoConfig {
	@Value("${spring.data.mongodb.database}")
	String db;

	@Bean
	public GridFSBucket getGridFSBucket(MongoClient mongoClient) {
		MongoDatabase database = mongoClient.getDatabase(db);
		GridFSBucket bucket = GridFSBuckets.create(database);
		return bucket;
	}
}
