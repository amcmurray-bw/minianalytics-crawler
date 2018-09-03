package amcmurray.bw.storage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import amcmurray.bw.storage.repositories.MentionRepository;
import amcmurray.bw.twitterdomainobjects.Mention;

@Component
public class MentionStorageService {

    private final MentionRepository mentionRepository;
    private final ObjectMapper mapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(MentionStorageService.class);

    @Autowired
    public MentionStorageService(MentionRepository mentionRepository, ObjectMapper mapper) {
        this.mentionRepository = mentionRepository;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "mentions")
    public void consume(String content) {

        try {
            Mention mention = mapper.readValue(content, Mention.class);
            LOGGER.debug("Adding Mention to DB " + mention.getId());
            mentionRepository.insert(mention);
        } catch (IOException e) {
            LOGGER.warn("Error adding Mention to DB " + e.getMessage());
        }
    }
}
