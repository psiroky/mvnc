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
        description = "List Maven artifacts based on groupId and artifactId, and their versions")
public class ListArtifactsCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "Maven artifact coordinates, e.g. org.apache.maven:maven-core")
    String artifactCoordinates;

    private final MavenCentralRestClient mavenCentralClient;

    public ListArtifactsCommand(@RestClient MavenCentralRestClient mavenCentralClient) {
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

        for (ArtifactWithLatestVersion artifact : searchResponse.response().docs()) {
            System.out.println(artifact.g() + ":" + artifact.a() + ":" + artifact.latestVersion());
        }
    }

    private void handleArtifactVersionsFetch(String groupId, String artifactId) {
        var searchTerm = "g:" + groupId + " AND " + "a:" + artifactId;
        ArtifactVersionsResponse artifactVersionsResponse = mavenCentralClient.fetchArtifactVersions(searchTerm);
        System.out.println("Group ID: " + groupId + ", artifact ID: " + artifactId);
        System.out.println("Versions (total: " + artifactVersionsResponse.response().numFound() + "): ");
        for (Artifact artifact : artifactVersionsResponse.response().docs()) {
            System.out.println(artifact.v());
        }
    }
}
