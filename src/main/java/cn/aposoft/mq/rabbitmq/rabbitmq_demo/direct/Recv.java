package cn.aposoft.mq.rabbitmq.rabbitmq_demo.direct;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Recv {

	private final static String QUEUE_NAME = "hello";

	public static void main(String[] argv) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, true, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println("Thread:" + Thread.currentThread().getId() + Thread.currentThread().getName());
				System.out.println(" [x] Received '" + message + "'");
			}
		};
		String consumerTag = channel.basicConsume(QUEUE_NAME, true, consumer);
		System.out.println("Thread:" + Thread.currentThread().getId() + "|" + Thread.currentThread().getName() + "|"
				+ consumerTag);

		try {
			Thread.sleep(1000 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			channel.close();
			connection.close();
		}
	}
}
