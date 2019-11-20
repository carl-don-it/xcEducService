package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 课程静态化所需要的模型
 *
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName CourseView
 * @date 2019年06月27日 下午 5:58
 */
@Data
@NoArgsConstructor
@ToString
public class CourseView implements Serializable {

	//课程基本信息
	private CourseBase courseBase;

	//课程营销
	private CourseMarket courseMarket;

	//课程图片
	private CoursePic coursePic;

	//教学计划
	private TeachplanNode teachplanNode;

}
