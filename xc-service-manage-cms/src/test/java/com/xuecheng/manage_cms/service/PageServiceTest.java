package com.xuecheng.manage_cms.service;

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
public class PageServiceTest {

	@Autowired
	CmsPageService cmsPageService;

	@Test
	public void testGetPageHtml() {
		String pageHtml = cmsPageService.getPageHtmlContent("5a795ac7dd573c04508f3a56");
		System.out.println(pageHtml);

	}

}
