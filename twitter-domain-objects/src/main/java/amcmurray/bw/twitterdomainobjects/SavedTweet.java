package amcmurray.bw.twitterdomainobjects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/*
class limits amount of information to process to just ID and the tweet text
 */
@Document(collection = "savedTweets")
public final class SavedTweet {

    @Id
    String id;

    String text;
    String queryId;

    public SavedTweet(String id, String text, String queryId) {
        this.id = id;
        this.text = text;
        this.queryId = queryId;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getQueryId() {
        return queryId;
    }

}
