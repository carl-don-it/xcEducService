package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePreviewResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName CourseService
 * @date 2019年06月20日 下午 5:17
 */
@Service
public class CourseService {

	@Autowired
	private TeachplanMapper teachplanMapper;

	@Autowired
	private TeachplanRepository teachplanRepository;

	@Autowired
	private CourseBaseRepository courseBaseRepository;

	@Autowired
	private CourseMapper courseMapper;

	@Autowired
	private CourseMarketRepository courseMarketRepository;

	@Autowired
	private CoursePicRepository coursePicRepository;

	@Autowired
	private CoursePubRepository coursePubRepository;

	@Autowired
	private TeachplanMediaRepository teachplanMediaRepository;

	@Autowired
	private TeachplanMediaPubRepository teachplanMediaPubRepository;

	//远程调用cmsPageContreller的功能
	@Autowired
	private CmsPageClient cmsPageClient;

	@Value("${course-publish.dataUrlPre}")
	private String publish_dataUrlPre;
	@Value("${course-publish.pagePhysicalPath}")
	private String publish_page_physicalpath;
	@Value("${course-publish.pageWebPath}")
	private String publish_page_webpath;
	@Value("${course-publish.siteId}")
	private String publish_siteId;
	@Value("${course-publish.templateId}")
	private String publish_templateId;
	@Value("${course-publish.previewUrl}")
	private String previewUrl;

	public TeachplanNode findTeachplanList(String courseId) {
		return teachplanMapper.findTeachplanList(courseId);
	}

	/**
	 * 保存课程计划,事务操作
	 *
	 * @param teachplan
	 * @return
	 */
	@Transactional
	public ResponseResult addTeachplan(Teachplan teachplan) {
		//1.先判断参数是否非法 pname, courseId, teachplan
		if (teachplan == null ||
				StringUtils.isEmpty(teachplan.getPname()) ||
				StringUtils.isEmpty(teachplan.getCourseid())) {
			ExceptionCast.cast(CommonCode.INVALID_PARAM);
		}

		//2.判断是否有根节点,根节点与级别息息相关
		//2.1 有根节点,顺便设置级别为3
		String parentId = teachplan.getParentid();
		if (StringUtils.isNotEmpty(parentId)) {
			teachplan.setGrade("3");
		} else {
			//2.2 没有根节点,先查找数据库有没有,没有就创立一个,顺便设置级别
			teachplan.setParentid(this.getRootTeachplanId(teachplan));
			teachplan.setGrade("2");
		}

		//3. 保存bean对象
		teachplanRepository.save(teachplan);

		//4. 返回成功code
		return new ResponseResult(CommonCode.SUCCESS);
	}

	/**
	 * 根据条件分页查询
	 *
	 * @param companyId
	 * @param page              第几页
	 * @param size              一页大小
	 * @param courseListRequest 里面就是companyId
	 * @return
	 */
	public QueryResponseResult<CourseInfo> findCourseList(String companyId, Integer page, Integer size,
	                                                      CourseListRequest courseListRequest) {
		if (courseListRequest == null) {
			courseListRequest = new CourseListRequest();
		}
		courseListRequest.setCompanyId(companyId);
		//1. 先把参数验证好,设置默认值
		if (page <= 0) {
			page = 1;
		}
		if (size <= 0) {
			size = 10;
		}
		//2. 进行分页查询，分页插件，PageHelper是mybatis的通用分页插件，通过mybatis的拦截器实现分页功能
		PageHelper.startPage(page, size);
		Page<CourseInfo> infoPage = courseMapper.findCourseList(courseListRequest);

		//3. 封装进queryresult
		QueryResult<CourseInfo> courseInfoQueryResult = new QueryResult<>();
		courseInfoQueryResult.setList(infoPage.getResult());
		courseInfoQueryResult.setTotal(infoPage.getTotal());

		//4. 返回结果
		return new QueryResponseResult<>(CommonCode.SUCCESS, courseInfoQueryResult);

	}

