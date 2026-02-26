package Crown_of_Farmland.exceptions;

/**
 * Fehler: Die Bewegung einer Einheit ist unzulaessig.
 *
 * Tritt auf wenn:
 * - das Zielfeld mehr als ein Feld entfernt ist (nicht angrenzend, diagonal nicht erlaubt)
 * - eine Einheit sich auf das Feld des eigenen Bauernkoenigs bewegt
 * - ein Bauernkoenig sich auf ein Feld bewegt, das von einer gegnerischen Einheit besetzt ist
 *
 * @author Programmieren-Team
 */
public class InvalidMoveException extends CommandException {

    /**
     * Erstellt eine neue InvalidMoveException.
     *
     * @param message die Beschreibung der ungueltigen Bewegung
     */
    public InvalidMoveException(String message) {
        super(message);
    }
}
