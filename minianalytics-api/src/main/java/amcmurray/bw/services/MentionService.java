package amcmurray.bw.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @return list of mentions
     * @throws if query not found
     */
    public List<Mention> findAllMentionsOfQuery(int queryId) {

        queryService.findQueryById(queryId);

        return mentionRepository.findAllByQueryId(queryId);
    }

    public List<Mention> getAllMentions() {
        return mentionRepository.findAll();
    }
}
