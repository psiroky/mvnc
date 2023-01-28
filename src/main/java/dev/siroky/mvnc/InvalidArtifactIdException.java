package dev.siroky.mvnc;

public class InvalidArtifactIdException extends RuntimeException {

    public InvalidArtifactIdException(String artifactId) {
        super("Invalid artifactId '" + artifactId + "'");
    }
}
