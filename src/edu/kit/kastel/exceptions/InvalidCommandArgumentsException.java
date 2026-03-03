package edu.kit.kastel.exceptions;

/**
 * Thrown when a command is called with wrong or invalid arguments (e.g. wrong count or format).
 *
 * @author usylb
 */
public class InvalidCommandArgumentsException extends CommandException {

    /**
     * Constructs a new InvalidCommandArgumentsException.
     *
     * @param message description of the error
     */
    public InvalidCommandArgumentsException(String message) {
        super(message);
    }
}
