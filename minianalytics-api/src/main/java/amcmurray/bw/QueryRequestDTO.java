package amcmurray.bw;

public class QueryRequestDTO {

    private String search;
    private String language;


    public QueryRequestDTO(String search, String language) {
        this.search = search;
        this.language = language;
    }

    public String getSearch() {
        return search;
    }

    public String getLanguage() {
        return language;
    }

    public QueryRequestDTO() {
    }
}
