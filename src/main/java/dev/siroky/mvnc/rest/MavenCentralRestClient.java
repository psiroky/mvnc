package dev.siroky.mvnc.rest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Simple HTTP client for <a href="https://repo1.maven.org/">Maven Central</a>.
 * <p>
 * It uses the standard Java HttpClient since the path in the URL is dynamic (e.g. groupId has unknown number of parts),
 * and thus the generated client via @GenerateRestClient would not work as expected.
 */
@ApplicationScoped
public class MavenCentralRestClient {

    private final String baseUrl;
    private final HttpClient httpClient;

    public MavenCentralRestClient(@ConfigProperty(name = "maven-central.url") String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    public String fetchPom(String groupId, String artifactId, String version) {
        var pomFilename = artifactId + "-" + version + ".pom";
        var pomUri = URI.create(
                String.format("%s/%s/%s/%s/%s", baseUrl, groupId.replace(".", "/"), artifactId, version, pomFilename));
        var request = HttpRequest.newBuilder().GET().uri(pomUri).build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            var responseCode = response.statusCode();
            switch (responseCode) {
                case 200:
                    return response.body();
                case 404:
                    throw new PomNotFoundException(groupId, artifactId, version);
                default:
                    throw new MavenCentralClientException("Failed to fetch POM, got HTTP code " + responseCode);
            }
        } catch (IOException e) {
            throw new MavenCentralClientException("Failed to fetch POM: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MavenCentralClientException("Interrupted when fetching POM: " + e.getMessage(), e);
        }
    }
}
