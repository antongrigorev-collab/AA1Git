package edu.kit.kastel.exceptions;

/**
 * Thrown when an invalid integer is given (e.g. seed, deck count, or ATK/DEF in units file).
 *
 * @author Programmieren-Team
 */
public class InvalidIntegerException extends StartupException {

    private static final String INVALID_INTEGER_PREFIX = "invalid integer: ";

    /**
     * Constructs a new InvalidIntegerException.
     *
     * @param value the invalid value string
     */
    public InvalidIntegerException(String value) {
        super(INVALID_INTEGER_PREFIX + value);
    }
}
