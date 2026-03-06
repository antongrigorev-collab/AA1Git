package edu.kit.kastel.exceptions;

/**
 * Thrown when a startup key is given more than once (e.g. seed, units, deck).
 *
 * @author usylb
 */
public class DuplicateArgumentException extends StartupException {

    private static final String DUPLICATE_ARGUMENT_PREFIX = "duplicate argument: ";

    /**
     * Constructs a new DuplicateArgumentException.
     *
     * @param key the duplicate key
     */
    public DuplicateArgumentException(String key) {
        super(DUPLICATE_ARGUMENT_PREFIX + key);
    }
}
