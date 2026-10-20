package dev.diagrammer.profile;

/**
 * Light or dark diagram palette. {@link #LIGHT} is the default.
 *
 * <p>Theme is valid with every type and every interactivity, so the combination
 * catalog has no row for it.
 */
public enum Theme implements ProfileChoice {
    LIGHT("light", "Light"),
    DARK("dark", "Dark");

    private final String wireValue;
    private final String label;

    Theme(String wireValue, String label) {
        this.wireValue = wireValue;
        this.label = label;
    }

    @Override
    public String wireValue() {
        return wireValue;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public boolean isDefault() {
        return this == LIGHT;
    }
}
