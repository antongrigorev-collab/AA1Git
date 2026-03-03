package edu.kit.kastel.exceptions;

/**
 * Thrown when move, flip or block is used on a unit that has already moved this turn.
 *
 * @author Programmieren-Team
 */
public class UnitAlreadyMovedException extends CommandException {

    private static final String ALREADY_MOVED_SUFFIX = " has already moved this turn";

    /**
     * Constructs a new UnitAlreadyMovedException.
     *
     * @param unitName the name of the unit that already moved
     */
    public UnitAlreadyMovedException(String unitName) {
        super(unitName + ALREADY_MOVED_SUFFIX);
    }
}
