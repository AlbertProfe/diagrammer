package dev.diagrammer.assistant;

/** One earlier line in the profile-assistant transcript. Roles are {@code user} and {@code assistant}. */
public record ChatTurn(String role, String text) {

    public static final String USER = "user";

    public static final String ASSISTANT = "assistant";

    public static ChatTurn user(String text) {
        return new ChatTurn(USER, text);
    }

    public static ChatTurn assistant(String text) {
        return new ChatTurn(ASSISTANT, text);
    }
}
