package amcmurray.bw.twittercrawler;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public TweetService(TweetRepository tweetRepository, Twitter twitter) {
        this.tweetRepository = tweetRepository;
        this.twitter = twitter;
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
