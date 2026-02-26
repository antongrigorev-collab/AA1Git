package Crown_of_Farmland.exceptions;

/**
 * Fehler: Ein Schluessel wurde beim Programmstart mehrfach angegeben.
 *
 * Tritt auf wenn:
 * - ein Schluessel (z.B. seed, units, deck) mehr als einmal als Argument uebergeben wird
 *
 * @author Programmieren-Team
 */
public class DuplicateArgumentException extends StartupException {

    /**
     * Erstellt eine neue DuplicateArgumentException.
     *
     * @param key der doppelt angegebene Schluessel
     */
    public DuplicateArgumentException(String key) {
        super("duplicate argument: " + key);
    }
}
