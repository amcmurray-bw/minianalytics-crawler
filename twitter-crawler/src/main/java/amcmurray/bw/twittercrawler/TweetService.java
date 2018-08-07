package amcmurray.bw.twittercrawler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;

import amcmurray.bw.twitterdomainobjects.SavedTweet;

@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final Twitter twitter;
    private final ExecutorService scheduledTaskExecutorService;

    @Autowired
    public TweetService(TweetRepository tweetRepository, Twitter twitter, ExecutorService scheduledTaskExecutorService) {
        this.tweetRepository = tweetRepository;
        this.twitter = twitter;
        this.scheduledTaskExecutorService = scheduledTaskExecutorService;
    }

    //cron set to every 15 minutes on the hour, eg 12:00, 12:15 etc
    @Scheduled(cron = "0 0/15 * * * *")
    public void getTweetsAndSaveToDB() {
        System.out.println("Scheduled task");
        CompletableFuture.runAsync(() -> saveTweetsIntoDB(), scheduledTaskExecutorService).exceptionally(throwable -> handleAsyncException(throwable));
    }

    public Void handleAsyncException(Throwable throwable) {
        System.out.println(throwable.getCause());
        return null;
    }


    private void saveTweetsIntoDB() {
        //searching for a blank space in a tweet, in english
        SearchParameters params = new SearchParameters("%20");
        params.lang("en");
        SearchResults rawSearch = twitter.searchOperations().search(params);

        for (Tweet tweet : rawSearch.getTweets()) {
            SavedTweet savedTweet = new SavedTweet(tweet.getId(), tweet.getText(), "-1");
            tweetRepository.insert(savedTweet);
        }

        System.out.println("Tweets saved");
    }
}
