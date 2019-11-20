package com.xuecheng.test.rabbitmq.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.test.rabbitmq.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName ReceiverHandler
 * @date 2019年06月18日 下午 1:22
 */
@Component
public class ReceiverHandler {

	@RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL})
	public void send_email(String msg, Message message, Channel channel) {
		System.out.println("receive message is:" + msg);

	}

	@RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_SMS})
	public void send_sms(String msg, Message message, Channel channel) {
		System.out.println("receive message is:" + msg);

	}
}
