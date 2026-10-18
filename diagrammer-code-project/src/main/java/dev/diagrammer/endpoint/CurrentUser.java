package dev.diagrammer.endpoint;

import java.util.List;

/** The logged-in user, as returned to the React app. */
public record CurrentUser(String userId, String username, String displayName, List<String> roles) {}
