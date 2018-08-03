package amcmurray.bw.twitterdomainobjects;

import org.springframework.data.annotation.Id;

public final class Query {

    @Id
    String id;
    String text;

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
