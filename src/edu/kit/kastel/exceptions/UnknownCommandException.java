package edu.kit.kastel.exceptions;

/**
 * Thrown when the user input does not match any known command (select, board, move, flip, block, hand, place, show, yield, state, quit).
 *
 * @author usylb
 */
public class UnknownCommandException extends CommandException {

    private static final String UNKNOWN_COMMAND_PREFIX = "unknown command: ";

    /**
     * Constructs a new UnknownCommandException.
     *
     * @param command the unrecognised command string
     */
    public UnknownCommandException(String command) {
        super(UNKNOWN_COMMAND_PREFIX + command);
    }
}
