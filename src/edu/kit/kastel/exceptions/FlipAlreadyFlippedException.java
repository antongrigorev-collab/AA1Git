package edu.kit.kastel.exceptions;

/**
 * Thrown when flip is used on a unit that is already revealed (revealed units cannot be flipped).
 *
 * @author Programmieren-Team
 */
public class FlipAlreadyFlippedException extends CommandException {

    private static final String ALREADY_FLIPPED_SUFFIX = " is already revealed";

    /**
     * Constructs a new FlipAlreadyFlippedException.
     *
     * @param unitName the name of the already revealed unit
     */
    public FlipAlreadyFlippedException(String unitName) {
        super(unitName + ALREADY_FLIPPED_SUFFIX);
    }
}
