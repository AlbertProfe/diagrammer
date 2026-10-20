package dev.diagrammer;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Browser smoke test for the Diagram Orchestrator chips. Login itself is
 * covered by {@link LoginSmokeTest}. This opens an already signed-in session
 * through {@link SignedInBrowser} and checks the intake.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrchestratorSmokeTest {

    private static final Pattern META =
            Pattern.compile("<meta\\s+name=\"(_csrf|_csrf_header)\"\\s+content=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);

    private static final String MULTI_SCENARIO_TOOLTIP = "Multi-scenario works with U-shape or Auto only.";

    private static final String COMPARE_TOOLTIP = "Comparison layout is v2, so Compare concepts stays on Auto.";

    @LocalServerPort
    private int port;

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private String base;

    @BeforeAll
    void openBrowserSignedIn() throws Exception {
        base = "http://127.0.0.1:" + port;
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setChannel("chrome")
                .setHeadless(true)
                .setArgs(List.of("--no-sandbox", "--disable-dev-shm-usage")));
        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1440, 1400));
        SignedInBrowser.signIn(context, base);
        page = context.newPage();
        page.setDefaultTimeout(20_000);
        page.navigate(base + "/");
        radio("Java").waitFor(attached(Duration.ofMinutes(3).toMillis()));
    }

    @AfterAll
    void closeBrowser() {
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void freshScreen() {
        page.navigate(base + "/");
        radio("Java").waitFor(attached(20_000));
    }

    @Test
    void freshScreenShowsDefaultsAndThePreProfileMatches() {
        assertChecked("Java");
        assertChecked("Explain flow");
        assertChecked("Step-by-step");
        assertChecked("Auto");
        assertChecked("With code");
        assertChecked("Light");
        assertChecked("Beginner");

        org.assertj.core.api.Assertions.assertThat(card("title")).isEqualTo("not set yet");
        org.assertj.core.api.Assertions.assertThat(card("domain")).isEqualTo("Java");
        org.assertj.core.api.Assertions.assertThat(card("goal")).isEqualTo("Explain flow");
        org.assertj.core.api.Assertions.assertThat(card("interactivity")).isEqualTo("Step-by-step");
        org.assertj.core.api.Assertions.assertThat(card("type")).isEqualTo("Auto");
        org.assertj.core.api.Assertions.assertThat(card("depth")).isEqualTo("With code");
        org.assertj.core.api.Assertions.assertThat(card("theme")).isEqualTo("Light");
        org.assertj.core.api.Assertions.assertThat(card("audience")).isEqualTo("Beginner");
        org.assertj.core.api.Assertions.assertThat(card("topic")).isEqualTo("not set yet");
        org.assertj.core.api.Assertions.assertThat(card("refinements")).isEqualTo("not set yet");

        org.assertj.core.api.Assertions.assertThat(wire("domain")).isEqualTo("java");
        org.assertj.core.api.Assertions.assertThat(wire("goal")).isEqualTo("explain-flow");
        org.assertj.core.api.Assertions.assertThat(wire("interactivity")).isEqualTo("step-by-step");
        org.assertj.core.api.Assertions.assertThat(wire("type")).isEqualTo("auto");
        org.assertj.core.api.Assertions.assertThat(wire("depth")).isEqualTo("with-code");
        org.assertj.core.api.Assertions.assertThat(wire("theme")).isEqualTo("light");
        org.assertj.core.api.Assertions.assertThat(wire("audience")).isEqualTo("beginner");

        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm & Generate").setExact(true)))
                .isDisabled();
        assertThat(page.locator("#generate-hint")).hasText("Title and Topic are needed.");
        assertThat(page.locator("vaadin-text-field.diagram-title input")).hasValue("");
        assertThat(page.locator(".assistant-offline"))
                .hasText("Offline assistant. Replies are scripted until a model is connected.");
        assertThat(page.locator("[data-kind='greeting']")).containsText("How adopt-dog travels from CLI to Repository");
        org.assertj.core.api.Assertions.assertThat(previewTheme()).isEqualTo("light");
        org.assertj.core.api.Assertions.assertThat(cssColor(".theme-preview")).isEqualTo("rgb(241, 245, 249)");
        org.assertj.core.api.Assertions.assertThat(cssColor(".theme-preview-action")).isEqualTo("rgb(59, 130, 246)");
    }

    @Test
    void pickingDarkUpdatesTheCardAndThePreview() {
        String shellBefore = cssColor(".app-shell");
        assertChecked("Light");
        org.assertj.core.api.Assertions.assertThat(card("theme")).isEqualTo("Light");

        choose("Dark");

        assertChecked("Dark");
        org.assertj.core.api.Assertions.assertThat(card("theme")).isEqualTo("Dark");
        org.assertj.core.api.Assertions.assertThat(wire("theme")).isEqualTo("dark");
        org.assertj.core.api.Assertions.assertThat(previewTheme()).isEqualTo("dark");
        org.assertj.core.api.Assertions.assertThat(cssColor(".theme-preview")).isEqualTo("rgb(15, 23, 42)");
        org.assertj.core.api.Assertions.assertThat(cssColor(".theme-preview-card")).isEqualTo("rgb(30, 41, 59)");
        org.assertj.core.api.Assertions.assertThat(cssColor(".theme-preview-action")).isEqualTo("rgb(56, 189, 248)");
        org.assertj.core.api.Assertions.assertThat(cssColor(".app-shell")).isEqualTo(shellBefore);

        String catalog = catalogJson();
        org.assertj.core.api.Assertions.assertThat(catalog).contains("\"theme\":\"LIGHT\"");
        org.assertj.core.api.Assertions.assertThat(catalog).contains("\"value\":\"dark\"");
    }

    @Test
    void changingEachChipGroupUpdatesTheCard() {
        choose("Python");
        org.assertj.core.api.Assertions.assertThat(card("domain")).isEqualTo("Python");
        org.assertj.core.api.Assertions.assertThat(wire("domain")).isEqualTo("python");

        choose("Show layers");
        org.assertj.core.api.Assertions.assertThat(card("goal")).isEqualTo("Show layers");
        org.assertj.core.api.Assertions.assertThat(wire("goal")).isEqualTo("layers");

        choose("Static");
        org.assertj.core.api.Assertions.assertThat(card("interactivity")).isEqualTo("Static");
        org.assertj.core.api.Assertions.assertThat(wire("interactivity")).isEqualTo("static");

        choose("U-shape request/response");
        org.assertj.core.api.Assertions.assertThat(card("type")).isEqualTo("U-shape request/response");
        org.assertj.core.api.Assertions.assertThat(wire("type")).isEqualTo("u-shape");

        choose("Overview");
        org.assertj.core.api.Assertions.assertThat(card("depth")).isEqualTo("Overview");
        org.assertj.core.api.Assertions.assertThat(wire("depth")).isEqualTo("overview");

        choose("Intermediate");
        org.assertj.core.api.Assertions.assertThat(card("audience")).isEqualTo("Intermediate");
        org.assertj.core.api.Assertions.assertThat(wire("audience")).isEqualTo("intermediate");

        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm & Generate").setExact(true)))
                .isDisabled();
    }

    @Test
    void typeShowsOnlyTheV1Layouts() {
        @SuppressWarnings("unchecked")
        List<String> labels = (List<String>) page
                .getByRole(AriaRole.RADIOGROUP, new Page.GetByRoleOptions().setName("Type").setExact(true))
                .locator("vaadin-radio-button")
                .evaluateAll("els => els.map((el) => el.label)");

        org.assertj.core.api.Assertions.assertThat(labels)
                .containsExactly("Auto", "Horizontal steps", "U-shape request/response", "Pyramid");
        org.assertj.core.api.Assertions.assertThat(radio("Comparison").count()).isZero();
    }

    @Test
    void pyramidAndMultiScenarioCannotBeSelectedTogether() {
        choose("Pyramid");
        assertChecked("Pyramid");
        assertDisabled("Multi-scenario");
        revealTooltip("chip-interactivity-multi-scenario", MULTI_SCENARIO_TOOLTIP);

        choose("Auto");
        choose("Multi-scenario");
        assertChecked("Multi-scenario");
        assertDisabled("Pyramid");
        assertDisabled("Horizontal steps");
        assertEnabled("U-shape request/response");
        assertEnabled("Auto");
        revealTooltip("chip-type-pyramid", MULTI_SCENARIO_TOOLTIP);

        choose("U-shape request/response");
        assertChecked("U-shape request/response");
        assertChecked("Multi-scenario");
    }

    @Test
    void compareConceptsKeepsTypeOnAuto() {
        choose("Pyramid");
        assertChecked("Pyramid");
        choose("Compare concepts");

        assertChecked("Compare concepts");
        assertChecked("Auto");
        org.assertj.core.api.Assertions.assertThat(card("goal")).isEqualTo("Compare concepts");
        org.assertj.core.api.Assertions.assertThat(card("type")).isEqualTo("Auto");
        org.assertj.core.api.Assertions.assertThat(wire("goal")).isEqualTo("compare");
        org.assertj.core.api.Assertions.assertThat(wire("type")).isEqualTo("auto");
        assertDisabled("Pyramid");
        assertDisabled("Horizontal steps");
        assertDisabled("U-shape request/response");
        revealTooltip("chip-type-pyramid", COMPARE_TOOLTIP);
    }

    @Test
    void catalogEndpointRejectsAnonymousCallers() throws Exception {
        HttpClient stranger = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .cookieHandler(new CookieManager())
                .build();
        HttpResponse<String> login = awaitLoginPage(stranger);
        String token = meta(login.body(), "_csrf");
        String header = meta(login.body(), "_csrf_header");
        org.assertj.core.api.Assertions.assertThat(token).isNotBlank();

        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(base + "/connect/ProfileEndpoint/catalog"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"));
        if (header != null) {
            request.header(header, token);
        }
        HttpResponse<String> response = stranger.send(request.build(), HttpResponse.BodyHandlers.ofString());
        org.assertj.core.api.Assertions.assertThat(response.statusCode()).isEqualTo(401);
    }

    private void choose(String name) {
        // The native radio is opacity 0, so click the visible chip label.
        Locator chip = radio(name).locator("xpath=ancestor::vaadin-radio-button[1]");
        chip.scrollIntoViewIfNeeded();
        chip.getByText(name, new Locator.GetByTextOptions().setExact(true)).click();
    }

    private void assertChecked(String name) {
        assertThat(radio(name)).isChecked();
    }

    private void assertDisabled(String name) {
        assertThat(radio(name)).isDisabled();
    }

    private void assertEnabled(String name) {
        assertThat(radio(name)).isEnabled();
    }

    private Locator radio(String name) {
        return page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName(name).setExact(true));
    }

    private static Locator.WaitForOptions attached(double timeoutMs) {
        return new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(timeoutMs);
    }

    private String card(String field) {
        return page.locator("[data-field='" + field + "'] dd").innerText().trim();
    }

    private String wire(String field) {
        return page.locator("[data-field='" + field + "'] dd").getAttribute("data-value");
    }

    private String previewTheme() {
        return page.locator(".theme-preview").getAttribute("data-theme");
    }

    private String cssColor(String selector) {
        return page.locator(selector).evaluate("el => getComputedStyle(el).backgroundColor").toString();
    }

    private String catalogJson() {
        Object body = page.evaluate(
                """
                async () => {
                  const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content') ?? '';
                  const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') ?? 'X-CSRF-TOKEN';
                  const response = await fetch('/connect/ProfileEndpoint/catalog', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json', [header]: token },
                    body: '{}'
                  });
                  return await response.text();
                }
                """);
        return body == null ? "" : body.toString();
    }

    private void revealTooltip(String chipId, String tooltip) {
        page.locator("#" + chipId).hover();
        // Closed tooltips stay in the DOM with the same copy. Assert the visible one.
        assertThat(page.getByText(tooltip).locator("visible=true").first()).isVisible();
    }

    private HttpResponse<String> awaitLoginPage(HttpClient http) throws Exception {
        long deadline = System.nanoTime() + Duration.ofMinutes(2).toNanos();
        HttpRequest request = HttpRequest.newBuilder(URI.create(base + "/login")).GET().build();
        HttpResponse<String> page = http.send(request, HttpResponse.BodyHandlers.ofString());
        while (System.nanoTime() < deadline
                && page.statusCode() == 200
                && page.body() != null
                && page.body().contains("Building front-end development bundle")) {
            Thread.sleep(500);
            page = http.send(request, HttpResponse.BodyHandlers.ofString());
        }
        return page;
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
}
