package dev.siroky.mvnc.rest;

import java.util.List;

public record SearchResponseData(int numFound, List<ArtifactWithLatestVersion> docs) {
}
