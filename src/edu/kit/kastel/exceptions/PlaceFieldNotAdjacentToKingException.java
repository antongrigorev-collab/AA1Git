package edu.kit.kastel.exceptions;

/**
 * Thrown when the place target is not adjacent to the own Farmer King (up to 8 fields including diagonals).
 *
 * @author usylb
 */
public class PlaceFieldNotAdjacentToKingException extends CommandException {

    private static final String CANNOT_PLACE_ON_PREFIX = "cannot place on ";
    private static final String NOT_ADJACENT_TO_KING_SUFFIX = ": not adjacent to Farmer King";

    /**
     * Constructs a new PlaceFieldNotAdjacentToKingException.
     *
     * @param field the target field coordinate
     */
    public PlaceFieldNotAdjacentToKingException(String field) {
        super(CANNOT_PLACE_ON_PREFIX + field + NOT_ADJACENT_TO_KING_SUFFIX);
    }
}
