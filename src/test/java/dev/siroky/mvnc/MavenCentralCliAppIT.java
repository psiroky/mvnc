package dev.siroky.mvnc;

import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import org.mockserver.integration.ClientAndServer;

@QuarkusMainIntegrationTest
class MavenCentralCliAppIT extends MavenCentralCliAppTest {

    public MavenCentralCliAppIT(ClientAndServer client) {
        super(client);
    }
}
