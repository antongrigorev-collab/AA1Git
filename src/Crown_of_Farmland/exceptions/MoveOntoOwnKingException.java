package Crown_of_Farmland.exceptions;

/**
 * Thrown when a unit (not the King) tries to move onto the own Farmer King's field.
 *
 * @author Programmieren-Team
 */
public class MoveOntoOwnKingException extends CommandException {

    /**
     * Constructs a new MoveOntoOwnKingException.
     */
    public MoveOntoOwnKingException() {
        super("cannot move onto your own Farmer King's field");
    }
}
