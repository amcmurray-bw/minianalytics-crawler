package amcmurray.bw.twittercrawler.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import amcmurray.bw.twitterdomainobjects.Mention;

public interface MentionRepository extends MongoRepository<Mention, String> {
}
