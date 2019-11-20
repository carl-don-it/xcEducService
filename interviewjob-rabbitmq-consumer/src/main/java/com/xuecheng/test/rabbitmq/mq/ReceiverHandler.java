package com.xuecheng.test.rabbitmq.mq;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	//更方便看出效果
	private static final Logger LOGGER = LoggerFactory.getLogger(ReceiverHandler.class);

	//监听email队列
	@RabbitListener(queues = {"${interview.queue.email}"})
	public void send_email(String msg, Message message, Channel channel) {
		System.out.println("receive email message is:" + msg);
		LOGGER.info(msg);

	}

	////监听sms队列
	//@RabbitListener(queues = {"${interview.queue.sms}"})
	//public void send_sms(String msg, Message message, Channel channel) {
	//    System.out.println("receive sms message is:" + msg);
	//    LOGGER.info(msg);
	//
	//}
}
