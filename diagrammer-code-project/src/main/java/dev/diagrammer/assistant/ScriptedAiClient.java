package dev.diagrammer.assistant;

import dev.diagrammer.profile.Domain;
import dev.diagrammer.profile.Goal;
import dev.diagrammer.profile.PreProfile;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;

/**
 * Deterministic profile assistant used when no Bedrock config is present.
 *
 * <p>A topic is vague when it has fewer than {@link #VAGUE_UNDER_WORDS} words,
 * or when the normalized text is on {@link #VAGUE_PHRASES}. A vague topic is
 * still stored as the topic, and the next user message is stored as
 * refinements. The clarifying question is asked once.
 *
 * <p>When Goal is Other, the assistant asks for a phrase, maps it to a
 * canonical goal, and sets that goal only after the user confirms. No other
 * chip is changed here. Combination rules still apply in the browser after a
 * confirmed goal.
 *
 * <p>A user message that contains an HTML tag ({@code <script}, {@code <img},
 * {@code <b>}, a closing tag, or {@code onerror=}) is returned unchanged as
 * the reply text and does not change the profile. That lets tests prove the
 * panel shows assistant text as text.
 */
public final class ScriptedAiClient implements AiClient {

    static final int VAGUE_UNDER_WORDS = 5;

    static final List<String> VAGUE_PHRASES = List.of(
            "diagram",
            "a diagram",
            "something",
            "help",
            "idk",
            "stuff",
            "things",
            "topic",
            "a topic",
            "please make me a diagram about this");

    private static final Set<String> CONFIRMATIONS =
            Set.of("yes", "y", "ok", "okay", "confirm", "confirmed", "yes confirm", "correct");

    private final MessageSource messages;

    public ScriptedAiClient(MessageSource messages) {
        this.messages = messages;
        if (systemPrompt().isBlank()) {
            throw new IllegalStateException("assistant.system is missing from the resource bundle");
        }
    }

    @Override
    public boolean offline() {
        return true;
    }

    @Override
    public String systemPrompt() {
        return text("assistant.system");
    }

    @Override
    public String greeting(Domain domain) {
        Domain selected = domain == null ? Domain.JAVA : domain;
        return text("assistant.greeting.lead") + " " + topicExample(selected);
    }

    @Override
    public AssistantReply chat(AssistantRequest request) {
        PreProfile profile = request.profile();
        String message = request.userMessage() == null ? "" : request.userMessage().trim();
        if (message.isEmpty()) {
            return new AssistantReply(text("assistant.empty"), null, null, null);
        }
        if (htmlProbe(message)) {
            return new AssistantReply(message, null, null, null);
        }
        List<ChatTurn> history = request.history() == null ? List.of() : request.history();
        String last = lastAssistant(history);
        return switch (phase(last)) {
            case DONE -> new AssistantReply(text("assistant.done"), profile.topic(), profile.refinements(), null);
            case GOAL_CONFIRM -> confirmGoal(profile, message, last);
            case GOAL_PHRASE -> mappedGoalReply(profile, mapGoal(message));
            case CLARIFY -> afterClarification(profile, message);
            case TOPIC -> takeTopic(profile, message);
        };
    }

    private AssistantReply takeTopic(PreProfile profile, String message) {
        if (vague(message)) {
            return new AssistantReply(text("assistant.clarify"), message, null, null);
        }
        if (profile.goal() == Goal.OTHER) {
            return new AssistantReply(text("assistant.goal.ask"), message, null, null);
        }
        PreProfile next = profile.withTopic(message);
        return new AssistantReply(summary(next), message, null, null);
    }

    private AssistantReply afterClarification(PreProfile profile, String message) {
        PreProfile next = profile.withRefinements(message);
        if (next.goal() == Goal.OTHER) {
            return new AssistantReply(text("assistant.goal.ask"), next.topic(), message, null);
        }
        return new AssistantReply(summary(next), next.topic(), message, null);
    }

    private AssistantReply confirmGoal(PreProfile profile, String message, @Nullable String last) {
        if (confirms(message)) {
            Goal mapped = goalNamedIn(last);
            PreProfile next = profile.withGoal(mapped);
            return new AssistantReply(summary(next), next.topic(), next.refinements(), mapped);
        }
        return mappedGoalReply(profile, mapGoal(message));
    }

    private AssistantReply mappedGoalReply(PreProfile profile, Goal mapped) {
        String reply = text("assistant.goal.mapped.prefix") + mapped.label() + text("assistant.goal.mapped.suffix");
        return new AssistantReply(reply, profile.topic(), profile.refinements(), null);
    }

