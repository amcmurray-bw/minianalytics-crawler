package amcmurray.bw.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import amcmurray.bw.Filter;
import amcmurray.bw.repositories.MentionRepository;
import amcmurray.bw.twitterdomainobjects.Mention;

@Service
public class MentionService {

    private MentionRepository mentionRepository;
    private QueryService queryService;

    @Autowired
    public MentionService(MentionRepository mentionRepository, QueryService queryService) {
        this.mentionRepository = mentionRepository;
        this.queryService = queryService;
    }

    /**
     * If a query is returned, find all mentions of the queryId
     *
     * @param filter filter object for endpoints
     * @return list of mentions
     * @throws {QueryNotFoundException} if query not found
     */
    public List<Mention> findAllMentionsOfQueryWithFilters(int queryId, Filter filter) {
        queryService.findQueryById(queryId);

        return mentionRepository.findMentionsOfQueryWithFilters(queryId, filter);
    }

    public List<Mention> getAllMentions() {
        return mentionRepository.findAllMentions();
    }
}
