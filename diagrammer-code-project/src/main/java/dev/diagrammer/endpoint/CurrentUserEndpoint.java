package dev.diagrammer.endpoint;

import com.vaadin.hilla.BrowserCallable;
import dev.diagrammer.auth.DiagrammerUser;
import dev.diagrammer.auth.UserStoragePrefix;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;

/**
 * Protected Hilla endpoint. Anonymous callers receive 401 from Spring Security
 * because the method is not {@code @AnonymousAllowed}.
 */
@BrowserCallable
public class CurrentUserEndpoint {

    @PermitAll
    public CurrentUser getCurrentUser() {
        DiagrammerUser user = UserStoragePrefix.currentUser();
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return new CurrentUser(user.getUserId(), user.getUsername(), user.getDisplayName(), roles);
    }
}
