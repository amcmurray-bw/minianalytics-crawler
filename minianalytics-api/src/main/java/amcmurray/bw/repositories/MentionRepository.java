package amcmurray.bw.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import amcmurray.bw.twitterdomainobjects.Mention;

public interface MentionRepository extends MongoRepository<Mention, Integer> {

    List<Mention> findAllByQueryId(int queryId);
}
