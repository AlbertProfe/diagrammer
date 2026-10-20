package dev.diagrammer.assistant;

import dev.diagrammer.profile.PreProfile;
import java.util.List;

/**
 * One chat turn. {@code history} is the earlier transcript, not including
 * {@code userMessage}. Chat state stays in the browser; this request is not stored.
 */
public record AssistantRequest(PreProfile profile, List<ChatTurn> history, String userMessage) {}
