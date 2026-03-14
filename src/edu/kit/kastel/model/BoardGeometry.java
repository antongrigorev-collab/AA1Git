package edu.kit.kastel.model;

/**
 * Geometry and coordinate helpers for the 7x7 game board.
 *
 * @author usylb
 */
public final class BoardGeometry {

    private static final int ADJACENT_MAX_OFFSET = 1;
    private static final int FIELD_COORDINATES_LENGTH = 2;
    private static final char COLUMN_MIN = 'A';
    private static final char COLUMN_MAX = 'G';
    private static final char ROW_CHAR_MIN = '1';
    private static final char ROW_CHAR_MAX = '7';
    private static final int CHAR_INDEX_COL = 0;
    private static final int CHAR_INDEX_ROW = 1;

    private BoardGeometry() {
    }

    /**
     * Manhattan distance 1 (including en place).
     *
     * @param fromRow row of source field
     * @param fromCol column of source field
     * @param toRow   row of target field
     * @param toCol   column of target field
     * @return true if the two fields are adjacent or the same
     */
    public static boolean isAdjacent(int fromRow, int fromCol, int toRow, int toCol) {
        return Math.abs(fromRow - toRow) + Math.abs(fromCol - toCol) <= ADJACENT_MAX_OFFSET;
    }

    /**
     * Parses "A1"-"G7" to row,col. Row 1 = index 0.
     *
     * @param coordinates field identifier A1 to G7
     * @return int array [row, col] or null if invalid
     */
    public static int[] parseField(String coordinates) {
        String u = coordinates.strip().toUpperCase();
        if (u.length() != FIELD_COORDINATES_LENGTH || u.charAt(CHAR_INDEX_COL) < COLUMN_MIN
                || u.charAt(CHAR_INDEX_COL) > COLUMN_MAX || u.charAt(CHAR_INDEX_ROW) < ROW_CHAR_MIN
                || u.charAt(CHAR_INDEX_ROW) > ROW_CHAR_MAX) {
            return null;
        }
        int col = u.charAt(CHAR_INDEX_COL) - COLUMN_MIN;
        int row = u.charAt(CHAR_INDEX_ROW) - ROW_CHAR_MIN;
        return new int[] { row, col };
    }
}

