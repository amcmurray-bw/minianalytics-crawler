package amcmurray.bw.integrationTests;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;

@Category(IntegrationTest.class)
public class miniananalyticsApi_IT {

    //fields for mongo
    private static final String MONGO_CONTAINER_NAME = "mongodbtest";
    private static final int MONGO_INTERNAL_PORT = 27017;
    private static int MONGO_EXTERNAL_PORT;

    private static final String API_CONTAINER = "minianalyticsapitest";
    private static final int API_INTERNAL_PORT = 8081;
    private static int API_EXTERNAL_PORT;


    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
            .file("src/test/resources/docker-compose-tests.yml")
            .projectName(ProjectName.random())
            .waitingForService(API_CONTAINER, HealthChecks.toHaveAllPortsOpen())
            .saveLogsTo("target/docker-compose-logs")
            .build();

    @BeforeClass
    public static void initialize() {
        DockerPort apiService = docker.containers()
                .container(API_CONTAINER)
                .port(API_INTERNAL_PORT);

        API_EXTERNAL_PORT = apiService.getExternalPort();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Before
    public void setup() {
    }


    @Test
    public void viewQueryThatDoesNotExist_throwsQueryNotFoundError() {
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + API_EXTERNAL_PORT + uri;
    }

}
