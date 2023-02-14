package dev.siroky.mvnc.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * See <a href="https://central.sonatype.org/search/rest-api-guide/">Maven Central Search API guide</a> for information
 * about the API itself and how to use it.
 */
@Path("/solrsearch/select")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "maven-central-search")
@Timeout(2000)
@Retry(maxRetries = 3, delay = 100)
public interface MavenCentralSearchRestClient {

    @GET
    @ClientQueryParam(name = "core", value = "gav")
    ArtifactSearchResponse searchByTerm(@QueryParam("q") String term, @QueryParam("rows") int rows);
}
