package dev.siroky.mvnc.rest;

public class PomNotFoundException extends MavenCentralClientException {

    public PomNotFoundException(String groupId, String artifactId, String version) {
        super(String.format("POM for '%s:%s:%s' not found", groupId, artifactId, version));
    }
}
