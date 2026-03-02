package Crown_of_Farmland.exceptions;
/**
 * Fehler: Die Einheit ist bereits aufgedeckt und kann nicht erneut aufgedeckt werden.
 *
 * Tritt auf wenn:
 * - flip auf eine Einheit angewendet wird, die bereits aufgedeckt ist
 * - aufgedeckte Einheiten koennen nicht wieder verdeckt werden
 *
 * @author Programmieren-Team
 */
public class FlipAlreadyFlippedException extends CommandException {

    private static final String ALREADY_FLIPPED_SUFFIX = " is already revealed";

    /**
     * Erstellt eine neue FlipAlreadyFlippedException.
     *
     * @param unitName der Name der bereits aufgedeckten Einheit
     */
    public FlipAlreadyFlippedException(String unitName) {
        super(unitName + ALREADY_FLIPPED_SUFFIX);
    }
}
