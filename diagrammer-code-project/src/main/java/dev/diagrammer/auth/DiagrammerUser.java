package dev.diagrammer.auth;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

/** Spring Security principal for a row in the credentials file. */
public class DiagrammerUser extends User {

    private final String userId;
    private final String displayName;

    public DiagrammerUser(StoredUser stored) {
        super(
                stored.username(),
                stored.password(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.userId = stored.userId();
        this.displayName = stored.displayName();
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
