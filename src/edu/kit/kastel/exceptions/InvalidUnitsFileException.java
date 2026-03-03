package edu.kit.kastel.exceptions;

/**
 * Thrown when the units file has invalid content (e.g. not exactly 4 semicolon-separated fields per line,
 * invalid ATK/DEF, more than 80 units, or trailing semicolon/extra spaces).
 *
 * @author Programmieren-Team
 */
public class InvalidUnitsFileException extends StartupException {

    /**
     * Constructs a new InvalidUnitsFileException.
     *
     * @param message description of the error
     */
    public InvalidUnitsFileException(String message) {
        super(message);
    }
}
