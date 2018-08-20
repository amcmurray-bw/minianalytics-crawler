package amcmurray.bw.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import amcmurray.bw.QueryRequestDTO;
import amcmurray.bw.exceptions.QueryExceptions;
import amcmurray.bw.repositories.QueryRepository;
import amcmurray.bw.twitterdomainobjects.Query;


@Service
public class QueryService {

    private QueryRepository queryRepository;

    @Autowired
    public QueryService(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    /**
     * Method to create a new query.
     *
     * @param request QueryDTO
     * @return new query, after saving to Database
     * @throws {QuerySearchNullException} if query string is blank
     */
    public Query createQuery(QueryRequestDTO request) {
        if (request.getSearch().equals("")) {
            throw new QueryExceptions.QuerySearchNullException();
        } else {
            Query query = new Query(getNewQueryId(), request.getSearch());
            return queryRepository.save(query);
        }
    }

    private int getNewQueryId() {
        Query lastQuery = queryRepository.findFirstByOrderByIdDesc();

        //if there are no queries set the Id to 0, else get last Id and +1
        return lastQuery == null ? 0 : lastQuery.getId() + 1;
    }

    /**
     * Method to find query.
     *
     * @param id of query
     * @return found query
     * @throws {QueryNotFoundException} if no query is found
     */
    public Query findQueryById(int id) {
        Query foundQuery = queryRepository.findById(id);

        if (foundQuery != null) {
            return foundQuery;
        } else {
            throw new QueryExceptions.QueryNotFoundException(id);
        }
    }

    public List<Query> getListAllQueries() {
        return queryRepository.findAll();
    }
}
