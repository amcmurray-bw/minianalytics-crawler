package amcmurray.bw.storage;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import amcmurray.bw.twitterdomainobjects.Mention;

@Configuration
public class StorageConfig {

    private static final String BOOTSTRAP_SERVER = "kafka:9092";
    private static final String GROUP_ID = "storage-mention-group";
    private static final String SOLR_URL = "http://solr:8983/solr/mentions";

    @Bean
    public KafkaConsumer<String, Mention> kafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new KafkaConsumer<>(props, new StringDeserializer(), new JsonDeserializer<>(Mention.class));
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public MentionStorageService consumerThread() {
        return new MentionStorageService(kafkaConsumer());
    }

    @Bean
    public SolrClient solrClient() {
        HttpSolrClient httpSolrClient = new HttpSolrClient.Builder(SOLR_URL).build();
        httpSolrClient.setParser(new XMLResponseParser());
        return httpSolrClient;
    }
}
