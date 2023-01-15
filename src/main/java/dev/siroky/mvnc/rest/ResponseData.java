package dev.siroky.mvnc.rest;

import java.util.List;

public record ResponseData(int numFound, List<MavenArtifact> docs) {
}
