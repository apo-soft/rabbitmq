package cn.aposoft.mq.rocketmq.hello.pull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

public class ConsumerPull {
	private static final Map<MessageQueue, Long> offseTable = new HashMap<MessageQueue, Long>();

	public static void main(String[] args) throws InterruptedException, MQClientException {

		// Instantiate with specified consumer group name.
		DefaultLitePullConsumer consumer = new DefaultLitePullConsumer("please_rename_unique_group_name");

		// Specify name server addresses.
		consumer.setNamesrvAddr("localhost:9876");
		consumer.start();
		System.out.printf("Consumer Started.%n");
		consumer.subscribe("Jodie_topic_1023", "*");
		int i = 0;
		while (i < 1000) {
			List<MessageExt> messages = consumer.poll();
			if (messages == null || messages.isEmpty()) {
				i++;
				continue;
			}
			for (MessageExt m : messages) {
				System.out.println(m);
			}
			consumer.commitSync();
		}
		consumer.shutdown();
	}

}