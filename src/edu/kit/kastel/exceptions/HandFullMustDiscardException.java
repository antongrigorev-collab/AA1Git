package edu.kit.kastel.exceptions;

/**
 * Thrown when yield is called without a discard index but the current team's
 * hand has 5 cards (a card must be discarded before ending the turn).
 *
 * @author Programmieren-Team
 */
public class HandFullMustDiscardException extends CommandException {

    /**
     * Constructs a new HandFullMustDiscardException.
     *
     * @param teamName the name of the team whose hand is full
     */
    public HandFullMustDiscardException(String teamName) {
        super(teamName + "'s hand is full!");
    }
}
