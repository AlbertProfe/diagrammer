package dev.diagrammer.profile;

/** One Audience chip. {@code id} is the enum the Pre-Profile stores; {@code value} is its wire token. */
public record AudienceOption(Audience id, String label, String value, boolean defaultSelected) {}
