package dev.diagrammer.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

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
                null,
                null);
        JsonNode serialized = json.readTree(json.writeValueAsString(dark));
        assertThat(serialized.get("theme").asString()).isEqualTo("DARK");
        assertThat(dark.withType(DiagramType.PYRAMID).theme()).isEqualTo(Theme.DARK);
        assertThat(serialized.get("refinements").isNull()).isTrue();
    }

    @Test
    void jsonWithoutRefinementsStillDeserializes() throws Exception {
        ObjectNode node = (ObjectNode) json.readTree(json.writeValueAsString(PreProfile.defaults()));
        node.remove("refinements");

        PreProfile profile = json.readValue(node.toString(), PreProfile.class);

        assertThat(profile.refinements()).isNull();
        assertThat(profile.title()).isNull();
        assertThat(profile.topic()).isNull();
        assertThat(profile.theme()).isEqualTo(Theme.LIGHT);
        assertThat(profile.domain()).isEqualTo(Domain.JAVA);
    }

    @Test
    void refinementsRoundTrip() throws Exception {
        PreProfile profile = PreProfile.defaults().withRefinements("the CLI path");

        PreProfile again = json.readValue(json.writeValueAsString(profile), PreProfile.class);

        assertThat(again).isEqualTo(profile);
        assertThat(again.withType(DiagramType.PYRAMID).refinements()).isEqualTo("the CLI path");
        assertThat(again.withTopic("How adopt-dog travels from CLI to Repository").refinements())
                .isEqualTo("the CLI path");
    }
}
