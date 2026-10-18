package dev.diagrammer.auth;

import java.util.List;
import java.util.Optional;

/**
 * Where login credentials come from. The production implementation reads
 * {@code auth/users.json} from S3. Tests and the local profile use a fixture.
 */
public interface UserStore {

    List<StoredUser> findAll();

    default Optional<StoredUser> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return findAll().stream().filter(user -> username.equals(user.username())).findFirst();
    }
}
