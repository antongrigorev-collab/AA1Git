package edu.kit.kastel.exceptions;

/**
 * Thrown when place is used but the team has already placed this turn (at most one place per turn).
 *
 * @author Programmieren-Team
 */
public class AlreadyPlacedException extends CommandException {

    /**
     * Constructs a new AlreadyPlacedException.
     */
    public AlreadyPlacedException() {
        super("already placed a unit this turn");
    }
}
