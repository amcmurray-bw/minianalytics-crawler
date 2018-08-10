package amcmurray.bw.twittercrawler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YY HH:mm:ss");

    @Autowired
    public MentionService(MentionRepository mentionRepository,
                          QueryRepository queryRepository, Twitter twitter,
                          ExecutorService scheduledTaskExecutorService) {

        this.mentionRepository = mentionRepository;
        this.queryRepository = queryRepository;
        this.twitter = twitter;
        this.scheduledTaskExecutorService = scheduledTaskExecutorService;
    }

    //cron set to every 5 minutes on the hour, eg 12:00, 12:05 etc
    @Scheduled(cron = "0 0/1 * * * *")
    public void getTweetsAndSaveToDB() {

        logger.info("Scheduled task started at {}", LocalDateTime.now().format(formatter));
        CompletableFuture
                .runAsync(() -> updateAllQueriesWithMentions(), scheduledTaskExecutorService)
                .exceptionally(throwable -> handleAsyncException(throwable));
    }

    private Void handleAsyncException(Throwable throwable) {
        logger.warn("Exception occurred while trying to add mentions into database: ", throwable);
        return null;
    }

    private void updateAllQueriesWithMentions() {

        logger.info("Updating all query mentions started at: {}", LocalDateTime.now().format(formatter));

        //for each query, get new mentions
        for (Query query : queryRepository.findAll()) {
            getNewMentions(query);
        }
        logger.info("Updating all query mentions finished at: {}", LocalDateTime.now().format(formatter));
    }

    //method to gather new mentions of a query
    private void getNewMentions(Query query) {

        SearchParameters params = new SearchParameters(query.getText());
        params.lang("en"); //english for now
        SearchResults rawSearch = twitter.searchOperations().search(params);

        for (Tweet tweet : rawSearch.getTweets()) {
            //getting tweet info and converting date to zonedDateTime
            Mention mention = new Mention(UUID.randomUUID().toString(),
                    query.getId(), MentionType.TWITTER,
                    tweet.getText(),
                    tweet.getLanguageCode(), tweet.getFavoriteCount());
            mentionRepository.insert(mention);

        }
    }
}
