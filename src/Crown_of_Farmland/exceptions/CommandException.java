package Crown_of_Farmland.exceptions;

/**
 * Abstrakte Basisklasse fuer alle Fehler, die waehrend der Befehlsausfuehrung auftreten.
 * Diese Fehler fuehren nicht zum Programmabbruch, sondern geben eine Fehlermeldung aus
 * und das Programm wartet auf die naechste Eingabe.
 *
 * @author Programmieren-Team
 */
public abstract class CommandException extends GameException {

    /**
     * Erstellt eine neue CommandException mit der gegebenen Fehlermeldung.
     *
     * @param message die Fehlermeldung
     */
    protected CommandException(String message) {
        super(message);
    }
}
