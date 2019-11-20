package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.respones.GetMediaResult;
import com.xuecheng.framework.domain.learning.respones.LearningCode;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.learning.client.CourseSearchClient;
import com.xuecheng.learning.dao.XcLearningCourseRepository;
import com.xuecheng.learning.dao.XcTaskHisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public class LearningService {

	@Autowired
	private CourseSearchClient courseSearchClient;
	@Autowired
	private XcTaskHisRepository xcTaskHisRepository;
	@Autowired
	private XcLearningCourseRepository xcLearningCourseRepository;

	//获取课程学习地址（视频播放地址）
	public GetMediaResult getmedia(String courseId, String teachplanId) {
		//校验学生的学生权限...

		//远程调用搜索服务查询课程计划所对应的课程媒资信息
		TeachplanMediaPub teachplanMediaPub = courseSearchClient.getmedia(teachplanId);
		if (teachplanMediaPub == null || StringUtils.isEmpty(teachplanMediaPub.getMediaUrl())) {
			//获取学习地址错误
			ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
		}
		return new GetMediaResult(CommonCode.SUCCESS, teachplanMediaPub.getMediaUrl());
	}

	/**
	 * 根据xctask添加选课,有就添加发送,无就发送
	 *
	 * @param xcTask
	 */
	@Transactional
	public boolean addCourse(String userId, String courseId, String valid, Date startTime, Date endTime, XcTask xcTask) {

		//2. 查看历史任务表
		Optional<XcTaskHis> xcTaskHisOptional = xcTaskHisRepository.findById(xcTask.getId());
		if (xcTaskHisOptional.isPresent()) {
			return true;
		} else {
			//添加选课
			if (StringUtils.isEmpty(courseId)) {
				ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
			}
			if (StringUtils.isEmpty(userId)) {
				ExceptionCast.cast(LearningCode.CHOOSECOURSE_USERISNULl);
			}
			//3. 查询是否已有选课,避免重复添加,双重保险
			XcLearningCourse xcLearningCourse =
					xcLearningCourseRepository.findXcLearningCourseByUserIdAndCourseId(userId, courseId);
			if (xcLearningCourse == null) {
				//没有选课记录则添加
				xcLearningCourse = new XcLearningCourse();
				xcLearningCourse.setUserId(userId);
				xcLearningCourse.setCourseId(courseId);
				xcLearningCourse.setStartTime(startTime);
				xcLearningCourse.setEndTime(endTime);
				xcLearningCourse.setStatus("501001");
				xcLearningCourseRepository.save(xcLearningCourse);
			} else {
				//有选课记录则更新日期
				xcLearningCourse.setValid(valid);
				xcLearningCourse.setStartTime(startTime);
				xcLearningCourse.setEndTime(endTime);
				xcLearningCourse.setStatus("501001");
				xcLearningCourseRepository.save(xcLearningCourse);
			}
			//保存历史任务
			XcTaskHis xcTaskHis = new XcTaskHis();
			BeanUtils.copyProperties(xcTask, xcTaskHis);
			xcTaskHisRepository.save(xcTaskHis);

			//发送mq成功信息
			return true;

		}

	}
}
