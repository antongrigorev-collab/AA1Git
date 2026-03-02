package Crown_of_Farmland.exceptions;

/**
 * Thrown when the Farmer King tries to move onto a field occupied by an enemy unit (Kings cannot initiate duels).
 *
 * @author Programmieren-Team
 */
public class KingCannotAttackException extends CommandException {

    /**
     * Constructs a new KingCannotAttackException.
     */
    public KingCannotAttackException() {
        super("Farmer King cannot move onto an enemy-occupied field");
    }
}
