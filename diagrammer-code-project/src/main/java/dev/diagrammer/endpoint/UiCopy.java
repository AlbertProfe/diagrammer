package dev.diagrammer.endpoint;

import java.util.Locale;
import org.springframework.context.MessageSource;

/** English UI strings for the title field, the generate gate, and the chat panel. */
public record UiCopy(
        String titleLabel,
        String titlePlaceholder,
        String titleError,
        String titleEmpty,
        String generateButton,
        String generateHint,
        String generateLater,
        String notSet,
        String refinementsLabel,
        String assistantHeading,
        String assistantOffline,
        String assistantSend,
        String assistantInput,
        String greetingLead,
        String topicJava,
        String topicMl,
        String topicReact,
        String topicNeutral,
        String assistantError,
        String loadError,
        String loading) {

    public static UiCopy english(MessageSource messages) {
        return new UiCopy(
                text(messages, "title.label"),
                text(messages, "title.placeholder"),
                text(messages, "title.error"),
                text(messages, "title.empty"),
                text(messages, "generate.button"),
                text(messages, "generate.hint"),
                text(messages, "generate.later"),
                text(messages, "card.notSet"),
                text(messages, "card.refinements"),
                text(messages, "assistant.heading"),
                text(messages, "assistant.offline"),
                text(messages, "assistant.send"),
                text(messages, "assistant.input"),
                text(messages, "assistant.greeting.lead"),
                text(messages, "assistant.topic.java"),
                text(messages, "assistant.topic.ml"),
                text(messages, "assistant.topic.react"),
                text(messages, "assistant.topic.neutral"),
                text(messages, "assistant.error"),
                text(messages, "ui.loadError"),
                text(messages, "ui.loading"));
    }

    private static String text(MessageSource messages, String code) {
        return messages.getMessage(code, null, Locale.ENGLISH);
    }
}
