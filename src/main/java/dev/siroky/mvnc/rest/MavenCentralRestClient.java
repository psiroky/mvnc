package dev.siroky.mvnc.rest;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@RegisterRestClient(configKey = "maven-central-search")
public interface MavenCentralRestClient {

    @GET
    @Path("/solrsearch/select")
    SearchResponse searchByTerm(@QueryParam("q") String term);
}
