package com.xuecheng.test.rabbitmq.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName RabbitmqConfig
 * @date 2019年06月17日 下午 5:19
 */
@Configuration
public class RabbitmqConfig {
	public static final String EXCHANGE_TOPICS_INFORM = "exchange_interview";

	//声明交换机
	@Bean(EXCHANGE_TOPICS_INFORM)
	public Exchange EXCHANGE_TOPICS_INFORM() {
		//durable(true) 持久化，mq重启之后交换机还在
		return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
	}

}
