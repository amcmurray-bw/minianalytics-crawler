package amcmurray.bw.integrationTests;

import java.io.IOException;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.configuration.ShutdownStrategy;
import com.palantir.docker.compose.connection.waiting.HealthChecks;

public class IntegrationTestRule implements TestRule {

    public static final IntegrationTestRule INSTANCE = new IntegrationTestRule();
    private volatile boolean running = false;

    private static final String MONGO_CONTAINER_NAME = "mongodbtest";
    private static final int MONGO_INTERNAL_PORT = 27017;
    private static int MONGO_EXTERNAL_PORT;

    private static final String API_CONTAINER_NAME = "minianalyticsapitest";
    private static final int API_INTERNAL_PORT = 8081;
    private static int API_EXTERNAL_PORT;

    private static final String DATABASE_NAME = "minianalytics";
    private static Datastore datastore;

    private static DockerComposeRule docker = DockerComposeRule.builder()
            .file("src/test/resources/docker-compose-tests.yml")
            .projectName(ProjectName.random())
            .waitingForService(API_CONTAINER_NAME, HealthChecks.toHaveAllPortsOpen())
            .saveLogsTo("target/docker-compose-test-logs")
            .shutdownStrategy(ShutdownStrategy.GRACEFUL)
            .build();

    private IntegrationTestRule() {
        // Start things running early
        new Thread(this::start).start();
        // Stop things running late
        Runtime.getRuntime().addShutdownHook(new Thread(this::end));
    }

    private static void initialize() {
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

    @Override
    public Statement apply(Statement statement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // Make sure we're started, and block on starting
                start();
                statement.evaluate();
            }
        };
    }

    private synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        //run the initialise method
        try {
            docker.before();
            initialize();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void end() {
        docker.after();
    }

    public int getApiExternalPort() {
        return API_EXTERNAL_PORT;
    }

    public Datastore getDatastore() {
        return datastore;
    }
}
