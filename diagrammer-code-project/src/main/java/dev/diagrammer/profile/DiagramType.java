package dev.diagrammer.profile;

/**
 * Layout of the diagram. {@link #AUTO} is the default.
 *
 * <p>Comparison is a v2 layout and is not a value here.
 */
public enum DiagramType implements ProfileChoice {
    AUTO("auto", "Auto"),
    HORIZONTAL_STEPS("horizontal-steps", "Horizontal steps"),
    U_SHAPE("u-shape", "U-shape request/response"),
    PYRAMID("pyramid", "Pyramid");

    private final String wireValue;
    private final String label;

    DiagramType(String wireValue, String label) {
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
        return this == AUTO;
    }
}
