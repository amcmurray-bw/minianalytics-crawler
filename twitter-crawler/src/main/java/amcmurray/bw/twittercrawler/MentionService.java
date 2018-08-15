package amcmurray.bw.twittercrawler;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;

import amcmurray.bw.twittercrawler.repositories.MentionRepository;
import amcmurray.bw.twittercrawler.repositories.QueryRepository;
import amcmurray.bw.twitterdomainobjects.Mention;
import amcmurray.bw.twitterdomainobjects.MentionType;
import amcmurray.bw.twitterdomainobjects.Query;

@Service
public class MentionService {

    private final QueryRepository queryRepository;
    private final MentionRepository mentionRepository;
    private final Twitter twitter;
    private final ExecutorService scheduledTaskExecutorService;
    private final Logger logger = LoggerFactory.getLogger(MentionService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss.SSS Z")
                    .withZone(ZoneId.systemDefault());

    @Autowired
    public MentionService(MentionRepository mentionRepository,
                          QueryRepository queryRepository, Twitter twitter,
                          ExecutorService scheduledTaskExecutorService) {

        this.mentionRepository = mentionRepository;
        this.queryRepository = queryRepository;
        this.twitter = twitter;
        this.scheduledTaskExecutorService = scheduledTaskExecutorService;
    }

    /**
     * Scheduled method to grab more mentions
     * cron set to every 5 minutes on the hour, eg 12:00, 12:05 etc
     */
    @Scheduled(cron = "0 0/5 * * * *")
    public void getTweetsAndSaveToDB() {

        logger.info("Scheduled task started at {}", DATE_TIME_FORMATTER.format(Instant.now()));

        try {
            updateAllQueriesWithMentions();
        } catch (Exception e) {
            logger.warn("Exception occurred while trying to add mentions into database: ", e.getMessage());
        }
    }


    private void updateAllQueriesWithMentions() {

        logger.info("Updating all query mentions started at: {}",
                DATE_TIME_FORMATTER.format(Instant.now()));

        //for each query, get new mentions
        for (Query query : queryRepository.findAll()) {
            getNewMentions(query);
        }
        logger.info("Updating all query mentions finished at: {}",
                DATE_TIME_FORMATTER.format(Instant.now()));
    }

    /**
     * Gathers new mentions of a query.
     */
    private void getNewMentions(Query query) {

        SearchParameters params = new SearchParameters(query.getText());
        params.lang("en"); //english for now
        SearchResults rawSearch = twitter.searchOperations().search(params);

        for (Tweet tweet : rawSearch.getTweets()) {
            Mention mention = new Mention(UUID.randomUUID().toString(),
                    query.getId(), MentionType.TWITTER,
                    tweet.getText(), tweet.getCreatedAt(),
                    tweet.getLanguageCode(), tweet.getFavoriteCount());
            mentionRepository.insert(mention);
        }
    }
}
