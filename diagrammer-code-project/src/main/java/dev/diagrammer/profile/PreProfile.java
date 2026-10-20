package dev.diagrammer.profile;

import org.springframework.lang.Nullable;

/**
 * The intake contract later stories send to the Orchestrator.
 *
 * <p>Title and topic stay unset until those stories. Chip state lives in the
 * browser for now; this type is the shape, not a saved document.
 */
public record PreProfile(
        @Nullable String title,
        Domain domain,
        Goal goal,
        Interactivity interactivity,
        DiagramType type,
        Depth depth,
        Theme theme,
        Audience audience,
        @Nullable String topic) {

    public static PreProfile defaults() {
        return new PreProfile(
                null,
                Domain.JAVA,
                Goal.EXPLAIN_FLOW,
                Interactivity.STEP_BY_STEP,
                DiagramType.AUTO,
                Depth.WITH_CODE,
                Theme.LIGHT,
                Audience.BEGINNER,
                null);
    }

    public PreProfile withType(DiagramType type) {
        return new PreProfile(title, domain, goal, interactivity, type, depth, theme, audience, topic);
    }
}
