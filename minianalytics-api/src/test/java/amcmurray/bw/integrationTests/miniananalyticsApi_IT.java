package amcmurray.bw.integrationTests;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;

import amcmurray.bw.QueryRequestDTO;
import amcmurray.bw.exceptions.QueryExceptions;
import amcmurray.bw.twitterdomainobjects.Query;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Category(IntegrationTest.class)
public class miniananalyticsApi_IT {

    //fields for mongo
    private static final String MONGO_CONTAINER_NAME = "mongodbtest";
    private static final int MONGO_INTERNAL_PORT = 27017;
    private static int MONGO_EXTERNAL_PORT;

    private static final String API_CONTAINER = "minianalyticsapitest";
    private static final int API_INTERNAL_PORT = 8081;
    private static int API_EXTERNAL_PORT;

    private static final String DATABASE_NAME = "minianalytics";
    private static Datastore datastore;

    private final Query query = new Query(0, "test search");
    private final Query anotherQuery = new Query(1, "another test search");


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
        DockerPort apiService = docker.containers()
                .container(API_CONTAINER)
                .port(API_INTERNAL_PORT);
        API_EXTERNAL_PORT = apiService.getExternalPort();

        //getting external port for Mongo
        DockerPort mongoService = docker.containers()
                .container(MONGO_CONTAINER_NAME)
                .port(MONGO_INTERNAL_PORT);

        MONGO_EXTERNAL_PORT = mongoService.getExternalPort();

        //using morphia to create new datastore
        final Morphia morphia = new Morphia();
        morphia.mapPackage("amcmurray.bw.twitterdomainobjects");
        //datastore uses mongo instance
        datastore = morphia.createDatastore(new MongoClient("localhost", MONGO_EXTERNAL_PORT), DATABASE_NAME);
        datastore.ensureIndexes();
    }

    @Before
    public void setup() {
        addQueriesToDB();
    }

    @After
    public void tearDown() {
        removeQueriesFromDB();
    }

    @Test
    public void viewQueryById_getsExpectedQuery() {

        Response response = with()
                .get(createURLWithPort("/queries/0"));

        response.then().assertThat()
                .statusCode(200)
                .body("id", equalTo(query.getId()))
                .body("text", equalTo(query.getText()));
    }

    @Test
    public void viewNonexistentQueryById_throwsQueryNotFoundException() {

        Response response = with()
                .get(createURLWithPort("/queries/-1"));

        response.then().assertThat()
                .statusCode(404)
                .body("exception",
                        equalTo(QueryExceptions.QueryNotFoundException.class.getName()));
    }

    @Test
    public void viewMultipleQueries_returnsMultiple() {

        Response response = with()
                .get(createURLWithPort("/queries"));

        response.then().assertThat()
                .statusCode(200)
                .body("id", hasSize(2))
                .body("id[0]", equalTo(query.getId()))
                .body("text[0]", equalTo(query.getText()))
                .body("id[1]", equalTo(anotherQuery.getId()))
                .body("text[1]", equalTo(anotherQuery.getText()));
    }

    @Test
    public void addNewQuery_returnsExpectedQuery() {

        QueryRequestDTO queryRequestDTO = new QueryRequestDTO("new search");

        Response response = with()
                .contentType(ContentType.JSON)
                .body(queryRequestDTO)
                .when()
                .post((createURLWithPort("/query")));

        response.then().assertThat()
                .statusCode(200)
                .body("id", equalTo(anotherQuery.getId() + 1))
                .body("text", equalTo(queryRequestDTO.getSearch()));
    }

    @Test
    public void addNewQueryWithBlankSearch_throwsQuerySearchNullException() {

        QueryRequestDTO queryRequestDTO = new QueryRequestDTO("");

        Response response = with()
                .contentType(ContentType.JSON)
                .body(queryRequestDTO)
                .when()
                .post((createURLWithPort("/query")));

        response.then().assertThat()
                .statusCode(400)
                .body("exception",
                        equalTo(QueryExceptions.QuerySearchNullException.class.getName()));
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + API_EXTERNAL_PORT + uri;
    }

    private void addQueriesToDB() {
        datastore.save(query);
        datastore.save(anotherQuery);
    }

    private void removeQueriesFromDB() {
        datastore.delete(query);
        datastore.delete(anotherQuery);
    }
}
