package dev.siroky.mvnc.rest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = ArtifactVersionsResponseJsonDeserializer.class)
public record ArtifactVersionsResponse(int total, List<Artifact> artifacts) {
}
