package Crown_of_Farmland.exceptions;

/**
 * Thrown when game initialization fails (e.g. hand unexpectedly full during
 * initial draw).
 *
 * @author Programmieren-Team
 */
public class InitializationException extends StartupException {

    /**
     * Constructs a new InitializationException.
     *
     * @param message description of the failure
     */
    public InitializationException(String message) {
        super(message);
    }
}
