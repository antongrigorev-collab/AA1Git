package edu.kit.kastel.exceptions;

/**
 * Base type for exceptions during program startup (e.g. invalid config or files).
 * These cause the program to terminate after printing the error message.
 *
 * @author usylb
 */
public abstract class StartupException extends GameException {

    /**
     * Constructs a new startup exception with the given message.
     *
     * @param message the error message
     */
    protected StartupException(String message) {
        super(message);
    }
}
