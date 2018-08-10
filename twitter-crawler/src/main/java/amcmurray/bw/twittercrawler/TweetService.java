package amcmurray.bw.twittercrawler;

import java.time.LocalDateTime;
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

import amcmurray.bw.twitterdomainobjects.Mention;
import amcmurray.bw.twitterdomainobjects.MentionType;

@Service
public class TweetService {

    private final MentionRepository mentionRepository;
    private final Twitter twitter;
    private final ExecutorService scheduledTaskExecutorService;
    private final Logger logger = LoggerFactory.getLogger(TweetService.class);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YY HH:mm:ss");


    @Autowired
    public TweetService(MentionRepository mentionRepository,
                        Twitter twitter,
                        ExecutorService scheduledTaskExecutorService) {

        this.mentionRepository = mentionRepository;
        this.twitter = twitter;
        this.scheduledTaskExecutorService = scheduledTaskExecutorService;
    }

    //cron set to every 15 minutes on the hour, eg 12:00, 12:15 etc
    @Scheduled(cron = "0 0/1 * * * *")
    public void getTweetsAndSaveToDB() {

        logger.info("Scheduled task started at {}", LocalDateTime.now().format(formatter));
        CompletableFuture
                .runAsync(() -> saveRandomTweetsIntoDB(), scheduledTaskExecutorService)
                .exceptionally(throwable -> handleAsyncException(throwable));
    }

    private Void handleAsyncException(Throwable throwable) {
        logger.warn("Exception occurred while trying to add mentions into database: ", throwable);
        return null;
    }

    private void saveRandomTweetsIntoDB() {
        //searching for a blank space in a tweet, in english
        SearchParameters params = new SearchParameters("%20");
        params.lang("en");
        SearchResults rawSearch = twitter.searchOperations().search(params);


        for (Tweet tweet : rawSearch.getTweets()) {
            Mention mention = new Mention(UUID.randomUUID().toString(), -1, MentionType.TWITTER,
                    tweet.getText(), tweet.getLanguageCode(),
                    tweet.getFavoriteCount());
            mentionRepository.insert(mention);
        }

        logger.info("Tweets saved at {}", LocalDateTime.now().format(formatter));
    }
}
