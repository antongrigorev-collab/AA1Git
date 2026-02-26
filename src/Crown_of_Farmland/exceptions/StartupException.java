package Crown_of_Farmland.exceptions;

/**
 * Abstrakte Basisklasse fuer alle Fehler, die beim Programmstart auftreten.
 * Diese Fehler fuehren zum Abbruch des Programms.
 *
 * @author Programmieren-Team
 */
public abstract class StartupException extends GameException {

    /**
     * Erstellt eine neue StartupException mit der gegebenen Fehlermeldung.
     *
     * @param message die Fehlermeldung
     */
    protected StartupException(String message) {
        super(message);
    }
}
