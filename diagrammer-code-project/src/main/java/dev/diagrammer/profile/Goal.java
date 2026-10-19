package dev.diagrammer.profile;

/**
 * What the diagram is for. {@link #EXPLAIN_FLOW} is the default.
 *
 * <p>Wire values are the v0.0.2 scope on issue 4: {@code explain-flow},
 * {@code use-case}, {@code compare}, {@code layers}, {@code other}.
 */
public enum Goal implements ProfileChoice {
    EXPLAIN_FLOW("explain-flow", "Explain flow"),
    USE_CASE("use-case", "Walk through use case"),
    COMPARE("compare", "Compare concepts"),
    LAYERS("layers", "Show layers"),
    OTHER("other", "Other");

    private final String wireValue;
    private final String label;

    Goal(String wireValue, String label) {
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
        return this == EXPLAIN_FLOW;
    }
}
