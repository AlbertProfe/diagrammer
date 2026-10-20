package dev.diagrammer.theme;

import dev.diagrammer.profile.Theme;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The one diagram palette. {@code diagram-theme.css} is the definition; this
 * type only reads it.
 *
 * <p>The Orchestrator preview loads that stylesheet and sets {@link
 * #cssClass(Theme)} on the sample card. The renderer (S8) should inline {@link
 * #stylesheet()} into each generated page and set the same class on the
 * document, so a standalone HTML file carries these custom properties without
 * a second copy of the colors.
 */
public final class DiagramThemeTokens {

    static final String RESOURCE = "/META-INF/resources/diagram-theme.css";

    private static final Pattern BLOCK =
            Pattern.compile("\\.diagram-theme-(light|dark)\\s*\\{([^}]*)}", Pattern.DOTALL);

    private static final Pattern PROPERTY = Pattern.compile("(--[a-z0-9-]+)\\s*:\\s*(#[0-9a-fA-F]{3,8})\\s*;");

    private DiagramThemeTokens() {}

    /** Raw stylesheet for the renderer to inline. */
    public static String stylesheet() {
        try (InputStream in = DiagramThemeTokens.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing classpath resource " + RESOURCE);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read " + RESOURCE, exception);
        }
    }

    /** Class the preview and the generated page set to select a palette. */
    public static String cssClass(Theme theme) {
        return theme == Theme.DARK ? "diagram-theme-dark" : "diagram-theme-light";
    }

    /** Custom properties for one palette, in stylesheet order. Keys include the {@code --} prefix. */
    public static Map<String, String> tokens(Theme theme) {
        String which = theme == Theme.DARK ? "dark" : "light";
        Matcher blocks = BLOCK.matcher(stylesheet());
        while (blocks.find()) {
            if (which.equals(blocks.group(1))) {
                return properties(blocks.group(2));
            }
        }
        throw new IllegalStateException("No " + cssClass(theme) + " block in " + RESOURCE);
    }

    private static Map<String, String> properties(String block) {
        Map<String, String> tokens = new LinkedHashMap<>();
        Matcher properties = PROPERTY.matcher(block);
        while (properties.find()) {
            tokens.put(properties.group(1), properties.group(2).toLowerCase(Locale.ROOT));
        }
        if (tokens.isEmpty()) {
            throw new IllegalStateException("Theme block defines no custom properties");
        }
        return Collections.unmodifiableMap(tokens);
    }
}
