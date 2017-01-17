/**
 * 
 */
package cn.aposoft.mq.rabbitmq.connection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * @author LiuJian
 *
 */
public class ConnectionPublisher {

    /**
     * @param args
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static void main(String[] args) throws IOException, TimeoutException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException,
            ShutdownSignalException, ConsumerCancelledException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();

        // 默认 vhost "/" 用户密码: guest:guest 端口 5672
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("panacea");
        factory.setHost("192.168.63.133");
        factory.setPort(5672);
        try (Connection conn = factory.newConnection();) {
            System.out.println("Connected.");
            Channel channel = conn.createChannel();
            final String exchangeName = "simpleDirect";
            final String routingKey = "routingKey";
            channel.exchangeDeclare(exchangeName, "direct", false);
            // String queueName = channel.queueDeclare().getQueue();
            // channel.queueBind(queueName, exchangeName, routingKey);

            // non-blocking operation
            try {
                byte[] messageBodyBytes = " Hello, world!".getBytes();
                for (int i = 0; i < 1000 * 1000*1000; i++)
                    channel.basicPublish(exchangeName, routingKey,
                            (new AMQP.BasicProperties.Builder()//
                                    .contentType("text/plain")//
                                    .deliveryMode(2)//
                                    .priority(1)//
                                    .userId("guest")// 与登录用户保持一致
                                    .build()),
                            messageBodyBytes);
            } finally {
                channel.close();
            }
            System.out.println("channel is closed.");
        }
    }

}
