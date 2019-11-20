package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常捕获类
 *
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName ExceptionCatch
 * @date 2019年06月15日 上午 10:44
 */
@ControllerAdvice//控制器增强
public class ExceptionCatch {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

	//使用EXCEPTIONS存放异常类型和错误代码的映射，ImmutableMap的特点的一旦创建不可改变，并且线程安全
	private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;
	//使用builder来构建一个异常类型和错误代码的异常
	protected static ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> builder =
			ImmutableMap.builder();

	//捕获 CustomException异常,并且返回值必须是json
	@ExceptionHandler(CustomException.class)
	@ResponseBody
	// 内部服务器错误都会返回正确数据，所以没有http状态码
	// @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseResult customException(CustomException e) {
		LOGGER.error("catch exception : {}\r\nexception: ", e.getMessage(), e);
		ResultCode resultCode = e.getResultCode();
		ResponseResult responseResult = new ResponseResult(resultCode);
		return responseResult;
	}

	//捕获Exception异常
	@ResponseBody
	@ExceptionHandler(Exception.class)
	//  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseResult exception(Exception e) {
		LOGGER.error("catch exception : {}\r\nexception: ", e.getMessage(), e);

		final ResultCode resultCode = EXCEPTIONS.get(e.getClass());
		final ResponseResult responseResult;
		if (resultCode != null) {
			responseResult = new ResponseResult(resultCode);
		} else {
			responseResult = new ResponseResult(CommonCode.SERVER_ERROR);
		}
		return responseResult;
	}

	static {
		//在这里加入一些基础的异常类型判断
		builder.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM);
	}

	public ExceptionCatch() {
		if (EXCEPTIONS == null)
			EXCEPTIONS = builder.build();
	}
}
