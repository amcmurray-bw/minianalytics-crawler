package amcmurray.bw.twitterdomainobjects;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "savedQueries")
public final class Query {

    @Id
    private int id;
    private String text;

    public Query(int id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query query = (Query) o;
        return id == query.id &&
                Objects.equals(text, query.text);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, text);
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
