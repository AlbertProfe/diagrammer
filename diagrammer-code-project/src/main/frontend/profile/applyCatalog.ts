import type CombinationRule from 'Frontend/generated/dev/diagrammer/profile/CombinationRule';
import type PreProfile from 'Frontend/generated/dev/diagrammer/profile/PreProfile';

/**
 * Browser copy of ProfileCombinationCatalog.resolve. Force a type from the
 * original profile, then collect disables from the forced profile. New
 * combinations are rows in the catalog, not branches here.
 */
export type AppliedProfile = {
  profile: PreProfile;
  disabledInteractivity: ReadonlyMap<string, string>;
  disabledTypes: ReadonlyMap<string, string>;
};

export function applyCatalog(
  profile: PreProfile,
  rules: readonly (CombinationRule | null | undefined)[] | undefined,
): AppliedProfile {
  const catalog = (rules ?? []).filter((rule): rule is CombinationRule => rule != null);
  const forcedType = catalog.reduce((type, rule) => {
    if (rule.forceType && matches(rule, profile)) {
      return rule.forceType;
    }
    return type;
  }, profile.type);
  const forced = forcedType === profile.type ? profile : { ...profile, type: forcedType };
  return {
    profile: forced,
    disabledInteractivity: disables(catalog, forced, (rule) => rule.disableInteractivity),
    disabledTypes: disables(catalog, forced, (rule) => rule.disableTypes),
  };
}

function matches(rule: CombinationRule, profile: PreProfile): boolean {
  if (rule.whenGoal != null && rule.whenGoal !== profile.goal) {
    return false;
  }
  if (rule.whenInteractivity != null && rule.whenInteractivity !== profile.interactivity) {
    return false;
  }
  if (rule.whenType != null && rule.whenType !== profile.type) {
    return false;
  }
  return true;
}

function disables(
  rules: readonly CombinationRule[],
  profile: PreProfile,
  values: (rule: CombinationRule) => readonly (string | null | undefined)[] | undefined,
): ReadonlyMap<string, string> {
  const disabled = new Map<string, string>();
  for (const rule of rules) {
    if (!matches(rule, profile)) {
      continue;
    }
    for (const value of values(rule) ?? []) {
      if (value != null && !disabled.has(value)) {
        disabled.set(value, rule.tooltip ?? '');
      }
    }
  }
  return disabled;
}
