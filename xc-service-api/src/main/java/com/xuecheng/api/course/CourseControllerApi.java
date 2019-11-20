package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePreviewResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(value = "course管理接口", description = "course管理接口，提供course管理相关信息的查询")
public interface CourseControllerApi {

	@ApiOperation("查询课程列表")
	public QueryResponseResult<CourseInfo> findCourseList(Integer page, Integer size, CourseListRequest courseListRequest);

	@ApiOperation("添加课程")
	public AddCourseResult findCoursebaseById(CourseBase courseBase);

	@ApiOperation("查询课程基础信息")
	public CourseBase findCoursebaseById(String courseId);

	@ApiOperation("修改课程基础信息")
	public ResponseResult updateCoursebase(String courseId, CourseBase courseBase);

	@ApiOperation("保存课程与图片的关联信息")
	public ResponseResult saveCoursePic(String courseId, String pic);

	@ApiOperation("查询课程与图片的关联信息")
	public CoursePic findCoursePicList(String courseId);

	@ApiOperation("删除课程与图片的关联信息")
	public ResponseResult deleteCoursePic(String courseId);

	@ApiOperation("查询课程营销信息")
	public CourseMarket findCourseMarketById(String courseId);

	@ApiOperation("修改课程营销信息")
	public ResponseResult updateCourseMarket(String courseId, CourseMarket courseMarket);

	@ApiOperation("课程计划查询")
	public TeachplanNode findTeachplanList(String courseId);

	@ApiOperation("课程计划添加")
	public ResponseResult addTeachplan(Teachplan teachplan);

	@ApiOperation("课程视图模型")
	public CourseView getCourseView(String id);

	/**
	 * 预览课程
	 *
	 * @param id courseId
	 * @return
	 */
	@ApiOperation("课程预览")
	@ApiImplicitParam("课程id")
	public CoursePreviewResult previewCourse(String id);

	/**
	 * 发布课程
	 *
	 * @param id courseId
	 * @return
	 */
	@ApiOperation("发布课程")
	@ApiImplicitParam(name = "id", value = "课程id", paramType = "path", dataType = "String", required = true)
	public CoursePublishResult publishCourse(String id);

	@ApiOperation("保存课程计划与媒资文件关联")
	public ResponseResult savemedia(TeachplanMedia teachplanMedia);
}
