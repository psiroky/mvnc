package dev.siroky.mvnc.rest;

import java.time.Instant;

public record Artifact(String groupId, String artifactId, String version, Instant timestamp) {}
