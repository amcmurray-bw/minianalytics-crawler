package amcmurray.bw.twitterdomainobjects;

import java.util.Date;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "savedMentions")
public class Mention {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mention mention = (Mention) o;
        return queryId == mention.queryId &&
                favouriteCount == mention.favouriteCount &&
                Objects.equals(id, mention.id) &&
                mentionType == mention.mentionType &&
                Objects.equals(text, mention.text) &&
                Objects.equals(createdAt, mention.createdAt) &&
                Objects.equals(languageCode, mention.languageCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, queryId, mentionType, text, createdAt, languageCode, favouriteCount);
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
