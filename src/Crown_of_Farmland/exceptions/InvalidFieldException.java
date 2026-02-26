package Crown_of_Farmland.exceptions;

/**
 * Fehler: Eine ungueltige Feldbezeichnung wurde angegeben.
 *
 * Tritt auf wenn:
 * - die Feldbezeichnung nicht dem Format A1 bis G7 entspricht
 * - die Spalte nicht zwischen A und G liegt
 * - die Zeile nicht zwischen 1 und 7 liegt
 *
 * @author Programmieren-Team
 */
public class InvalidFieldException extends CommandException {

    /**
     * Erstellt eine neue InvalidFieldException.
     *
     * @param field die ungueltige Feldbezeichnung
     */
    public InvalidFieldException(String field) {
        super("invalid field: " + field);
    }
}
