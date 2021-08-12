package cn.aposoft.mq.rocketmq.hello.sync;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

public class ConsumerSync {

	public static void main(String[] args) throws InterruptedException, MQClientException {

		// Instantiate with specified consumer group name.
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");

		// Specify name server addresses.
		consumer.setNamesrvAddr("192.168.81.129:9876");

		// Subscribe one more more topics to consume.
		consumer.subscribe("TopicTest", "*");
		// Register callback to execute on arrival of messages fetched from brokers.
		consumer.registerMessageListener(new MessageListenerOrderly() {
			@Override
			public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
				System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
				return ConsumeOrderlyStatus.SUCCESS;
			}
		});

		// Launch the consumer instance.
		consumer.start();

		System.out.printf("Consumer Started.%n");
	}
}