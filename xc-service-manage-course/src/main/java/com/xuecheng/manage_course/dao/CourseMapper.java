package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator.
 */
@Mapper
@Repository//没什么意思,只是不报错,直接异常压制
public interface CourseMapper {
	CourseBase findCourseBaseById(String id);

	public Page<CourseInfo> findCourseList(CourseListRequest courseListRequest);

}
