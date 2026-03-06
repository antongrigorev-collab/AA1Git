package edu.kit.kastel.exceptions;

/**
 * Thrown when the Farmer King tries to move onto a field occupied by an enemy unit (Kings cannot initiate duels).
 *
 * @author usylb
 */
public class KingCannotAttackException extends CommandException {

    private static final String KING_CANNOT_ATTACK_MESSAGE =
            "Farmer King cannot move onto an enemy-occupied field";

    /**
     * Constructs a new KingCannotAttackException.
     */
    public KingCannotAttackException() {
        super(KING_CANNOT_ATTACK_MESSAGE);
    }
}
