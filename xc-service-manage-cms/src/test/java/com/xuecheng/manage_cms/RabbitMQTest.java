package com.xuecheng.manage_cms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName RabbitMQTest
 * @date 2019年06月19日 下午 8:23
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RabbitMQTest {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	public void testRabbitTemplate() {
		rabbitTemplate.convertAndSend("ex_routing_cms_postpage", "5a751fab6abb5044e0d19ea1}", "test");
		//  rabbitTemplate.convertAndSend(RabbitMQConfig.EX_ROUTING_CMS_POSTPAGE, "queue_cms_postpage_01","test");
	}

}
