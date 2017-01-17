package cn.aposoft.mq.rabbitmq.sync;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MerchantBaseInfoReceiver {
    private static final String EXCHANGE_NAME = "sync_cf_merchant_base_info";

    public static String queueName;

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.63.132");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        queueName = EXCHANGE_NAME + (System.currentTimeMillis() % 4);
        channel.queueDeclare(queueName, true, false, false, null);
        // String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [" + queueName + "] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [" + queueName + "] Received '" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
