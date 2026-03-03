package edu.kit.kastel.exceptions;

/**
 * Thrown when the team must discard first (hand full) but attempts another command
 * such as place (e.g. "cannot place a card, you must discard!").
 *
 * @author usylb
 */
public class MustDiscardException extends CommandException {

    /**
     * Constructs a new MustDiscardException.
     *
     * @param attemptedCommand the command that was attempted (e.g. "place a card")
     */
    public MustDiscardException(String attemptedCommand) {
        super("cannot " + attemptedCommand + ", you must discard!");
    }
}
