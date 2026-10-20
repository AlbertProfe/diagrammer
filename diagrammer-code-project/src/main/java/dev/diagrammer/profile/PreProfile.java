package dev.diagrammer.profile;

import org.springframework.lang.Nullable;

/**
 * The intake contract later stories send to the Orchestrator.
 *
 * <p>Title, topic, and refinements stay null until the user sets them. Chip
 * state and the chat transcript live in the browser for now; this type is the
 * shape, not a saved document. {@code refinements} is optional so a payload
 * written before that field existed still deserializes.
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
        @Nullable String topic,
        @Nullable String refinements) {

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
                null,
                null);
    }

    public PreProfile withTitle(@Nullable String title) {
        return new PreProfile(title, domain, goal, interactivity, type, depth, theme, audience, topic, refinements);
    }

    public PreProfile withGoal(Goal goal) {
        return new PreProfile(title, domain, goal, interactivity, type, depth, theme, audience, topic, refinements);
    }

    public PreProfile withType(DiagramType type) {
        return new PreProfile(title, domain, goal, interactivity, type, depth, theme, audience, topic, refinements);
    }

    public PreProfile withTopic(@Nullable String topic) {
        return new PreProfile(title, domain, goal, interactivity, type, depth, theme, audience, topic, refinements);
    }

    public PreProfile withRefinements(@Nullable String refinements) {
        return new PreProfile(title, domain, goal, interactivity, type, depth, theme, audience, topic, refinements);
    }
}
