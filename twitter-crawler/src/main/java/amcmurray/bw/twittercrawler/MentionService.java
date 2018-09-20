package amcmurray.bw.twittercrawler;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;

import amcmurray.bw.twitterdomainobjects.Mention;
import amcmurray.bw.twitterdomainobjects.MentionType;
import amcmurray.bw.twitterdomainobjects.Query;

@Service
public class MentionService {

    private final KafkaTemplate<String, Mention> kafkaTemplate;
    private final Twitter twitter;
    private final Logger logger = LoggerFactory.getLogger(MentionService.class);
    private final String kafkaTopic = "mentions";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss.SSS Z")
                    .withZone(ZoneId.systemDefault());


    @Autowired
    public MentionService(KafkaTemplate<String, Mention> kafkaTemplate, Twitter twitter) {
        this.kafkaTemplate = kafkaTemplate;
        this.twitter = twitter;
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

            try {
                kafkaTemplate.send("mentions", mention);
            } catch (Exception e) {
                logger.error("Error occurred while producing to kafka topic " + kafkaTopic, e);
            }
        }
    }
}
