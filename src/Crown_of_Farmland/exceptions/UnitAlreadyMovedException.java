package Crown_of_Farmland.exceptions;

/**
 * Fehler: Die Einheit wurde in diesem Zug bereits bewegt.
 *
 * Tritt auf wenn:
 * - move auf eine Einheit angewendet wird, die sich in diesem Zug bereits bewegt hat
 * - flip auf eine Einheit angewendet wird, die sich in diesem Zug bereits bewegt hat
 * - block auf eine Einheit angewendet wird, die sich in diesem Zug bereits bewegt hat
 *
 * @author Programmieren-Team
 */
public class UnitAlreadyMovedException extends CommandException {

    private static final String ALREADY_MOVED_SUFFIX = " has already moved this turn";

    /**
     * Erstellt eine neue UnitAlreadyMovedException.
     *
     * @param unitName der Name der bereits bewegten Einheit
     */
    public UnitAlreadyMovedException(String unitName) {
        super(unitName + ALREADY_MOVED_SUFFIX);
    }
}
