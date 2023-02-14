package dev.siroky.mvnc;

public class InvalidArtifactCoordinatesException extends RuntimeException {

    public InvalidArtifactCoordinatesException(String artifactCoordinates) {
        super("Invalid artifact coordinates '" + artifactCoordinates + "'");
    }

    public InvalidArtifactCoordinatesException(String groupId, String artifactId, String version) {
        this(groupId + ":" + artifactId + ":" + version);
    }

    public InvalidArtifactCoordinatesException(String groupId, String artifactId) {
        this(groupId + ":" + artifactId);
    }
}