	/**
	 * 添加课程
	 *
	 * @param courseBase
	 * @return
	 */
	@Transactional
	public AddCourseResult addCourse(CourseBase courseBase) {
		//课程状态默认为未发布
		courseBase.setStatus("202001");
		courseBaseRepository.save(courseBase);
		//返回的类型需要注意,需要courseId
		return new AddCourseResult(CommonCode.SUCCESS, courseBase.getId());
	}

	/**
	 * 没有根节点的时候创建一个,根据课程基本信息创建
	 *
	 * @param teachplan
	 */
	private String getRootTeachplanId(Teachplan teachplan) {
		//1. 先查询课程计划数据库是否有课程计划
		String courseid = teachplan.getCourseid();
		List<Teachplan> rootTeachplanList = teachplanRepository.findByCourseidAndParentid(courseid, "0");

		//2. 如果没有,则创建一个,创建根课程计划需要名字和课程id
		if (rootTeachplanList == null || rootTeachplanList.size() <= 0) {
			//2.1. 名字:先拿出课程,课程不存在则抛出异常
			Optional<CourseBase> byId = courseBaseRepository.findById(courseid);
			if (!byId.isPresent()) {
				ExceptionCast.cast(CourseCode.COURSE_NOTEXIST);
			}
			//2.2. 课程存在,创建课程计划根节点
			CourseBase courseBase = byId.get();
			Teachplan rooTeachplan = new Teachplan();
			rooTeachplan.setPname(courseBase.getName());
			rooTeachplan.setCourseid(courseid);
			rooTeachplan.setParentid("0");
			rooTeachplan.setGrade("1");//一级结点
			rooTeachplan.setStatus("0");
			teachplanRepository.save(rooTeachplan);
			return teachplan.getId();
		} else {
			//3. 课程计划根目录存在,
			return rootTeachplanList.get(0).getId();
		}
	}

	/**
	 * 根据courseId查找课程基础信息,不存在则返回null
	 *
	 * @param courseId
	 * @return
	 */
	public CourseBase findCoursebaseById(String courseId) {
		Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
		return courseBaseOptional.orElse(null);
	}

	/**
	 * 更新课程基本信息,如果课程不存在,不存在就报错
	 *
	 * @param courseId
	 * @param courseBase
	 * @return
	 */
	@Transactional
	public ResponseResult updateCoursebase(String courseId, CourseBase courseBase) {
		//1.判断参数
		if (courseBase == null || StringUtils.isEmpty(courseId)) {
			ExceptionCast.cast(CommonCode.INVALID_PARAM);
		}
		//2.取出课程
		CourseBase one = this.findCoursebaseById(courseId);
		if (one == null) {
			ExceptionCast.cast(CourseCode.COURSE_NOTEXIST);
		}
		//3. 在原有课程基础上修改课程
		one.setName(courseBase.getName());
		one.setMt(courseBase.getMt());
		one.setSt(courseBase.getSt());
		one.setGrade(courseBase.getGrade());
		one.setStudymodel(courseBase.getStudymodel());
		one.setUsers(courseBase.getUsers());
		one.setDescription(courseBase.getDescription());

		courseBaseRepository.save(one);
		return new ResponseResult(CommonCode.SUCCESS);
	}

	/**
	 * 根据id=courseid查询课程营销信息
	 *
	 * @param courseId
	 * @return
	 */
	public CourseMarket findCourseMarketById(String courseId) {
		Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(courseId);
		return courseMarketOptional.orElse(null);
	}

	/**
	 * 课程营销信息可以不存在,所以不用报错
	 * 保存或者更新课程营销信息
	 * 因为有事务,不确定是否成功,因此resultCode不可控制,在controller中控制
	 *
	 * @param courseId
	 * @param courseMarket
	 * @return
	 */
	@Transactional
	public CourseMarket updateCourseMarket(String courseId, CourseMarket courseMarket) {
		CourseMarket one = this.findCourseMarketById(courseId);
		//已有信息,则更新保存
		if (one != null) {
			one.setCharge(courseMarket.getCharge());
			one.setStartTime(courseMarket.getStartTime());//课程有效期，开始时间
			one.setEndTime(courseMarket.getEndTime());//课程有效期，结束时间
			one.setPrice(courseMarket.getPrice());
			one.setQq(courseMarket.getQq());
			one.setValid(courseMarket.getValid());
			courseMarketRepository.save(one);
		} else {
			//不存在,创建存入
			one = new CourseMarket();
			BeanUtils.copyProperties(courseMarket, one);
			courseMarketRepository.save(one);
		}
		return one;
	}

