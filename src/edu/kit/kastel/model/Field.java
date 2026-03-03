package edu.kit.kastel.model;

/**
 * A single cell on the 7x7 board. Has fixed row/column (0-based) and may hold
 * one unit. Row 0 = spec row 1 (bottom), col 0 = A.
 */
public final class Field {

    private final int row;
    private final int col;

    private Unit unit;

    /**
     * Creates an empty field at the given position.
     *
     * @param row row index 0..6 (1..7 in spec)
     * @param col column index 0..6 (A..G)
     */
    public Field(int row, int col) {
        this.row = row;
        this.col = col;
        this.unit = null;
    }

    /**
     * Returns the row index (0-based).
     *
     * @return row 0..6
     */
    public int row() {
        return row;
    }

    /**
     * Returns the column index (0-based, 0 = A).
     *
     * @return column 0..6
     */
    public int col() {
        return col;
    }

    /**
     * Returns whether this field has no unit.
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return unit == null;
    }

    /**
     * Returns the unit on this field, or null if empty.
     *
     * @return the unit or null
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Places a unit on this field.
     *
     * @param unit the unit to place (may be null to clear)
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * Removes the unit from this field.
     */
    public void removeUnit() {
        this.unit = null;
    }

    /**
     * Returns the coordinate string (e.g. "A1", "D7").
     *
     * @return the field coordinate
     */
    public String coordinate() {
        char c = (char) ('A' + col);
        int r = row + 1;
        return "" + c + r;
    }
}
