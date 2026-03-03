package edu.kit.kastel.exceptions;

/**
 * Thrown when a required startup argument is missing (e.g. seed, units, or
 * deck/deck1+deck2).
 *
 * @author usylb
 */
public class MissingArgumentException extends StartupException {

    /**
     * Constructs a new MissingArgumentException.
     *
     * @param argumentName the name of the missing argument
     */
    public MissingArgumentException(String argumentName) {
        super("missing required argument: " + argumentName);
    }
}
