package Crown_of_Farmland.exceptions;

/**
 * Thrown when an invalid key or value is given at startup (e.g. unknown key,
 * argument without '=', invalid verbosity other than "all" or "compact").
 *
 * @author Programmieren-Team
 */
public class InvalidArgumentException extends StartupException {

    /**
     * Constructs a new InvalidArgumentException.
     *
     * @param message description of the invalid argument
     */
    public InvalidArgumentException(String message) {
        super(message);
    }
}
