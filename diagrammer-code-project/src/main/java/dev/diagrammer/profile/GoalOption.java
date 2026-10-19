package dev.diagrammer.profile;

/** One Goal chip. {@code id} is the enum the Pre-Profile stores; {@code value} is its wire token. */
public record GoalOption(Goal id, String label, String value, boolean defaultSelected) {}
