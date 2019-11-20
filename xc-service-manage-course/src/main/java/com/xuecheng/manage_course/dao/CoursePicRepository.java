package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator.
 */
public interface CoursePicRepository extends JpaRepository<CoursePic, String> {

	//CoursePicRepository父类提供的delete方法没有返回值，无法知道是否删除成功
	//这个由返回值,表示影响记录的行数
	long deleteByCourseid(String courseid);
}
