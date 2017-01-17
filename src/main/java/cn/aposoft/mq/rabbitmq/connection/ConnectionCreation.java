/**
 * 
 */
package cn.aposoft.mq.rabbitmq.connection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * @author LiuJian
 *
 */
public class ConnectionCreation {

    /**
     * @param args
     * @throws TimeoutException
     * @throws IOException
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws InterruptedException
     * @throws ConsumerCancelledException
     * @throws ShutdownSignalException
     */
    public static void main(String[] args) throws IOException, TimeoutException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException,
            ShutdownSignalException, ConsumerCancelledException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        // amqp://guest:guest@192.168.63.133:5672 可以访问默认 "/"vhost
        // amqp://guest:guest@192.168.63.133:5672 访问的是""vhost,会报异常
        // amqp://guest:guest@192.168.63.133:5672// 访问的是Multiple segments in
        // path of AMQP URI: //,会报异常
        // 默认 vhost "/"
        factory.setUri("amqp://guest:guest@192.168.63.133:5672");
        // 默认 vhost "/" 用户密码: guest:guest
        factory.setUri("amqp://192.168.63.133:5672");
        // 默认 vhost "/" 用户密码: guest:guest 端口 5672
        factory.setUri("amqp://192.168.63.133/panacea");
        try (Connection conn = factory.newConnection();) {
            System.out.println("Connected.");
            Channel channel = conn.createChannel();
            final String exchangeName = "simpleDirect";
            final String routingKey = "routingKey";
            channel.exchangeDeclare(exchangeName, "direct", false);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchangeName, routingKey);
            QueueingConsumer consumer = new QueueingConsumer(channel);
            // non-blocking operation
            String consumerTag = channel.basicConsume(queueName, consumer);
            int i = 0;
            try {
                while (i++ < 1000*1000*1000) {

                    Delivery delivery = consumer.nextDelivery();

                    byte[] bytes = delivery.getBody();
                    String msg = new String(bytes, "UTF-8");
                    System.out.println(delivery.getEnvelope().getDeliveryTag());
                    System.out.println(msg);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
                }

            } finally {
                channel.close();
            }
            System.out.println("channel is closed.");
        }
    }

}
