package dev.diagrammer.endpoint;

import com.vaadin.hilla.BrowserCallable;
import dev.diagrammer.profile.ProfileCatalog;
import dev.diagrammer.profile.ProfileCombinationCatalog;
import jakarta.annotation.security.PermitAll;

/**
 * Publishes the Pre-Profile contract and the chip catalog. The browser keeps
 * the selection; this endpoint does not validate, store, or generate.
 */
@BrowserCallable
public class ProfileEndpoint {

    @PermitAll
    public ProfileCatalog catalog() {
        return ProfileCombinationCatalog.catalog();
    }
}
