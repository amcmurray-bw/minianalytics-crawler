package amcmurray.bw.twitterdomainobjects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Document(collection = "savedQueries")
public final class Query {

    @Id
    private int id;
    private String text;

    public Query(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Query not found.")  // 404 error
    public static class QueryNotFoundException extends RuntimeException {
        public int id;

        public QueryNotFoundException(int id) {
            this.id = id;
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Query text must not be empty!")  // 400 error
    public static class QuerySearchNullException extends RuntimeException {
    }
}
