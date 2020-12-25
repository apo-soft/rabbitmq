/**
 * 
 */
package cn.aposoft.mq.activemq.hello.p2p;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * @author Jann Liu
 *
 */
public class HelloConsumer {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new HelloConsumer().TestMQConsumerQueue();
	}

	public void TestMQConsumerQueue() throws Exception {
		// 1、创建工厂连接对象，需要制定ip和端口号
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
		// 2、使用连接工厂创建一个连接对象
		Connection connection = connectionFactory.createConnection();
		// 3、开启连接
		connection.start();
		// 4、使用连接对象创建会话（session）对象
		Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		// 5、使用会话对象创建目标对象，包含queue和topic（一对一和一对多）
		Queue queue = session.createQueue("test-queue");
		// 6、使用会话对象创建生产者对象
		MessageConsumer consumer = session.createConsumer(queue);
		// 7、向consumer对象中设置一个messageListener对象，用来接收消息
		consumer.setMessageListener(new MessageListener() {
			AtomicInteger seq = new AtomicInteger(0);
			@Override
			public void onMessage(Message message) {
				try {
					if (message instanceof TextMessage) {
						TextMessage textMessage = (TextMessage) message;
						try {
							if (seq.incrementAndGet() % 100 == 0) {
								System.out.println(new Date() + ":" + textMessage.getText());
							}
						} catch (JMSException e) {
//							e.printStackTrace();
						}
					} else {
						System.out.println(seq.incrementAndGet() + message.getClass().getName());
					}
				} finally {
					try {
						message.acknowledge();
					} catch (JMSException e) {
//						e.printStackTrace();
					}
				}
			}
		});
		// 8、程序等待接收用户消息
		System.in.read();
		// 9、关闭资源
		consumer.close();
		
		session.close();
		connection.close();
	}

}
