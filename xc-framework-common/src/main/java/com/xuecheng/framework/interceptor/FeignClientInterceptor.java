package com.xuecheng.framework.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * focus 还没用过
 *
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName FeignClientInterceptor
 * @date 2019年07月10日 上午 9:50
 */
public class FeignClientInterceptor implements RequestInterceptor {
	@Override
	public void apply(RequestTemplate template) {
		try {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = requestAttributes.getRequest();
			Enumeration<String> headerNames = request.getHeaderNames();
			if (headerNames != null) {
				while (headerNames.hasMoreElements()) {
					String name = headerNames.nextElement();
					String values = request.getHeader(name);
					if (name.equals("authorization")) {
						//System.out.println("name="+name+"values="+values);
						template.header(name, values);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
