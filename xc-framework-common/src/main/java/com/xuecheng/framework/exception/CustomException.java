package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;

/**
 * 自定义异常类
 *
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName CustomException
 * @date 2019年06月15日 上午 10:15
 */
@Data
public class CustomException extends RuntimeException {

	private ResultCode resultCode;

	public CustomException(ResultCode resultCode) {
		super("错误代码：" + resultCode.code() + "错误信息：" + resultCode.message());
		this.resultCode = resultCode;
	}
}