	/**
	 * 保存课程与图片的关联信息,因为关联信息只有一个,所以先查询,再保存
	 *
	 * @param courseId
	 * @param pic
	 * @return
	 */
	@Transactional
	public ResponseResult saveCoursePic(String courseId, String pic) {
		CoursePic coursePic;
		//1. 查询课程与图片的关联信息
		Optional<CoursePic> coursePicOptional = coursePicRepository.findById(courseId);

		if (coursePicOptional.isPresent()) {
			//2. 存在,用原来的对象
			coursePic = coursePicOptional.get();
		} else {
			//3. 不存在,用新的对象
			coursePic = new CoursePic();
		}
		//4. 传入信息
		coursePic.setCourseid(courseId);
		coursePic.setPic(pic);
		coursePicRepository.save(coursePic);
		return new ResponseResult(CommonCode.SUCCESS);
	}

	/**
	 * 查询课程相关的图片信息
	 *
	 * @param courseId
	 * @return
	 */
	public CoursePic findCoursePicList(String courseId) {
		Optional<CoursePic> coursePicOptional = coursePicRepository.findById(courseId);
		return coursePicOptional.orElse(null);
	}

	/**
	 * 删除课程相关的图片信息
	 *
	 * @param courseId
	 * @return
	 */

	@Transactional
	public ResponseResult deleteCoursePic(String courseId) {
		long result = coursePicRepository.deleteByCourseid(courseId);
		if (result > 0) {
			return new ResponseResult(CommonCode.SUCCESS);

		}
		return new ResponseResult(CommonCode.FAIL);

	}

	/**
	 * 查询课程模型信息视图
	 *
	 * @param id
	 * @return
	 */
	public CourseView getCourseView(String id) {
		CourseView courseView = new CourseView();

		//查询课程基本信息
		Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
		if (courseBaseOptional.isPresent()) {
			courseView.setCourseBase(courseBaseOptional.get());
		}

		//查询课程图片
		Optional<CoursePic> coursePicOptional = coursePicRepository.findById(id);
		if (coursePicOptional.isPresent()) {
			courseView.setCoursePic(coursePicOptional.get());
		}
		//查询课程营销
		Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
		if (courseMarketOptional.isPresent()) {
			courseView.setCourseMarket(courseMarketOptional.get());
		}
		//查询教学计划
		TeachplanNode teachplanList = teachplanMapper.findTeachplanList(id);
		courseView.setTeachplanNode(teachplanList);

		return courseView;
	}

	/**
	 * 远程调用cmsPage的save方法,需要传入一个cmspage,返回带有pageId的cmspage
	 * 页面预览要返回的东西
	 * 不需要事务控制,仅仅是远程调用
	 *
	 * @param id courseId
	 * @return CoursePreviewResult 里面有url可以预览
	 */
	public CoursePreviewResult previewCourse(String id) {
		//1. 构建CmsPage对象
		CmsPage cmsPage = this.generateCmsPageByCourseId(id);

		//2. 远程调用cms的savePage,添加课程详情页面,返回 CmsPageResult
		CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);

