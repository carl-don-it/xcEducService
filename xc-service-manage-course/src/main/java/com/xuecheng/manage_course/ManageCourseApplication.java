package com.xuecheng.manage_course;

import com.xuecheng.framework.interceptor.FeignClientInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestOperations;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EntityScan("com.xuecheng.framework.domain.course")//扫描实体类
@ComponentScan(basePackages = {"com.xuecheng.api"})//扫描接口
@ComponentScan(basePackages = {"com.xuecheng.manage_course"})
@ComponentScan(basePackages = {"com.xuecheng.framework"})//扫描common下的所有类
public class ManageCourseApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ManageCourseApplication.class, args);
	}

	//Feign拦截器,添加header
	@Bean
	public FeignClientInterceptor feignClientInterceptor() {
		return new FeignClientInterceptor();
	}

	@Bean
	@LoadBalanced
	RestOperations restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
    /*
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }*/
}
