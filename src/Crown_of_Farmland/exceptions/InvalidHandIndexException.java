package Crown_of_Farmland.exceptions;

/**
 * Fehler: Ein ungueltiger Index fuer die Hand wurde angegeben.
 *
 * Tritt auf wenn:
 * - der Index bei place oder yield keiner Einheit auf der Hand zugeordnet werden kann
 * - der Index kleiner als 1 oder groesser als die Anzahl der Einheiten auf der Hand ist
 *
 * @author Programmieren-Team
 */
public class InvalidHandIndexException extends CommandException {

    /**
     * Erstellt eine neue InvalidHandIndexException.
     *
     * @param index der ungueltige Index
     */
    public InvalidHandIndexException(int index) {
        super("invalid hand index: " + index);
    }
}
