package dev.diagrammer.profile;

/** How the reader moves through the diagram. {@link #STEP_BY_STEP} is the default. */
public enum Interactivity implements ProfileChoice {
    STATIC("static", "Static"),
    STEP_BY_STEP("step-by-step", "Step-by-step"),
    MULTI_SCENARIO("multi-scenario", "Multi-scenario");

    private final String wireValue;
    private final String label;

    Interactivity(String wireValue, String label) {
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
        return this == STEP_BY_STEP;
    }
}
