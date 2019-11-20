package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName SiteService
 * @date 2019年06月14日 上午 11:43
 */
@Service
public class CmsSiteService {
	@Autowired
	private CmsSiteRepository cmsSiteRepositoy;

	/**
	 * 查询cms_Site，不用分页
	 *
	 * @return
	 */
	public QueryResponseResult<CmsSite> findList() {
		List<CmsSite> cmsSiteList = cmsSiteRepositoy.findAll();

		QueryResult<CmsSite> QueryResult = new QueryResult<>();
		QueryResult.setList(cmsSiteList);
		QueryResult.setTotal(cmsSiteList.size());

		QueryResponseResult<CmsSite> queryResponseResult = new QueryResponseResult<CmsSite>(CommonCode.SUCCESS, QueryResult);

		return queryResponseResult;
	}
}
