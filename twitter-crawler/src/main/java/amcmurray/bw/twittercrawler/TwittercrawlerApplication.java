package amcmurray.bw.twittercrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.social.TwitterAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAutoConfiguration(exclude = {TwitterAutoConfiguration.class})
@SpringBootApplication
public class TwittercrawlerApplication {

    public static void main(String[] args) {

        SpringApplication.run(TwittercrawlerApplication.class, args);
    }
}
