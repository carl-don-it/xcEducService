package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName CmsPageRepositoryTest
 * @date 2019年06月09日 下午 10:39
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

	@Autowired
	private CmsPageRepository cmsPageRepository;

	/**
	 * 查询所有
	 */
	@Test
	public void testFindAll() {
		List<CmsPage> list = cmsPageRepository.findAll();
		System.out.println(list.toString());
	}

	/**
	 * 分页查询
	 */
	@Test
	public void testFindAllOnPage() {
		int page = 0;
		int size = 10;
		PageRequest pageable = PageRequest.of(page, size);
		Page<CmsPage> all = cmsPageRepository.findAll(pageable);
		System.out.println(all);
	}

	/**
	 * 按条件分页查询
	 */
	@Test
	public void testFindAllOnPageAndTerms() {
		//如何分页
		int page = 0;
		int size = 10;
		PageRequest pageable = PageRequest.of(page, size);

		//按什么条件
		ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher
				("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
		CmsPage cmsPage = new CmsPage();
		cmsPage.setPageAliase("预览");

		Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
		Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
		System.out.println(all);
	}

	//修改
	@Test
	public void testUpdate() {
		Optional<CmsPage> optional = cmsPageRepository.findById("5ae1979f0e6618644cd7a6fd");
		if (optional.isPresent()) {
			CmsPage cmsPage = optional.get();
			cmsPage.setPageName("测试页面01");
			cmsPageRepository.save(cmsPage);
		}
	}

	//save _class是否自动添加
	@Test
	public void testSave() {
		CmsPage cmsPage = new CmsPage();
		cmsPage.setPageName("_class是否自动添加");
		cmsPageRepository.save(cmsPage);
	}

}
