package dev.siroky.mvnc;

import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@QuarkusMainTest
@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = MavenCentralCliAppTest.PORT)
@TestProfile(MavenCentralCliAppTest.TestProfile.class)
class MavenCentralCliAppTest {
    static final int PORT = 59999;

    private final ClientAndServer mockServerClient;

    public MavenCentralCliAppTest(ClientAndServer mockServerClient) {
        this.mockServerClient = mockServerClient;
    }

    @Test
    void searchExistingArtifact(QuarkusMainLauncher launcher) {
        // create mocked Maven Central Search endpoint
        var response = loadClassPathResource("/artifact-id-search_maven-core.json");
        mockServerClient.when(request()
                        .withMethod("GET")
                        .withPath("/solrsearch/select")
                        .withQueryStringParameter("q", "a:maven-core"))
                .respond(httpRequest -> response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response));

        LaunchResult result = launcher.launch("maven-core");

        assertThat(result.exitCode()).isZero();
        assertThat(result.getOutput()).isEqualTo("""
                org.apache.maven:maven-core:4.0.0-alpha-3
                io.tesla.maven:maven-core:3.1.2""");
        assertThat(result.getErrorOutput()).isEmpty();
    }

    @Test
    void searchNonExistingArtifact(QuarkusMainLauncher launcher) {
        // create mocked Maven Central Search endpoint
        var response = loadClassPathResource("/artifact-id-search_non-existing.json");

        mockServerClient.when(request()
                        .withMethod("GET")
                        .withPath("/solrsearch/select")
                        .withQueryStringParameter("q", "a:non-existing"))
                .respond(httpRequest -> response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response));


        LaunchResult result = launcher.launch("non-existing");

        assertThat(result.exitCode()).isZero();
        assertThat(result.getOutput()).isEmpty();
        assertThat(result.getErrorOutput()).isEmpty();
    }

    @Test
    void failWithoutAnyArgument(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch();

        assertThat(result.exitCode()).isNotZero();
        assertThat(result.getOutput()).isEmpty();
        assertThat(result.getErrorOutput()).contains("Missing required parameter");
        assertThat(result.getErrorOutput()).contains("Usage:");
    }

    private String loadClassPathResource(String classpath) {
        try (var resourceStream = getClass().getResourceAsStream(classpath)) {
            if (resourceStream == null) {
                throw new IllegalArgumentException("Classpath resource '" + classpath + "' does not exist.");
            }
            return new String(resourceStream.readAllBytes());
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot load classpath resource '" + classpath + "'", e);
        }
    }


    public static class TestProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.rest-client.maven-central-search.url", "http://localhost:" + PORT);
        }
    }
}