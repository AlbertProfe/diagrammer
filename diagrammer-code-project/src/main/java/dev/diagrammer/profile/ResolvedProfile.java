package dev.diagrammer.profile;

import java.util.Map;

/**
 * A profile after the combination catalog has been applied, plus the chip
 * tooltips for options the current selection rules out.
 */
public record ResolvedProfile(
        PreProfile profile,
        Map<Interactivity, String> disabledInteractivity,
        Map<DiagramType, String> disabledTypes) {}
