package edu.kit.kastel.exceptions;

/**
 * Thrown when game initialization fails (e.g. hand unexpectedly full during
 * initial draw).
 *
 * @author usylb
 */
public class InitializationException extends StartupException {

    /**
     * Constructs a new InitializationException.
     *
     * @param message description of the failure
     */
    public InitializationException(String message) {
        super(message);
    }
}
