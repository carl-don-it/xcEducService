package com.xuecheng.auth.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName UcenterClient
 * @date 2019年07月08日 下午 5:08
 */
@FeignClient(value = XcServiceList.XC_SERVICE_UCENTER)
public interface UcenterClient {

	@GetMapping("/ucenter/getUserExt")
	public XcUserExt getUserExt(@RequestParam("username") String username);
}
