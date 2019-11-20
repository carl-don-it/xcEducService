package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

//不需要@repository
public interface CmsPageRepository extends MongoRepository<CmsPage, String> {

	//根据页面名称，站点id，页面webPath查询页面方法
	CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName, String siteId, String pageWebPath);
}
