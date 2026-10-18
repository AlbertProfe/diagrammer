package dev.diagrammer;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * The Render probe is unauthenticated and reports status only. Login still
 * covers the rest of the app. This uses the embedded server because Vaadin's
 * servlet mapping is what would otherwise swallow {@code /actuator/health}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class HealthEndpointTest {

    @LocalServerPort
    private int port;

    @Test
    void healthIsPublicAndHidesDetails() throws Exception {
        HttpResponse<String> health = get("/actuator/health");

        assertThat(health.statusCode()).isEqualTo(200);
        assertThat(health.headers().firstValue("content-type").orElse(""))
                .contains("application/vnd.spring-boot.actuator.v3+json");
        assertThat(health.body().trim()).isEqualTo("{\"status\":\"UP\"}");
    }

    @Test
    void otherActuatorEndpointsAreNotExposed() throws Exception {
        assertThat(get("/actuator/info").statusCode()).isNotEqualTo(200);
        assertThat(get("/actuator/env").statusCode()).isNotEqualTo(200);
        assertThat(get("/actuator").statusCode()).isNotEqualTo(200);
    }

    @Test
    void homeStillRequiresLogin() throws Exception {
        HttpResponse<String> home = get("/");

        assertThat(home.statusCode()).isBetween(300, 399);
        assertThat(home.headers().firstValue("location").orElse("")).contains("/login");
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                .GET()
                .build();
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }
}
