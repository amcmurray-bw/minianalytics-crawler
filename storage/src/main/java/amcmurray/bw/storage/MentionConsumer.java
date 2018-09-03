package amcmurray.bw.storage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import amcmurray.bw.storage.repositories.MentionStorageRepository;
import amcmurray.bw.twitterdomainobjects.Mention;

@Component
public class MentionConsumer {

    private final MentionStorageRepository mentionStorageRepository;
    private final ObjectMapper mapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(MentionConsumer.class);

    @Autowired
    public MentionConsumer(MentionStorageRepository mentionStorageRepository, ObjectMapper mapper) {
        this.mentionStorageRepository = mentionStorageRepository;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "mentions")
    public void consume(String content) {

        try {
            Mention mention = mapper.readValue(content, Mention.class);
            LOGGER.info("Adding Mention to DB");
            mentionStorageRepository.insert(mention);
        } catch (IOException e) {
            LOGGER.warn("Error adding Mention to DB " + e.getMessage());
        }
    }
}
