package dev.siroky.mvnc.rest;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = SearchResponseJsonDeserializer.class)
public record ArtifactSearchResponse(List<Artifact> artifacts) {}
