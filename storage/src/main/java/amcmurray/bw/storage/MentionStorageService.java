package amcmurray.bw.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import amcmurray.bw.storage.repositories.MentionRepository;
import amcmurray.bw.twitterdomainobjects.Mention;

@Component
public class MentionStorageService {

    private final MentionRepository mentionRepository;

    private static final Logger logger = LoggerFactory.getLogger(MentionStorageService.class);

    @Autowired
    public MentionStorageService(MentionRepository mentionRepository) {
        this.mentionRepository = mentionRepository;
    }

    @KafkaListener(topics = "mentions", containerFactory = "kafkaListenerContainerFactory")
    public void consume(Mention mention, Acknowledgment ack) {

        logger.debug("Adding Mention to DB " + mention.getId());

        try {
            mentionRepository.insert(mention);
            ack.acknowledge();
        } catch (Exception e) {
            logger.error("Exception occured adding mention to DB " + e.getMessage());
        }
    }
}

