package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Walker_Don
 * @version V1.0
 * @Description TODO
 * @ClassName Consumer
 * @date 2019年06月17日 下午 5:39
 */
public class Consumer {
	private static final String QUEUE = "helloworld";

	public static void main(String[] args) throws IOException, TimeoutException {
		//连接
		Connection connection = null;
		//通道
		Channel channel = null;

		//1.建立TCP连接
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("127.0.0.1");
		connectionFactory.setPort(5672);
		//connectionFactory.setUsername("admin");
		//connectionFactory.setPassword("admin");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("123");

		//1.1指定mq的一个虚拟机
		connectionFactory.setVirtualHost("/");
		connection = connectionFactory.newConnection();

		//2.建立通道
		channel = connection.createChannel();
		//3.声名监听队列,生产者已经声明则不需要再次声明
		channel.queueDeclare(QUEUE, true, false, false, null);
		//4. 获取数据的回调方法
		DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
			/**
			 *
			 * @param consumerTag 消费者的标签，在channel.basicConsume()去指定
			 * @param envelope 消息包的内容，可从中获取消息id，消息routingkey，交换机，消息和重传标志(收到消息失败后是否需要重新发送)
			 * @param properties
			 * @param body
			 * @throws IOException
			 */
			@Override
			public void handleDelivery(String consumerTag,
			                           Envelope envelope,
			                           AMQP.BasicProperties properties,
			                           byte[] body)
					throws IOException {
				//交换机
				String exchange = envelope.getExchange();
				//路由key
				String routingKey = envelope.getRoutingKey();
				//消息id
				long deliveryTag = envelope.getDeliveryTag();
				//消息内容
				String msg = new String(body, "utf-8");
				System.out.println("receive message.." + msg);
			}
		};
		//5.监听队列
		/**
		 * 监听队列String queue, boolean autoAck,Consumer callback
		 * 参数明细
		 * 1、队列名称
		 * 2、是否自动回复，设置为true为表示消息接收到自动向mq回复接收到了，mq接收到回复会删除消息，设置为false则需要手动回复
		 * 3、消费消息的方法，消费者接收到消息后调用此方法
		 */
		channel.basicConsume(QUEUE, true, defaultConsumer);
	}

}
