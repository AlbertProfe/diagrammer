package dev.diagrammer.profile;

import java.util.List;

/**
 * Chip groups and combination rules for the Diagram Orchestrator.
 * Defaults are a {@link PreProfile} so the browser and the server share one shape.
 */
public record ProfileCatalog(
        PreProfile defaults,
        List<DomainOption> domains,
        List<GoalOption> goals,
        List<InteractivityOption> interactivity,
        List<DiagramTypeOption> types,
        List<DepthOption> depths,
        List<AudienceOption> audiences,
        List<CombinationRule> rules) {}
