package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName UcenterService
 * @date 2019年07月08日 下午 4:53
 */
@Service
public class UcenterService {
	@Autowired
	private XcCompanyUserRepository xcCompanyUserRepository;
	@Autowired
	private XcUserRepository xcUserRepository;
	@Autowired
	private XcMenuMapper xcMenuMapper;

	/**
	 * 根据username查找XcUserExt
	 *
	 * @param username
	 * @return
	 */
	public XcUserExt getUserExt(String username) {
		//1. 查找XcUser
		XcUser xcUser = xcUserRepository.findByUsername(username);
		if (xcUser == null) {
			return null;
		}
		//2. 转换成XcUserExt
		XcUserExt xcUserExt = new XcUserExt();
		BeanUtils.copyProperties(xcUser, xcUserExt);

		//3. 根据userid查找XcUserCompany
		String userId = xcUser.getId();

		XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(userId);
		if (xcCompanyUser != null) {
			xcUserExt.setCompanyId(xcCompanyUser.getCompanyId());
		}

		//4. 根据userid查找XcUserCompany
		List<XcMenu> xcMenuList = xcMenuMapper.findXcMenuByUserId(userId);
		xcUserExt.setPermissions(xcMenuList);

		return xcUserExt;
	}
}

