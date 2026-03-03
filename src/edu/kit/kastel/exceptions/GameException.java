package edu.kit.kastel.exceptions;

/**
 * Base type for all exceptions in Crown of Farmland (Krone von Ackerland).
 * Formatted messages start with "ERROR: " followed by a descriptive message (A.5).
 *
 * @author Programmieren-Team
 */
public abstract class GameException extends Exception {

    /** Prefix for all error messages (A.5). */
    private static final String ERROR_MESSAGE_PREFIX = "ERROR: ";

    /**
     * Constructs a new game exception with the given message.
     *
     * @param message the error message (without the "ERROR: " prefix)
     */
    protected GameException(String message) {
        super(message);
    }

    /**
     * Returns the formatted error message starting with "ERROR: ".
     *
     * @return the formatted message for console output
     */
    public String getFormattedMessage() {
        return ERROR_MESSAGE_PREFIX + getMessage();
    }
}
