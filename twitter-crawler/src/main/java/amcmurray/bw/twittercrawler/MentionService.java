package amcmurray.bw.twittercrawler;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import amcmurray.bw.twittercrawler.repositories.MentionRepository;
import amcmurray.bw.twitterdomainobjects.Mention;
import amcmurray.bw.twitterdomainobjects.MentionType;
import amcmurray.bw.twitterdomainobjects.Query;

@Service
public class MentionService {

    private final Producer<String, String> producer;
    private final MentionRepository mentionRepository;
    private final Twitter twitter;
    private final ObjectMapper jsonObjectMapper;
    private final Logger logger = LoggerFactory.getLogger(MentionService.class);
    private final String kafkaTopic = "mentions";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss.SSS Z")
                    .withZone(ZoneId.systemDefault());


    @Autowired
    public MentionService(Producer<String, String> producer, MentionRepository mentionRepository, Twitter twitter, ObjectMapper jsonObjectMapper) {

        this.producer = producer;
        this.mentionRepository = mentionRepository;
        this.twitter = twitter;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    /**
     * Saves new mentions of a query via the mention repository.
     */
    public void saveNewMentions(Query query) {

        SearchParameters params = new SearchParameters(query.getText());

        //if query has a specified language, set to that
        if (StringUtils.isNotBlank(query.getLanguage())) {
            params.lang(query.getLanguage());
        }
        params.count(10);

        SearchResults rawSearch = twitter.searchOperations().search(params);

        for (Tweet tweet : rawSearch.getTweets()) {
            Mention mention = new Mention(UUID.randomUUID().toString(),
                    query.getId(), MentionType.TWITTER,
                    tweet.getUser().getScreenName(), tweet.getText(), tweet.getCreatedAt(),
                    tweet.getLanguageCode(), tweet.getFavoriteCount());
            mentionRepository.insert(mention);

            try {
                String mappedObject = jsonObjectMapper.writeValueAsString(mention);
                producer.send(new ProducerRecord<String, String>("mentions", mention.getId(), mappedObject));

            } catch (JsonProcessingException e) {
                logger.info("Error occured while producing to kafka topic " + kafkaTopic + " at ",
                        DATE_TIME_FORMATTER.format(Instant.now()));
                logger.error(e.getMessage() + " " + e.getCause().toString());
            }

        }
    }
}
