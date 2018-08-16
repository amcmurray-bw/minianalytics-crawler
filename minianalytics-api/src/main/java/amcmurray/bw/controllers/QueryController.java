package amcmurray.bw.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import amcmurray.bw.QueryRequestDTO;
import amcmurray.bw.services.QueryService;
import amcmurray.bw.twitterdomainobjects.Query;

@RestController
public class QueryController {

    private QueryService queryService;

    @Autowired
    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * Create a new query.
     */
    @PostMapping("/query")
    public Query addQuery(@RequestBody QueryRequestDTO request) {
        return queryService.createQuery(request);
    }

    /**
     * View a single query.
     */
    @GetMapping("/queries/{id}")
    public Query viewQuery(@PathVariable("id") int id) {
        return queryService.findQueryById(id);
    }

    /**
     * View all queries.
     */
    @GetMapping("/queries")
    public List<Query> viewQueries() {
        return queryService.getListAllQueries();
    }
}
