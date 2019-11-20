package com.xuecheng.test.rabbitmq.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * todo
 *
 * @author Walker_Don
 * @version V1.0
 * @ClassName ProducerController
 * @date 2019年09月02日 下午 9:08
 */
@RestController
public class ProducerController {
	@Autowired
	RabbitTemplate rabbitTemplate;

	private String routingKey = "interview";

	@GetMapping("/produce/{message}")
	public String produce(@PathVariable("message") String message) {
		String sendMessage = "生产者发来的信息" + message;
		rabbitTemplate.convertAndSend("exchange_interview", routingKey, sendMessage);

		return "发送成功";
	}

}
