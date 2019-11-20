package com.xuecheng.framework.domain.search;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 课程搜索的复杂参数对象,不包含如何分页的参数,配合分页使用
 *
 * @date 2019-07-01 14:00:44
 */
@Data
@ToString
public class CourseSearchParam implements Serializable {
	//搜索框里的关键字
	String keyword;
	//一级分类
	String mt;
	//二级分类
	String st;

	//难度等级
	String grade;

	//价格区间
	Float price_min;
	Float price_max;

	//排序字段
	String sort;
	//过虑字段
	String filter;

}
