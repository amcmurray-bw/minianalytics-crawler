package amcmurray.bw.twitterdomainobjects;

import java.util.Objects;

public class MentionDTO {

    private String id;
    private int queryId;
    private MentionType mentionType;
    private String text;
    private String dateCreated;
    private String languageCode;
    private int favouriteCount;

    public MentionDTO(String id, int queryId, MentionType mentionType,
                      String text, String dateCreated,
                      String languageCode, int favouriteCount) {
        this.id = id;
        this.queryId = queryId;
        this.mentionType = mentionType;
        this.dateCreated = dateCreated;
        this.text = text;
        this.languageCode = languageCode;
        this.favouriteCount = favouriteCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MentionDTO that = (MentionDTO) o;
        return queryId == that.queryId &&
                favouriteCount == that.favouriteCount &&
                Objects.equals(id, that.id) &&
                mentionType == that.mentionType &&
                Objects.equals(text, that.text) &&
                Objects.equals(dateCreated, that.dateCreated) &&
                Objects.equals(languageCode, that.languageCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, queryId, mentionType, text, dateCreated, languageCode, favouriteCount);
    }

    public String getDateCreated() {
        return dateCreated;
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

    public String getLanguageCode() {
        return languageCode;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

}
