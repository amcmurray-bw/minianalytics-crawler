package amcmurray.bw.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MentionConsumer {

    public static Logger LOGGER = LoggerFactory.getLogger(MentionConsumer.class);

    @KafkaListener(topics = "mentions")
    public void consume(String content) {
        LOGGER.info("Consumed data :: " + content);
    }

}
