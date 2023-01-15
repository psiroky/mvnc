package dev.siroky.mvnc;

import dev.siroky.mvnc.rest.MavenArtifact;
import dev.siroky.mvnc.rest.MavenCentralRestClient;
import dev.siroky.mvnc.rest.SearchResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import javax.enterprise.inject.Default;

@Default
@CommandLine.Command(name = "mvnc")
public class ListArtifactsCommand implements Runnable {

    @CommandLine.Parameters(index = "0")
    String artifactCoordinates;

    private final MavenCentralRestClient mavenCentralClient;

    public ListArtifactsCommand(@RestClient MavenCentralRestClient mavenCentralClient) {
        this.mavenCentralClient = mavenCentralClient;
    }

    @Override
    public void run() {
        SearchResponse searchResponse = mavenCentralClient.searchByTerm("a:" + artifactCoordinates);

        for (MavenArtifact doc : searchResponse.response().docs()) {
            System.out.println(doc.g() + ":" + doc.a() + ":" + doc.latestVersion());
        }
    }
}
