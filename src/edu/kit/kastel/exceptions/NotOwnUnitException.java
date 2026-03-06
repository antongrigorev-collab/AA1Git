package edu.kit.kastel.exceptions;

/**
 * Thrown when move, flip or block is used on an enemy unit (only own units can be controlled).
 *
 * @author usylb
 */
public class NotOwnUnitException extends CommandException {

    private static final String CANNOT_CONTROL_ENEMY_UNIT_MESSAGE = "cannot control an enemy unit";

    /**
     * Constructs a new NotOwnUnitException.
     */
    public NotOwnUnitException() {
        super(CANNOT_CONTROL_ENEMY_UNIT_MESSAGE);
    }
}
