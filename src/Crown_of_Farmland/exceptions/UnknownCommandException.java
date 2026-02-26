package Crown_of_Farmland.exceptions;

/**
 * Fehler: Ein unbekannter Befehl wurde eingegeben.
 *
 * Tritt auf wenn:
 * - der eingegebene Befehl keinem der definierten Befehle entspricht
 *   (select, board, move, flip, block, hand, place, show, yield, state, quit)
 *
 * @author Programmieren-Team
 */
public class UnknownCommandException extends CommandException {

    /**
     * Erstellt eine neue UnknownCommandException.
     *
     * @param command der unbekannte Befehl
     */
    public UnknownCommandException(String command) {
        super("unknown command: " + command);
    }
}
