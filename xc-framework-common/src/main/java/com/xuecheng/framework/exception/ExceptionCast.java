package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName ExceptionCast
 * @date 2019年06月15日 上午 10:43
 */
public class ExceptionCast {
	//使用此静态方法抛出自定义异常
	public static void cast(ResultCode resultCode) {
		throw new CustomException(resultCode);
	}
}
