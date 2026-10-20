package dev.diagrammer;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.options.Cookie;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Opens a Playwright context that is already the fixture user.
 *
 * <p>Feature tests start here. Login itself stays in {@link LoginSmokeTest};
 * this posts the fixture credentials and copies the session cookie so the
 * browser never walks the login form.
 */
public final class SignedInBrowser {

    public static final String USERNAME = "albert";

    public static final String PASSWORD = "diagrammer";

    private static final Pattern META =
            Pattern.compile("<meta\\s+name=\"(_csrf|_csrf_header)\"\\s+content=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);

    private SignedInBrowser() {}

    public static void signIn(BrowserContext context, String baseUrl) throws Exception {
        URI base = URI.create(baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl);
        CookieManager cookies = new CookieManager();
        HttpClient http = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .cookieHandler(cookies)
                .build();
        String token = awaitCsrf(http, base.resolve("/login"));
        String body = "username=" + encode(USERNAME) + "&password=" + encode(PASSWORD) + "&_csrf=" + encode(token);
        HttpRequest login = HttpRequest.newBuilder(base.resolve("/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = http.send(login, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        String location = response.headers().firstValue("location").orElse("");
        if (status < 300 || status >= 400 || location.contains("error")) {
            throw new IllegalStateException("Fixture sign-in failed: " + status + " " + location);
        }
        List<Cookie> session = new ArrayList<>();
        for (HttpCookie cookie : cookies.getCookieStore().get(base)) {
            session.add(new Cookie(cookie.getName(), cookie.getValue()).setUrl(base.toString()));
        }
        if (session.isEmpty()) {
            throw new IllegalStateException("Fixture sign-in did not set a session cookie");
        }
        context.addCookies(session);
    }

    private static String awaitCsrf(HttpClient http, URI login) throws Exception {
        long deadline = System.nanoTime() + Duration.ofMinutes(3).toNanos();
        HttpRequest request = HttpRequest.newBuilder(login).GET().build();
        while (true) {
            HttpResponse<String> page = http.send(request, HttpResponse.BodyHandlers.ofString());
            String token = meta(page.body(), "_csrf");
            if (token != null && !token.isBlank() && !isPlaceholder(page)) {
                return token;
            }
            if (System.nanoTime() >= deadline) {
                throw new IllegalStateException("Timed out waiting for a login CSRF token");
            }
            Thread.sleep(500);
        }
    }

    private static boolean isPlaceholder(HttpResponse<String> page) {
        return page.body() != null && page.body().contains("Building front-end development bundle");
    }

    private static String meta(String html, String name) {
        if (html == null) {
            return null;
        }
        Matcher matcher = META.matcher(html);
        while (matcher.find()) {
            if (name.equals(matcher.group(1))) {
                return matcher.group(2);
            }
        }
        return null;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
