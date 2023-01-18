package dev.siroky.mvnc;

import dev.siroky.mvnc.rest.Artifact;
import dev.siroky.mvnc.rest.MavenCentralRestClient;
import dev.siroky.mvnc.rest.ArtifactSearchResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import javax.enterprise.inject.Default;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Default
@CommandLine.Command(name = "mvnc", mixinStandardHelpOptions = true, versionProvider = PicocliVersionProvider.class,
        description = "Search Maven Central based on groupId and artifactId")
public class ListArtifactsCommand implements Runnable {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy")
            .withZone(ZoneId.systemDefault());

    @CommandLine.Option(names = {"-l", "--limit"}, defaultValue = "20", description = "Maximum number of results to show")
    int limit;

    @CommandLine.Parameters(index = "0", description = "Maven artifact coordinates, e.g. org.apache.maven:maven-core or just maven-core")
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
            searchByArtifactId(parts[0]);
        } else {
            searchByGroupIdAndArtifactId(parts[0], parts[1]);
        }
    }

    private void searchByArtifactId(String artifactId) {
        var searchTerm = "a:" + artifactId;
        ArtifactSearchResponse searchResponse = mavenCentralClient.searchByTerm(searchTerm, limit);
        printArtifactTable(searchResponse.artifacts());
    }

    private void searchByGroupIdAndArtifactId(String groupId, String artifactId) {
        var searchTerm = "g:" + groupId + " AND " + "a:" + artifactId;
        ArtifactSearchResponse searchResponse = mavenCentralClient.searchByTerm(searchTerm, limit);
        printArtifactTable(searchResponse.artifacts());

    }

    private void printArtifactTable(List<Artifact> artifacts) {
        var groupIdColumn = new TableColumn("Group ID", artifacts.stream().map(Artifact::groupId).toList());
        var artifactIdColumn = new TableColumn("Artifact ID", artifacts.stream().map(Artifact::artifactId).toList());
        var versionColumn = new TableColumn("Version", artifacts.stream().map(Artifact::version).toList());
        var releasedOnColumn = new TableColumn("Released on", artifacts.stream().map(a -> DATE_FORMATTER.format(a.timestamp())).toList());
        var formatter = new SimpleTabularFormatter(List.of(groupIdColumn, artifactIdColumn, versionColumn, releasedOnColumn));
        System.out.println(formatter.format());
    }
}
