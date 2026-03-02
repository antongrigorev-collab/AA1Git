package Crown_of_Farmland.exceptions;

/**
 * Thrown when the move target is more than one step away (only cardinal moves allowed).
 *
 * @author Programmieren-Team
 */
public class FieldTooFarException extends CommandException {

    /**
     * Constructs a new FieldTooFarException.
     *
     * @param from the source field coordinate
     * @param to   the target field coordinate
     */
    public FieldTooFarException(String from, String to) {
        super("cannot move from " + from + " to " + to + ": too far away");
    }
}
