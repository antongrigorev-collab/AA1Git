package edu.kit.kastel.exceptions;

/**
 * Thrown when a startup key is given more than once (e.g. seed, units, deck).
 *
 * @author Programmieren-Team
 */
public class DuplicateArgumentException extends StartupException {

    /**
     * Constructs a new DuplicateArgumentException.
     *
     * @param key the duplicate key
     */
    public DuplicateArgumentException(String key) {
        super("duplicate argument: " + key);
    }
}
