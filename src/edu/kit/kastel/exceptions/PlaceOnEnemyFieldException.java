package edu.kit.kastel.exceptions;

/**
 * Thrown when place targets a field occupied by an enemy unit or the enemy Farmer King.
 *
 * @author usylb
 */
public class PlaceOnEnemyFieldException extends CommandException {

    private static final String PLACE_ON_ENEMY_PREFIX = "cannot place on ";
    private static final String PLACE_ON_ENEMY_SUFFIX = ": occupied by enemy";

    /**
     * Constructs a new PlaceOnEnemyFieldException.
     *
     * @param field the occupied field coordinate
     */
    public PlaceOnEnemyFieldException(String field) {
        super(PLACE_ON_ENEMY_PREFIX + field + PLACE_ON_ENEMY_SUFFIX);
    }
}
