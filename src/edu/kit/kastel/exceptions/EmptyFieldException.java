package edu.kit.kastel.exceptions;

/**
 * Thrown when move, flip or block is used on an empty field, or when a command
 * expects a unit on the selected field but it is empty.
 *
 * @author usylb
 */
public class EmptyFieldException extends CommandException {

    private static final String NO_UNIT_ON_FIELD_PREFIX = "no unit on field ";

    /**
     * Constructs a new EmptyFieldException.
     *
     * @param field the coordinate of the empty field
     */
    public EmptyFieldException(String field) {
        super(NO_UNIT_ON_FIELD_PREFIX + field);
    }
}
