package dev.diagrammer.profile;

/** Who the diagram is for. {@link #BEGINNER} is the default. */
public enum Audience implements ProfileChoice {
    BEGINNER("beginner", "Beginner"),
    INTERMEDIATE("intermediate", "Intermediate");

    private final String wireValue;
    private final String label;

    Audience(String wireValue, String label) {
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
        return this == BEGINNER;
    }
}
