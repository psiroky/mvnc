package dev.siroky.mvnc;

import javax.enterprise.inject.Default;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dev.siroky.mvnc.rest.Artifact;
import dev.siroky.mvnc.rest.ArtifactSearchResponse;
import dev.siroky.mvnc.rest.MavenCentralRestClient;
import dev.siroky.mvnc.rest.MavenCentralSearchRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

@Default
@CommandLine.Command(
        name = "mvnc",
        mixinStandardHelpOptions = true,
        versionProvider = PicocliVersionProvider.class,
        description = "Search Maven Central based on groupId and artifactId")
public class MvncCommand implements Runnable {
    private static final String COORDINATE_DELIMITER = ":";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy").withZone(ZoneId.systemDefault());

    @CommandLine.Option(
            names = {"-l", "--limit"},
            defaultValue = "20",
            description = "Maximum number of results to show")
    int limit;

    @CommandLine.Parameters(
            index = "0",
            description = "Maven artifact coordinates, e.g. org.apache.maven:maven-core or just maven-core")
    String artifactCoordinates;

    private final MavenCentralSearchRestClient mavenCentralSearchClient;
    private final MavenCentralRestClient mavenCentralClient;

    public MvncCommand(
            @RestClient MavenCentralSearchRestClient mavenCentralSearchClient,
            MavenCentralRestClient mavenCentralClient) {
        this.mavenCentralSearchClient = mavenCentralSearchClient;
        this.mavenCentralClient = mavenCentralClient;
    }

    @Override
    public void run() {
        if (artifactCoordinates.contains(COORDINATE_DELIMITER)) {
            String[] parts = artifactCoordinates.split(COORDINATE_DELIMITER);
            if (parts.length == 2) {
                runWithGroupIdAndArtifactId(parts[0], parts[1]);
            } else if (parts.length == 3) {
                runWithGroupIdArtifactIdAndVersion(parts[0], parts[1], parts[2]);
            } else {
                throw new InvalidArtifactCoordinatesException(artifactCoordinates);
            }
        } else {
            runWithArtifactId(artifactCoordinates);
        }
    }

    private void runWithGroupIdAndArtifactId(String groupId, String artifactId) {
        var sanitizedGroupId = sanitizeCoordinate(groupId);
        var sanitizedArtifactId = sanitizeCoordinate(artifactId);
        if (sanitizedGroupId.isBlank() || sanitizedArtifactId.isBlank()) {
            throw new InvalidArtifactCoordinatesException(groupId, artifactId);
        }
        searchByGroupIdAndArtifactId(sanitizedGroupId, sanitizedArtifactId);
    }

    private void runWithGroupIdArtifactIdAndVersion(String groupId, String artifactId, String version) {
        var sanitizedGroupId = sanitizeCoordinate(groupId);
        var sanitizedArtifactId = sanitizeCoordinate(artifactId);
        var sanitizedVersion = sanitizeCoordinate(version);
        if (sanitizedGroupId.isBlank() || sanitizedArtifactId.isBlank() || sanitizedVersion.isBlank()) {
            throw new InvalidArtifactCoordinatesException(groupId, artifactId, version);
        }
        fetchPomContent(groupId, artifactId, version);
    }

    private void fetchPomContent(String groupId, String artifactId, String version) {
        var pomContent = mavenCentralClient.fetchPom(groupId, artifactId, version);
        System.out.println(pomContent);
    }

    private void runWithArtifactId(String artifactId) {
        var sanitizedArtifactId = sanitizeCoordinate(artifactId);
        if (sanitizedArtifactId.isBlank()) {
            throw new InvalidArtifactIdException(artifactId);
        }
        searchByArtifactId(sanitizedArtifactId);
    }

    private void searchByArtifactId(String artifactId) {
        var searchTerm = "a:" + artifactId;
        ArtifactSearchResponse searchResponse = mavenCentralSearchClient.searchByTerm(searchTerm, limit);
        printArtifactTable(searchResponse.artifacts());
    }

    private String sanitizeCoordinate(String coordinate) {
        if (coordinate.startsWith("*")) {
            String sanitized;
            if (coordinate.length() == 1) {
                sanitized = "";
            } else {
                sanitized = coordinate.substring(1);
            }
            System.err.printf(
                    "Artifact coordinate cannot start with '*', using '%s' instead of '%s'.%n", sanitized, coordinate);
            return sanitized;
        } else {
            return coordinate;
        }
    }

    private void searchByGroupIdAndArtifactId(String groupId, String artifactId) {
        var searchTerm = "g:" + groupId + " AND " + "a:" + artifactId;
        ArtifactSearchResponse searchResponse = mavenCentralSearchClient.searchByTerm(searchTerm, limit);
        printArtifactTable(searchResponse.artifacts());
    }

    private void printArtifactTable(List<Artifact> artifacts) {
        var groupIdColumn = new TableColumn(
                "Group ID", artifacts.stream().map(Artifact::groupId).toList());
        var artifactIdColumn = new TableColumn(
                "Artifact ID", artifacts.stream().map(Artifact::artifactId).toList());
        var versionColumn = new TableColumn(
                "Version", artifacts.stream().map(Artifact::version).toList());
        var releasedOnColumn = new TableColumn(
                "Released on",
                artifacts.stream()
                        .map(a -> DATE_FORMATTER.format(a.timestamp()))
                        .toList());
        var formatter =
                new SimpleTabularFormatter(List.of(groupIdColumn, artifactIdColumn, versionColumn, releasedOnColumn));
        System.out.println(formatter.format());
    }
}
