package amcmurray.bw.twittercrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class TweetController {

    private final Twitter twitter;
    private final ConnectionRepository connectionRepository;
    private final TweetService tweetService;

    @Autowired
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


