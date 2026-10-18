package dev.diagrammer.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Classpath stand-in for the S3 credentials file. Active for the {@code local}
 * and {@code test} profiles so neither needs AWS.
 */
@Component
@Profile({"local", "test"})
public class FixtureUserStore implements UserStore {

    private final List<StoredUser> users;

    public FixtureUserStore(UsersJson usersJson) throws IOException {
        try (InputStream in = FixtureUserStore.class.getResourceAsStream("/fixture/users.json")) {
            if (in == null) {
                throw new IllegalStateException("Missing classpath resource fixture/users.json");
            }
            this.users = List.copyOf(usersJson.parse(in.readAllBytes()));
        }
    }

    @Override
    public List<StoredUser> findAll() {
        return users;
    }
}
