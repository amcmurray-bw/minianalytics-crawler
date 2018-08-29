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
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.configuration.ShutdownStrategy;
import com.palantir.docker.compose.connection.waiting.HealthChecks;

import amcmurray.bw.QueryRequestDTO;
import amcmurray.bw.exceptions.QueryExceptions;
import amcmurray.bw.twitterdomainobjects.Mention;
import amcmurray.bw.twitterdomainobjects.MentionType;
import amcmurray.bw.twitterdomainobjects.Query;
import io.restassured.http.ContentType;

@Category(IntegrationTest.class)
public class miniananalyticsApiIT {

    private static final String MONGO_CONTAINER_NAME = "mongodbtest";
    private static final int MONGO_INTERNAL_PORT = 27017;
    private static int MONGO_EXTERNAL_PORT;

    private static final String API_CONTAINER_NAME = "minianalyticsapitest";
    private static final int API_INTERNAL_PORT = 8081;
    private static int API_EXTERNAL_PORT;

    private static final String DATABASE_NAME = "minianalytics";
    private static Datastore datastore;

    private final Date testDate1 = Timestamp.valueOf(LocalDateTime.parse("2018.08.31.18.30.00",
            DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss")));
    private final Date testDate2 = Timestamp.valueOf(LocalDateTime.parse("2018.08.31.13.45.00",
            DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss")));
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

    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
            .file("src/test/resources/docker-compose-tests.yml")
            .projectName(ProjectName.random())
            .waitingForService(API_CONTAINER_NAME, HealthChecks.toHaveAllPortsOpen())
            .saveLogsTo("target/docker-compose-test-logs")
            .shutdownStrategy(ShutdownStrategy.GRACEFUL)
            .build();

    @BeforeClass
    public static void initialize() {
        //getting external port for API service
        API_EXTERNAL_PORT = docker.containers()
                .container(API_CONTAINER_NAME)
                .port(API_INTERNAL_PORT).getExternalPort();

        MONGO_EXTERNAL_PORT = docker.containers()
                .container(MONGO_CONTAINER_NAME)
                .port(MONGO_INTERNAL_PORT).getExternalPort();

        //using morphia to create new datastore
        final Morphia morphia = new Morphia();
        morphia.mapPackage("amcmurray.bw.twitterdomainobjects");

        //datastore uses mongo instance
        datastore = morphia.createDatastore(
                new MongoClient("localhost", MONGO_EXTERNAL_PORT), DATABASE_NAME);
        datastore.ensureIndexes();
    }

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
    public void viewQueryById_getsExpectedQuery() {
        with().get(createURLWithPort("/queries/0"))
                .then().assertThat()
                .statusCode(200)
                .body("id", equalTo(query1.getId()))
                .body("text", equalTo(query1.getText()));
    }

    @Test
    public void viewNonexistentQueryById_throwsQueryNotFoundException() {
        with().get(createURLWithPort("/queries/-1"))

                .then().assertThat()
                .statusCode(404)
                .body("exception",
                        equalTo(QueryExceptions.QueryNotFoundException.class.getName()));
    }

    @Test
    public void viewMultipleQueries_returnsMultiple() {
        with().get(createURLWithPort("/queries"))

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(2))
                .body("id[0]", equalTo(query1.getId()))
                .body("text[0]", equalTo(query1.getText()))
                .body("id[1]", equalTo(query2.getId()))
                .body("text[1]", equalTo(query2.getText()));
    }

    @Test
    public void addNewQuery_returnsExpectedQuery() {
        QueryRequestDTO queryRequestDTO = new QueryRequestDTO("new search", "en");

        with().contentType(ContentType.JSON)
                .body(queryRequestDTO)
                .when()
                .post((createURLWithPort("/queries")))

                .then().assertThat()
                .statusCode(200)
                .body("id", equalTo(query2.getId() + 1))
                .body("text", equalTo(queryRequestDTO.getSearch()));
    }

    @Test
    public void addNewQueryWithBlankSearch_throwsQuerySearchNullException() {
        QueryRequestDTO queryRequestDTO = new QueryRequestDTO("", "en");

        with().contentType(ContentType.JSON)
                .body(queryRequestDTO)
                .when()
                .post((createURLWithPort("/queries")))

                .then().assertThat()
                .statusCode(400)
                .body("exception",
                        equalTo(QueryExceptions.QuerySearchNullException.class.getName()));
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
                .body("languageCode[0]", equalTo(mention3.getLanguageCode()));
    }

    @Test
    public void viewMentionsOfQueryByIdWithDateBefore_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query2.getId() + "/?endDate=01-09-2018 12:30:00")

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
        with().get(createURLWithPort("/mentions/") + query2.getId() + "/?languageCode=en&endDate=01-09-2018 12:30:00")

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(1))
                .body("id[0]", equalTo(mention2.getId()))
                .body("queryId[0]", equalTo(query2.getId()));
    }

    @Test
    public void viewMentionsOfQueryByIdWithDateAfter_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query2.getId() + "/?startDate=31-08-2018 16:00:00")

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(1))
                .body("id[0]", equalTo(mention2.getId()))
                .body("queryId[0]", equalTo(query2.getId()));
    }

    @Test
    public void viewMentionsOfQueryByIdWithDateAfterAndLanguageCode_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query2.getId() + "/?languageCode=en&startDate=31-08-2018 16:00:00")

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(1))
                .body("id[0]", equalTo(mention2.getId()))
                .body("queryId[0]", equalTo(query2.getId()));
    }

    @Test
    public void viewMentionsOfQueryByIdWithDateRange_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query2.getId()
                + "/?startDate=30-08-2018 10:30:00&endDate=01-09-2018 12:00:00")

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
                + "/?languageCode=en&startDate=30-08-2018 10:30:00&endDate=01-09-2018 12:00:00")

                .then().assertThat()
                .statusCode(200)
                .body("id", hasSize(1))
                .body("id[0]", equalTo(mention2.getId()))
                .body("queryId[0]", equalTo(query2.getId()));
    }

    @Test
    public void viewMentionsOfQueryByIdWithAuthorFilter_returnsValidMentions() {
        with().get(createURLWithPort("/mentions/") + query2.getId()
                + "/?author=testAuthor2")
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
                .body("exception",
                        equalTo(QueryExceptions.QueryNotFoundException.class.getName()));
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + API_EXTERNAL_PORT + uri;
    }

    private void addQueriesToDB() {
        datastore.save(query1);
        datastore.save(query2);
    }

    private void dropDB() {
        datastore.getDB().getCollection("savedQueries").drop();
        datastore.getDB().getCollection("savedMentions").drop();
    }

    private void addMentionsToDB() {
        datastore.save(mention1);
        datastore.save(mention2);
        datastore.save(mention3);
    }
}
