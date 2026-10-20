package dev.diagrammer;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Saves the v0.0.4 screenshots against an already running dev server.
 *
 * <p>Not a test. Run it with the app up from {@code ./mvnw spring-boot:run -Pdev}.
 */
public final class ScreenshotCapture {

    private static final String CLEAR = "How adopt-dog travels from CLI to Repository";

    private ScreenshotCapture() {}

    public static void main(String[] args) throws Exception {
        String base = args.length > 0 ? args[0] : "http://127.0.0.1:8080";
        Path out = Path.of(args.length > 1 ? args[1] : "/opt/cursor/artifacts/screenshots");
        Files.createDirectories(out);

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setChannel("chrome")
                    .setHeadless(true)
                    .setArgs(List.of("--no-sandbox", "--disable-dev-shm-usage")));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1440, 1600));
            SignedInBrowser.signIn(context, base);
            Page page = context.newPage();
            page.setDefaultTimeout(30_000);
            open(page, base);

            full(page, out.resolve("s3-empty-title.png"));
            page.locator(".assistant-panel").screenshot(new Locator.ScreenshotOptions().setPath(out.resolve("s4-greeting-java.png")));

            setVaadinValue(page, "vaadin-text-field.diagram-title", "ab");
            page.locator("#title-error").waitFor();
            full(page, out.resolve("s3-title-too-short.png"));

            open(page, base);
            setVaadinValue(page, "vaadin-text-field.diagram-title", "Adopt a dog");
            say(page, "dogs");
            page.getByText("That topic is brief. What should the diagram make clear?").waitFor();
            full(page, out.resolve("s4-clarifying-question.png"));

            say(page, "the path from the CLI into the repository");
            page.locator("#confirm-generate").waitFor();
            full(page, out.resolve("s3-title-topic-generate-enabled.png"));
            page.locator(".assistant-panel").screenshot(new Locator.ScreenshotOptions().setPath(out.resolve("s4-profile-summary.png")));

            open(page, base);
            setVaadinValue(page, "vaadin-text-field.diagram-title", "Layered app");
            choose(page, "Other");
            say(page, CLEAR);
            say(page, "show the layers of the app");
            page.getByText("That maps to Show layers").waitFor();
            full(page, out.resolve("s4-goal-other.png"));

            context.close();
            browser.close();
        }
    }

    private static void open(Page page, String base) {
        page.navigate(base + "/");
        page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Java").setExact(true))
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(180_000));
        page.locator("[data-kind='greeting']").waitFor();
        page.evaluate(
                "() => document.querySelector('vite-plugin-checker-error-overlay')?.remove()");
    }

    private static void full(Page page, Path path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(path).setFullPage(true));
    }

    private static void say(Page page, String text) {
        int before = page.locator(".assistant-message[data-role='assistant']").count();
        setVaadinValue(page, "vaadin-text-field.assistant-input", text);
        page.locator(".assistant-send").click();
        page.locator(".assistant-message[data-role='assistant']").nth(before).waitFor();
    }

    private static void setVaadinValue(Page page, String selector, String value) {
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

    private static void choose(Page page, String name) {
        Locator radio = page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName(name).setExact(true));
        Locator chip = radio.locator("xpath=ancestor::vaadin-radio-button[1]");
        chip.scrollIntoViewIfNeeded();
        chip.getByText(name, new Locator.GetByTextOptions().setExact(true)).click();
    }
}
