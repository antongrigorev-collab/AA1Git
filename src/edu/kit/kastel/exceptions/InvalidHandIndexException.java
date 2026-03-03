package edu.kit.kastel.exceptions;

/**
 * Thrown when a hand index for place or yield is out of range (must be 1-based and within hand size).
 *
 * @author usylb
 */
public class InvalidHandIndexException extends CommandException {

    /**
     * Constructs a new InvalidHandIndexException.
     *
     * @param index the invalid index
     */
    public InvalidHandIndexException(int index) {
        super("invalid hand index: " + index);
    }
}
