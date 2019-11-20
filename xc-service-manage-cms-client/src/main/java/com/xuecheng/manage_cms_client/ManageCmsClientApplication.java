package com.xuecheng.manage_cms_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName ManageCmsClientApplication
 * @date 2019年06月19日 上午 8:34
 */
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")//扫描实体类
@ComponentScan(basePackages = {"com.xuecheng.framework"})
@ComponentScan(basePackages = {"com.xuecheng.manage_cms_client"})
public class ManageCmsClientApplication {
	public static void main(String[] args) {
		SpringApplication.run(ManageCmsClientApplication.class, args);
	}

}
