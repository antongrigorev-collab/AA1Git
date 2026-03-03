package edu.kit.kastel.exceptions;

/**
 * Thrown when move, flip or block is used on an enemy unit (only own units can be controlled).
 *
 * @author Programmieren-Team
 */
public class NotOwnUnitException extends CommandException {

    /**
     * Constructs a new NotOwnUnitException.
     */
    public NotOwnUnitException() {
        super("cannot control an enemy unit");
    }
}
