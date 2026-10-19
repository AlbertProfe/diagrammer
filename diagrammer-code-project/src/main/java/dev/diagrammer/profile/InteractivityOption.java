package dev.diagrammer.profile;

/** One Interactivity chip. {@code id} is the enum the Pre-Profile stores; {@code value} is its wire token. */
public record InteractivityOption(Interactivity id, String label, String value, boolean defaultSelected) {}
