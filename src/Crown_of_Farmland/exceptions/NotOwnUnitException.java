package Crown_of_Farmland.exceptions;

/**
 * Fehler: Die ausgewaehlte Einheit gehoert nicht dem Team, das am Zug ist.
 *
 * Tritt auf wenn:
 * - move, flip oder block auf einer gegnerischen Einheit ausgefuehrt wird
 * - ein Befehl versucht, eine Einheit des Gegners zu steuern
 *
 * @author Programmieren-Team
 */
public class NotOwnUnitException extends CommandException {

    /**
     * Erstellt eine neue NotOwnUnitException.
     */
    public NotOwnUnitException() {
        super("cannot control an enemy unit");
    }
}
