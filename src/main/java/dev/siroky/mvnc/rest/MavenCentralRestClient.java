package dev.siroky.mvnc.rest;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/solrsearch/select")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "maven-central-search")
public interface MavenCentralRestClient {

    @GET
    SearchResponse searchByTerm(@QueryParam("q") String term, @QueryParam("rows") int rows);

    @GET
    @ClientQueryParam(name = "core", value = "gav")
    ArtifactVersionsResponse fetchArtifactVersions(@QueryParam("q") String term, @QueryParam("rows") int rows);
}