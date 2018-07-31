package amcmurray.bw.twittercrawler;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TwittercrawlerApplication{

    public static void main(String[] args) {

        SpringApplication.run(TwittercrawlerApplication.class, args);

    }


}
