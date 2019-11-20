package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName TeachplanMapper
 * @date 2019年06月20日 下午 7:18
 */
@Mapper
public interface TeachplanMapper {

	TeachplanNode findTeachplanList(String courseId);

}
