package Crown_of_Farmland.exceptions;

/**
 * Abstrakte Basisklasse fuer alle Fehler im Spiel "Crown of Farmland".
 * Alle Fehlermeldungen beginnen mit "ERROR: " gefolgt von einer beschreibenden Nachricht.
 *
 * @author Programmieren-Team
 */
public abstract class GameException extends Exception {

    /** Prefix for all error messages (A.5). */
    private static final String ERROR_MESSAGE_PREFIX = "ERROR: ";

    /**
     * Erstellt eine neue GameException mit der gegebenen Fehlermeldung.
     *
     * @param message die Fehlermeldung
     */
    protected GameException(String message) {
        super(message);
    }

    /**
     * Gibt die formatierte Fehlermeldung zurueck, die mit "ERROR: " beginnt.
     *
     * @return die formatierte Fehlermeldung
     */
    public String getFormattedMessage() {
        return ERROR_MESSAGE_PREFIX + getMessage();
    }
}
