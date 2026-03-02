package Crown_of_Farmland.exceptions;

/**
 * Thrown when a hand index for place or yield is out of range (must be 1-based and within hand size).
 *
 * @author Programmieren-Team
 */
public class InvalidHandIndexException extends CommandException {

    /**
     * Constructs a new InvalidHandIndexException.
     *
     * @param index the invalid index
     */
    public InvalidHandIndexException(int index) {
        super("invalid hand index: " + index);
    }
}
