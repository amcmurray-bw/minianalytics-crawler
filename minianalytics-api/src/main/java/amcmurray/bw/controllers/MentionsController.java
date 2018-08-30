package amcmurray.bw.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import amcmurray.bw.Filter;
import amcmurray.bw.MentionPresenter;
import amcmurray.bw.services.MentionService;
import amcmurray.bw.twitterdomainobjects.MentionDTO;

@RestController
public class MentionsController {

    private MentionService mentionService;
    private MentionPresenter mentionPresenter;

    @Autowired
    public MentionsController(MentionService mentionService, MentionPresenter mentionPresenter) {
        this.mentionService = mentionService;
        this.mentionPresenter = mentionPresenter;
    }

    /**
     * View mentions of single query. Finds mentions and converts to DTO.
     *
     * @param queryId the id of the query to be viewed.
     * @return returns a list of MentionDTOs with the same queryId.
     */
    @GetMapping("/mentions/{queryId}")
    public List<MentionDTO> viewMentionsOfQuery(@PathVariable("queryId") int queryId, Filter filter) {

        return mentionPresenter.toDTOs(
                mentionService.findAllMentionsOfQueryWithFilters(queryId, filter));
    }

    /**
     * view all mentions, not relating to any query
     * (possibility to be removed in future)
     *
     * @return all mentions as MentionDTOs
     */
    @GetMapping("/mentions")
    public List<MentionDTO> viewAllMentions() {
        return mentionPresenter.toDTOs(
                mentionService.getAllMentions());
    }
}
