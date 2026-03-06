package edu.kit.kastel.exceptions;

/**
 * Thrown when a field coordinate does not match A1–G7 (column A–G, row 1–7).
 *
 * @author usylb
 */
public class InvalidFieldException extends CommandException {

    private static final String INVALID_FIELD_PREFIX = "invalid field: ";

    /**
     * Constructs a new InvalidFieldException.
     *
     * @param field the invalid field string
     */
    public InvalidFieldException(String field) {
        super(INVALID_FIELD_PREFIX + field);
    }
}
