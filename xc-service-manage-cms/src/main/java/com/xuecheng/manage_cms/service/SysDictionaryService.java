package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName SysDicthinaryService
 * @date 2019年06月21日 下午 7:21
 */
@Service
public class SysDictionaryService {
	@Autowired
	private SysDictionaryRepository sysDictionaryRepository;

	public SysDictionary getByType(String dType) {
		if (StringUtils.isEmpty(dType)) {
			ExceptionCast.cast(CommonCode.INVALID_PARAM);
		}
		return sysDictionaryRepository.findByDType(dType);

	}
}
