package com.xuecheng.test.freemarker.controller;

import com.xuecheng.test.freemarker.model.Student;
import freemarker.template.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.*;

/**
 * 在线渲染ftl文件,SpringMVC视图解析器会找模板,然后渲染上去
 *
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName FreemarkerController
 * @date 2019年06月15日 下午 4:34
 */
@RequestMapping("/freemarker")
@Controller
public class FreemarkerController {
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 自定义模型数据
	 *
	 * @param map
	 * @return
	 */
	@RequestMapping("/test1")
	public String freemarker(Map<String, Object> map) {
		InitialMap(map);
		//返回模板文件名称
		return "test1";
	}

	/**
	 * 远程请求模型数据,轮播图的
	 *
	 * @param map
	 * @return
	 */
	@RequestMapping("/banner")
	public String index_banner(Map<String, Object> map) {
		String dataUrl = "http://localhost:31001/cms/config/find/5a791725dd573c3574ee333f";
		ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
		Map body = forEntity.getBody();
		map.putAll(body);
		return "index_banner";
	}

	@RequestMapping("/course")
	public String course(Map<String, Object> map) {
		String dataUrl = "http://localhost:31200/course/courseview/4028e581617f945f01617f9dabc40000";
		ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
		Map body = forEntity.getBody();
		map.putAll(body);
		return "course";
	}

	/**
	 * 自己定义的模型数据
	 *
	 * @param map
	 * @return
	 */
	private void InitialMap(Map<String, Object> map) {
		map.put("name", "黑马程序员");
		Student stu1 = new Student();
		stu1.setName("小明");
		stu1.setAge(18);
		stu1.setMondy(1000.86f);
		stu1.setBirthday(new Date());
		Student stu2 = new Student();
		stu2.setName("小红");
		stu2.setMondy(200.1f);
		stu2.setAge(19);
		//stu2.setBirthday(new Date());
		List<Student> friends = new ArrayList<>();
		friends.add(stu1);
		stu2.setFriends(friends);
		stu2.setBestFriend(stu1);
		List<Student> stus = new ArrayList<>();
		stus.add(stu1);
		stus.add(stu2);
		//向数据模型放数据
		map.put("stus", stus);
		//准备map数据
		HashMap<String, Student> stuMap = new HashMap<>();
		stuMap.put("stu1", stu1);
		stuMap.put("stu2", stu2);
		//向数据模型放数据
		map.put("stu1", stu1);
		//向数据模型放数据
		map.put("stuMap", stuMap);
	}

	//测试类路径
	public static void main(String[] args) throws Exception {
		//创建配置类
		Configuration configuration = new Configuration(Configuration.getVersion());
		//设置模板路径
		String classpath = FreemarkerController.class.getResource("/").getPath();
		configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));

	}
}