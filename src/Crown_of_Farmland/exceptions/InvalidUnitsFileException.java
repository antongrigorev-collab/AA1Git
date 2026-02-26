package Crown_of_Farmland.exceptions;

/**
 * Fehler: Die Einheiten-Datei hat einen ungueltigen Inhalt.
 *
 * Tritt auf wenn:
 * - eine Zeile nicht genau 4 durch Semikolon getrennte Felder enthaelt (Qualifikator;Rolle;ATK;DEF)
 * - ATK oder DEF keine gueltige nichtnegative Ganzzahl ist
 * - die Datei mehr als 80 Einheiten enthaelt
 * - die Zeile mit einem Semikolon endet oder zusaetzliche Leerzeichen enthaelt
 *
 * @author Programmieren-Team
 */
public class InvalidUnitsFileException extends StartupException {

    /**
     * Erstellt eine neue InvalidUnitsFileException.
     *
     * @param message die Beschreibung des Fehlers
     */
    public InvalidUnitsFileException(String message) {
        super(message);
    }
}
