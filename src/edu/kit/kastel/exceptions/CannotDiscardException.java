package edu.kit.kastel.exceptions;

/**
 * Thrown when yield is called with a discard index but the hand has fewer than 5 cards (discard only when full).
 *
 * @author usylb
 */
public class CannotDiscardException extends CommandException {

    /**
     * Constructs a new CannotDiscardException.
     */
    public CannotDiscardException() {
        super("cannot discard: hand is not full");
    }
}
