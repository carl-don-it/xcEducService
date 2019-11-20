package com.xuecheng.manage_cms.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName RabbitMQConfig
 * @date 2019年06月19日 下午 4:17
 */
@Configuration
public class RabbitMQConfig {
	//交换机的名称
	public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";

	//声明交换机,
	@Bean(EX_ROUTING_CMS_POSTPAGE)
	public Exchange EX_ROUTING_CMS_POSTPAGE() {
		return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
	}
}