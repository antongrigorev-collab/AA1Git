package edu.kit.kastel.exceptions;

/**
 * Thrown when a required startup argument is missing (e.g. seed, units, or
 * deck/deck1+deck2).
 *
 * @author usylb
 */
public class MissingArgumentException extends StartupException {

    private static final String MISSING_ARGUMENT_PREFIX = "missing required argument: ";

    /**
     * Constructs a new MissingArgumentException.
     *
     * @param argumentName the name of the missing argument
     */
    public MissingArgumentException(String argumentName) {
        super(MISSING_ARGUMENT_PREFIX + argumentName);
    }
}
