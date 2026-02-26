package Crown_of_Farmland.exceptions;

/**
 * Fehler: Ein ungueltiger Schluessel oder ein ungueltiges Argument wurde beim Programmstart angegeben.
 *
 * Tritt auf wenn:
 * - ein unbekannter Schluessel verwendet wird (nicht in Tabelle A.1)
 * - ein Argument kein '=' enthaelt oder leer ist
 * - ein ungueltiger Wert fuer verbosity angegeben wird (nicht "all" oder "compact")
 *
 * @author Programmieren-Team
 */
public class InvalidArgumentException extends StartupException {

    /**
     * Erstellt eine neue InvalidArgumentException.
     *
     * @param message die Beschreibung des ungueltigen Arguments
     */
    public InvalidArgumentException(String message) {
        super(message);
    }
}
