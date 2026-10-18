package dev.diagrammer.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class UserStoragePrefixTest {

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void prefixForUserIdUsesUsersFolder() {
        assertThat(UserStoragePrefix.forUserId("u1")).isEqualTo("users/u1/");
    }

    @Test
    void currentUserPrefixUsesTheLoggedInUserId() {
        DiagrammerUser user = new DiagrammerUser(new StoredUser("u1", "albert", "{noop}diagrammer", "Albert"));
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

        assertThat(UserStoragePrefix.forCurrentUser()).isEqualTo("users/u1/");
    }

    @Test
    void rejectsAUserIdThatCouldEscapeTheFolder() {
        assertThatThrownBy(() -> UserStoragePrefix.forUserId("a/b"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> UserStoragePrefix.forUserId(".."))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