    private Phase phase(@Nullable String lastAssistant) {
        if (lastAssistant == null || lastAssistant.isBlank()) {
            return Phase.TOPIC;
        }
        if (lastAssistant.startsWith(text("assistant.summary.heading"))) {
            return Phase.DONE;
        }
        if (lastAssistant.startsWith(text("assistant.goal.mapped.prefix"))) {
            return Phase.GOAL_CONFIRM;
        }
        if (lastAssistant.equals(text("assistant.goal.ask"))) {
            return Phase.GOAL_PHRASE;
        }
        if (lastAssistant.equals(text("assistant.clarify"))) {
            return Phase.CLARIFY;
        }
        return Phase.TOPIC;
    }

    static boolean vague(String message) {
        String normalized = normalize(message);
        if (normalized.isEmpty() || VAGUE_PHRASES.contains(normalized)) {
            return true;
        }
        return normalized.split(" ").length < VAGUE_UNDER_WORDS;
    }

    static boolean htmlProbe(String message) {
        String lower = message.toLowerCase(Locale.ROOT);
        return lower.contains("<script")
                || lower.contains("<img")
                || lower.contains("<b>")
                || lower.contains("</")
                || lower.contains("onerror=");
    }

    private Goal mapGoal(String phrase) {
        String normalized = " " + normalize(phrase) + " ";
        if (containsAny(normalized, "compare", "comparison", "versus", " vs ", "difference")) {
            return Goal.COMPARE;
        }
        if (containsAny(normalized, "layer", "stack", "pyramid")) {
            return Goal.LAYERS;
        }
        if (containsAny(
                normalized, "use case", "use-case", "usecase", "scenario", "walk through", "walkthrough")) {
            return Goal.USE_CASE;
        }
        return Goal.EXPLAIN_FLOW;
    }

    private Goal goalNamedIn(@Nullable String lastAssistant) {
        if (lastAssistant == null) {
            return Goal.EXPLAIN_FLOW;
        }
        String prefix = text("assistant.goal.mapped.prefix");
        String rest = lastAssistant.startsWith(prefix) ? lastAssistant.substring(prefix.length()) : lastAssistant;
        Goal found = Goal.EXPLAIN_FLOW;
        int matched = -1;
        for (Goal goal : Goal.values()) {
            if (goal == Goal.OTHER) {
                continue;
            }
            if (rest.startsWith(goal.label()) && goal.label().length() > matched) {
                found = goal;
                matched = goal.label().length();
            }
        }
        return found;
    }

    private String summary(PreProfile profile) {
        return String.join(
                "\n",
                text("assistant.summary.heading"),
                line("assistant.summary.title", display(profile.title())),
                line("assistant.summary.domain", profile.domain().label()),
                line("assistant.summary.goal", profile.goal().label()),
                line("assistant.summary.interactivity", profile.interactivity().label()),
                line("assistant.summary.type", profile.type().label()),
                line("assistant.summary.depth", profile.depth().label()),
                line("assistant.summary.theme", profile.theme().label()),
                line("assistant.summary.audience", profile.audience().label()),
                line("assistant.summary.topic", display(profile.topic())),
                line("assistant.summary.refinements", display(profile.refinements())));
    }

    private String topicExample(Domain domain) {
        return switch (domain) {
            case JAVA -> text("assistant.topic.java");
            case ML -> text("assistant.topic.ml");
            case REACT -> text("assistant.topic.react");
            case SPRING_BOOT, PYTHON -> text("assistant.topic.neutral");
        };
    }

    private String line(String labelKey, String value) {
        return text(labelKey) + ": " + value;
    }

    private String display(@Nullable String value) {
        if (value == null || value.isBlank()) {
            return text("card.notSet");
        }
        return value.trim();
    }

    private String text(String code) {
        return messages.getMessage(code, null, Locale.ENGLISH);
    }

    private static boolean confirms(String message) {
        return CONFIRMATIONS.contains(normalize(message).replaceAll("[.!,?]", ""));
    }

    private static boolean containsAny(String normalized, String... needles) {
        for (String needle : needles) {
            if (normalized.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String message) {
        return message.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    @Nullable
    private static String lastAssistant(List<ChatTurn> history) {
        for (int i = history.size() - 1; i >= 0; i--) {
            ChatTurn turn = history.get(i);
            if (turn != null && ChatTurn.ASSISTANT.equals(turn.role()) && turn.text() != null) {
                return turn.text();
            }
        }
        return null;
    }

    private enum Phase {
        TOPIC,
        CLARIFY,
        GOAL_PHRASE,
        GOAL_CONFIRM,
        DONE
    }
}
