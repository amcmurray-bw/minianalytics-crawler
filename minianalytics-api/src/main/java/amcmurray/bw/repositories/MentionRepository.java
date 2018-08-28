package amcmurray.bw.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import amcmurray.bw.Filter;
import amcmurray.bw.twitterdomainobjects.Mention;

@Repository
public class MentionRepository {

    private MongoTemplate mongoTemplate;

    @Autowired
    public MentionRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Mention> findMentionsOfQueryWithFilters(int queryId, Filter filter) {

        Query dynamicQuery = new Query();

        Criteria idCriteria = Criteria.where("queryId").is(queryId);
        dynamicQuery.addCriteria(idCriteria);

        if (filter.getLanguageCode() != null) {
            Criteria languageCodeCriteria = Criteria.where("languageCode").is(filter.getLanguageCode());
            dynamicQuery.addCriteria(languageCodeCriteria);
        }

        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            Criteria rangeCriteria = Criteria.where("createdAt").gte(filter.getStartDate()).lte((filter.getEndDate()));

            dynamicQuery.addCriteria(rangeCriteria);
        } else if (filter.getStartDate() != null) {
            Criteria rangeCriteria = Criteria.where("createdAt").gte(filter.getStartDate());
            dynamicQuery.addCriteria(rangeCriteria);

        } else if (filter.getEndDate() != null) {
            Criteria rangeCriteria = Criteria.where("createdAt").lte(filter.getEndDate());
            dynamicQuery.addCriteria(rangeCriteria);
        }

        return mongoTemplate.find(dynamicQuery, Mention.class, "savedMentions");

    }

    public List<Mention> findAllMentions() {
        return mongoTemplate.findAll(Mention.class, "savedMentions");
    }

}