		//3. 拿到pageId,拼接课程预览url : www.xuecheng.com/cms/preview/+pageId
		String pageId = cmsPageResult.getCmsPage().getPageId();
		if (StringUtils.isEmpty(pageId)) {
			ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CDETAILERROR);
		}

		String url = previewUrl + pageId;

		return new CoursePreviewResult(CommonCode.SUCCESS, url);

	}

	/**
	 * course内调用cms实现发布课程
	 *
	 * @param id courseId
	 */
	@Transactional
	public CoursePublishResult publishCourse(String id) {
		//1. 生成cmsPage
		CmsPage cmsPage = this.generateCmsPageByCourseId(id);
		//2. 远程调用发布cmsPage
		CmsPostPageResult cmsPostPageResult = cmsPageClient.quickPostPage(cmsPage);
		//这里需要对远程调用返回的信息进行检验
		if (!cmsPostPageResult.isSuccess()) {
			ExceptionCast.cast(CommonCode.FAIL);
		}

		//3.更新课程状态
		CourseBase courseBase = saveCoursePubState(id);

		//4.保存课程索引信息
		//4.1 创建课程发布表模型
		CoursePub coursePub = this.createCoursePubByCourseId(id);
		//4.2 把课程发布表保存到数据库中,供ES系统索引
		CoursePub newCoursePub = this.saveCoursePub(id, coursePub);

		if (newCoursePub == null) {
			//创建课程索引信息失败
			ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CREATE_INDEX_ERROR);
		}
		//缓存课程的信息

		//向teachplanMediaPub中保存课程媒资信息
		this.saveTeachplanMediaPub(id);

		//4. 返回发布结果,使用CoursePublishResult,带上页面url
		String pageUrl = cmsPostPageResult.getPageUrl();
		return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
	}

	/**
	 * 保存课程计划与媒资文件的关联信息
	 *
	 * @param teachplanMedia
	 * @return
	 */
	public ResponseResult savemedia(TeachplanMedia teachplanMedia) {
		//1. 非法参数校验
		if (teachplanMedia == null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())) {
			ExceptionCast.cast(CommonCode.INVALID_PARAM);
		}

		//2. 校验课程计划是否是3级
		//课程计划id
		String teachplanId = teachplanMedia.getTeachplanId();
		//查询到课程计划
		Optional<Teachplan> optional = teachplanRepository.findById(teachplanId);
		if (!optional.isPresent()) {
			ExceptionCast.cast(CommonCode.INVALID_PARAM);
		}
		//查询到课程计划
		Teachplan teachplan = optional.get();
		//取出等级
		String grade = teachplan.getGrade();
		if (StringUtils.isEmpty(grade) || !grade.equals("3")) {
			//只允许选择第三级的课程计划关联视频
			ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
		}
		//3. 查询teachplanMedia
		Optional<TeachplanMedia> mediaOptional = teachplanMediaRepository.findById(teachplanId);
		TeachplanMedia one = null;
		if (mediaOptional.isPresent()) {
			one = mediaOptional.get();
		} else {
			one = new TeachplanMedia();
		}

		//4. 将one保存到数据库
		one.setCourseId(teachplan.getCourseid());//课程id
		one.setMediaId(teachplanMedia.getMediaId());//媒资文件的id
		one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());//媒资文件的原始名称
		one.setMediaUrl(teachplanMedia.getMediaUrl());//媒资文件的url
		one.setTeachplanId(teachplanId);
		teachplanMediaRepository.save(one);

		return new ResponseResult(CommonCode.SUCCESS);
	}

	/**
	 * 创建课程发布表,根据courseId
	 *
	 * @param id courseId
	 * @return
	 */
	private CoursePub createCoursePubByCourseId(String id) {
		CoursePub coursePub = new CoursePub();
		//根据课程id查询course_base
		Optional<CourseBase> baseOptional = courseBaseRepository.findById(id);
		if (baseOptional.isPresent()) {
			CourseBase courseBase = baseOptional.get();
			//将courseBase属性拷贝到CoursePub中
			BeanUtils.copyProperties(courseBase, coursePub);
		}

		//查询课程图片
		Optional<CoursePic> picOptional = coursePicRepository.findById(id);
		if (picOptional.isPresent()) {
			CoursePic coursePic = picOptional.get();
			BeanUtils.copyProperties(coursePic, coursePub);
		}

		//课程营销信息
		Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
		if (marketOptional.isPresent()) {
			CourseMarket courseMarket = marketOptional.get();
			BeanUtils.copyProperties(courseMarket, coursePub);
		}

		//课程计划信息
		TeachplanNode teachplanNode = teachplanMapper.findTeachplanList(id);
		String jsonString = JSON.toJSONString(teachplanNode);
		//将课程计划信息json串保存到 course_pub中
		coursePub.setTeachplan(jsonString);
		return coursePub;
	}

	/**
	 * 根据Id查找CoursePic
	 *
	 * @param id courseId
	 * @return
	 */
	private CoursePic findCoursePicById(String id) {
		Optional<CoursePic> CoursePicOptional = coursePicRepository.findById(id);
		return CoursePicOptional.orElse(null);
	}

	/**
	 * 将coursePub对象保存到数据库,ES系统会自动索引CoursePub表
	 *
	 * @param id
	 * @param coursePub
	 * @return coursePub new ,has been renewed
	 */
	private CoursePub saveCoursePub(String id, CoursePub coursePub) {
		CoursePub coursePubNew = null;
		//有就更新,没有就创建
		Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);

		if (coursePubOptional.isPresent()) {
			coursePubNew = coursePubOptional.get();
		} else {
			coursePubNew = new CoursePub();
		}
		BeanUtils.copyProperties(coursePub, coursePubNew);
		//id最后要在设置一下
		coursePubNew.setId(id);
		//时间戳,给logstach使用
		coursePubNew.setTimestamp(new Date());
		//发布时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		String date = simpleDateFormat.format(new Date());
		coursePubNew.setPubTime(date);

		coursePubRepository.save(coursePubNew);
		return coursePubNew;
	}

	/**
	 * 更新课程发布状态
	 *
	 * @param courseId
	 * @return
	 */
	private CourseBase saveCoursePubState(String courseId) {
		CourseBase courseBase = this.findCoursebaseById(courseId);
		//更新发布状态
		courseBase.setStatus("202002");
		CourseBase save = courseBaseRepository.save(courseBase);
		return save;
	}

	/**
	 * 根据courseId生成一个CmsPage对象
	 *
	 * @param courseId
	 * @return
	 */
	private CmsPage generateCmsPageByCourseId(String courseId) {
		//新建一个CmsPage对象
		CmsPage cmsPage = new CmsPage();

		/*dataUrl,pageAlias,pageName才是与id相关的,其他都是默认的*/
		//pageName : id
		cmsPage.setPageName(courseId + ".html");
		//pageAlias : name
		cmsPage.setPageAliase(this.findCoursebaseById(courseId).getName());
		//dataUrl : "http://XC-SERVICE-MANAGE-COURSE/course/courseview/"+id  要考虑注册中心
		cmsPage.setDataUrl(publish_dataUrlPre + courseId);

		/*下面固定*/
		//siteId: "5d14bb30b1a1ad4d24a18926"
		cmsPage.setSiteId(publish_siteId);
		//pageWebPath : "/course/detail/" 完整的页面查看url="www.xuecheng.com"+pageWebPath+pageName
		cmsPage.setPageWebPath(publish_page_webpath);
		//pagePhysicalPath : \\course\\detail\\
		cmsPage.setPagePhysicalPath(publish_page_physicalpath);
		//pageCreateTime : new Date
		cmsPage.setPageCreateTime(new Date());
		//templateId : "5d14ac1ab1a1ad24c80e6344"
		cmsPage.setTemplateId(publish_templateId);

		return cmsPage;
	}

	/**
	 * 向teachplanMediaPub中保存课程媒资信息
	 *
	 * @param courseId
	 */
	private void saveTeachplanMediaPub(String courseId) {
		//先删除teachplanMediaPub中的数据
		teachplanMediaPubRepository.deleteByCourseId(courseId);
		//从teachplanMedia中查询
		List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseId(courseId);
		List<TeachplanMediaPub> teachplanMediaPubs = new ArrayList<>();
		//将teachplanMediaList数据放到teachplanMediaPubs中
		for (TeachplanMedia teachplanMedia : teachplanMediaList) {
			TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
			BeanUtils.copyProperties(teachplanMedia, teachplanMediaPub);
			//添加时间戳
			teachplanMediaPub.setTimestamp(new Date());
			teachplanMediaPubs.add(teachplanMediaPub);
		}

		//将teachplanMediaList插入到teachplanMediaPub
		teachplanMediaPubRepository.saveAll(teachplanMediaPubs);
	}

}
