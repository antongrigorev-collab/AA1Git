package Crown_of_Farmland.exceptions;

/**
 * Thrown when a field coordinate does not match A1–G7 (column A–G, row 1–7).
 *
 * @author Programmieren-Team
 */
public class InvalidFieldException extends CommandException {

    /**
     * Constructs a new InvalidFieldException.
     *
     * @param field the invalid field string
     */
    public InvalidFieldException(String field) {
        super("invalid field: " + field);
    }
}
