package dev.diagrammer.endpoint;

import com.vaadin.hilla.BrowserCallable;
import dev.diagrammer.assistant.AiClient;
import dev.diagrammer.assistant.AssistantReply;
import dev.diagrammer.assistant.AssistantRequest;
import dev.diagrammer.assistant.ChatTurn;
import dev.diagrammer.profile.Domain;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;

/**
 * Profile-assistant chat. The reply text is plain text. This endpoint does not
 * store the transcript, does not generate a diagram, and does not call AWS.
 */
@BrowserCallable
public class AssistantEndpoint {

    private final AiClient aiClient;
    private final MessageSource messages;

    public AssistantEndpoint(AiClient aiClient, MessageSource messages) {
        this.aiClient = aiClient;
        this.messages = messages;
    }

    @PermitAll
    public AssistantReply chat(AssistantRequest request) {
        if (request == null || request.profile() == null) {
            throw new IllegalArgumentException("A profile is required");
        }
        String userMessage = request.userMessage() == null ? "" : request.userMessage().trim();
        if (userMessage.isEmpty()) {
            return new AssistantReply(text("assistant.empty"), null, null, null);
        }
        List<ChatTurn> history = request.history() == null ? List.of() : request.history();
        return aiClient.chat(new AssistantRequest(request.profile(), history, userMessage));
    }

    @PermitAll
    public boolean offline() {
        return aiClient.offline();
    }

    @PermitAll
    public String greeting(Domain domain) {
        return aiClient.greeting(domain == null ? Domain.JAVA : domain);
    }

    private String text(String code) {
        return messages.getMessage(code, null, Locale.ENGLISH);
    }
}
