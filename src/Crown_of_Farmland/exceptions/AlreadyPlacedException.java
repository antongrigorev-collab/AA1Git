package Crown_of_Farmland.exceptions;

/**
 * Fehler: In diesem Zug wurde bereits eine Einheit platziert.
 *
 * Tritt auf wenn:
 * - place ausgefuehrt wird, aber das Team in diesem Zug bereits Einheiten platziert hat
 * - pro Zug darf hoechstens einmal eine Einheit (oder mehrere via Zusammenschluss) platziert werden
 *
 * @author Programmieren-Team
 */
public class AlreadyPlacedException extends CommandException {

    /**
     * Erstellt eine neue AlreadyPlacedException.
     */
    public AlreadyPlacedException() {
        super("already placed a unit this turn");
    }
}
