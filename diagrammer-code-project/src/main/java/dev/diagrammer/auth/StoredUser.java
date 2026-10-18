package dev.diagrammer.auth;

/**
 * One row of {@code auth/users.json}.
 *
 * <p>Passwords are plaintext with the {@code {noop}} prefix for v1.
 */
public record StoredUser(String userId, String username, String password, String displayName) {}
