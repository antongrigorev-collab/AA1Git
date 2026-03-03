package edu.kit.kastel.exceptions;

/**
 * Thrown when the deck file has invalid content (e.g. wrong line count, total not 40, invalid numbers).
 *
 * @author usylb
 */
public class InvalidDeckFileException extends StartupException {

    /**
     * Constructs a new InvalidDeckFileException.
     *
     * @param message description of the error
     */
    public InvalidDeckFileException(String message) {
        super(message);
    }
}
