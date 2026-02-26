package Crown_of_Farmland.exceptions;

/**
 * Fehler: Der Bauernkoenig kann nicht aufgedeckt werden (er ist immer aufgedeckt).
 *
 * Tritt auf wenn:
 * - flip auf den Bauernkoenig angewendet wird
 *
 * @author Programmieren-Team
 */
public class CannotFlipKingException extends CommandException {

    /**
     * Erstellt eine neue CannotFlipKingException.
     */
    public CannotFlipKingException() {
        super("cannot flip the Farmer King");
    }
}
