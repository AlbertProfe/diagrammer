package dev.diagrammer.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProfileCombinationCatalogTest {

    @Test
    void defaultsMatchTheChipGroups() {
        PreProfile defaults = PreProfile.defaults();

        assertThat(defaults.title()).isNull();
        assertThat(defaults.topic()).isNull();
        assertThat(defaults.domain()).isEqualTo(Domain.JAVA);
        assertThat(defaults.goal()).isEqualTo(Goal.EXPLAIN_FLOW);
        assertThat(defaults.interactivity()).isEqualTo(Interactivity.STEP_BY_STEP);
        assertThat(defaults.type()).isEqualTo(DiagramType.AUTO);
        assertThat(defaults.depth()).isEqualTo(Depth.WITH_CODE);
        assertThat(defaults.audience()).isEqualTo(Audience.BEGINNER);

        ProfileCatalog catalog = ProfileCombinationCatalog.catalog();
        assertThat(catalog.defaults()).isEqualTo(defaults);
        assertThat(catalog.rules()).containsExactlyElementsOf(ProfileCombinationCatalog.rules());
    }

    @Test
    void catalogListsEveryChipInOrderWithWireValues() {
        ProfileCatalog catalog = ProfileCombinationCatalog.catalog();

        assertThat(catalog.domains())
                .extracting(DomainOption::label, DomainOption::value, DomainOption::defaultSelected)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Java", "java", true),
                        org.assertj.core.groups.Tuple.tuple("Spring Boot", "spring-boot", false),
                        org.assertj.core.groups.Tuple.tuple("Python", "python", false),
                        org.assertj.core.groups.Tuple.tuple("ML", "ml", false),
                        org.assertj.core.groups.Tuple.tuple("React", "react", false));
        assertThat(catalog.goals())
                .extracting(GoalOption::label, GoalOption::value, GoalOption::defaultSelected)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Explain flow", "explain-flow", true),
                        org.assertj.core.groups.Tuple.tuple("Walk through use case", "use-case", false),
                        org.assertj.core.groups.Tuple.tuple("Compare concepts", "compare", false),
                        org.assertj.core.groups.Tuple.tuple("Show layers", "layers", false),
                        org.assertj.core.groups.Tuple.tuple("Other", "other", false));
        assertThat(catalog.interactivity())
                .extracting(InteractivityOption::label, InteractivityOption::value, InteractivityOption::defaultSelected)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Static", "static", false),
                        org.assertj.core.groups.Tuple.tuple("Step-by-step", "step-by-step", true),
                        org.assertj.core.groups.Tuple.tuple("Multi-scenario", "multi-scenario", false));
        assertThat(catalog.types())
                .extracting(DiagramTypeOption::label, DiagramTypeOption::value, DiagramTypeOption::defaultSelected)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Auto", "auto", true),
                        org.assertj.core.groups.Tuple.tuple("Horizontal steps", "horizontal-steps", false),
                        org.assertj.core.groups.Tuple.tuple("U-shape request/response", "u-shape", false),
                        org.assertj.core.groups.Tuple.tuple("Pyramid", "pyramid", false));
        assertThat(catalog.depths())
                .extracting(DepthOption::label, DepthOption::value, DepthOption::defaultSelected)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Overview", "overview", false),
                        org.assertj.core.groups.Tuple.tuple("With code", "with-code", true),
                        org.assertj.core.groups.Tuple.tuple("With code + framework parallels", "with-code-parallels", false));
        assertThat(catalog.audiences())
                .extracting(AudienceOption::label, AudienceOption::value, AudienceOption::defaultSelected)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Beginner", "beginner", true),
                        org.assertj.core.groups.Tuple.tuple("Intermediate", "intermediate", false));
    }

    @Test
    void typeHasNoComparisonLayout() {
        assertThat(DiagramType.values()).hasSize(4);
        assertThat(Arrays.stream(DiagramType.values()).map(Enum::name)).doesNotContain("COMPARISON");
        assertThat(ProfileCombinationCatalog.catalog().types())
                .extracting(DiagramTypeOption::value)
                .doesNotContain("comparison");
        assertThat(ProfileCombinationCatalog.catalog().types())
                .extracting(DiagramTypeOption::label)
                .noneMatch(label -> label.toLowerCase().contains("comparison"));
    }

    @Test
    void rulesAreOneListWithUniqueIds() {
        List<CombinationRule> rules = ProfileCombinationCatalog.rules();

        assertThat(rules).hasSize(4);
        assertThat(rules).extracting(CombinationRule::id).doesNotHaveDuplicates();
        assertThat(rules)
                .extracting(CombinationRule::id)
                .containsExactly(
                        "multi-scenario-limits-type",
                        "pyramid-limits-multi-scenario",
                        "horizontal-steps-limits-multi-scenario",
                        "compare-keeps-type-on-auto");
    }

    @Test
    void staticAndStepByStepWorkWithEveryType() {
        for (Interactivity interactivity : List.of(Interactivity.STATIC, Interactivity.STEP_BY_STEP)) {
            for (DiagramType type : DiagramType.values()) {
                ResolvedProfile resolved = ProfileCombinationCatalog.resolve(profile(Goal.EXPLAIN_FLOW, interactivity, type));

                assertThat(resolved.profile().interactivity()).isEqualTo(interactivity);
                assertThat(resolved.profile().type()).isEqualTo(type);
                assertThat(resolved.disabledTypes()).isEmpty();
                assertThat(resolved.disabledInteractivity()).doesNotContainKey(interactivity);
                if (type == DiagramType.PYRAMID || type == DiagramType.HORIZONTAL_STEPS) {
                    assertThat(resolved.disabledInteractivity())
                            .containsOnlyKeys(Interactivity.MULTI_SCENARIO)
                            .containsEntry(Interactivity.MULTI_SCENARIO, ProfileCombinationCatalog.MULTI_SCENARIO_TOOLTIP);
                } else {
                    assertThat(resolved.disabledInteractivity()).isEmpty();
                }
            }
        }
    }

    @Test
    void pyramidAndMultiScenarioDisableEachOther() {
        ResolvedProfile pyramid = ProfileCombinationCatalog.resolve(
                profile(Goal.EXPLAIN_FLOW, Interactivity.STEP_BY_STEP, DiagramType.PYRAMID));
        assertThat(pyramid.disabledInteractivity())
                .containsEntry(Interactivity.MULTI_SCENARIO, ProfileCombinationCatalog.MULTI_SCENARIO_TOOLTIP);
        assertThat(pyramid.disabledTypes()).isEmpty();

        ResolvedProfile horizontal = ProfileCombinationCatalog.resolve(
                profile(Goal.LAYERS, Interactivity.STATIC, DiagramType.HORIZONTAL_STEPS));
        assertThat(horizontal.disabledInteractivity())
                .containsEntry(Interactivity.MULTI_SCENARIO, ProfileCombinationCatalog.MULTI_SCENARIO_TOOLTIP);

        ResolvedProfile multiScenario = ProfileCombinationCatalog.resolve(
                profile(Goal.USE_CASE, Interactivity.MULTI_SCENARIO, DiagramType.AUTO));
        assertThat(multiScenario.disabledTypes())
                .containsEntry(DiagramType.PYRAMID, ProfileCombinationCatalog.MULTI_SCENARIO_TOOLTIP)
                .containsEntry(DiagramType.HORIZONTAL_STEPS, ProfileCombinationCatalog.MULTI_SCENARIO_TOOLTIP)
                .doesNotContainKey(DiagramType.AUTO)
                .doesNotContainKey(DiagramType.U_SHAPE);
        assertThat(multiScenario.disabledInteractivity()).isEmpty();

        ResolvedProfile uShape = ProfileCombinationCatalog.resolve(
                profile(Goal.EXPLAIN_FLOW, Interactivity.MULTI_SCENARIO, DiagramType.U_SHAPE));
        assertThat(uShape.profile().type()).isEqualTo(DiagramType.U_SHAPE);
        assertThat(uShape.disabledTypes()).containsOnlyKeys(DiagramType.HORIZONTAL_STEPS, DiagramType.PYRAMID);
    }

    @Test
    void compareConceptsKeepsTypeOnAuto() {
        ResolvedProfile fromPyramid = ProfileCombinationCatalog.resolve(
                profile(Goal.COMPARE, Interactivity.STEP_BY_STEP, DiagramType.PYRAMID));

        assertThat(fromPyramid.profile().goal()).isEqualTo(Goal.COMPARE);
        assertThat(fromPyramid.profile().type()).isEqualTo(DiagramType.AUTO);
        assertThat(fromPyramid.disabledTypes())
                .containsEntry(DiagramType.HORIZONTAL_STEPS, ProfileCombinationCatalog.COMPARE_TOOLTIP)
                .containsEntry(DiagramType.U_SHAPE, ProfileCombinationCatalog.COMPARE_TOOLTIP)
                .containsEntry(DiagramType.PYRAMID, ProfileCombinationCatalog.COMPARE_TOOLTIP)
                .doesNotContainKey(DiagramType.AUTO);
        assertThat(fromPyramid.disabledInteractivity()).isEmpty();

        ResolvedProfile withMultiScenario = ProfileCombinationCatalog.resolve(
                profile(Goal.COMPARE, Interactivity.MULTI_SCENARIO, DiagramType.U_SHAPE));
        assertThat(withMultiScenario.profile().type()).isEqualTo(DiagramType.AUTO);
        assertThat(withMultiScenario.profile().interactivity()).isEqualTo(Interactivity.MULTI_SCENARIO);
        assertThat(withMultiScenario.disabledInteractivity()).isEmpty();
    }

    @Test
    void otherGoalsDoNotForceTheType() {
        ResolvedProfile resolved = ProfileCombinationCatalog.resolve(
                profile(Goal.OTHER, Interactivity.STATIC, DiagramType.PYRAMID));

        assertThat(resolved.profile().type()).isEqualTo(DiagramType.PYRAMID);
        assertThat(resolved.disabledTypes()).isEmpty();
    }

    private static PreProfile profile(Goal goal, Interactivity interactivity, DiagramType type) {
        PreProfile defaults = PreProfile.defaults();
        return new PreProfile(
                defaults.title(),
                defaults.domain(),
                goal,
                interactivity,
                type,
                defaults.depth(),
                defaults.audience(),
                defaults.topic());
    }
}
