package Crown_of_Farmland.exceptions;

/**
 * Fehler: Es wurde kein Feld ausgewaehlt, obwohl ein Befehl eine Auswahl voraussetzt.
 *
 * Tritt auf wenn:
 * - move, flip, block, show oder place ausgefuehrt wird, ohne dass zuvor
 *   ein Feld mit select ausgewaehlt wurde
 *
 * @author Programmieren-Team
 */
public class NoFieldSelectedException extends CommandException {

    /**
     * Erstellt eine neue NoFieldSelectedException.
     */
    public NoFieldSelectedException() {
        super("no field selected");
    }
}
