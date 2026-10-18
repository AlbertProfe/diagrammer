package dev.diagrammer.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Resolves the S3 prefix for the logged-in user: {@code users/{userId}/}.
 */
public final class UserStoragePrefix {

    private UserStoragePrefix() {}

    public static String forUserId(String userId) {
        if (userId == null
                || userId.isBlank()
                || userId.contains("/")
                || userId.contains("\\")
                || userId.contains("..")) {
            throw new IllegalArgumentException("userId must be a single path segment");
        }
        return "users/" + userId + "/";
    }

    public static String forCurrentUser() {
        return forUserId(currentUser().getUserId());
    }

    public static DiagrammerUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof DiagrammerUser user)) {
            throw new IllegalStateException("No logged-in user");
        }
        return user;
    }
}
