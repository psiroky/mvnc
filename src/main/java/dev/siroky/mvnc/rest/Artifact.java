package dev.siroky.mvnc.rest;

public record Artifact(
        String groupId,
        String artifactId,
        String version) {
}
