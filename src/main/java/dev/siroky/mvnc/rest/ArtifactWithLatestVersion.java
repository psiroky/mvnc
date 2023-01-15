package dev.siroky.mvnc.rest;

public record ArtifactWithLatestVersion(
        String g,
        String a,
        String latestVersion) {
}
