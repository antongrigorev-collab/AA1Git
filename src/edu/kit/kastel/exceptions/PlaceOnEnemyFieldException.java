package edu.kit.kastel.exceptions;

/**
 * Thrown when place targets a field occupied by an enemy unit or the enemy Farmer King.
 *
 * @author usylb
 */
public class PlaceOnEnemyFieldException extends CommandException {

    /**
     * Constructs a new PlaceOnEnemyFieldException.
     *
     * @param field the occupied field coordinate
     */
    public PlaceOnEnemyFieldException(String field) {
        super("cannot place on " + field + ": occupied by enemy");
    }
}
