package amcmurray.bw.twitterdomainobjects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/*
class limits amount of information to process to just ID and the tweet text
 */
@Document(collection = "savedTweets")
public final class SavedTweet {

    @Id
    long id;

    String text;
    int queryId;

    public SavedTweet(long id, String text, int queryId) {
        this.id = id;
        this.text = text;
        this.queryId = queryId;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getQueryId() {
        return queryId;
    }

}
