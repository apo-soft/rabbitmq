package cn.aposoft.mq.rabbitmq.rabbitmq_demo.rpc;

import java.util.concurrent.atomic.AtomicInteger;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RPCClient {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private String replyQueueName;
    private QueueingConsumer consumer;
    private String tName;
    private AtomicInteger ai = new AtomicInteger(0);

    public RPCClient() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        // factory.setHost("localhost");
        factory.setHost("192.168.63.133");
        factory.setConnectionTimeout(600000);
        factory.setHandshakeTimeout(600000);
        long begin = System.currentTimeMillis();
        connection = factory.newConnection();
        long end = System.currentTimeMillis();
        System.out.println("Connection connected:" + (end - begin));
        channel = connection.createChannel();

        replyQueueName = channel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(replyQueueName, true, consumer);
        tName = Thread.currentThread().getName();
    }

    public String call(String message) throws Exception {
        String response = null;
        String corrId = tName + ai.incrementAndGet();// UUID.randomUUID().toString();

        BasicProperties props = new BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName).build();

        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response = new String(delivery.getBody(), "UTF-8");
                break;
            }
        }

        return response;
    }

    public void close() throws Exception {
        connection.close();
    }

    public static void main(String[] argv) {
        RPCClient fibonacciRpc = null;
        String response = null;
        try {
            fibonacciRpc = new RPCClient();
            long begin = System.currentTimeMillis();
            for (int j = 0; j < 1000; j++) {
                for (int i = 0; i < 200; i++) {
                    // System.out.println(" [x] Requesting fib(" + i + ")");
                    response = fibonacciRpc.call(String.valueOf(1));
                    // System.out.println(" [.] Got '" + response + "'");
                }
            }
            long end = System.currentTimeMillis();
            System.out.printf("%d,%d~%d", (end - begin), begin, end);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fibonacciRpc != null) {
                try {
                    fibonacciRpc.close();
                } catch (Exception ignore) {
                	
                }
            }
        }
    }
}
