package dev.diagrammer;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
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
 * S3 title gate and S4 profile assistant. Login stays in {@link LoginSmokeTest};
 * this opens an already signed-in session and checks only these features.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TitleAndAssistantSmokeTest {

    private static final Pattern META =
            Pattern.compile("<meta\\s+name=\"(_csrf|_csrf_header)\"\\s+content=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);

    private static final String CLEAR = "How adopt-dog travels from CLI to Repository";

    private static final String CLARIFY = "That topic is brief. What should the diagram make clear?";

    private static final String HTML =
            "<script>window.__xss=1</script><img src=x onerror=\"window.__xss=1\"><b>injected</b>";

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
        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1440, 1600));
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
        page.locator("[data-kind='greeting']").waitFor();
    }

    @Test
    void applicationStartsAndMainUiLoadsWithAnEmptyTitle() {
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Diagram Orchestrator")))
                .isVisible();
        assertThat(titleInput()).hasValue("");
        assertThat(page.locator("#title-error")).hasCount(0);
        org.assertj.core.api.Assertions.assertThat(card("title")).isEqualTo("not set yet");
        org.assertj.core.api.Assertions.assertThat(card("topic")).isEqualTo("not set yet");
        org.assertj.core.api.Assertions.assertThat(card("refinements")).isEqualTo("not set yet");
        BoundingBox titleBox = page.locator("vaadin-text-field.diagram-title").boundingBox();
        BoundingBox domainBox = radio("Java").boundingBox();
        org.assertj.core.api.Assertions.assertThat(titleBox).isNotNull();
        org.assertj.core.api.Assertions.assertThat(domainBox).isNotNull();
        org.assertj.core.api.Assertions.assertThat(titleBox.y).isLessThan(domainBox.y);
        assertThat(generateButton()).isDisabled();
        assertThat(page.locator("#generate-hint")).hasText("Title and Topic are needed.");
        assertThat(page.locator(".assistant-offline"))
                .hasText("Offline assistant. Replies are scripted until a model is connected.");
        assertThat(page.locator("[data-kind='greeting']"))
                .containsText("Hello. Set the chips and the Title field on the left.");
        assertThat(page.locator("[data-kind='greeting']")).containsText(CLEAR);
        assertChecked("Java");
    }

    @Test
    void titleLengthIsCheckedAndAValidTitleUpdatesTheCard() {
        setTitle("ab");
        assertThat(page.locator("#title-error")).hasText("Use 3 to 80 characters.");
        org.assertj.core.api.Assertions.assertThat(card("title")).isEqualTo("not set yet");

        setTitle("a".repeat(81));
        assertThat(page.locator("#title-error")).hasText("Use 3 to 80 characters.");
        org.assertj.core.api.Assertions.assertThat(card("title")).isEqualTo("not set yet");

        setTitle("abc");
        assertThat(page.locator("#title-error")).hasCount(0);
        org.assertj.core.api.Assertions.assertThat(card("title")).isEqualTo("abc");

        String eighty = "a".repeat(80);
        setTitle(eighty);
        assertThat(page.locator("#title-error")).hasCount(0);
        org.assertj.core.api.Assertions.assertThat(card("title")).isEqualTo(eighty);
        assertThat(generateButton()).isDisabled();
        assertThat(page.locator("#generate-hint")).isVisible();
    }

    @Test
    void emptyTitleShowsAnErrorOnlyAfterTheUserTypesAndLeaves() {
        titleInput().click();
        page.locator("h1").click();
        assertThat(page.locator("#title-error")).hasCount(0);

        setTitle("abc");
        setTitle("");
        blurTitle();
        assertThat(page.locator("#title-error")).hasText("Enter a title.");
        org.assertj.core.api.Assertions.assertThat(card("title")).isEqualTo("not set yet");
    }

    @Test
    void titleIsRenderedAsText() {
        setTitle("<b>Hi</b>");
        Locator title = page.locator("[data-field='title'] dd");
        assertThat(title).hasText("<b>Hi</b>");
        org.assertj.core.api.Assertions.assertThat(title.innerHTML()).contains("&lt;b&gt;").doesNotContain("<b>");
        org.assertj.core.api.Assertions.assertThat(title.locator("b").count()).isZero();
    }

    @Test
    void greetingUsesTheDomainPlaceholder() {
        assertThat(page.locator("[data-kind='greeting']")).containsText(CLEAR);

        choose("ML");
        assertThat(page.locator("[data-kind='greeting']")).containsText("What happens in a train/test split");

        choose("React");
        assertThat(page.locator("[data-kind='greeting']")).containsText("Lifecycle of a component with useEffect");

        choose("Spring Boot");
        assertThat(page.locator("[data-kind='greeting']"))
                .containsText("Describe your topic in one sentence");

        choose("Python");
        assertThat(page.locator("[data-kind='greeting']"))
                .containsText("Describe your topic in one sentence");

        choose("Java");
        assertThat(page.locator("[data-kind='greeting']")).containsText(CLEAR);
    }

    @Test
    void titleAndTopicTogetherEnableGenerateAndTheClickDoesNotCallTheServer() {
        say(CLEAR);
        org.assertj.core.api.Assertions.assertThat(card("topic")).isEqualTo(CLEAR);
        assertThat(generateButton()).isDisabled();
        assertThat(page.locator("#generate-hint")).hasText("Title and Topic are needed.");

        page.navigate(base + "/");
        radio("Java").waitFor(attached(20_000));
        setTitle("Adopt a dog");
        assertThat(generateButton()).isDisabled();

        say(CLEAR);
        Locator summary = lastAssistant();
        assertThat(summary).containsText("Profile summary");
        assertThat(summary).containsText("Title: Adopt a dog");
        assertThat(summary).containsText("Domain: Java");
        assertThat(summary).containsText("Goal: Explain flow");
        assertThat(summary).containsText("Interactivity: Step-by-step");
        assertThat(summary).containsText("Type: Auto");
        assertThat(summary).containsText("Depth: With code");
        assertThat(summary).containsText("Theme: Light");
        assertThat(summary).containsText("Audience: Beginner");
        assertThat(summary).containsText("Topic: " + CLEAR);
        assertThat(summary).containsText("Refinements: not set yet");
        org.assertj.core.api.Assertions.assertThat(summary.innerText()).doesNotContain("likely");
        assertThat(generateButton()).isEnabled();
        assertThat(page.locator("#generate-hint")).hasCount(0);

        List<String> requests = new ArrayList<>();
        page.onRequest(request -> {
            String type = request.resourceType();
            if ("fetch".equals(type) || "xhr".equals(type)) {
                requests.add(request.url());
            }
        });
        generateButton().click();
        page.waitForTimeout(500);
        org.assertj.core.api.Assertions.assertThat(requests).isEmpty();
        assertThat(page.locator("#generate-note")).hasText("Generation arrives in a later release");
        org.assertj.core.api.Assertions.assertThat(card("title")).isEqualTo("Adopt a dog");
        org.assertj.core.api.Assertions.assertThat(card("topic")).isEqualTo(CLEAR);
    }

    @Test
    void vagueTopicAsksOneQuestionAndStoresTheAnswerAsRefinements() {
        setTitle("Adopt a dog");
        say("dogs");
        assertThat(page.getByText(CLARIFY)).hasCount(1);
        org.assertj.core.api.Assertions.assertThat(card("topic")).isEqualTo("dogs");
        org.assertj.core.api.Assertions.assertThat(card("refinements")).isEqualTo("not set yet");

        String answer = "the path from the CLI into the repository";
        say(answer);
        assertThat(page.getByText(CLARIFY)).hasCount(1);
        org.assertj.core.api.Assertions.assertThat(card("refinements")).isEqualTo(answer);
        assertThat(lastAssistant()).containsText("Profile summary");
        assertThat(lastAssistant()).containsText("Refinements: " + answer);
        assertThat(generateButton()).isEnabled();

        say("more");
        assertThat(page.getByText(CLARIFY)).hasCount(1);
        assertThat(lastAssistant()).containsText("The profile is already complete.");
    }

    @Test
    void goalOtherConfirmsAMappedGoalAndLeavesTheOtherChips() {
        setTitle("Layered app");
        choose("Other");
        say(CLEAR);
        assertThat(lastAssistant()).hasText("Goal is Other. Phrase what you want the diagram to do.");
        assertChecked("Other");
        org.assertj.core.api.Assertions.assertThat(card("topic")).isEqualTo(CLEAR);

        say("show the layers of the app");
        assertThat(lastAssistant()).containsText("That maps to Show layers");
        assertChecked("Other");
        assertChecked("Java");
        assertChecked("Auto");
        assertChecked("Step-by-step");
        assertChecked("With code");
        assertChecked("Light");
        assertChecked("Beginner");

        say("yes");
        assertChecked("Show layers");
        assertChecked("Java");
        assertChecked("Auto");
        assertChecked("Step-by-step");
        assertChecked("With code");
        assertChecked("Light");
        assertChecked("Beginner");
        org.assertj.core.api.Assertions.assertThat(card("goal")).isEqualTo("Show layers");
        assertThat(lastAssistant()).containsText("Profile summary");
        assertThat(lastAssistant()).containsText("Goal: Show layers");
        assertThat(lastAssistant()).containsText("Title: Layered app");
        assertThat(lastAssistant()).containsText("Type: Auto");
        assertThat(generateButton()).isEnabled();
    }

    @Test
    void assistantHtmlIsShownAsText() {
        say(HTML);
        Locator bubble = lastAssistant();
        assertThat(bubble).containsText("<script>window.__xss=1</script>");
        assertThat(bubble).containsText("<b>injected</b>");
        org.assertj.core.api.Assertions.assertThat(bubble.innerHTML())
                .contains("&lt;script&gt;")
                .doesNotContain("<script");
        org.assertj.core.api.Assertions.assertThat(bubble.locator("script, img, b").count()).isZero();
        org.assertj.core.api.Assertions.assertThat(page.evaluate("() => window.__xss")).isNull();
        org.assertj.core.api.Assertions.assertThat(card("topic")).isEqualTo("not set yet");
    }

    @Test
    void chatEndpointRejectsAnonymousCallers() throws Exception {
        HttpClient stranger = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .cookieHandler(new CookieManager())
                .build();
        HttpResponse<String> login = awaitLoginPage(stranger);
        String token = meta(login.body(), "_csrf");
        String header = meta(login.body(), "_csrf_header");
        org.assertj.core.api.Assertions.assertThat(token).isNotBlank();

        assertAnonymous(stranger, "/connect/AssistantEndpoint/chat", header, token);
        assertAnonymous(stranger, "/connect/AssistantEndpoint/offline", header, token);
        assertAnonymous(stranger, "/connect/AssistantEndpoint/greeting", header, token);
    }

    private void assertAnonymous(HttpClient stranger, String path, String header, String token) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(base + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"));
        if (header != null) {
            request.header(header, token);
        }
        HttpResponse<String> response = stranger.send(request.build(), HttpResponse.BodyHandlers.ofString());
        org.assertj.core.api.Assertions.assertThat(response.statusCode()).isEqualTo(401);
    }

    private void setTitle(String value) {
        setVaadinValue("vaadin-text-field.diagram-title", value);
    }

    private void blurTitle() {
        page.locator("vaadin-text-field.diagram-title")
                .evaluate("el => el.dispatchEvent(new FocusEvent('blur', { bubbles: true }))");
        page.locator("h1").click();
    }

    private void say(String text) {
        int before = page.locator(".assistant-message[data-role='assistant']").count();
        setVaadinValue("vaadin-text-field.assistant-input", text);
        page.locator(".assistant-send").click();
        page.locator(".assistant-message[data-role='assistant']").nth(before).waitFor();
    }

    private void setVaadinValue(String selector, String value) {
        page.locator(selector)
                .evaluate(
                        """
                        (el, value) => {
                          el.value = value;
                          el.dispatchEvent(new CustomEvent('value-changed', { bubbles: true, detail: { value } }));
                        }
                        """,
                        value);
    }

    private Locator lastAssistant() {
        return page.locator(".assistant-message[data-role='assistant']").last();
    }

    private Locator titleInput() {
        return page.locator("vaadin-text-field.diagram-title input");
    }

    private Locator generateButton() {
        return page.getByRole(
                AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm & Generate").setExact(true));
    }

    private void choose(String name) {
        Locator chip = radio(name).locator("xpath=ancestor::vaadin-radio-button[1]");
        chip.scrollIntoViewIfNeeded();
        chip.getByText(name, new Locator.GetByTextOptions().setExact(true)).click();
    }

    private void assertChecked(String name) {
        assertThat(radio(name)).isChecked();
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

    private HttpResponse<String> awaitLoginPage(HttpClient http) throws Exception {
        long deadline = System.nanoTime() + Duration.ofMinutes(2).toNanos();
        HttpRequest request = HttpRequest.newBuilder(URI.create(base + "/login")).GET().build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        while (System.nanoTime() < deadline
                && response.statusCode() == 200
                && response.body() != null
                && response.body().contains("Building front-end development bundle")) {
            Thread.sleep(500);
            response = http.send(request, HttpResponse.BodyHandlers.ofString());
        }
        return response;
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
