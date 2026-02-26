package Crown_of_Farmland.exceptions;

/**
 * Fehler: Eine Einheit versucht, sich auf das Feld des eigenen Bauernkoenigs zu bewegen.
 *
 * Tritt auf wenn:
 * - eine Einheit (nicht der Bauernkoenig selbst) sich auf das Feld des eigenen Bauernkoenigs bewegt
 *
 * @author Programmieren-Team
 */
public class MoveOntoOwnKingException extends CommandException {

    /**
     * Erstellt eine neue MoveOntoOwnKingException.
     */
    public MoveOntoOwnKingException() {
        super("cannot move onto your own Farmer King's field");
    }
}
