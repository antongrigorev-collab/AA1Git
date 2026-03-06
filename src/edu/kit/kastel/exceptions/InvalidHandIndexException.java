package edu.kit.kastel.exceptions;

/**
 * Thrown when a hand index for place or yield is out of range (must be 1-based and within hand size).
 *
 * @author usylb
 */
public class InvalidHandIndexException extends CommandException {

    private static final String INVALID_HAND_INDEX_PREFIX = "invalid hand index: ";

    /**
     * Constructs a new InvalidHandIndexException.
     *
     * @param index the invalid index
     */
    public InvalidHandIndexException(int index) {
        super(INVALID_HAND_INDEX_PREFIX + index);
    }
}
