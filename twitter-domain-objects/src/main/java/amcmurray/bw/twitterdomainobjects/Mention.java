package amcmurray.bw.twitterdomainobjects;

import java.util.Date;
import java.util.Objects;

import org.apache.solr.client.solrj.beans.Field;
import org.mongodb.morphia.annotations.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity("savedMentions")
@CompoundIndex(name = "mentionId", def = "{'id' : 1, 'queryId': 1}")
@Document(collection = "savedMentions")
public class Mention {

    @Id
    @org.mongodb.morphia.annotations.Id
    @Field
    private String id;
    @Field
    private int queryId;
    @Field
    private MentionType mentionType;
    @Field
    private String author;
    @Field
    private String text;
    @Field
    private Date createdAt;
    @Field
    private String languageCode;
    @Field
    private int favouriteCount;

    public Mention(String id, int queryId, MentionType mentionType,
                   String author, String text, Date createdAt,
                   String languageCode, int favouriteCount) {
        this.id = id;
        this.queryId = queryId;
        this.mentionType = mentionType;
        this.author = author;
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

    public Mention() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public MentionType getMentionType() {
        return mentionType;
    }

    public void setMentionType(MentionType mentionType) {
        this.mentionType = mentionType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(int favouriteCount) {
        this.favouriteCount = favouriteCount;
    }
}
