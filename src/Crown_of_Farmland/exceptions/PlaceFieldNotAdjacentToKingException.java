package Crown_of_Farmland.exceptions;

/**
 * Fehler: Das Zielfeld zum Platzieren ist nicht an den eigenen Bauernkoenig angrenzend.
 *
 * Tritt auf wenn:
 * - das Zielfeld fuer place entlang der Reihen, Spalten und Diagonalen
 *   mehr als ein Feld vom eigenen Bauernkoenig entfernt ist
 * - nur die bis zu 8 Felder um den Bauernkoenig (inkl. diagonal) sind gueltige Platzierungsfelder
 *
 * @author Programmieren-Team
 */
public class PlaceFieldNotAdjacentToKingException extends CommandException {

    /**
     * Erstellt eine neue PlaceFieldNotAdjacentToKingException.
     *
     * @param field das Zielfeld
     */
    public PlaceFieldNotAdjacentToKingException(String field) {
        super("cannot place on " + field + ": not adjacent to Farmer King");
    }
}
