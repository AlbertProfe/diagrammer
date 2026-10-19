package dev.diagrammer.profile;

/** One Type chip. {@code id} is the enum the Pre-Profile stores; {@code value} is its wire token. */
public record DiagramTypeOption(DiagramType id, String label, String value, boolean defaultSelected) {}
