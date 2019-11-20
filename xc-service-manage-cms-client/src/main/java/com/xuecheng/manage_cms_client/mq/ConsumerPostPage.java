package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.xuecheng.manage_cms_client.service.CmsPageService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName ReceiverHandler
 * @date 2019年06月19日 上午 11:03
 */
@Component
public class ConsumerPostPage {
	@Autowired
	private CmsPageService cmsPageService;

	/**
	 * 监听队列，并且获取页面id，下载文件，保存到服务器
	 *
	 * @param msg
	 * @param message
	 * @param channel
	 */
	@RabbitListener(queues = {"${xuecheng.mq.queue}"})
	public void save_html(String msg, Message message, Channel channel) {
		//解析json数据，获取页面id
		Map map = JSON.parseObject(msg, Map.class);
		String pageId = (String) map.get("pageId");

		//调用pageService保存到服务器
		cmsPageService.saveHtmlToServer(pageId);

	}

}