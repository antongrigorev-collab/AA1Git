package edu.kit.kastel.exceptions;

/**
 * Base type for exceptions during command execution. The program prints the
 * error message and continues waiting for the next input instead of terminating.
 *
 * @author Programmieren-Team
 */
public abstract class CommandException extends GameException {

    /**
     * Constructs a new command exception with the given message.
     *
     * @param message the error message
     */
    protected CommandException(String message) {
        super(message);
    }
}
