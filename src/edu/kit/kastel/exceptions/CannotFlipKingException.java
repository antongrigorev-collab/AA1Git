package edu.kit.kastel.exceptions;

/**
 * Thrown when flip is used on the Farmer King (the King is always revealed).
 *
 * @author usylb
 */
public class CannotFlipKingException extends CommandException {

    private static final String CANNOT_FLIP_KING_MESSAGE = "cannot flip the Farmer King";

    /**
     * Constructs a new CannotFlipKingException.
     */
    public CannotFlipKingException() {
        super(CANNOT_FLIP_KING_MESSAGE);
    }
}
