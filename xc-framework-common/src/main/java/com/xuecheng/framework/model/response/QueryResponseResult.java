package com.xuecheng.framework.model.response;

import lombok.Data;
import lombok.ToString;

/**
 * 用于返回搜索结果时list
 * 里面有一个queryResult,包含总记录数和当前返回的list
 */
@Data
@ToString
public class QueryResponseResult<T> extends ResponseResult {

	QueryResult<T> queryResult;

	/**
	 * @param resultCode
	 * @param queryResult
	 */
	public QueryResponseResult(ResultCode resultCode, QueryResult queryResult) {
		super(resultCode);
		this.queryResult = queryResult;
	}

}
