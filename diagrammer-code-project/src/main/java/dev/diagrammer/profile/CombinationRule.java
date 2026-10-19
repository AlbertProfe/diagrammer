package dev.diagrammer.profile;

import java.util.List;
import org.springframework.lang.Nullable;

/**
 * One row of the combination catalog. A null {@code when*} field matches any
 * current value. The catalog grows by adding rows, not by new branches in the UI.
 */
public record CombinationRule(
        String id,
        String tooltip,
        @Nullable Goal whenGoal,
        @Nullable Interactivity whenInteractivity,
        @Nullable DiagramType whenType,
        List<Interactivity> disableInteractivity,
        List<DiagramType> disableTypes,
        @Nullable DiagramType forceType) {}
