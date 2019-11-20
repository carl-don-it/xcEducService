package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePreviewResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName CourseController
 * @date 2019年06月20日 下午 6:57
 */
//有几种方法可以拿到request
@RestController
@RequestMapping("/course")
@PreAuthorize("hasAuthority('xc_teachmanager_course')")//全部方法都需要权限
public class CourseController extends BaseController implements CourseControllerApi {

	@Autowired
	private CourseService courseService;

	@Override
	@PreAuthorize("hasAuthority('course_list_teachplan')")//方法注解优先于类注解
	@GetMapping("/teachplan/list/{courseId}")
	public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {
		return courseService.findTeachplanList(courseId);
	}

	@Override
	@PostMapping("/teachplan/add")
	public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
		return courseService.addTeachplan(teachplan);
	}

	@Override
	@GetMapping("/coursebase/list/{page}/{size}")
	public QueryResponseResult<CourseInfo> findCourseList(
			@PathVariable("page") Integer page,
			@PathVariable("size") Integer size,
			CourseListRequest courseListRequest) {
		//调用工具类取出request中的headers中的jwt令牌，拿出令牌中的companyID
		XcOauth2Util xcOauth2Util = new XcOauth2Util();
		XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
		if (userJwt == null) {
			ExceptionCast.cast(CommonCode.UNAUTHENTICATED);
		}
		String companyId = userJwt.getCompanyId();

		return courseService.findCourseList(companyId, page, size, courseListRequest);
	}

	@Override
	@PostMapping("/coursebase/add")
	public AddCourseResult findCoursebaseById(@RequestBody CourseBase courseBase) {
		return courseService.addCourse(courseBase);
	}

	//@PreAuthorize("hasAuthority('course_get_baseinfo')")//权限控制
	@Override
	@GetMapping("/coursebase/find/{courseId}")
	public CourseBase findCoursebaseById(@PathVariable("courseId") String courseId) {
		return courseService.findCoursebaseById(courseId);
	}

	@Override
	@PostMapping("/coursebase/update/{courseId}")
	public ResponseResult updateCoursebase(@PathVariable("courseId") String courseId,
	                                       @RequestBody CourseBase courseBase) {
		return courseService.updateCoursebase(courseId, courseBase);
	}

	@Override
	@GetMapping("/market/find/{courseId}")
	public CourseMarket findCourseMarketById(@PathVariable("courseId") String courseId) {
		return courseService.findCourseMarketById(courseId);
	}

	@Override
	@PostMapping("/market/update/{courseId}")
	public ResponseResult updateCourseMarket(@PathVariable("courseId") String courseId,
	                                         @RequestBody CourseMarket courseMarket) {
		CourseMarket courseMarket_new = courseService.updateCourseMarket(courseId, courseMarket);
		if (courseMarket_new != null) {
			return new ResponseResult(CommonCode.SUCCESS);
		} else {
			return new ResponseResult(CommonCode.FAIL);
		}
	}

	@Override
	@PostMapping("/coursepic/add")
	public ResponseResult saveCoursePic(@RequestParam("courseId") String courseId,
	                                    @RequestParam("pic") String pic) {
		return courseService.saveCoursePic(courseId, pic);
	}

	//@PreAuthorize("hasAuthority('course_find_pic')")//控制权限
	@Override
	@GetMapping("/coursepic/list/{courseId}")
	public CoursePic findCoursePicList(@PathVariable("courseId") String courseId) {
		return courseService.findCoursePicList(courseId);
	}

	@Override
	@DeleteMapping("/coursepic/delete")
	public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
		return courseService.deleteCoursePic(courseId);
	}

	@Override
	@GetMapping("/courseview/{id}")
	public CourseView getCourseView(@PathVariable("id") String id) {
		return courseService.getCourseView(id);
	}

	@Override
	@PostMapping("/preview/{id}")
	public CoursePreviewResult previewCourse(@PathVariable("id") String id) {
		return courseService.previewCourse(id);
	}

	@Override
	@PostMapping("/publish/{id}")
	public CoursePublishResult publishCourse(@PathVariable("id") String id) {
		return courseService.publishCourse(id);
	}

	@Override
	@PostMapping("/savemedia")
	public ResponseResult savemedia(@RequestBody TeachplanMedia teachplanMedia) {
		return courseService.savemedia(teachplanMedia);
	}
}
