package amcmurray.bw.twittercrawler;

import javax.inject.Inject;

import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;

@Service
public class TweetService {

    private TweetRepository tweetRepository;
    private Twitter twitter;

    @Inject
    public TweetService(Twitter twitter, TweetRepository tweetRepository) {
        this.twitter = twitter;
        this.tweetRepository = tweetRepository;
    }

    public void getTweetsAndSaveToDB() {

        //searching for a blank space in a tweet, in english
        SearchParameters params = new SearchParameters("%20");
        params.lang("en");
        SearchResults rawSearch = twitter.searchOperations().search(params);

        for (Tweet tweet : rawSearch.getTweets()) {

            SavedTweet savedTweet = new SavedTweet(tweet.getId(), tweet.getText(), "-1");

            tweetRepository.insert(savedTweet);
        }
    }

}
