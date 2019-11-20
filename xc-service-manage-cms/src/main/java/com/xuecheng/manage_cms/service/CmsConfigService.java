package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName CmsConfigService
 * @date 2019年06月16日 下午 1:54
 */
@Service
public class CmsConfigService {
	@Autowired
	private CmsConfigRepository cmsConfigRepository;

	public CmsConfig findById(String id) {
		Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
		return optional.orElse(null);
	}
}
