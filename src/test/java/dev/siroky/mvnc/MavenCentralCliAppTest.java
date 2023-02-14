package dev.siroky.mvnc;

import java.util.Map;

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
    void searchExistingArtifactByArtifactId(QuarkusMainLauncher launcher) {
        // create mocked Maven Central Search endpoint
        var response = loadClassPathResource("/artifact-id-search_maven-core.json");
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/solrsearch/select")
                        .withQueryStringParameter("q", "a:maven-core"))
                .respond(httpRequest -> response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response));

        LaunchResult result = launcher.launch("maven-core");

        assertThat(result.exitCode()).isZero();
        assertThat(result.getOutput()).containsOnlyOnce("org.apache.maven");
        assertThat(result.getOutput()).containsOnlyOnce("io.tesla.maven");
        assertThat(result.getErrorOutput()).isEmpty();
    }

    @Test
    void searchExistingArtifactByArtifactId_stripLeadingAsterisk(QuarkusMainLauncher launcher) {
        // create mocked Maven Central Search endpoint
        var response = loadClassPathResource("/artifact-id-search_maven-core.json");
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/solrsearch/select")
                        .withQueryStringParameter("q", "a:maven-core"))
                .respond(httpRequest -> response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response));

        LaunchResult result = launcher.launch("*maven-core");

        assertThat(result.exitCode()).isZero();
        assertThat(result.getOutput()).containsOnlyOnce("org.apache.maven");
        assertThat(result.getOutput()).containsOnlyOnce("io.tesla.maven");
        assertThat(result.getErrorOutput())
                .contains("Artifact coordinate cannot start with '*', using 'maven-core' instead of '*maven-core'.");
    }

    @Test
    void searchExistingArtifact_withLimit(QuarkusMainLauncher launcher) {
        // create mocked Maven Central Search endpoint
        var response = loadClassPathResource("/artifact-id-search_maven-core_limit-1.json");
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/solrsearch/select")
                        .withQueryStringParameter("q", "a:maven-core")
                        .withQueryStringParameter("rows", "1"))
                .respond(httpRequest -> response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response));

        LaunchResult result = launcher.launch("maven-core", "-l", "1");

        assertThat(result.exitCode()).isZero();
        assertThat(result.getOutput()).containsOnlyOnce("org.apache.maven");
        assertThat(result.getOutput()).doesNotContain("io.tesla.maven");
        assertThat(result.getErrorOutput()).isEmpty();
    }

    @Test
    void searchNonExistingArtifact(QuarkusMainLauncher launcher) {
        // create mocked Maven Central Search endpoint
        var response = loadClassPathResource("/artifact-id-search_non-existing.json");

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/solrsearch/select")
                        .withQueryStringParameter("q", "a:non-existing"))
                .respond(httpRequest -> response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response));

        LaunchResult result = launcher.launch("non-existing");

        assertThat(result.exitCode()).isZero();
        assertThat(result.getOutput()).contains("Group ID     Artifact ID     Version     Released on");
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

    @Test
    void failWithBlankArtifactId(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("");

        assertThat(result.exitCode()).isNotZero();
        assertThat(result.getOutput()).isEmpty();
        assertThat(result.getErrorOutput()).contains("Invalid artifactId ''");
    }

    @Test
    void failWithBlankGroupId(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch(":test");

        assertThat(result.exitCode()).isNotZero();
        assertThat(result.getOutput()).isEmpty();
        assertThat(result.getErrorOutput()).contains("Invalid artifact coordinates ':test'");
    }

    @Test
    void searchArtifactByGroupIdAndArtifactId(QuarkusMainLauncher launcher) {
        // create mocked Maven Central Search endpoint
        var response = loadClassPathResource("/groupId-and-artifactId-search_maven-core.json");

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/solrsearch/select")
                        .withQueryStringParameter("q", "g:org.apache.maven AND a:maven-core")
                        .withQueryStringParameter("core", "gav"))
                .respond(httpRequest -> response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response));

        LaunchResult result = launcher.launch("org.apache.maven:maven-core");

        assertThat(result.exitCode()).isZero();
        assertThat(result.getOutput()).containsOnlyOnce("3.8.7");
        assertThat(result.getOutput()).containsOnlyOnce("4.0.0-alpha-3");
        assertThat(result.getErrorOutput()).isEmpty();
    }

    @Test
    void searchArtifactByGroupIdAndArtifactId_stripLeadingAsterisk(QuarkusMainLauncher launcher) {
        // create mocked Maven Central Search endpoint
        var response = loadClassPathResource("/groupId-and-artifactId-search_maven-core.json");

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/solrsearch/select")
                        .withQueryStringParameter("q", "g:org.apache.maven AND a:maven-core")
                        .withQueryStringParameter("core", "gav"))
                .respond(httpRequest -> response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response));

        LaunchResult result = launcher.launch("*org.apache.maven:*maven-core");

        assertThat(result.exitCode()).isZero();
        assertThat(result.getOutput()).containsOnlyOnce("3.8.7");
        assertThat(result.getOutput()).containsOnlyOnce("4.0.0-alpha-3");
        assertThat(result.getErrorOutput())
                .contains(
                        "Artifact coordinate cannot start with '*', using 'org.apache.maven' instead of '*org.apache.maven'.")
                .contains("Artifact coordinate cannot start with '*', using 'maven-core' instead of '*maven-core'.");
    }

    @Test
    void printPomContent(QuarkusMainLauncher launcher) {
        var response = loadClassPathResource("/quarkus-arc-2.16.1.Final.pom");

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/io/quarkus/quarkus-arc/2.16.1.Final/quarkus-arc-2.16.1.Final.pom"))
                .respond(httpRequest -> response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(response));

        LaunchResult result = launcher.launch("io.quarkus:quarkus-arc:2.16.1.Final");

        assertThat(result.exitCode()).isZero();
        assertThat(result.getOutput()).containsOnlyOnce("<artifactId>quarkus-arc</artifactId>");
        assertThat(result.getOutput()).containsOnlyOnce("<version>2.16.1.Final</version>");
        assertThat(result.getErrorOutput()).isEmpty();
    }

    @Test
    void printPomContent_notFound(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("org.non.existing:non-existing:1.0.0");

        assertThat(result.exitCode()).isNotZero();
        assertThat(result.getOutput()).isEmpty();
        assertThat(result.getErrorOutput()).contains("POM for 'org.non.existing:non-existing:1.0.0' not found");
    }

    @Test
    void showVersionShortOption(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("-V");

        assertThat(result.exitCode()).isZero();
        assertThat(result.getOutput()).contains("mvnc").doesNotContain("null");

        assertThat(result.getErrorOutput()).isEmpty();
    }

    @Test
    void showVersionLongOption(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("--version");

        assertThat(result.exitCode()).isZero();
        assertThat(result.getOutput()).contains("mvnc").doesNotContain("null");
        assertThat(result.getErrorOutput()).isEmpty();
    }

    @Test
    void showHelpShortOption(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("-h");

        assertThat(result.exitCode()).isZero();
        assertThat(result.getOutput()).contains("Usage: ");
        assertThat(result.getErrorOutput()).isEmpty();
    }

    @Test
    void showHelpLongOption(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("--help");

        assertThat(result.exitCode()).isZero();
        assertThat(result.getOutput()).contains("Usage: ");
        assertThat(result.getErrorOutput()).isEmpty();
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
            return Map.of(
                    "quarkus.rest-client.maven-central-search.url", "http://localhost:" + PORT,
                    "maven-central.url", "http://localhost:" + PORT);
        }
    }
}
