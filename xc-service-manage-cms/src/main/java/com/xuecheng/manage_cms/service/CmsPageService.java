package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitMQConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName PageService
 * @date 2019年06月09日 下午 11:11
 */
@Service
public class CmsPageService {

	@Autowired
	private CmsPageRepository cmsPageRepository;

	@Autowired
	private CmsConfigRepository cmsConfigRepository;

	@Autowired
	private CmsTemplateRepository cmsTemplateRepository;

	@Autowired
	private CmsSiteRepository cmsSiteRepository;

	//远程请求
	@Autowired
	RestTemplate restTemplate;

	//发送消息队列
	@Autowired
	private RabbitTemplate rabbitTemplate;

	//存入文件到mongodb
	@Autowired
	GridFsTemplate gridFsTemplate;

	//从mongodb取出文件
	@Autowired
	GridFSBucket gridFSBucket;

	/**
	 * 页面列表分页查询
	 *
	 * @param page             当前页码
	 * @param size             页面显示个数
	 * @param queryPageRequest 查询条件
	 * @return 页面列表
	 */
	public QueryResponseResult<CmsPage> findList(int page, int size, QueryPageRequest queryPageRequest) {
		//初始化查询页面和查询大小
		if (page <= 0) {
			page = 1;
		}
		page = page - 1;//为了适应mongodb的接口将页码减1
		if (size <= 0) {
			size = 10;
		}

		//分页对象; Pageable pageable = new PageRequest(page, size); 废弃了？
		Pageable pageable = PageRequest.of(page, size);
		Page<CmsPage> all;

		//根据是否有查询条件用不同的查询方法
		if (queryPageRequest == null) {
			all = cmsPageRepository.findAll(pageable);
		} else {
			CmsPage cmsPage = new CmsPage();
			//pageAliase
			if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
				cmsPage.setPageAliase(queryPageRequest.getPageAliase());
			}
			//pageId
			if (StringUtils.isNotEmpty(queryPageRequest.getPageId())) {
				cmsPage.setPageId(queryPageRequest.getPageId());
			}
			//pageName
			if (StringUtils.isNotEmpty(queryPageRequest.getPageName())) {
				cmsPage.setPageName(queryPageRequest.getPageName());
			}
			//siteId
			if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
				cmsPage.setSiteId(queryPageRequest.getSiteId());
			}
			//templateId
			if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())) {
				cmsPage.setTemplateId(queryPageRequest.getTemplateId());
			}
			//pageType
			if (StringUtils.isNotEmpty(queryPageRequest.getPageType())) {
				cmsPage.setPageType(queryPageRequest.getPageType());
			}

			//模糊查询：pageAliase，pageName
			ExampleMatcher matcher = ExampleMatcher.matching()
					.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains())
					.withMatcher("pageName", ExampleMatcher.GenericPropertyMatchers.contains());

			//创建查询实例
			Example<CmsPage> example = Example.of(cmsPage, matcher);
			all = cmsPageRepository.findAll(example, pageable);
		}

		//分页查询

		QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<CmsPage>();
		cmsPageQueryResult.setList(all.getContent());
		cmsPageQueryResult.setTotal(all.getTotalElements());
		//返回结果
		return new QueryResponseResult<CmsPage>(CommonCode.SUCCESS, cmsPageQueryResult);
	}

	/**
	 * 新增，返回CmsPageResult
	 * 存在,则不添加,不存在,则添加
	 *
	 * @param cmsPage
	 * @return
	 */
	public CmsPageResult add(CmsPage cmsPage) {
		//1.先查询，然后判断是否存在，存在返回一个对象，添加失败
		CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath
				(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
		if (cmsPage1 != null) {
			//return new CmsPageResult(CommonCode.FAIL, null);
			//存在则抛出异常
			ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
		}

		//2. 返回null，不存在重复对象，可以添加, 返回的对象会把Id加上
		CmsPage cmsPage2 = cmsPageRepository.save(cmsPage);
		return new CmsPageResult(CommonCode.SUCCESS, cmsPage2);

	}

	/**
	 * 根据id查询页面
	 *
	 * @param id
	 * @return
	 */
	public CmsPage findById(String id) {
		Optional<CmsPage> optional = cmsPageRepository.findById(id);
		return optional.orElse(null);
	}

	/**
	 * 先根据id查询页面，然后修改
	 * 单独给一个id是因为cmspage的id可能与前面不一样
	 *
	 * @param id
	 * @param cmsPage
	 * @return
	 */
	public CmsPageResult update(String id, CmsPage cmsPage) {
		//先查询
		CmsPage one = this.findById(id);

		//判断是否为null，null则抛出异常
		if (one == null) {
			//页面不存在，则抛出异常
			ExceptionCast.cast(CmsCode.CMS_FINDPAGE_PAGENOTEXIST);
		}
		//更新模板id
		one.setTemplateId(cmsPage.getTemplateId());
		//更新所属站点
		one.setSiteId(cmsPage.getSiteId());
		//更新页面别名
		one.setPageAliase(cmsPage.getPageAliase());
		//更新页面名称
		one.setPageName(cmsPage.getPageName());
		//更新访问路径
		one.setPageWebPath(cmsPage.getPageWebPath());
		//更新dataUrl
		one.setDataUrl(cmsPage.getDataUrl());
		//更新物理路径
		one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
		//执行更新
		CmsPage save = cmsPageRepository.save(one);

		//返回成功
		CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
		return cmsPageResult;

		//非null，则返回修改后页面信息

	}

	/**
	 * 根据id删除页面
	 *
	 * @param id
	 * @return
	 */
	public ResponseResult delete(String id) {
		CmsPage cmsPage = this.findById(id);
		if (cmsPage == null) {
			//页面不存在，则抛出异常
			ExceptionCast.cast(CmsCode.CMS_FINDPAGE_PAGENOTEXIST);
		}
		cmsPageRepository.deleteById(id);
		return new ResponseResult(CommonCode.SUCCESS);
	}

	//页面静态化方法

	/**
	 * 远离,网站是由一个个的页面组成的,提高速度就要生成大量的静态化页面,减少数据库存取,
	 * 因此把所有的页面的有关信息都在cms系统中管理,里面包括了template,dataURL,可以很方便地进行静态化,
	 * 因此其他子系统管理的有关页面信息的东西都要在这里保存,供cms主系统管理
	 * <p>
	 * 1. 静态化程序获取页面的DataUrl,静态化程序远程请求DataUrl获取数据模型。
	 * 2. 静态化程序获取页面的模板信息
	 * 3. 执行页面静态化
	 */
	public String getPageHtmlContent(String pageId) {

		//获取数据模型
		Map model = this.getModelByPageId(pageId);
		if (model == null) {
			//数据模型获取不到
			ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
		}

		//获取页面的模板信息
		String template = this.getTemplateByPageId(pageId);
		if (StringUtils.isEmpty(template)) {
			ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
		}

		//执行静态化
		String html = this.generateHtmlContent(template, model);
		return html;

	}

	/**
	 * @param templateContent String
	 * @param model           String
	 * @return HtmlContent of String
	 */
	private String generateHtmlContent(String templateContent, Map model) {
		//创建配置对象
		Configuration configuration = new Configuration(Configuration.getVersion());
		//创建模板加载器
		StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
		stringTemplateLoader.putTemplate("template", templateContent);
		//向configuration配置模板加载器
		configuration.setTemplateLoader(stringTemplateLoader);
		//获取模板
		try {
			Template template = configuration.getTemplate("template");
			//调用api进行静态化
			String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	//获取页面的模板信息
	private String getTemplateByPageId(String pageId) {
		//取出页面的信息
		CmsPage cmsPage = this.findById(pageId);
		if (cmsPage == null) {
			//页面不存在
			ExceptionCast.cast(CmsCode.CMS_FINDPAGE_PAGENOTEXIST);
		}
		//获取页面的模板id
		String templateId = cmsPage.getTemplateId();
		if (StringUtils.isEmpty(templateId)) {
			ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
		}
		//查询模板信息
		Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
		if (optional.isPresent()) {
			CmsTemplate cmsTemplate = optional.get();
			//获取模板文件id
			String templateFileId = cmsTemplate.getTemplateFileId();
			//从GridFS中取模板文件内容

			//根据文件id查询文件
			GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
			//打开一个下载流对象
			GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
			//创建GridFsResource对象，获取流
			GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
			//从流中取数据
			try {
				String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
				return content;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;

	}

	//获取数据模型
	private Map getModelByPageId(String pageId) {
		//取出页面的信息
		CmsPage cmsPage = this.findById(pageId);
		if (cmsPage == null) {
			//页面不存在
			ExceptionCast.cast(CmsCode.CMS_FINDPAGE_PAGENOTEXIST);
		}
		//取出页面的dataUrl
		String dataUrl = cmsPage.getDataUrl();
		if (StringUtils.isEmpty(dataUrl)) {
			//页面dataUrl为空
			ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
		}
		//通过restTemplate请求dataUrl获取数据
		ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
		Map body = forEntity.getBody();
		return body;
	}

	/**
	 * 根据页面id发布页面到GridFS
	 *
	 * @param id 页面id
	 * @return
	 */
	public ResponseResult postPage(String id) {
		//1.获取html内容
		String pageHtmlContent = this.getPageHtmlContent(id);
		if (StringUtils.isEmpty(pageHtmlContent)) {
			ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
		}

		//2.保存html文件
		CmsPage cmsPage = this.sveHtml(id, pageHtmlContent);

		//3.向消息队列发送信息
		this.sendPostPage(id);

		//4.返回结果
		return new ResponseResult(CommonCode.SUCCESS);
	}

	/**
	 * 向消息队列发送信息，页面已经静态化并且存储成功
	 *
	 * @param id pageId
	 */
	private void sendPostPage(String id) {
		//为了查找siteid，需要先查找siteId
		CmsPage cmsPage = this.findById(id);
		if (cmsPage == null) {
			ExceptionCast.cast(CmsCode.CMS_FINDPAGE_PAGENOTEXIST);
		}
		String siteId = cmsPage.getSiteId();

		//制作json信息
		HashMap<String, String> map = new HashMap<>();
		map.put("pageId", id);
		String message = JSON.toJSONString(map);

		//以交换机，routingkey（siteId）作为条件发送信息
		rabbitTemplate.convertAndSend(RabbitMQConfig.EX_ROUTING_CMS_POSTPAGE, siteId, message);
	}

	/**
	 * 保存html文件到GridFS
	 *
	 * @param id
	 * @param pageHtmlContent
	 * @return
	 */
	private CmsPage sveHtml(String id, String pageHtmlContent) {
		//1.获取cmsPage
		CmsPage cmsPage = this.findById(id);

		//2.存储前先删除
		String htmlFileId = cmsPage.getHtmlFileId();
		if (StringUtils.isNotEmpty(htmlFileId)) {
			gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
		}

		//3.存储html文件到GridFs
		InputStream inputStream = null;
		ObjectId objectId = null;
		try {
			inputStream = IOUtils.toInputStream(pageHtmlContent, "utf-8");
			objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
		} catch (IOException e) {
			ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_SAVEHTMLERROR);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//4.更新htmlFileId
		cmsPage.setHtmlFileId(objectId.toString());
		cmsPageRepository.save(cmsPage);

		return cmsPage;
	}

	/**
	 * 查找是否存在,存在则更新,否则新增
	 *
	 * @param cmsPage
	 * @return
	 */
	public CmsPageResult save(CmsPage cmsPage) {
		//1.先查询，然后判断是否存在，存在返回一个对象，修改
		CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath
				(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
		if (cmsPage1 != null) {
			//return new CmsPageResult(CommonCode.FAIL, null);
			return this.update(cmsPage1.getPageId(), cmsPage);
		} else {
			//2. 返回null，不存在重复对象，可以添加, 返回的对象会把Id加上
			return this.add(cmsPage);
		}

	}

	/**
	 * 添加页面,并且把添加的页面发布出去
	 *
	 * @param cmsPage
	 * @return
	 */
	public CmsPostPageResult quickPost(CmsPage cmsPage) {
		//判断非法参数
		if (cmsPage == null) {
			ExceptionCast.cast(CommonCode.INVALID_PARAM);
		}
		//1. 添加页面
		CmsPageResult cmsPageResult = this.save(cmsPage);

		//2. 发布页面
		//拿到cmsPage,pageId
		CmsPage cmsPageSave = cmsPageResult.getCmsPage();
		String pageId = cmsPageSave.getPageId();
		//调用postPage方法
		ResponseResult responseResult = this.postPage(pageId);
		if (!responseResult.isSuccess()) {
			ExceptionCast.cast(CommonCode.FAIL);
		}

		//3. 拼接url,需要拿到cmsSite里面hostname,siteWebPath,和cmsPage中的pageWebPath,pageName
		CmsSite cmsSite = this.findCmsSiteById(cmsPage.getSiteId());
		//返回页面的url
		String publishUrl = cmsSite.getSiteDomain() + cmsSite.getSiteWebPath() + cmsPage.getPageWebPath() + cmsPage.getPageName();
		return new CmsPostPageResult(CommonCode.SUCCESS, publishUrl);
	}

	/**
	 * 根据siteId查找cmsSite
	 *
	 * @param siteId
	 * @return
	 */
	private CmsSite findCmsSiteById(String siteId) {
		Optional<CmsSite> cmsSiteOptional = cmsSiteRepository.findById(siteId);
		if (!cmsSiteOptional.isPresent()) {
			ExceptionCast.cast(CommonCode.INVALID_PARAM);
		}
		return cmsSiteOptional.get();
	}
}
