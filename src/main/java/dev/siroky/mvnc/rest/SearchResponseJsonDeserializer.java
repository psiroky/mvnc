package dev.siroky.mvnc.rest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SearchResponseJsonDeserializer extends StdDeserializer<ArtifactSearchResponse> {

    public SearchResponseJsonDeserializer() {
        this(null);
    }

    public SearchResponseJsonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ArtifactSearchResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);
        JsonNode responseNode = rootNode.get("response");
        List<Artifact> artifacts = new ArrayList<>();
        for (JsonNode doc : responseNode.get("docs")) {
            var groupId = doc.get("g").asText();
            var artifactId = doc.get("a").asText();
            var version = doc.get("v").asText();
            var timestamp = Instant.ofEpochMilli(doc.get("timestamp").asLong());
            artifacts.add(new Artifact(groupId, artifactId, version, timestamp));
        }
        return new ArtifactSearchResponse(artifacts);
    }
}
