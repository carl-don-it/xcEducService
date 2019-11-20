package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName CategoryService
 * @date 2019年06月21日 下午 4:17
 */
@Service
public class CategoryService {
	@Autowired
	private CategoryMapper categoryMapper;

	public CategoryNode findCategoryList() {
		return categoryMapper.findCategoryList();
	}

}
