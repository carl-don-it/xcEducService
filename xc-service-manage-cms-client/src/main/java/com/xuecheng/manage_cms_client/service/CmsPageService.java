package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName PageService
 * @date 2019年06月19日 上午 11:26
 */
@Service
public class CmsPageService {
	//store file to GridFS
	@Autowired
	private GridFsTemplate gridFsTemplate;

	//get file from GridFs
	@Autowired
	private GridFSBucket gridFSBucket;

	@Autowired
	private CmsPageRepository cmsPageRepository;

	@Autowired
	private CmsSiteRepository cmsSiteRepository;

	/**
	 * 根据页面id下载html文件到特定地址
	 *
	 * @param pageId
	 */
	public void saveHtmlToServer(String pageId) {
		//1.把CmsPage、cmsSite查询出来,如果为null,则抛出异常

		CmsPage cmsPage = this.findPageById(pageId);
		if (cmsPage == null) {
			ExceptionCast.cast(CmsCode.CMS_FINDPAGE_PAGENOTEXIST);
		}

		CmsSite cmsSite = this.getSiteByPageId(cmsPage.getSiteId());
		if (cmsSite == null) {
			ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_SAVEHTMLERROR);
		}

		//2.获取site、page、html三者的物理路径，拼装
		String htmlPath = cmsSite.getSitePhysicalPath() + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();

		//3.获取htmlFileId，从GridfS中查询出来文件的输入流
		InputStream inputStream = this.getFileStreamByFileId(cmsPage.getHtmlFileId());

		//4.保存文件
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(new File(htmlPath));
			IOUtils.copy(inputStream, fileOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * htmlFileId，从GridfS中查询出来文件的输入流
	 *
	 * @param htmlFileId
	 * @return
	 */
	private InputStream getFileStreamByFileId(String htmlFileId) {
		InputStream inputStream = null;
		try {
			//根据id查询文件
			GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
			//打开下载流对象
			GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
			//创建gridFsResource
			GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
			//获取流对象
			inputStream = gridFsResource.getInputStream();
		} catch (IOException e) {
			//拿不到流对象或者上述任何一步有问题，都抛出异常
			ExceptionCast.cast(CmsCode.CMS_GETHTML_HTMLNOTEXIST);
		}
		return inputStream;
	}

	/**
	 * find Site By SiteId
	 *
	 * @param SiteId
	 */
	private CmsSite getSiteByPageId(String SiteId) {
		Optional<CmsSite> siteOptional = cmsSiteRepository.findById(SiteId);
		return siteOptional.orElse(null);

	}

	/**
	 * find one Page By pageId
	 *
	 * @param pageId
	 * @return
	 */
	public CmsPage findPageById(String pageId) {
		Optional<CmsPage> pageOptional = cmsPageRepository.findById(pageId);
		return pageOptional.orElse(null);
	}
}
