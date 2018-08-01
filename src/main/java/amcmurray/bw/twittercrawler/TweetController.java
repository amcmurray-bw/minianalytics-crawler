package amcmurray.bw.twittercrawler;

import javax.inject.Inject;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class TweetController {

    private Twitter twitter;
    private ConnectionRepository connectionRepository;
    private TweetService tweetService;

    @Inject
    public TweetController(Twitter twitter, ConnectionRepository connectionRepository, TweetService tweetService) {
        this.twitter = twitter;
        this.connectionRepository = connectionRepository;
        this.tweetService = tweetService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String helloTwitter(Model model) {

        if (connectionRepository.findPrimaryConnection(Twitter.class) == null) {
            return "redirect:/connect/twitter";
        }

        model.addAttribute(twitter.userOperations().getUserProfile());

        tweetService.getTweetsAndSaveToDB();

        return "hello";
    }
}


