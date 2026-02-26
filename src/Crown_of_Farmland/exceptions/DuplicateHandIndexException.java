package Crown_of_Farmland.exceptions;

/**
 * Fehler: Ein Index wurde bei einem place-Befehl mehrfach angegeben.
 *
 * Tritt auf wenn:
 * - ein Index im place-Befehl doppelt aufgefuehrt ist
 *
 * @author Programmieren-Team
 */
public class DuplicateHandIndexException extends CommandException {

    /**
     * Erstellt eine neue DuplicateHandIndexException.
     *
     * @param index der doppelt angegebene Index
     */
    public DuplicateHandIndexException(int index) {
        super("duplicate hand index: " + index);
    }
}
