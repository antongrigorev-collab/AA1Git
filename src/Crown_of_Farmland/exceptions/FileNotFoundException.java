package Crown_of_Farmland.exceptions;

/**
 * Fehler: Eine angegebene Datei wurde nicht gefunden.
 *
 * Tritt auf wenn:
 * - der Pfad fuer board, units, deck, deck1 oder deck2 nicht existiert
 *
 * @author Programmieren-Team
 */
public class FileNotFoundException extends StartupException {

    /**
     * Erstellt eine neue FileNotFoundException.
     *
     * @param filePath der Pfad der nicht gefundenen Datei
     */
    public FileNotFoundException(String filePath) {
        super("file not found: " + filePath);
    }
}
