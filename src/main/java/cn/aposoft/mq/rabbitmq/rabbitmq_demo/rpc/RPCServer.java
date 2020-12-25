package cn.aposoft.mq.rabbitmq.rabbitmq_demo.rpc;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static long fib(int n) {
        if (n == 0)
            return 0;
        if (n == 1)
            return 1;
        long v = 0;
        long vn_1 = 1;
        long vn_2 = 0;
        for (int i = 2; i <= n; i++) {
            v = vn_1 + vn_2;
            vn_2 = vn_1;
            vn_1 = v;
        }
        return v;
        // return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] argv) {
        Connection connection = null;
        Channel channel = null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            // factory.setHost("localhost");
            factory.setHost("192.168.63.133");
            factory.setConnectionTimeout(600000);
            factory.setHandshakeTimeout(600000);
            connection = factory.newConnection();
            System.out.println("Connection connected:");
            channel = connection.createChannel();

            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

            channel.basicQos(1);

            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

            System.out.println(" [x] Awaiting RPC requests");

            while (true) {
                String response = null;

                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                BasicProperties props = delivery.getProperties();
                BasicProperties replyProps = new BasicProperties.Builder().correlationId(props.getCorrelationId()).build();

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    int n = Integer.parseInt(message);

                    // System.out.println(" [.] fib(" + message + ")");
                    response = "" + fib(n);
                } catch (Exception e) {
                    // System.out.println(" [.] " + e.toString());
                    response = "";
                } finally {
                    channel.basicPublish("", props.getReplyTo(), replyProps, response.getBytes("UTF-8"));

                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ignore) {
                }
            }
        }
    }
}
