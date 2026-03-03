package edu.kit.kastel.exceptions;

/**
 * Thrown when the board symbol file has invalid content (e.g. not exactly 29 characters in one line).
 *
 * @author Programmieren-Team
 */
public class InvalidBoardFileException extends StartupException {

    /**
     * Constructs a new InvalidBoardFileException.
     *
     * @param message description of the error
     */
    public InvalidBoardFileException(String message) {
        super(message);
    }
}
