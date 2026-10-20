package dev.diagrammer.assistant;

import dev.diagrammer.profile.Goal;
import org.springframework.lang.Nullable;

/**
 * The assistant's next line plus the only profile fields it may set.
 *
 * <p>{@code text} is plain text. The browser renders it as text, never as HTML.
 * A null topic, refinements, or confirmed goal means "leave that field as it is".
 * {@code confirmedGoal} is set only after the user confirms a mapped goal.
 */
public record AssistantReply(
        String text,
        @Nullable String topic,
        @Nullable String refinements,
        @Nullable Goal confirmedGoal) {}
