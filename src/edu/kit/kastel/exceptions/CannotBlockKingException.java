package edu.kit.kastel.exceptions;

/**
 * Thrown when block is used on the Farmer King (the King cannot block).
 *
 * @author usylb
 */
public class CannotBlockKingException extends CommandException {

    /**
     * Constructs a new CannotBlockKingException.
     */
    public CannotBlockKingException() {
        super("Farmer King cannot block");
    }
}
