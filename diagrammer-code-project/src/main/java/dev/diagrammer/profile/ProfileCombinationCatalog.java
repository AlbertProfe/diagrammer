package dev.diagrammer.profile;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The one combination catalog. Static and step-by-step are absent on purpose:
 * they work with every type, so they have no row. Add a row to restrict a new pair.
 *
 * <p>The browser applies these same rows locally. Keep that pass in step with
 * {@link #resolve(PreProfile)}: force a type from the original profile, then
 * collect disables from the forced profile.
 */
public final class ProfileCombinationCatalog {

    static final String MULTI_SCENARIO_TOOLTIP = "Multi-scenario works with U-shape or Auto only.";

    static final String COMPARE_TOOLTIP = "Comparison layout is v2, so Compare concepts stays on Auto.";

    private static final List<CombinationRule> RULES = List.of(
            new CombinationRule(
                    "multi-scenario-limits-type",
                    MULTI_SCENARIO_TOOLTIP,
                    null,
                    Interactivity.MULTI_SCENARIO,
                    null,
                    List.of(),
                    List.of(DiagramType.HORIZONTAL_STEPS, DiagramType.PYRAMID),
                    null),
            new CombinationRule(
                    "pyramid-limits-multi-scenario",
                    MULTI_SCENARIO_TOOLTIP,
                    null,
                    null,
                    DiagramType.PYRAMID,
                    List.of(Interactivity.MULTI_SCENARIO),
                    List.of(),
                    null),
            new CombinationRule(
                    "horizontal-steps-limits-multi-scenario",
                    MULTI_SCENARIO_TOOLTIP,
                    null,
                    null,
                    DiagramType.HORIZONTAL_STEPS,
                    List.of(Interactivity.MULTI_SCENARIO),
                    List.of(),
                    null),
            new CombinationRule(
                    "compare-keeps-type-on-auto",
                    COMPARE_TOOLTIP,
                    Goal.COMPARE,
                    null,
                    null,
                    List.of(),
                    List.of(DiagramType.HORIZONTAL_STEPS, DiagramType.U_SHAPE, DiagramType.PYRAMID),
                    DiagramType.AUTO));

    private ProfileCombinationCatalog() {}

    public static List<CombinationRule> rules() {
        return RULES;
    }

    public static ProfileCatalog catalog() {
        return new ProfileCatalog(
                PreProfile.defaults(),
                options(Domain.values(), DomainOption::new),
                options(Goal.values(), GoalOption::new),
                options(Interactivity.values(), InteractivityOption::new),
                options(DiagramType.values(), DiagramTypeOption::new),
                options(Depth.values(), DepthOption::new),
                options(Audience.values(), AudienceOption::new),
                RULES);
    }

    /**
     * Applies the catalog. A forced type (Compare concepts stays on Auto) is
     * written onto the profile. Other illegal pairs are reported as disabled
     * chips; the current value is left as the caller set it.
     */
    public static ResolvedProfile resolve(PreProfile profile) {
        PreProfile forced = forceType(profile);
        return new ResolvedProfile(forced, disabledInteractivity(forced), disabledTypes(forced));
    }

    private static PreProfile forceType(PreProfile profile) {
        DiagramType forced = profile.type();
        for (CombinationRule rule : RULES) {
            if (rule.forceType() != null && matches(rule, profile)) {
                forced = rule.forceType();
            }
        }
        if (forced == profile.type()) {
            return profile;
        }
        return profile.withType(forced);
    }

    private static Map<Interactivity, String> disabledInteractivity(PreProfile profile) {
        Map<Interactivity, String> disabled = new LinkedHashMap<>();
        for (CombinationRule rule : RULES) {
            if (!matches(rule, profile)) {
                continue;
            }
            for (Interactivity interactivity : rule.disableInteractivity()) {
                disabled.putIfAbsent(interactivity, rule.tooltip());
            }
        }
        return Map.copyOf(disabled);
    }

    private static Map<DiagramType, String> disabledTypes(PreProfile profile) {
        Map<DiagramType, String> disabled = new LinkedHashMap<>();
        for (CombinationRule rule : RULES) {
            if (!matches(rule, profile)) {
                continue;
            }
            for (DiagramType type : rule.disableTypes()) {
                disabled.putIfAbsent(type, rule.tooltip());
            }
        }
        return Map.copyOf(disabled);
    }

    private static boolean matches(CombinationRule rule, PreProfile profile) {
        if (rule.whenGoal() != null && rule.whenGoal() != profile.goal()) {
            return false;
        }
        if (rule.whenInteractivity() != null && rule.whenInteractivity() != profile.interactivity()) {
            return false;
        }
        if (rule.whenType() != null && rule.whenType() != profile.type()) {
            return false;
        }
        return true;
    }

    private static <E extends Enum<E> & ProfileChoice, O> List<O> options(E[] values, OptionFactory<E, O> factory) {
        return Arrays.stream(values)
                .map(choice -> factory.create(choice, choice.label(), choice.wireValue(), choice.isDefault()))
                .toList();
    }

    @FunctionalInterface
    private interface OptionFactory<E, O> {
        O create(E id, String label, String value, boolean defaultSelected);
    }
}
