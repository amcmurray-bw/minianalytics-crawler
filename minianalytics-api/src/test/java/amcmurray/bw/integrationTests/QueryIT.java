package amcmurray.bw.integrationTests;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import amcmurray.bw.QueryRequestDTO;
import amcmurray.bw.twitterdomainobjects.Query;
import io.restassured.http.ContentType;

@Category(IntegrationTest.class)
public class QueryIT {

    @ClassRule
    public static IntegrationTestRule rule = IntegrationTestRule.INSTANCE;

    private final Query query1 = new Query(0, "test query", "en");
    private final Query query2 = new Query(1, "another search", "");

    @Before
    public void setup() {
        addQueriesToDB();
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
                .body("message",
                        equalTo("Query not found."));
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
        QueryRequestDTO queryRequestDTO = new QueryRequestDTO("new search", "pl");

        with().contentType(ContentType.JSON)
                .body(queryRequestDTO)
                .when()
                .post((createURLWithPort("/queries")))

                .then().assertThat()
                .statusCode(200)
                .body("id", equalTo(query2.getId() + 1))
                .body("text", equalTo(queryRequestDTO.getSearch()))
                .body("language", equalTo(queryRequestDTO.getLanguage()));
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
                .body("message",
                        equalTo("Query text must not be empty!"));
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
    }
}
