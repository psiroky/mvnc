package dev.siroky.mvnc;

public class InvalidArtifactCoordinatesException extends RuntimeException {
    public InvalidArtifactCoordinatesException(String artifactCoordinates) {
        super("Invalid artifact coordinates '" + artifactCoordinates + "'");
    }
}
