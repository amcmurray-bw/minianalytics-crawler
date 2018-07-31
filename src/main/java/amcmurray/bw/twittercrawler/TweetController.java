package amcmurray.bw.twittercrawler;

import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.*;


@Controller
@RequestMapping("/")
public class TweetController {

    private Twitter twitter;

    private ConnectionRepository connectionRepository;


    private Map<String, SavedTweet> listOfTweets = new HashMap<>();


    private MongoClient client = new MongoClient("localhost", 27017);
    private MongoDatabase database = client.getDatabase("test2");
    private MongoCollection<Document> collection = database.getCollection("savedTweets");

    @Inject
    public TweetController(Twitter twitter, ConnectionRepository connectionRepository) {
        this.twitter = twitter;
        this.connectionRepository = connectionRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String helloTwitter(Model model) {
        if (connectionRepository.findPrimaryConnection(Twitter.class) == null) {

            return "redirect:/connect/twitter";
        }

        model.addAttribute(twitter.userOperations().getUserProfile());

        searchTwitter();

        saveToDB();

        return "hello";
    }



    public Map<String, SavedTweet> getListOfTweets() {
        return listOfTweets;
    }

    private void searchTwitter() {

        SearchParameters params = new SearchParameters("%20");
        params.lang("en");


        SearchResults rawSearch = twitter.searchOperations().search(params);

        for (Tweet tweet : rawSearch.getTweets()) {
            listOfTweets.put((Long.toString(tweet.getId())), new SavedTweet(tweet.getId(), tweet.getText(), "-1"));
        }

    }



    /*
    method to save to DB using Gson Converter
     */

    private void saveToDB() {

        Gson gson = new Gson();

        Iterator it = listOfTweets.entrySet().iterator();
        Document document;

        while (it.hasNext()) {
            Map.Entry tweet = (Map.Entry) it.next();
            document = Document.parse(gson.toJson(tweet.getValue()));
            collection.insertOne(document);
            it.remove();
        }

    }

}


