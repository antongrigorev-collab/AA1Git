package edu.kit.kastel.exceptions;

/**
 * Thrown when the user input does not match any known command (select, board, move, flip, block, hand, place, show, yield, state, quit).
 *
 * @author Programmieren-Team
 */
public class UnknownCommandException extends CommandException {

    /**
     * Constructs a new UnknownCommandException.
     *
     * @param command the unrecognised command string
     */
    public UnknownCommandException(String command) {
        super("unknown command: " + command);
    }
}
