package Crown_of_Farmland.exceptions;

/**
 * Fehler: Das ausgewaehlte Feld ist leer, obwohl eine Einheit erwartet wird.
 *
 * Tritt auf wenn:
 * - move, flip oder block auf einem leeren Feld ausgefuehrt wird
 * - eine Einheit auf dem ausgewaehlten Feld vorausgesetzt wird, das Feld aber leer ist
 *
 * @author Programmieren-Team
 */
public class EmptyFieldException extends CommandException {

    /**
     * Erstellt eine neue EmptyFieldException.
     *
     * @param field die Feldbezeichnung des leeren Feldes
     */
    public EmptyFieldException(String field) {
        super("no unit on field " + field);
    }
}
