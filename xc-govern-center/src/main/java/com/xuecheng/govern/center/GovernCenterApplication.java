package com.xuecheng.govern.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName GovernCenterApplication
 * @date 2019年06月26日 下午 2:30
 */

@SpringBootApplication
@EnableEurekaServer
public class GovernCenterApplication {
	public static void main(String[] args) {
		SpringApplication.run(GovernCenterApplication.class, args);
	}
}
