package dev.diagrammer.profile;

/** One Theme chip. {@code id} is the enum the Pre-Profile stores; {@code value} is its wire token. */
public record ThemeOption(Theme id, String label, String value, boolean defaultSelected) {}
