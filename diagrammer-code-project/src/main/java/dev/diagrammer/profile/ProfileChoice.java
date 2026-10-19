package dev.diagrammer.profile;

/**
 * One chip in a profile group. The wire value is the Pre-Profile token later
 * stories send on. The label is the chip text.
 */
public interface ProfileChoice {

    String wireValue();

    String label();

    boolean isDefault();
}
