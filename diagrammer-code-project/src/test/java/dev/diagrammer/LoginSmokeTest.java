package dev.diagrammer;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Exercises login against the embedded server. MockMvc does not run Vaadin's
 * servlet initializer, so the security matchers cannot see Hilla routes there.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginSmokeTest {

    private static final Pattern META =
            Pattern.compile("<meta\\s+name=\"(_csrf|_csrf_header)\"\\s+content=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);

    @LocalServerPort
    private int port;

    private HttpClient client;

    @BeforeEach
    void newClient() {
        client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .cookieHandler(new CookieManager())
                .build();
    }

    @Test
    void unauthenticatedRequestRedirectsToLogin() throws Exception {
        HttpResponse<String> home = send(get("/"));
        assertThat(home.statusCode()).isBetween(300, 399);
        assertThat(location(home)).contains("/login");

        HttpResponse<String> login = awaitFrontend("/login");
        assertThat(login.statusCode()).isEqualTo(200);
        assertThat(login.body()).contains("Diagrammer");
    }

    @Test
    void validCredentialsLogInAndWrongOnesStayLoggedOut() throws Exception {
        HttpResponse<String> loggedIn = send(form("/login", "albert", "diagrammer", csrf()));
        assertThat(loggedIn.statusCode()).isBetween(300, 399);
        assertThat(location(loggedIn)).doesNotContain("error");

        HttpResponse<String> user = send(endpoint(csrfFrom("/")));
        assertThat(user.statusCode()).isEqualTo(200);
        assertThat(user.body()).contains("\"userId\":\"u1\"");
        assertThat(user.body()).contains("\"username\":\"albert\"");
        assertThat(user.body()).contains("\"displayName\":\"Albert\"");

        HttpClient stranger = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .cookieHandler(new CookieManager())
                .build();
        HttpResponse<String> rejected = stranger.send(form("/login", "albert", "wrong", csrf(stranger)), body());
        assertThat(rejected.statusCode()).isBetween(300, 399);
        assertThat(location(rejected)).contains("error");

        HttpResponse<String> stillOut = stranger.send(endpoint(csrf(stranger)), body());
        assertThat(stillOut.statusCode()).isEqualTo(401);
    }

    @Test
    void protectedEndpointReturns401AfterLogout() throws Exception {
        CsrfToken token = csrfFrom("/login");
        HttpResponse<String> loggedIn = send(form("/login", "albert", "diagrammer", token));
        assertThat(loggedIn.statusCode()).isBetween(300, 399);

        CsrfToken afterLogin = csrfFrom("/");
        HttpResponse<String> logout = send(postForm("/logout", afterLogin.headerName(), afterLogin.token()));
        assertThat(logout.statusCode()).isBetween(300, 399);
        assertThat(location(logout)).contains("/login");

        HttpResponse<String> user = send(endpoint(csrfFrom("/login")));
        assertThat(user.statusCode()).isEqualTo(401);
    }

    @Test
    void publicDiagramUrlIsReachableWithoutLogin() throws Exception {
        HttpResponse<String> response = send(get("/diagrams/anything"));
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body()).contains("Diagram not found");
    }

    private HttpRequest get(String path) {
        return HttpRequest.newBuilder(uri(path)).GET().build();
    }

    private HttpRequest form(String path, String username, String password, CsrfToken csrf) {
        String body = "username=" + username + "&password=" + password;
        if (csrf.parameterName() != null) {
            body += "&" + csrf.parameterName() + "=" + csrf.token();
        }
        return HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private HttpRequest postForm(String path, String headerName, String token) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("_csrf=" + token));
        if (headerName != null && !headerName.isBlank()) {
            builder.header(headerName, token);
        }
        return builder.build();
    }

    private HttpRequest endpoint(CsrfToken csrf) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri("/connect/CurrentUserEndpoint/getCurrentUser"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"));
        if (csrf.headerName() != null && csrf.token() != null) {
            builder.header(csrf.headerName(), csrf.token());
        }
        return builder.build();
    }

    private CsrfToken csrf() throws Exception {
        return csrfFrom("/login");
    }

    private CsrfToken csrf(HttpClient http) throws Exception {
        return csrfFrom(http, "/login");
    }

    private CsrfToken csrfFrom(String path) throws Exception {
        return csrfFrom(client, path);
    }

    private CsrfToken csrfFrom(HttpClient http, String path) throws Exception {
        HttpResponse<String> page = awaitFrontend(http, path);
        String token = meta(page.body(), "_csrf");
        String header = meta(page.body(), "_csrf_header");
        assertThat(token).as("CSRF token in " + path).isNotBlank();
        return new CsrfToken(header == null ? "X-CSRF-TOKEN" : header, "_csrf", token);
    }

    private HttpResponse<String> awaitFrontend(String path) throws Exception {
        return awaitFrontend(client, path);
    }

    /**
     * Dev mode answers with a placeholder until Vite finishes the bundle. Wait
     * for the real shell, which carries the Diagrammer title and the CSRF meta.
     */
    private HttpResponse<String> awaitFrontend(HttpClient http, String path) throws Exception {
        long deadline = System.nanoTime() + Duration.ofMinutes(3).toNanos();
        HttpResponse<String> page = http.send(request(http, path), body());
        while (System.nanoTime() < deadline && isDevModePlaceholder(page)) {
            Thread.sleep(500);
            page = http.send(request(http, path), body());
        }
        return page;
    }

    private HttpRequest request(HttpClient http, String path) {
        return get(path);
    }

    private static boolean isDevModePlaceholder(HttpResponse<String> page) {
        return page.statusCode() == 200 && page.body() != null && page.body().contains("Building front-end development bundle");
    }

    private static String meta(String html, String name) {
        Matcher matcher = META.matcher(html);
        while (matcher.find()) {
            if (name.equals(matcher.group(1))) {
                return matcher.group(2);
            }
        }
        return null;
    }

    private HttpResponse<String> send(HttpRequest request) throws Exception {
        return client.send(request, body());
    }

    private static HttpResponse.BodyHandler<String> body() {
        return HttpResponse.BodyHandlers.ofString();
    }

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + port + path);
    }

    private static String location(HttpResponse<?> response) {
        return response.headers().firstValue("location").orElse("");
    }

    private record CsrfToken(String headerName, String parameterName, String token) {}
}
