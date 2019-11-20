package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName CmsTemplateService
 * @date 2019年06月14日 下午 8:10
 */
@Service
public class CmsTemplateService {
	@Autowired
	private CmsTemplateRepository cmsTemplateRepository;

	/**
	 * 查询template，不用分页
	 *
	 * @return
	 */
	public QueryResponseResult<CmsTemplate> findList() {
		List<CmsTemplate> templateList = cmsTemplateRepository.findAll();

		QueryResult<CmsTemplate> queryResult = new QueryResult<CmsTemplate>();
		queryResult.setTotal(templateList.size());
		queryResult.setList(templateList);

		QueryResponseResult<CmsTemplate> queryResponseResult = new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);

		return queryResponseResult;
	}
}
