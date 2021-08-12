package cn.aposoft.mq.kafka.hello;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;

public class AutoCommitConsumerDemo {

	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "test");
		props.put("enable.auto.commit", "false");
		props.put("auto.commit.interval.ms", "1000");
		props.put("max.poll.records", 10);
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
		@SuppressWarnings("resource")
		KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("test"));
		for (int i = 0; i < 30; /* i++ */) {

			ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100L));

			for (ConsumerRecord<String, byte[]> record : records) {
				i++;
				System.out.printf("value = %s, offset = %d, key = %s, %n", //
						new String(record.value()), record.offset(), record.key());

				Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
				TopicPartition partition = new TopicPartition("test", record.partition());
				OffsetAndMetadata offset = new OffsetAndMetadata(record.offset() + 1);// next fetch offset = current
																						// offset + 1
				offsets.put(partition, offset);
//				consumer.commitSync(offsets);
				OffsetCommitCallback callback = new OffsetCommitCallback() {

					@Override
					public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
						System.out.println(offsets + "is commited async");
					};

				};
				consumer.commitAsync(offsets, callback);
			}

		}
		consumer.unsubscribe();
		consumer.close();
	}
}
