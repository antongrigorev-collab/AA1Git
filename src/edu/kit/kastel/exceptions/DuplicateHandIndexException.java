package edu.kit.kastel.exceptions;

/**
 * Thrown when a hand index is given more than once in a single place command.
 *
 * @author usylb
 */
public class DuplicateHandIndexException extends CommandException {

    private static final String DUPLICATE_HAND_INDEX_PREFIX = "duplicate hand index: ";

    /**
     * Constructs a new DuplicateHandIndexException.
     *
     * @param index the duplicate index
     */
    public DuplicateHandIndexException(int index) {
        super(DUPLICATE_HAND_INDEX_PREFIX + index);
    }
}
