package dev.siroky.mvnc;

import dev.siroky.mvnc.rest.Artifact;
import dev.siroky.mvnc.rest.ArtifactVersionsResponse;
import dev.siroky.mvnc.rest.MavenCentralRestClient;
import dev.siroky.mvnc.rest.SearchResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import javax.enterprise.inject.Default;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Default
@CommandLine.Command(name = "mvnc", mixinStandardHelpOptions = true, versionProvider = PicocliVersionProvider.class,
        description = "Search Maven Central based on groupId and artifactId")
public class MainCommand implements Runnable {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy")
            .withZone(ZoneId.systemDefault());

    @CommandLine.Option(names = {"-l", "--limit"}, defaultValue = "20", description = "Maximum number of values")
    int limit;

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
        SearchResponse searchResponse = mavenCentralClient.searchByTerm(searchTerm, limit);

        List<Artifact> artifacts = searchResponse.artifacts();
        var groupIdColumn = new TableColumn("Group ID", artifacts.stream().map(Artifact::groupId).toList());
        var artifactIdColumn = new TableColumn("Artifact ID", artifacts.stream().map(Artifact::artifactId).toList());
        var versionColumn = new TableColumn("Version", artifacts.stream().map(Artifact::version).toList());
        var releasedOnColumn = new TableColumn("Released on", artifacts.stream().map(a -> DATE_FORMATTER.format(a.timestamp())).toList());
        var formatter = new SimpleTabularFormatter(List.of(groupIdColumn, artifactIdColumn, versionColumn, releasedOnColumn));
        System.out.println(formatter.format());
    }

    private void handleArtifactVersionsFetch(String groupId, String artifactId) {
        var searchTerm = "g:" + groupId + " AND " + "a:" + artifactId;
        ArtifactVersionsResponse artifactVersionsResponse = mavenCentralClient.fetchArtifactVersions(searchTerm, limit);

        List<Artifact> artifacts = artifactVersionsResponse.artifacts();
        var versionColumn = new TableColumn("Version", artifacts.stream().map(Artifact::version).toList());
        var releasedOnColumn = new TableColumn("Released on", artifacts.stream().map(a -> DATE_FORMATTER.format(a.timestamp())).toList());
        var formatter = new SimpleTabularFormatter(List.of(versionColumn, releasedOnColumn));
        System.out.println(formatter.format());
    }
}
