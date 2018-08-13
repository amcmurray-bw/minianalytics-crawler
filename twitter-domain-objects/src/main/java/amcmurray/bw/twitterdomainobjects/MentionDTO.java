package amcmurray.bw.twitterdomainobjects;

public class MentionDTO {


    private String id;
    private  int queryId;
    private  MentionType mentionType;
    private  String text;
    private  String dateCreated;
    private  String languageCode;
    private  int favouriteCount;

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

    public MentionDTO() {}

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
