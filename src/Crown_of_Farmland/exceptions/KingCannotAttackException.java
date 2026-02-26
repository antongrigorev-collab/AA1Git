package Crown_of_Farmland.exceptions;

/**
 * Fehler: Der Bauernkoenig versucht, sich auf ein Feld zu bewegen, das von einer gegnerischen Einheit besetzt ist.
 *
 * Tritt auf wenn:
 * - ein Bauernkoenig sich auf ein Feld bewegt, das durch eine gegnerische Einheit besetzt ist
 * - Bauernkoenige koennen kein Duell initiieren
 *
 * @author Programmieren-Team
 */
public class KingCannotAttackException extends CommandException {

    /**
     * Erstellt eine neue KingCannotAttackException.
     */
    public KingCannotAttackException() {
        super("Farmer King cannot move onto an enemy-occupied field");
    }
}
