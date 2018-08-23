package amcmurray.bw.integrationTests;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.time.Instant;
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

    //fields for mongo
    private static final String MONGO_CONTAINER_NAME = "mongodbtest";
    private static final int MONGO_INTERNAL_PORT = 27017;
    private static int MONGO_EXTERNAL_PORT;

    private static final String API_CONTAINER = "minianalyticsapitest";
    private static final int API_INTERNAL_PORT = 8081;
    private static int API_EXTERNAL_PORT;

    private static final String DATABASE_NAME = "minianalytics";
    private static Datastore datastore;

    private final Date testDate = Date.from(Instant.now());
    private final String testDateMentionDTO = ZonedDateTime.ofInstant(testDate.toInstant(), ZoneId.of("UTC"))
            .format(DateTimeFormatter.ofPattern("dd:MM:YYYY HH:mm:ss z Z"));
    private final Query query1 = new Query(0, "test query1");
    private final Query query2 = new Query(1, "another search");

    private final Mention mention1 = new Mention("123abc", 0, MentionType.TWITTER,
            "this is a mention of a test query1", testDate, "en", 0);
    private final Mention mention2 = new Mention("456def", 1, MentionType.TWITTER,
            "this is a mention of another search", testDate, "en", 0);
    private final Mention mention3 = new Mention("789hij", 1, MentionType.TWITTER,
            "this is a mention of another  search", testDate, "en", 0);


    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
            .file("src/test/resources/docker-compose-tests.yml")
            .projectName(ProjectName.random())
            .waitingForService(API_CONTAINER, HealthChecks.toHaveAllPortsOpen())
            .saveLogsTo("target/docker-compose-test-logs")
            .shutdownStrategy(ShutdownStrategy.GRACEFUL)
            .build();

    @BeforeClass
    public static void initialize() {
        //getting external port for API service
        API_EXTERNAL_PORT = docker.containers()
                .container(API_CONTAINER)
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

        with()
                .get(createURLWithPort("/queries/0"))

                .then().assertThat()
                .statusCode(200)
                .body("id", equalTo(query1.getId()))
                .body("text", equalTo(query1.getText()));
    }

    @Test
    public void viewNonexistentQueryById_throwsQueryNotFoundException() {

        with()
                .get(createURLWithPort("/queries/-1"))

                .then().assertThat()
                .statusCode(404)
                .body("exception",
                        equalTo(QueryExceptions.QueryNotFoundException.class.getName()));
    }

    @Test
    public void viewMultipleQueries_returnsMultiple() {

        with()
                .get(createURLWithPort("/queries"))

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

        QueryRequestDTO queryRequestDTO = new QueryRequestDTO("new search");

        with()
                .contentType(ContentType.JSON)
                .body(queryRequestDTO)
                .when()
                .post((createURLWithPort("/query")))

                .then().assertThat()
                .statusCode(200)
                .body("id", equalTo(query2.getId() + 1))
                .body("text", equalTo(queryRequestDTO.getSearch()));
    }

    @Test
    public void addNewQueryWithBlankSearch_throwsQuerySearchNullException() {

        QueryRequestDTO queryRequestDTO = new QueryRequestDTO("");

        with()
                .contentType(ContentType.JSON)
                .body(queryRequestDTO)
                .when()
                .post((createURLWithPort("/query")))

                .then().assertThat()
                .statusCode(400)
                .body("exception",
                        equalTo(QueryExceptions.QuerySearchNullException.class.getName()));
    }

    @Test
    public void viewMentionsOfQueryById_returnsValidMentions() {

        with()
                .get(createURLWithPort("/mentions/") + query1.getId())

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
    public void viewAllMentions_returnsValidMentions() {

        with()
                .get(createURLWithPort("/mentions"))

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

        with()
                .get(createURLWithPort("/mentions/-1"))

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
        datastore.getMongo().getDatabase(DATABASE_NAME).drop();
    }

    private void addMentionsToDB() {
        datastore.save(mention1);
        datastore.save(mention2);
        datastore.save(mention3);
    }
}
