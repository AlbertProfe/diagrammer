package dev.diagrammer.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.diagrammer.DiagrammerApplication;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.Profiles;
import org.springframework.core.env.StandardEnvironment;

class UserStoreProfileGuardTest {

    @Test
    void noProfileAndNoS3EnvFailsWithoutActivatingTheFixture() {
        ConfigurableEnvironment environment = environment();

        assertThatThrownBy(() -> UserStoreProfileGuard.apply(environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(UserStoreProfileGuard.FAILURE_MESSAGE);
        assertThat(environment.acceptsProfiles(Profiles.of("local"))).isFalse();
        assertThat(environment.acceptsProfiles(Profiles.of("test"))).isFalse();
        assertThat(environment.acceptsProfiles(Profiles.of("s3"))).isFalse();
    }

    @Test
    void bucketWithoutRegionDoesNotActivateS3() {
        ConfigurableEnvironment environment = environment(UserStoreProfileGuard.BUCKET_ENV, "diagrammer-bucket");

        assertThatThrownBy(() -> UserStoreProfileGuard.apply(environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("fixture login is dev-only");
        assertThat(environment.acceptsProfiles(Profiles.of("s3"))).isFalse();
    }

    @Test
    void noProfileWithBucketAndRegionActivatesS3NotTheFixture() {
        ConfigurableEnvironment environment = environment(
                UserStoreProfileGuard.BUCKET_ENV, "diagrammer-bucket",
                UserStoreProfileGuard.REGION_ENV, "example-region");

        UserStoreProfileGuard.apply(environment);

        assertThat(environment.acceptsProfiles(Profiles.of("s3"))).isTrue();
        assertThat(environment.acceptsProfiles(Profiles.of("local"))).isFalse();
        assertThat(environment.acceptsProfiles(Profiles.of("test"))).isFalse();
    }

    @Test
    void explicitLocalProfileStaysOnTheFixtureEvenWhenS3EnvIsSet() {
        ConfigurableEnvironment environment = environment(
                UserStoreProfileGuard.BUCKET_ENV, "diagrammer-bucket",
                UserStoreProfileGuard.REGION_ENV, "example-region");
        environment.addActiveProfile("local");

        UserStoreProfileGuard.apply(environment);

        assertThat(environment.acceptsProfiles(Profiles.of("local"))).isTrue();
        assertThat(environment.acceptsProfiles(Profiles.of("s3"))).isFalse();
    }

    @Test
    void applicationRefusesToStartWithoutAProfileOrS3Env() {
        assertThatThrownBy(() -> SpringApplication.run(
                        DiagrammerApplication.class,
                        "--spring.profiles.active=",
                        "--DIAGRAMMER_S3_BUCKET=",
                        "--AWS_REGION=",
                        "--server.port=0"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(UserStoreProfileGuard.FAILURE_MESSAGE);
    }

    private static ConfigurableEnvironment environment(String... keysAndValues) {
        StandardEnvironment environment = new StandardEnvironment();
        Map<String, Object> overrides = new HashMap<>();
        overrides.put(UserStoreProfileGuard.BUCKET_ENV, "");
        overrides.put(UserStoreProfileGuard.REGION_ENV, "");
        for (int i = 0; i < keysAndValues.length; i += 2) {
            overrides.put(keysAndValues[i], keysAndValues[i + 1]);
        }
        environment.getPropertySources().addFirst(new MapPropertySource("test-overrides", overrides));
        return environment;
    }
}
