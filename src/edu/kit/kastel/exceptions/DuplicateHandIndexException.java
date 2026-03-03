package edu.kit.kastel.exceptions;

/**
 * Thrown when a hand index is given more than once in a single place command.
 *
 * @author usylb
 */
public class DuplicateHandIndexException extends CommandException {

    /**
     * Constructs a new DuplicateHandIndexException.
     *
     * @param index the duplicate index
     */
    public DuplicateHandIndexException(int index) {
        super("duplicate hand index: " + index);
    }
}
