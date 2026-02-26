package Crown_of_Farmland.exceptions;

/**
 * Fehler: Sowohl deck als auch deck1/deck2 wurden beim Programmstart angegeben.
 *
 * Tritt auf wenn:
 * - deck zusammen mit deck1 und/oder deck2 verwendet wird
 * - nur deck1 oder nur deck2 (aber nicht beide) angegeben wird
 *
 * @author Programmieren-Team
 */
public class ConflictingDeckArgumentsException extends StartupException {

    /**
     * Erstellt eine neue ConflictingDeckArgumentsException.
     *
     * @param message die Beschreibung des Konflikts
     */
    public ConflictingDeckArgumentsException(String message) {
        super(message);
    }
}
