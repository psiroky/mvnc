package dev.siroky.mvnc;

import dev.siroky.mvnc.rest.Artifact;
import dev.siroky.mvnc.rest.ArtifactVersionsResponse;
import dev.siroky.mvnc.rest.ArtifactWithLatestVersion;
import dev.siroky.mvnc.rest.MavenCentralRestClient;
import dev.siroky.mvnc.rest.SearchResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import javax.enterprise.inject.Default;

@Default
@CommandLine.Command(name = "mvnc", mixinStandardHelpOptions = true, versionProvider = PicocliVersionProvider.class,
        description = "Search Maven Central based on groupId and artifactId")
public class MainCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "Maven artifact coordinates, e.g. org.apache.maven:maven-core or just maven-core")
    String artifactCoordinates;

    private final MavenCentralRestClient mavenCentralClient;

    public MainCommand(@RestClient MavenCentralRestClient mavenCentralClient) {
        this.mavenCentralClient = mavenCentralClient;
    }

    @Override
    public void run() {
        String[] parts = artifactCoordinates.split(":");
        if (parts.length == 0 || parts.length > 2) {
            throw new IllegalArgumentException("Invalid artifact coordinates '" + artifactCoordinates + "'");
        }
        if (parts.length == 1) {
            handleArtifactSearch(parts[0]);
        } else {
            handleArtifactVersionsFetch(parts[0], parts[1]);
        }
    }

    private void handleArtifactSearch(String artifactId) {
        var searchTerm = "a:" + artifactId;
        SearchResponse searchResponse = mavenCentralClient.searchByTerm(searchTerm);

        for (Artifact artifact : searchResponse.artifacts()) {
            System.out.println(artifact.groupId() + ":" + artifact.artifactId() + ":" + artifact.version());
        }
    }

    private void handleArtifactVersionsFetch(String groupId, String artifactId) {
        var searchTerm = "g:" + groupId + " AND " + "a:" + artifactId;
        ArtifactVersionsResponse artifactVersionsResponse = mavenCentralClient.fetchArtifactVersions(searchTerm);
        System.out.println("Group ID: " + groupId + ", artifact ID: " + artifactId);
        System.out.println("Versions (total: " + artifactVersionsResponse.total() + "): ");
        for (Artifact artifact : artifactVersionsResponse.artifacts()) {
            System.out.println(artifact.version());
        }
    }
}
