package amcmurray.bw.twittercrawler;

import org.springframework.data.mongodb.repository.MongoRepository;

import amcmurray.bw.twitterdomainobjects.Mention;

public interface MentionRepository extends MongoRepository<Mention, String> {
}
