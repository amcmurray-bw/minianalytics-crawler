package amcmurray.bw.exceptions;

public class QueryExceptions extends Exception {

    public static class QueryNotFoundException extends RuntimeException {
        public int id;

        public QueryNotFoundException(int id) {
            this.id = id;
        }
    }

    public static class QuerySearchNullException extends RuntimeException {
    }


}
