package com.xuecheng.manage_cms_client.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName RabbitConfig
 * @date 2019年06月19日 上午 9:02
 */
@Configuration
public class RabbitConfig {
	/*
	 *常量：交换机名字、队列名字、路由key
	 */
	//队列bean的名称
	public static final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";
	//交换机的名称
	public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";
	//队列的名称
	@Value("${xuecheng.mq.queue}")
	public String queue_cms_postpage_name;
	//routingKey 即门户站点Id
	@Value("${xuecheng.mq.portKey}")
	public String portKey;
	//routingKey 即课程详情站点Id
	@Value("${xuecheng.mq.courseKey}")
	public String courseKey;

	/**
	 * declare Exchange
	 *
	 * @return Exchange
	 */
	@Bean(EX_ROUTING_CMS_POSTPAGE)
	public Exchange EX_ROUTING_CMS_POSTPAGE() {
		return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
	}

	/**
	 * declare Queue
	 *
	 * @return Queue
	 */
	@Bean(QUEUE_CMS_POSTPAGE)
	public Queue QUEUE_CMS_POSTPAGE() {
		return new Queue(queue_cms_postpage_name);
	}

	/**
	 * Binding queue to exchange with routingKey
	 *
	 * @param queue
	 * @param exchange
	 * @return
	 */
	@Bean
	public Binding BINDING_QUEUE_INFORM_CMS(
			@Qualifier(QUEUE_CMS_POSTPAGE) Queue queue,
			@Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(portKey).noargs();
	}

	@Bean
	public Binding BINDING_QUEUE_INFORM_CMS2(
			@Qualifier(QUEUE_CMS_POSTPAGE) Queue queue,
			@Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(courseKey).noargs();
	}

}
