package dev.diagrammer.theme;

import static org.assertj.core.api.Assertions.assertThat;

import dev.diagrammer.profile.Theme;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DiagramThemeTokensTest {

    @Test
    void lightAndDarkDefineTheSameKeys() {
        Map<String, String> light = DiagramThemeTokens.tokens(Theme.LIGHT);
        Map<String, String> dark = DiagramThemeTokens.tokens(Theme.DARK);

        assertThat(light.keySet()).isEqualTo(dark.keySet());
        assertThat(light.keySet())
                .containsExactly("--bg", "--text", "--heading", "--muted", "--card", "--border", "--accent", "--accent2");
        assertThat(dark.keySet()).containsExactlyElementsOf(light.keySet());
    }

    @Test
    void palettesUseTheHouseColors() {
        Map<String, String> light = DiagramThemeTokens.tokens(Theme.LIGHT);
        assertThat(light)
                .containsEntry("--bg", "#f1f5f9")
                .containsEntry("--text", "#334155")
                .containsEntry("--heading", "#1e293b")
                .containsEntry("--muted", "#64748b")
                .containsEntry("--card", "#ffffff")
                .containsEntry("--border", "#e2e8f0")
                .containsEntry("--accent", "#3b82f6")
                .containsEntry("--accent2", "#8b5cf6");

        Map<String, String> dark = DiagramThemeTokens.tokens(Theme.DARK);
        assertThat(dark)
                .containsEntry("--bg", "#0f172a")
                .containsEntry("--text", "#e2e8f0")
                .containsEntry("--heading", "#f8fafc")
                .containsEntry("--muted", "#94a3b8")
                .containsEntry("--card", "#1e293b")
                .containsEntry("--border", "#475569")
                .containsEntry("--accent", "#38bdf8")
                .containsEntry("--accent2", "#a78bfa");
    }

    @Test
    void stylesheetIsTheSharedSourceTheRendererInlines() {
        String css = DiagramThemeTokens.stylesheet();

        assertThat(css).contains(".diagram-theme-light").contains(".diagram-theme-dark");
        assertThat(DiagramThemeTokens.cssClass(Theme.LIGHT)).isEqualTo("diagram-theme-light");
        assertThat(DiagramThemeTokens.cssClass(Theme.DARK)).isEqualTo("diagram-theme-dark");
        assertThat(css).contains(DiagramThemeTokens.tokens(Theme.DARK).get("--accent"));
    }
}
