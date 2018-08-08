package amcmurray.bw.twittercrawler;


import org.springframework.data.mongodb.repository.MongoRepository;

import amcmurray.bw.twitterdomainobjects.Query;

public interface QueryRepository extends MongoRepository<Query, String> {

    Query findById(String id);
    Query findByText(String text);

}
