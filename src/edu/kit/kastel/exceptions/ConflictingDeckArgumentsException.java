package edu.kit.kastel.exceptions;

/**
 * Thrown when deck is used together with deck1/deck2, or only one of deck1/deck2 is given.
 *
 * @author usylb
 */
public class ConflictingDeckArgumentsException extends StartupException {

    /**
     * Constructs a new ConflictingDeckArgumentsException.
     *
     * @param message description of the conflict
     */
    public ConflictingDeckArgumentsException(String message) {
        super(message);
    }
}
