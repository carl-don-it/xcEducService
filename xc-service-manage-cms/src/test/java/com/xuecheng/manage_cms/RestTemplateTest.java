package com.xuecheng.manage_cms;

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
public class RestTemplateTest {

	@Autowired
	RestTemplate restTemplate;

	@Test
	public void testRestTemplate() {
		ResponseEntity<Map> forEntity = restTemplate.getForEntity
				("http://127.0.0.1:31001/cms/page/find/5a754adf6abb500ad05688d9", Map.class);
		Map body = forEntity.getBody();
		System.out.println(body);
	}

}
