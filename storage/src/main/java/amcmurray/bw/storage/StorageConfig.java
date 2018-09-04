package amcmurray.bw.storage;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import amcmurray.bw.twitterdomainobjects.Mention;

@EnableKafka
@Configuration
public class StorageConfig {

    private static final String address = "kafka:9092";
    private static final String groupId = "kafka-mentions-group";

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, address);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return props;
    }

    @Bean
    public ConsumerFactory<String, Mention> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(), new JsonDeserializer<>(Mention.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Mention> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Mention> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckOnError(false);
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
     //   factory.getContainerProperties().setErrorHandler(new SeekToCurrentErrorHandler());
     //   factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.RECORD);
        return factory;
    }


}
