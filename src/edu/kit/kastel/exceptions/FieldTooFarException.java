package edu.kit.kastel.exceptions;

/**
 * Thrown when the move target is more than one step away (only cardinal moves allowed).
 *
 * @author usylb
 */
public class FieldTooFarException extends CommandException {

    private static final String CANNOT_MOVE_FROM_PREFIX = "cannot move from ";
    private static final String CANNOT_MOVE_TO_INFIX = " to ";
    private static final String CANNOT_MOVE_TOO_FAR_SUFFIX = ": too far away";

    /**
     * Constructs a new FieldTooFarException.
     *
     * @param from the source field coordinate
     * @param to   the target field coordinate
     */
    public FieldTooFarException(String from, String to) {
        super(CANNOT_MOVE_FROM_PREFIX + from + CANNOT_MOVE_TO_INFIX + to + CANNOT_MOVE_TOO_FAR_SUFFIX);
    }
}
