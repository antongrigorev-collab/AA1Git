package Crown_of_Farmland.exceptions;

/**
 * Thrown when the place target is not adjacent to the own Farmer King (up to 8 fields including diagonals).
 *
 * @author Programmieren-Team
 */
public class PlaceFieldNotAdjacentToKingException extends CommandException {

    /**
     * Constructs a new PlaceFieldNotAdjacentToKingException.
     *
     * @param field the target field coordinate
     */
    public PlaceFieldNotAdjacentToKingException(String field) {
        super("cannot place on " + field + ": not adjacent to Farmer King");
    }
}
