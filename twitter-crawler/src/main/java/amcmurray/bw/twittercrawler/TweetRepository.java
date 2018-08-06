package amcmurray.bw.twittercrawler;

import org.springframework.data.mongodb.repository.MongoRepository;

import amcmurray.bw.twitterdomainobjects.SavedTweet;

public interface TweetRepository extends MongoRepository<SavedTweet, String> {
}
