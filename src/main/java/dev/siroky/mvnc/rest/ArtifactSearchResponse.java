package dev.siroky.mvnc.rest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = SearchResponseJsonDeserializer.class)
public record ArtifactSearchResponse(List<Artifact> artifacts) {
}
