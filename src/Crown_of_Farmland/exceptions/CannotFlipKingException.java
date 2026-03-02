package Crown_of_Farmland.exceptions;

/**
 * Thrown when flip is used on the Farmer King (the King is always revealed).
 *
 * @author Programmieren-Team
 */
public class CannotFlipKingException extends CommandException {

    /**
     * Constructs a new CannotFlipKingException.
     */
    public CannotFlipKingException() {
        super("cannot flip the Farmer King");
    }
}
