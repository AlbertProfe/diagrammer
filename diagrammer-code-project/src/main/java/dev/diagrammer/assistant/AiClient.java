package dev.diagrammer.assistant;

import dev.diagrammer.profile.Domain;

/**
 * Model roles for Diagrammer. v0.0.4 implements the chat role only.
 *
 * <p>The JSON planner and the diagrammer/renderer roles are not on this
 * interface yet. Nothing in this release calls AWS.
 */
public interface AiClient {

    /** Profile-assistant turn. The {@link AssistantReply#text()} is plain text. */
    AssistantReply chat(AssistantRequest request);

    /** Opening line for the selected domain, including that domain's topic example. */
    String greeting(Domain domain);

    /**
     * True when this client is the scripted stand-in and no model is called.
     * The chat panel shows the offline-assistant note while this is true.
     */
    boolean offline();

    /**
     * Instructions for the chat role. Tells the model to complete the profile
     * only and not to output diagrams, specs, or HTML.
     */
    String systemPrompt();
}
