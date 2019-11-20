package com.xuecheng.manage_course;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-12 18:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRestRibbon {

	@Autowired
	RestTemplate restTemplate;

	//负载均衡测试
	@Test
	public void testRestRibbon() {

		String serviceId = "XC-SERVICE-MANAGE-COURSE";
		//服务id
		for (int i = 0; i < 20; i++) {
			ResponseEntity<Map> forEntity = restTemplate.getForEntity
					("http://" + serviceId
							+ "/cms/page/find/5a754adf6abb500ad05688d9", Map.class);
			Map body = forEntity.getBody();
			System.out.println(body);
		}
	}

	//测试XC-SERVICE-MANAGE-COURSE
	@Test
	public void testCourse() {

		String serviceId = "XC-SERVICE-MANAGE-COURSE";
		//服务id
		String url = "http://XC-SERVICE-MANAGE-COURSE/course/courseview/4028e581617f945f01617f9dabc40000";

        /*for (int i = 0; i < 20; i++) {
            ResponseEntity<Map> forEntity = restTemplate.getForEntity
                    ("http://" + serviceId
                            + "/course/courseview/4028e581617f945f01617f9dabc40000", Map.class);*/
		for (int i = 0; i < 20; i++) {
			ResponseEntity<Map> forEntity = restTemplate.getForEntity
					(url, Map.class);
			Map body = forEntity.getBody();
			System.out.println(body);
		}
	}

}
