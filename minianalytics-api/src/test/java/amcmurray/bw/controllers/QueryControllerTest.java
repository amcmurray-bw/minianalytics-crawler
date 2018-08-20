package amcmurray.bw.controllers;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import amcmurray.bw.QueryApplication;
import amcmurray.bw.QueryRequestDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = QueryApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueryControllerTest {


    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate;
    HttpHeaders headers;
    HttpEntity<QueryRequestDTO> entity;
    ResponseEntity<String> response;


    @Before
    public void setup() {

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate = new TestRestTemplate();
        entity = new HttpEntity<>(null, headers);
    }

    @Test
    public void addFirstQuery_returnsExpectedQuery() {

        addAQueryToDB();

        assertEquals("{\"id\":0,\"text\":\"test string\"}", response.getBody());
    }

    @Test
    public void viewQueryById_returnsCorrectQuery() {

        //add a second query
        addAQueryToDB();

        response =
                restTemplate.exchange(
                        createURLWithPort("/queries/1"),
                        HttpMethod.GET, entity, String.class);

        assertEquals("{\"id\":1,\"text\":\"test string\"}", response.getBody());
    }


    @Test
    public void viewQueryThatDoesNotExist_throwsQueryNotFoundError() {

        response =
                restTemplate.exchange(
                        createURLWithPort("/queries/-1"),
                        HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void viewMultipleQueries() {

        //two queries have been added throughout testing
        //expect two returned
        response =
                restTemplate.exchange(
                        createURLWithPort("/queries"),
                        HttpMethod.GET, entity, String.class);

        assertEquals(
                "[{\"id\":0,\"text\":\"test string\"},"
                        + "{\"id\":1,\"text\":\"test string\"}]",
                response.getBody());

    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private void addAQueryToDB() {

        QueryRequestDTO query = new QueryRequestDTO("test string");
        HttpEntity<QueryRequestDTO> entity = new HttpEntity<>(query, headers);

        response =
                restTemplate.exchange(
                        createURLWithPort("/query"),
                        HttpMethod.POST, entity, String.class);
    }
}
