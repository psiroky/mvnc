package dev.siroky.mvnc.rest;

import java.util.List;

public record ArtifactVersionsResponseData(int numFound, List<Artifact> docs) {
}
