package amcmurray.bw.twittercrawler;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;

import amcmurray.bw.twittercrawler.repositories.MentionRepository;
import amcmurray.bw.twitterdomainobjects.Mention;
import amcmurray.bw.twitterdomainobjects.MentionType;
import amcmurray.bw.twitterdomainobjects.Query;

@Service
public class MentionService {

    private final MentionRepository mentionRepository;
    private final Twitter twitter;

    @Autowired
    public MentionService(MentionRepository mentionRepository, Twitter twitter) {

        this.mentionRepository = mentionRepository;
        this.twitter = twitter;
    }

    /**
     * Saves new mentions of a query via the mention repository.
     */
    public void saveNewMentions(Query query) {

        SearchParameters params = new SearchParameters(query.getText());
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
