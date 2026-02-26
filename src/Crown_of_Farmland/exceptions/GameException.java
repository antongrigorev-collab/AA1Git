package Crown_of_Farmland.exceptions;

/**
 * Abstrakte Basisklasse fuer alle Fehler im Spiel "Crown of Farmland".
 * Alle Fehlermeldungen beginnen mit "ERROR: " gefolgt von einer beschreibenden Nachricht.
 *
 * @author Programmieren-Team
 */
public abstract class GameException extends Exception {

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
        return "ERROR: " + getMessage();
    }
}
