package Crown_of_Farmland.exceptions;

/**
 * Fehler: Die Spielbrettsymbole-Datei hat einen ungueltigen Inhalt.
 *
 * Tritt auf wenn:
 * - die Datei nicht genau 29 Zeichen in einer einzigen Zeile enthaelt
 *
 * @author Programmieren-Team
 */
public class InvalidBoardFileException extends StartupException {

    /**
     * Erstellt eine neue InvalidBoardFileException.
     *
     * @param message die Beschreibung des Fehlers
     */
    public InvalidBoardFileException(String message) {
        super(message);
    }
}
