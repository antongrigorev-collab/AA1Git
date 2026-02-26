package Crown_of_Farmland.exceptions;

/**
 * Fehler: Ein Befehl wurde mit falschen oder ungueltigen Argumenten aufgerufen.
 *
 * Tritt auf wenn:
 * - die Anzahl der Argumente nicht zum Befehl passt
 * - ein Argument nicht das erwartete Format hat (z.B. ungueltige Feldbezeichnung)
 *
 * @author Programmieren-Team
 */
public class InvalidCommandArgumentsException extends CommandException {

    /**
     * Erstellt eine neue InvalidCommandArgumentsException.
     *
     * @param message die Beschreibung des Fehlers
     */
    public InvalidCommandArgumentsException(String message) {
        super(message);
    }
}
