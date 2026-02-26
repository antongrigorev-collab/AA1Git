package Crown_of_Farmland.exceptions;

/**
 * Fehler: Eine Einheit soll auf einem Feld platziert werden, das durch eine gegnerische
 * Einheit oder den gegnerischen Bauernkoenig besetzt ist.
 *
 * Tritt auf wenn:
 * - das Zielfeld fuer place durch eine gegnerische Einheit besetzt ist
 * - das Zielfeld fuer place durch den gegnerischen Bauernkoenig besetzt ist
 *
 * @author Programmieren-Team
 */
public class PlaceOnEnemyFieldException extends CommandException {

    /**
     * Erstellt eine neue PlaceOnEnemyFieldException.
     *
     * @param field das besetzte Feld
     */
    public PlaceOnEnemyFieldException(String field) {
        super("cannot place on " + field + ": occupied by enemy");
    }
}
