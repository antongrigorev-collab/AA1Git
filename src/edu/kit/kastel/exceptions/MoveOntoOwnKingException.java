package edu.kit.kastel.exceptions;

/**
 * Thrown when a unit (not the King) tries to move onto the own Farmer King's field.
 *
 * @author usylb
 */
public class MoveOntoOwnKingException extends CommandException {

    private static final String MOVE_ONTO_OWN_KING_MESSAGE =
            "cannot move onto your own Farmer King's field";

    /**
     * Constructs a new MoveOntoOwnKingException.
     */
    public MoveOntoOwnKingException() {
        super(MOVE_ONTO_OWN_KING_MESSAGE);
    }
}
