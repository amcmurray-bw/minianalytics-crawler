package amcmurray.bw.twittercrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class TweetController {


    private final TweetService tweetService;

    @Autowired
    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String helloTwitter() {

        tweetService.getTweetsAndSaveToDB();

        return "hello";
    }
}


