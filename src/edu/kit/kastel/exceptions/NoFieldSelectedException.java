package edu.kit.kastel.exceptions;

/**
 * Thrown when a command requires a selected field but none is selected (e.g. move,
 * flip, block, show, place without a prior select).
 *
 * @author usylb
 */
public class NoFieldSelectedException extends CommandException {

    private static final String NO_FIELD_SELECTED_MESSAGE = "no field selected";

    /**
     * Constructs a new NoFieldSelectedException.
     */
    public NoFieldSelectedException() {
        super(NO_FIELD_SELECTED_MESSAGE);
    }
}
