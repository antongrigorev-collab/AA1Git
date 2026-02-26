package Crown_of_Farmland.exceptions;

/**
 * Fehler: Ein erforderliches Argument (z.B. seed, units, deck) fehlt beim Programmstart.
 *
 * Tritt auf wenn:
 * - seed nicht angegeben wurde
 * - units nicht angegeben wurde
 * - weder deck noch deck1+deck2 angegeben wurden
 *
 * @author Programmieren-Team
 */
public class MissingArgumentException extends StartupException {

    /**
     * Erstellt eine neue MissingArgumentException.
     *
     * @param argumentName der Name des fehlenden Arguments
     */
    public MissingArgumentException(String argumentName) {
        super("missing required argument: " + argumentName);
    }
}
