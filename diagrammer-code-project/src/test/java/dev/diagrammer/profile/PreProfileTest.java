package dev.diagrammer.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class PreProfileTest {

    private final JsonMapper json = JsonMapper.builder().build();

    @Test
    void preProfileSerializesTheme() {
        JsonNode defaults = json.readTree(json.writeValueAsString(PreProfile.defaults()));

        assertThat(defaults.get("theme").asString()).isEqualTo("LIGHT");
        assertThat(defaults.get("depth").asString()).isEqualTo("WITH_CODE");

        PreProfile dark = new PreProfile(
                null,
                Domain.JAVA,
                Goal.EXPLAIN_FLOW,
                Interactivity.STEP_BY_STEP,
                DiagramType.AUTO,
                Depth.WITH_CODE,
                Theme.DARK,
                Audience.BEGINNER,
                null);
        JsonNode serialized = json.readTree(json.writeValueAsString(dark));
        assertThat(serialized.get("theme").asString()).isEqualTo("DARK");
        assertThat(dark.withType(DiagramType.PYRAMID).theme()).isEqualTo(Theme.DARK);
    }
}
