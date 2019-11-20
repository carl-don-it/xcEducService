package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName ESCourseService
 * @date 2019年07月01日 下午 2:11
 */
@Service
public class EsCourseService {
	private static final Logger LOGGER = LoggerFactory.getLogger(EsCourseService.class);

	@Value("${xuecheng.course.index}")
	private String course_index;
	@Value("${xuecheng.media.index}")
	private String media_index;
	@Value("${xuecheng.course.type}")
	private String course_type;
	@Value("${xuecheng.media.type}")
	private String media_type;
	@Value("${xuecheng.course.source_field}")
	private String course_source_field;
	@Value("${xuecheng.media.source_field}")
	private String media_source_field;

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Autowired
	private RestClient restClient;

	/**
	 * 搜索栏目搜索课程
	 * 按复杂条件并且分页展示
	 *
	 * @param page
	 * @param size
	 * @param courseSearchParam 查找参数
	 * @return
	 */
	public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {

		if (courseSearchParam == null) {
			courseSearchParam = new CourseSearchParam();
		}
		//判断参数,修正page,size,第一页是1,
		if (page <= 0) {
			page = 1;
		}
		if (size <= 0) {
			size = 5;
		}

		//1 搜索请求对象
		SearchRequest searchRequest = new SearchRequest(course_index);
		//2 指定类型
		searchRequest.types(course_type);
		//3 构建搜索源对象
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		//3.1 设置分页
		searchSourceBuilder.from((page - 1) * size);
		searchSourceBuilder.size(size);
		//3.2 搜索方式  boolQuery搜索方式
		searchSourceBuilder.query(this.getBoolQueryBuilder(courseSearchParam));
		//3.3 添加排序
		searchSourceBuilder.sort("grade", SortOrder.ASC);
		//3.4 设置原字段过滤
		searchSourceBuilder.fetchSource(course_source_field.split(","), new String[]{});
		//3.5 设置高亮
		searchSourceBuilder.highlighter(this.getHighlighter());

		//4. 向搜索请求中设置搜索源
		searchRequest.source(searchSourceBuilder);
		//5. 执行搜索,发起http请求
		SearchResponse searchResponse = null;
		try {
			searchResponse = restHighLevelClient.search(searchRequest);
		} catch (IOException e) {
			e.printStackTrace();
			ExceptionCast.cast(CommonCode.FAIL);
		}
		//6. 得到结果
		//搜索结果
		SearchHits hits = searchResponse.getHits();

		//7. 封装结果,返回
		return this.getQueryResponseResult(hits);
	}

