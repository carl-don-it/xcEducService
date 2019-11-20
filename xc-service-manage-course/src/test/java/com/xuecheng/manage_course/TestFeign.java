package com.xuecheng.manage_course;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-12 18:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFeign {

	@Autowired
	CmsPageClient cmsPageClient;//接口的代理对象

	//负载均衡测试
	@Test
	public void testRestRibbon() {

		for (int i = 0; i < 20; i++) {
			CmsPage cmsPage = cmsPageClient.findById("5a754adf6abb500ad05688d9");
			System.out.println(cmsPage);
		}
	}

}
