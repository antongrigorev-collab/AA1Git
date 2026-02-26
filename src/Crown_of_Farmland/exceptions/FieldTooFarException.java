package Crown_of_Farmland.exceptions;

/**
 * Fehler: Das Zielfeld ist zu weit entfernt fuer eine Bewegung.
 *
 * Tritt auf wenn:
 * - das Zielfeld bei move entlang der Zeilen und Spalten (nicht diagonal)
 *   mehr als ein Feld entfernt ist
 *
 * @author Programmieren-Team
 */
public class FieldTooFarException extends CommandException {

    /**
     * Erstellt eine neue FieldTooFarException.
     *
     * @param from das Ausgangsfeld
     * @param to das Zielfeld
     */
    public FieldTooFarException(String from, String to) {
        super("cannot move from " + from + " to " + to + ": too far away");
    }
}
