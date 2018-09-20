package amcmurray.bw.integrationTests;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import amcmurray.bw.twitterdomainobjects.Mention;
import amcmurray.bw.twitterdomainobjects.MentionType;
import amcmurray.bw.twitterdomainobjects.Query;

@Category(IntegrationTest.class)
public class MentionIT {

    @ClassRule
    public static IntegrationTestRule rule = IntegrationTestRule.INSTANCE;

    private final Date testDate1 = Timestamp.valueOf(LocalDateTime.parse("2018-08-31T18:30:00.000-00:00",
            DateTimeFormatter.ISO_DATE_TIME));
    private final Date testDate2 = Timestamp.valueOf(LocalDateTime.parse("2018-08-31T13:45:00.000-00:00",
            DateTimeFormatter.ISO_DATE_TIME));
    private final String testDateMentionDTO = ZonedDateTime
            .ofInstant(testDate1.toInstant(), ZoneId.of("UTC"))
            .format(DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss z Z"));

    private final Query query1 = new Query(0, "test query", "en");
    private final Query query2 = new Query(1, "another search", "");

    private final Mention mention1 = new Mention("123abc", 0, MentionType.TWITTER,
            "testAuthor1", "this is a mention of a test query", testDate1, "en", 0);
    private final Mention mention2 = new Mention("456def", 1, MentionType.TWITTER,
            "testAuthor2", "this is a mention of another search", testDate1, "en", 0);
    private final Mention mention3 = new Mention("789hij", 1, MentionType.TWITTER,
            "testAuthor1", "eine test Suche", testDate2, "de", 0);

    @Before
    public void setup() {
        addQueriesToDB();
        addMentionsToDB();
    }

    @After
    public void tearDown() {
        dropDB();
    }

    @Test
    public void viewMentionsOfQueryById_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query1.getId())

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(1))
                .body("id[0]", equalTo(mention1.getId()))
                .body("queryId[0]", equalTo(0))
                .body("text[0]", equalTo(mention1.getText()))
                .body("dateCreated[0]", equalTo(testDateMentionDTO))
                .body("languageCode[0]", equalTo(mention1.getLanguageCode()))
                .body("favouriteCount[0]", equalTo(mention1.getFavouriteCount()));
    }

    @Test
    public void viewMentionsOfQueryByIdWithLanguageCodeFilter_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query2.getId() + "/?languageCode=de")

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(1))
                .body("id[0]", equalTo(mention3.getId()))
                .body("queryId[0]", equalTo(query2.getId()))
                .body("languageCode[0]", equalTo("de"));
    }

    @Test
    public void viewMentionsOfQueryByIdWithDateBefore_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query2.getId() + "/?endDate=2018-09-01T12:30:00.000-00:00")

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(2))
                .body("id[0]", equalTo(mention2.getId()))
                .body("queryId[0]", equalTo(query2.getId()))
                .body("id[1]", equalTo(mention3.getId()))
                .body("queryId[1]", equalTo(query2.getId()));
    }

    @Test
    public void viewMentionsOfQueryByIdWithDateBeforeAndLanguageCode_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query2.getId() + "/?languageCode=en&endDate=2018-09-01T12:30:00.000-00:00")

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(1))
                .body("id[0]", equalTo(mention2.getId()))
                .body("queryId[0]", equalTo(query2.getId()));
    }

    @Test
    public void viewMentionsOfQueryByIdWithDateAfter_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query2.getId() + "/?startDate=2018-08-31T16:00:00.000-00:00")

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(1))
                .body("id[0]", equalTo(mention2.getId()))
                .body("queryId[0]", equalTo(query2.getId()));
    }

    @Test
    public void viewMentionsOfQueryByIdWithDateAfterAndLanguageCode_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query2.getId() + "/?languageCode=en&startDate=2018-08-30T16:00:00.000-00:00")

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(1))
                .body("id[0]", equalTo(mention2.getId()))
                .body("queryId[0]", equalTo(query2.getId()));
    }

    @Test
    public void viewMentionsOfQueryByIdWithDateRange_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query2.getId()
                + "/?startDate=2018-08-30T10:30:00.000-00:00&endDate=2018-09-01T12:00:00.000-00:00").prettyPeek()

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(2))
                .body("id[0]", equalTo(mention2.getId()))
                .body("queryId[0]", equalTo(query2.getId()))
                .body("id[1]", equalTo(mention3.getId()))
                .body("queryId[1]", equalTo(query2.getId()));
    }

    @Test
    public void viewMentionsOfQueryByIdWithDateRangeAndLanguageFilter_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query2.getId()
                + "/?languageCode=en&startDate=2018-08-30T10:30:00.000-00:00&endDate=2018-09-01T12:00:00.000-00:00")

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(1))
                .body("id[0]", equalTo(mention2.getId()))
                .body("queryId[0]", equalTo(query2.getId()));
    }

    @Test
    public void viewMentionsOfQueryByIdWithAuthorFilter_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query2.getId()+"/?author=testAuthor2")

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(1))
                .body("id[0]", equalTo(mention2.getId()))
                .body("queryId[0]", equalTo(query2.getId()));
    }

    @Test
    public void viewAllMentions_returnsValidMentions() {
        with().get(createURLWithPort("/mentions"))

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(3))
                .body("id[0]", equalTo(mention1.getId()))
                .body("queryId[0]", equalTo(0))
                .body("text[0]", notNullValue())
                .body("id[1]", equalTo(mention2.getId()))
                .body("queryId[1]", equalTo(1))
                .body("text[1]", notNullValue())
                .body("id[2]", equalTo(mention3.getId()))
                .body("queryId[2]", equalTo(1))
                .body("text[2]", notNullValue());
    }

    @Test
    public void viewMentionsByNonexistentQueryById_throwsQueryNotFoundException() {
        with().get(createURLWithPort("/mentions/-1"))

                .then().assertThat()
                .statusCode(404)
                .body("message",
                        equalTo("Query not found."));
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + rule.getApiExternalPort() + uri;
    }

    private void addQueriesToDB() {
        rule.getDatastore().save(query1);
        rule.getDatastore().save(query2);
    }

    private void dropDB() {
        rule.getDatastore().getDB().getCollection("savedQueries").drop();
        rule.getDatastore().getDB().getCollection("savedMentions").drop();
    }

    private void addMentionsToDB() {
        rule.getDatastore().save(mention1);
        rule.getDatastore().save(mention2);
        rule.getDatastore().save(mention3);
    }
}
