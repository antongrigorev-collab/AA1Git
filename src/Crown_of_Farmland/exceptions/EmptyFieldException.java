package Crown_of_Farmland.exceptions;

/**
 * Thrown when move, flip or block is used on an empty field, or when a command
 * expects a unit on the selected field but it is empty.
 *
 * @author Programmieren-Team
 */
public class EmptyFieldException extends CommandException {

    /**
     * Constructs a new EmptyFieldException.
     *
     * @param field the coordinate of the empty field
     */
    public EmptyFieldException(String field) {
        super("no unit on field " + field);
    }
}
