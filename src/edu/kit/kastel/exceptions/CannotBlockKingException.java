package edu.kit.kastel.exceptions;

/**
 * Thrown when block is used on the Farmer King (the King cannot block).
 *
 * @author usylb
 */
public class CannotBlockKingException extends CommandException {

    private static final String CANNOT_BLOCK_KING_MESSAGE = "Farmer King cannot block";

    /**
     * Constructs a new CannotBlockKingException.
     */
    public CannotBlockKingException() {
        super(CANNOT_BLOCK_KING_MESSAGE);
    }
}
