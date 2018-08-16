package amcmurray.bw.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
     * view mentions of single query
     */
    @GetMapping("/mentions/{queryId}")
    public List<MentionDTO> viewMentionsOfQuery(@PathVariable("queryId") int queryId) {
        return mentionPresenter.toDTOs(
                mentionService.findAllMentionsOfQuery(queryId));
    }

    /**
     * view all mentions, not relating to any query
     * (possibility to be removed in future)
     */
    @GetMapping("/mentions")
    public List<MentionDTO> viewAllMentions() {
        return mentionPresenter.toDTOs(
                mentionService.getAllMentions());
    }
}
