package amcmurray.bw.twittercrawler;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import amcmurray.bw.twittercrawler.repositories.QueryRepository;
import amcmurray.bw.twitterdomainobjects.Query;

@Component
public class TwitterCrawlerJob {

    private final QueryRepository queryRepository;
    private final MentionService mentionService;

    private final Logger logger = LoggerFactory.getLogger(MentionService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss.SSS Z")
                    .withZone(ZoneId.systemDefault());

    private TwitterCrawlerJob(QueryRepository queryRepository,
                              MentionService mentionService) {
        this.queryRepository = queryRepository;
        this.mentionService = mentionService;
    }

    /**
     * Scheduled method to grab more mentions
     * cron set to every 5 minutes on the hour, eg 12:00, 12:05 etc
     */
    @Scheduled(cron = "0 0/1 * * * *")
    private void getTweetsAndSaveToDB() {

        logger.info("Scheduled tweet gathering started at {}",
                DATE_TIME_FORMATTER.format(Instant.now()));

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
            mentionService.saveNewMentions(query);
        }
        logger.info("Updating all query mentions finished at: {}",
                DATE_TIME_FORMATTER.format(Instant.now()));
    }
}