	/**
	 * 使用ES的客户端向ES请求查询索引信息
	 * 根据id查找CoursePub对象
	 *
	 * @param id courseId
	 * @return
	 */
	public Map<String, CoursePub> getall(String id) {
		//定义一个搜索请求对象
		SearchRequest searchRequest = new SearchRequest(course_index);
		//指定type
		searchRequest.types(course_type);

		//定义SearchSourceBuilder
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		//设置使用termQuery
		searchSourceBuilder.query(QueryBuilders.termQuery("id", id));
		//过虑源字段，不用设置源字段，取出所有字段
//        searchSourceBuilder.fetchSource()
		searchRequest.source(searchSourceBuilder);
		//最终要返回的课程信息

		Map<String, CoursePub> map = new HashMap<>();
		try {
			SearchResponse search = restHighLevelClient.search(searchRequest);
			SearchHits hits = search.getHits();
			SearchHit[] searchHits = hits.getHits();
			for (SearchHit hit : searchHits) {
				CoursePub coursePub = new CoursePub();
				//获取源文档的内容
				Map<String, Object> sourceAsMap = hit.getSourceAsMap();
				//课程id
				String courseId = (String) sourceAsMap.get("id");
				String name = (String) sourceAsMap.get("name");
				String grade = (String) sourceAsMap.get("grade");
				String charge = (String) sourceAsMap.get("charge");
				String pic = (String) sourceAsMap.get("pic");
				String description = (String) sourceAsMap.get("description");
				String teachplan = (String) sourceAsMap.get("teachplan");
				coursePub.setId(courseId);
				coursePub.setName(name);
				coursePub.setPic(pic);
				coursePub.setGrade(grade);
				coursePub.setTeachplan(teachplan);
				coursePub.setDescription(description);
				map.put(courseId, coursePub);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 根据多个课程计划查询课程媒资信息
	 *
	 * @param teachplanIds
	 * @return
	 */
	public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds) {
		//定义一个搜索请求对象
		SearchRequest searchRequest = new SearchRequest(media_index);
		//指定type
		searchRequest.types(media_type);

		//定义SearchSourceBuilder
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		//设置使用termsQuery根据多个id 查询
		searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id", teachplanIds));
		//过虑源字段
		String[] includes = media_source_field.split(",");
		searchSourceBuilder.fetchSource(includes, new String[]{});
		searchRequest.source(searchSourceBuilder);
		//使用es客户端进行搜索请求Es
		List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
		long total = 0;
		try {
			//执行搜索
			SearchResponse search = restHighLevelClient.search(searchRequest);
			SearchHits hits = search.getHits();
			total = hits.totalHits;
			SearchHit[] searchHits = hits.getHits();
			for (SearchHit hit : searchHits) {
				TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
				Map<String, Object> sourceAsMap = hit.getSourceAsMap();
				//取出课程计划媒资信息
				String courseid = (String) sourceAsMap.get("courseid");
				String media_id = (String) sourceAsMap.get("media_id");
				String media_url = (String) sourceAsMap.get("media_url");
				String teachplan_id = (String) sourceAsMap.get("teachplan_id");
				String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");

				teachplanMediaPub.setCourseId(courseid);
				teachplanMediaPub.setMediaUrl(media_url);
				teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
				teachplanMediaPub.setMediaId(media_id);
				teachplanMediaPub.setTeachplanId(teachplan_id);
				teachplanMediaPubList.add(teachplanMediaPub);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		//数据集合
		QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
		queryResult.setList(teachplanMediaPubList);
		queryResult.setTotal(total);
		QueryResponseResult<TeachplanMediaPub> queryResponseResult = new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);
		return queryResponseResult;

	}

	/**
	 * 获取HighlightBuilder
	 *
	 * @return
	 */
	private HighlightBuilder getHighlighter() {
		//设置高亮
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.preTags("<font class='eslight'>");
		highlightBuilder.postTags("</font>");
		// 设置高亮字段
		highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
		return highlightBuilder;

	}

	/**
	 * 设置分页
	 *
	 * @param searchSourceBuilder
	 * @param page
	 * @param size
	 */
	private void setPageSearch(SearchSourceBuilder searchSourceBuilder, int page, int size) {

	}

	/**
	 * MultiMatchQuery+过滤器
	 *
	 * @param courseSearchParam
	 * @return
	 */
	private BoolQueryBuilder getBoolQueryBuilder(CourseSearchParam courseSearchParam) {
		//1. 定义一个boolQuery
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		//2. 加入MultiMatchQuery
		setMust(boolQueryBuilder, courseSearchParam);
		//3. 加入filter
		setFilter(boolQueryBuilder, courseSearchParam);
		return boolQueryBuilder;
	}

	/**
	 * 加入MultiMatchQuery
	 *
	 * @param boolQueryBuilder
	 * @param courseSearchParam
	 */
	private void setMust(BoolQueryBuilder boolQueryBuilder, CourseSearchParam courseSearchParam) {

		if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())) {
			//搜索field
			String[] field = {"name", "description", "teachplan"};
			MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), field).
					minimumShouldMatch("70%").field("name", 10);
			boolQueryBuilder.must(multiMatchQueryBuilder);
		}
	}

	/**
	 * 加入filter
	 *
	 * @param boolQueryBuilder
	 * @param courseSearchParam
	 */
	private void setFilter(BoolQueryBuilder boolQueryBuilder, CourseSearchParam courseSearchParam) {
		//一级分类
		if (StringUtils.isNotEmpty(courseSearchParam.getMt())) {
			boolQueryBuilder.filter(QueryBuilders.termQuery("mt", courseSearchParam.getMt()));
		}

		//二级分类
		if (StringUtils.isNotEmpty(courseSearchParam.getSt())) {
			boolQueryBuilder.filter(QueryBuilders.termQuery("st", courseSearchParam.getSt()));
		}

		//难度类型
		if (StringUtils.isNotEmpty(courseSearchParam.getGrade())) {
			boolQueryBuilder.filter(QueryBuilders.termQuery("grade", courseSearchParam.getGrade()));
		}

		//价格区间,有些价格是null,这样会被筛选出去
		if (courseSearchParam.getPrice_min() != null || courseSearchParam.getPrice_max() != null) {
			boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").
					gte(courseSearchParam.getPrice_min()).lte(courseSearchParam.getPrice_max()));
		}

		//排序字段

		//过滤字段

	}

	/**
	 * 封装hits到模型类
	 *
	 * @param hits
	 * @return
	 */
	private QueryResponseResult<CoursePub> getQueryResponseResult(SearchHits hits) {
		List<CoursePub> list = new ArrayList<>();
		for (SearchHit hit : hits) {
			CoursePub coursePub = new CoursePub();
			//源文档
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			//取出id
			String id = (String) sourceAsMap.get("id");
			coursePub.setId(id);
			//取出name
			String name = (String) sourceAsMap.get("name");
			//name换成高亮字段
			//取出高亮字段map
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			if (highlightFields != null) {
				//取出高亮字段name
				HighlightField nameHighlight = highlightFields.get("name");
				if (nameHighlight != null) {
					Text[] fragments = nameHighlight.getFragments();
					StringBuffer stringBuffer = new StringBuffer();
					for (Text text : fragments) {
						stringBuffer.append(text);
					}
					name = stringBuffer.toString();
				}
			}
			coursePub.setName(name);
			//取出grade
			String grade = (String) sourceAsMap.get("grade");
			coursePub.setGrade(grade);
			//图片
			String pic = (String) sourceAsMap.get("pic");
			coursePub.setPic(pic);
			//价格
			Double price = null;
			try {
				if (sourceAsMap.get("price") != null) {
					price = (Double) sourceAsMap.get("price");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			coursePub.setPrice(price);
			//旧价格
			Double price_old = null;
			try {
				if (sourceAsMap.get("price_old") != null) {
					price_old = (Double) sourceAsMap.get("price_old");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			coursePub.setPrice_old(price_old);
			//将coursePub对象放入list
			list.add(coursePub);
		}
		QueryResult<CoursePub> queryResult = new QueryResult<CoursePub>();
		queryResult.setTotal(hits.getTotalHits());
		queryResult.setList(list);

		return new QueryResponseResult<CoursePub>(CommonCode.SUCCESS, queryResult);
	}
}
