package dev.diagrammer.profile;

/** Subject area of the diagram. {@link #JAVA} is the default. */
public enum Domain implements ProfileChoice {
    JAVA("java", "Java"),
    SPRING_BOOT("spring-boot", "Spring Boot"),
    PYTHON("python", "Python"),
    ML("ml", "ML"),
    REACT("react", "React");

    private final String wireValue;
    private final String label;

    Domain(String wireValue, String label) {
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
        return this == JAVA;
    }
}
