package Crown_of_Farmland.exceptions;

/**
 * Fehler: Der Bauernkoenig kann keine Blockade einleiten.
 *
 * Tritt auf wenn:
 * - block auf den Bauernkoenig angewendet wird
 *
 * @author Programmieren-Team
 */
public class CannotBlockKingException extends CommandException {

    /**
     * Erstellt eine neue CannotBlockKingException.
     */
    public CannotBlockKingException() {
        super("Farmer King cannot block");
    }
}
