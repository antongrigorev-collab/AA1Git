package edu.kit.kastel.exceptions;

/**
 * Thrown when a command requires a selected field but none is selected (e.g. move,
 * flip, block, show, place without a prior select).
 *
 * @author Programmieren-Team
 */
public class NoFieldSelectedException extends CommandException {

    /**
     * Constructs a new NoFieldSelectedException.
     */
    public NoFieldSelectedException() {
        super("no field selected");
    }
}
