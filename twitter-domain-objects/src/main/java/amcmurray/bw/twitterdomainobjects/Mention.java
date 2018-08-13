package amcmurray.bw.twitterdomainobjects;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "savedMentions")
public final class Mention {

    @Id
    private String id;
    private int queryId;
    private MentionType mentionType;
    private String text;
    private Date createdAt;
    private String languageCode;
    private int favouriteCount;

    public Mention(String id, int queryId, MentionType mentionType,
                   String text, Date createdAt,
                   String languageCode, int favouriteCount) {
        this.id = id;
        this.queryId = queryId;
        this.mentionType = mentionType;
        this.text = text;
        this.createdAt = createdAt;
        this.languageCode = languageCode;
        this.favouriteCount = favouriteCount;
    }

    public String getId() {
        return id;
    }

    public int getQueryId() {
        return queryId;
    }

    public MentionType getMentionType() {
        return mentionType;
    }

    public String getText() {
        return text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

}
