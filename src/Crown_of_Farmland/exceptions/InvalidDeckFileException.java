package Crown_of_Farmland.exceptions;

/**
 * Fehler: Die Stapelzusammensetzungs-Datei hat einen ungueltigen Inhalt.
 *
 * Tritt auf wenn:
 * - die Anzahl der Zeilen nicht mit der Anzahl der definierten Einheiten uebereinstimmt
 * - die Gesamtanzahl der Einheiten im Stapel nicht 40 betraegt
 * - eine Zeile keine gueltige nichtnegative Ganzzahl enthaelt
 *
 * @author Programmieren-Team
 */
public class InvalidDeckFileException extends StartupException {

    /**
     * Erstellt eine neue InvalidDeckFileException.
     *
     * @param message die Beschreibung des Fehlers
     */
    public InvalidDeckFileException(String message) {
        super(message);
    }
}
