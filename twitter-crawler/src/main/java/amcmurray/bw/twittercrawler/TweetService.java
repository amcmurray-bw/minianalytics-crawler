package amcmurray.bw.twittercrawler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import amcmurray.bw.twitterdomainobjects.Query;
import amcmurray.bw.twitterdomainobjects.SavedTweet;

@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final QueryRepository queryRepository;
    private final Twitter twitter;
    private final ExecutorService scheduledTaskExecutorService;
    private final Logger logger = LoggerFactory.getLogger(TweetService.class);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YY HH:mm:ss");


    @Autowired
    public TweetService(TweetRepository tweetRepository,
                        QueryRepository queryRepository, Twitter twitter,
                        ExecutorService scheduledTaskExecutorService) {

        this.tweetRepository = tweetRepository;
        this.queryRepository = queryRepository;
        this.twitter = twitter;
        this.scheduledTaskExecutorService = scheduledTaskExecutorService;
    }

    //cron set to every 5 minutes on the hour, eg 12:00, 12:05 etc
    @Scheduled(cron = "0 0/5 * * * *")
    public void getTweetsAndSaveToDB() {

        logger.info("Scheduled task started at {}", LocalDateTime.now().format(formatter));
        CompletableFuture.runAsync(() -> updateAllQueriesWithMentions(),
                scheduledTaskExecutorService).exceptionally(throwable -> handleAsyncException(throwable));
    }

    private Void handleAsyncException(Throwable throwable) {
        logger.warn("Exception occurred: ", throwable);
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

    private void getNewMentions(Query query) {

        SearchParameters params = new SearchParameters(query.getText());
        params.lang("en");
        SearchResults rawSearch = twitter.searchOperations().search(params);

        for (Tweet tweet : rawSearch.getTweets()) {
            SavedTweet savedTweet = new SavedTweet(tweet.getIdStr(), tweet.getText(), query.getId());
            tweetRepository.save(savedTweet);
        }

    }


}
