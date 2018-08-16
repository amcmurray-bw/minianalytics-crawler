package amcmurray.bw;

public class QueryRequestDTO {

    private String search;

    public QueryRequestDTO(String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public QueryRequestDTO() {
    }
}
