package dev.diagrammer.profile;

/** How much detail the diagram carries. {@link #WITH_CODE} is the default. */
public enum Depth implements ProfileChoice {
    OVERVIEW("overview", "Overview"),
    WITH_CODE("with-code", "With code"),
    WITH_CODE_PARALLELS("with-code-parallels", "With code + framework parallels");

    private final String wireValue;
    private final String label;

    Depth(String wireValue, String label) {
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
        return this == WITH_CODE;
    }
}
