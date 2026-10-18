package dev.diagrammer.auth;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Profiles;

/**
 * Fail closed when nobody chose a user store.
 *
 * <p>The fixture store is opt-in ({@code local} or {@code test} only). A
 * packaged run with no profile does not load it. If {@code DIAGRAMMER_S3_BUCKET}
 * and {@code AWS_REGION} are both set, the {@code s3} profile is activated
 * instead. Otherwise startup stops with a message that names the two choices.
 */
public class UserStoreProfileGuard implements EnvironmentPostProcessor, Ordered {

    static final String BUCKET_ENV = "DIAGRAMMER_S3_BUCKET";
    static final String REGION_ENV = "AWS_REGION";

    static final String FAILURE_MESSAGE =
            "No user store selected. The fixture login is dev-only and is not active. "
                    + "For local dev, run ./mvnw spring-boot:run -Pdev or set SPRING_PROFILES_ACTIVE=local. "
                    + "For the S3 user store, set SPRING_PROFILES_ACTIVE=s3, "
                    + "or set DIAGRAMMER_S3_BUCKET and AWS_REGION and start with no profile.";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        apply(environment);
    }

    static void apply(ConfigurableEnvironment environment) {
        if (environment.acceptsProfiles(Profiles.of("local", "test", "s3"))) {
            return;
        }
        if (isSet(environment.getProperty(BUCKET_ENV)) && isSet(environment.getProperty(REGION_ENV))) {
            environment.addActiveProfile("s3");
            return;
        }
        throw new IllegalStateException(FAILURE_MESSAGE);
    }

    private static boolean isSet(String value) {
        return value != null && !value.isBlank();
    }

    @Override
    public int getOrder() {
        // After config data, so an explicit spring.profiles.active is already visible.
        return Ordered.LOWEST_PRECEDENCE;
    }
}
