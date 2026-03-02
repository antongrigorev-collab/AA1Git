package Crown_of_Farmland.exceptions;

/**
 * Fehler: Eine ungueltige Ganzzahl wurde angegeben.
 *
 * Tritt auf wenn:
 * - der Wert fuer seed keine gueltige Ganzzahl ist
 * - eine Zeile in der Deck-Datei keine gueltige nichtnegative Ganzzahl ist
 * - ATK oder DEF in der Units-Datei keine gueltige nichtnegative Ganzzahl ist
 *
 * @author Programmieren-Team
 */
public class InvalidIntegerException extends StartupException {

    private static final String INVALID_INTEGER_PREFIX = "invalid integer: ";

    /**
     * Erstellt eine neue InvalidIntegerException.
     *
     * @param value der ungueltige Wert
     */
    public InvalidIntegerException(String value) {
        super(INVALID_INTEGER_PREFIX + value);
    }
}
