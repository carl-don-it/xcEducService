package com.xuecheng.test.rabbitmq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
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

	//queueBean的ID
	public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
	public static final String QUEUE_INFORM_SMS = "queue_inform_sms";

	//queue的name
	@Value("${interview.queue.email}")
	public String queue_inform_email_name;
	@Value("${interview.queue.sms}")
	public String queue_inform_sms_name;

	//exchange Bean的ID和name相同
	public static final String EXCHANGE_TOPICS_INFORM = "exchange_interview";

	//routingkey
	@Value("${interview.routingkey.email}")
	public String ROUTINGKEY_EMAIL;
	@Value("${interview.routingkey.sms}")
	public String ROUTINGKEY_SMS;

	////声明交换机
	//@Bean(EXCHANGE_TOPICS_INFORM)
	//public Exchange EXCHANGE_TOPICS_INFORM() {
	//    //durable(true) 持久化，mq重启之后交换机还在
	//    return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
	//}

	//声明QUEUE_INFORM_EMAIL队列
	@Bean(QUEUE_INFORM_EMAIL)
	public Queue QUEUE_INFORM_EMAIL() {
		return new Queue(queue_inform_email_name);
	}

	////声明QUEUE_INFORM_SMS队列
	//@Bean(QUEUE_INFORM_SMS)
	//public Queue QUEUE_INFORM_SMS() {
	//    return new Queue(queue_inform_sms_name);
	//}

	//ROUTINGKEY_EMAIL队列绑定交换机，指定routingKey
	//@Bean
	//public Binding BINDING_QUEUE_INFORM_EMAIL(@Qualifier(QUEUE_INFORM_EMAIL) Queue queue,
	//                                          @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange) {
	//    return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_EMAIL).noargs();
	//}

	////ROUTINGKEY_SMS队列绑定交换机，指定routingKey
	//@Bean
	//public Binding BINDING_ROUTINGKEY_SMS(@Qualifier(QUEUE_INFORM_SMS) Queue queue,
	//                                      @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange) {
	//    return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_SMS).noargs();
	//}
}
