package dev.diagrammer.endpoint;

import com.vaadin.hilla.BrowserCallable;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.MessageSource;

/** Publishes the English resource bundle to the browser. */
@BrowserCallable
public class MessagesEndpoint {

    private final MessageSource messages;

    public MessagesEndpoint(MessageSource messages) {
        this.messages = messages;
    }

    @PermitAll
    public UiCopy ui() {
        return UiCopy.english(messages);
    }
}
