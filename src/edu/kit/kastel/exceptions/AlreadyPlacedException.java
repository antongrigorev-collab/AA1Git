package edu.kit.kastel.exceptions;

/**
 * Thrown when place is used but the team has already placed this turn (at most one place per turn).
 *
 * @author usylb
 */
public class AlreadyPlacedException extends CommandException {

    private static final String ALREADY_PLACED_MESSAGE = "already placed a unit this turn";

    /**
     * Constructs a new AlreadyPlacedException.
     */
    public AlreadyPlacedException() {
        super(ALREADY_PLACED_MESSAGE);
    }
}
