package dev.diagrammer.profile;

/** One Depth chip. {@code id} is the enum the Pre-Profile stores; {@code value} is its wire token. */
public record DepthOption(Depth id, String label, String value, boolean defaultSelected) {}
