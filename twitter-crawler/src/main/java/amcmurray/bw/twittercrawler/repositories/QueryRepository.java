package amcmurray.bw.twittercrawler.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import amcmurray.bw.twitterdomainobjects.Query;

public interface QueryRepository extends MongoRepository<Query, String> {

}
