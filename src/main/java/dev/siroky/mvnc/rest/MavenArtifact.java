package dev.siroky.mvnc.rest;

public record MavenArtifact(
        String g,
        String a,
        String latestVersion) {
}
