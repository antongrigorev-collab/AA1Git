package edu.kit.kastel.exceptions;

/**
 * Thrown when a move is not allowed (e.g. target too far, move onto own King, King onto enemy).
 *
 * @author usylb
 */
public class InvalidMoveException extends CommandException {

    /**
     * Constructs a new InvalidMoveException.
     *
     * @param message description of the invalid move
     */
    public InvalidMoveException(String message) {
        super(message);
    }
}
