package edu.kit.kastel.model;

import edu.kit.kastel.commands.SymbolSet;
import edu.kit.kastel.commands.VerbosityMode;

import java.util.ArrayList;
import java.util.List;

/**
 * The 7x7 game board for Crown of Farmland. Holds a grid of {@link Field}s and
 * renders the board (with optional selected field highlight) using the configured
 * symbol set and verbosity mode. Row 0 = bottom (spec row 1), column 0 = A.
 *
 * @author usylb
 */
public class GameBoard {
    /** Board dimension (7x7). */
    public static final int SIZE = 7;

    /** Separator between row number and cell content. */
    private static final String ROW_NUMBER_SEPARATOR = " ";

    /** Indent for board row labels and separator lines. */
    private static final String BOARD_ROW_INDENT = "  ";

    /** Padding before column labels (A..G). */
    private static final String COLUMN_LABEL_LEFT_PADDING = "    ";

    /** Spacing between column labels. */
    private static final String COLUMN_LABEL_SPACING = "   ";

    /** Content for empty cell (3 spaces). */
    private static final String EMPTY_CELL_CONTENT = "   ";

    /** Custom symbol set: index for horizontal line (a-z order). */
    private static final int CUSTOM_SYMBOL_INDEX_HORIZONTAL = 8;

    /** Custom symbol set: index for vertical line. */
    private static final int CUSTOM_SYMBOL_INDEX_VERTICAL = 9;

    private static final int CUSTOM_SYMBOL_INDEX_TOP_LEFT_CORNER = 0;
    private static final int CUSTOM_SYMBOL_INDEX_TOP_RIGHT_CORNER = 1;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_LEFT_CORNER = 2;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_RIGHT_CORNER = 3;
    private static final int CUSTOM_SYMBOL_INDEX_TOP_MID = 4;
    private static final int CUSTOM_SYMBOL_INDEX_RIGHT_MID = 5;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_MID = 6;
    private static final int CUSTOM_SYMBOL_INDEX_LEFT_MID = 7;
    private static final int CUSTOM_SYMBOL_INDEX_CENTER = 10;

    private static final int CUSTOM_SYMBOL_INDEX_TOP_LEFT_CORNER_SELECTED = 11;
    private static final int CUSTOM_SYMBOL_INDEX_TOP_RIGHT_CORNER_SELECTED = 12;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_LEFT_CORNER_SELECTED = 13;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_RIGHT_CORNER_SELECTED = 14;
    private static final int CUSTOM_SYMBOL_INDEX_TOP_MID_SELECTED_LEFT = 15;
    private static final int CUSTOM_SYMBOL_INDEX_TOP_MID_SELECTED_RIGHT = 16;
    private static final int CUSTOM_SYMBOL_INDEX_RIGHT_MID_SELECTED_ABOVE = 17;
    private static final int CUSTOM_SYMBOL_INDEX_RIGHT_MID_SELECTED_BELOW = 18;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_MID_SELECTED_LEFT = 19;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_MID_SELECTED_RIGHT = 20;
    private static final int CUSTOM_SYMBOL_INDEX_LEFT_MID_SELECTED_ABOVE = 21;
    private static final int CUSTOM_SYMBOL_INDEX_LEFT_MID_SELECTED_BELOW = 22;
    private static final int CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_TOP_LEFT = 25;
    private static final int CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_TOP_RIGHT = 26;
    private static final int CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_BOTTOM_LEFT = 27;
    private static final int CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_BOTTOM_RIGHT = 28;

    /** Custom symbol set: index for horizontal line when selected. */
    private static final int CUSTOM_SYMBOL_INDEX_HORIZONTAL_SELECTED = 23;

    /** Custom symbol set: index for vertical line when selected. */
    private static final int CUSTOM_SYMBOL_INDEX_VERTICAL_SELECTED = 24;

    /** Custom symbol set: offset from base corner index to selected variant. */
    private static final int CUSTOM_CORNER_SELECTED_OFFSET = 11;

    /** Standard symbols: corner, horizontal, vertical, then selected variants. */
    private static final char STD_CORNER = '+';
    private static final char STD_H = '-';
    private static final char STD_V = '|';
    private static final char STD_CORNER_SEL = '#';
    private static final char STD_H_SEL = '=';
    private static final char STD_V_SEL = 'N';

    private final SymbolSet symbolSet;
    private final VerbosityMode verbosityMode;
    private final Field[][] grid;

    /**
     * Creates a 7x7 game board with the given symbol set and verbosity mode.
     *
     * @param symbolSet    symbols for board rendering
     * @param verbosityMode all or compact output
     */
    public GameBoard(SymbolSet symbolSet, VerbosityMode verbosityMode) {
        this.symbolSet = symbolSet;
        this.verbosityMode = verbosityMode;
        this.grid = new Field[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                this.grid[row][col] = new Field(row, col);
            }
        }
    }

    /**
     * Places a unit on the field at the given row and column (0-based).
     * Row 0 is the bottom (row 1 in spec), row 6 is the top (row 7 in spec).
     * Column 0 is A, column 3 is D.
     *
     * @param row   row index 0..6
     * @param col   column index 0..6
     * @param unit  the unit to place
     */
    public void placeUnit(int row, int col, Unit unit) {
        this.grid[row][col].setUnit(unit);
    }

    /**
     * Returns the field at the given row and column.
     *
     * @param row row index 0..6
     * @param col column index 0..6
     * @return the field at that position
     */
    public Field getField(int row, int col) {
        return this.grid[row][col];
    }

    /**
     * Returns the symbol set used for rendering.
     *
     * @return the symbol set
     */
    public SymbolSet getSymbolSet() {
        return symbolSet;
    }

    /**
     * Returns the verbosity mode (full or compact).
     *
     * @return the verbosity mode
     */
    public VerbosityMode getVerbosityMode() {
        return verbosityMode;
    }

    /**
     * Renders the board as a list of lines (no trailing spaces).
     * Row 7 at top, row 1 at bottom; columns A..G.
     *
     * @param selectedField  the currently selected field, or null
     * @param teamShownAsX   the team whose units are displayed as x/X (e.g. the human player)
     * @param currentTeam    the team whose turn it is (used for '*' can-move prefix)
     * @return lines to print
     */
    public List<String> render(Field selectedField, Team teamShownAsX, Team currentTeam) {
        List<String> out = new ArrayList<>();
        boolean compact = verbosityMode == VerbosityMode.COMPACT;
        boolean useStandard = !symbolSet.isCustom();
        char[] sym = symbolSet.raw();


        for (int r = SIZE - 1; r >= 0; r--) {
            if (!compact) {
                out.add(buildSeparatorLine(r, true, selectedField, useStandard, sym));
            }
            out.add(buildCellLine(r, selectedField, teamShownAsX, currentTeam, useStandard, sym));
        }
        if (!compact) {
            out.add(buildSeparatorLine(-1, false, selectedField, useStandard, sym));
        }
        out.add(buildColumnLabelLine());
        return out;
    }

    private boolean isSelected(int row, int col, Field selectedField) {
        if (selectedField == null) {
            return false;
        }
        return selectedField.row() == row && selectedField.col() == col;
    }

    private boolean isCornerLeftSelected(int r, int c, int rAbove, Field selectedField) {
        if (c == 0) {
            boolean selR = isSelected(r, 0, selectedField);
            boolean selAbove = rAbove >= 0 && isSelected(rAbove, 0, selectedField);
            return selR || selAbove;
        }
        boolean leftSel = isSelected(r, c - 1, selectedField);
        boolean curSel = isSelected(r, c, selectedField);
        boolean aboveLeft = rAbove >= 0 && isSelected(rAbove, c - 1, selectedField);
        boolean aboveCur = rAbove >= 0 && isSelected(rAbove, c, selectedField);
        return leftSel || curSel || aboveLeft || aboveCur;
    }

    private String buildSeparatorLine(int row, boolean unused, Field selectedField, boolean useStandard, char[] sym) {
        // Draw the separator line above the given row (or the bottom border for row=-1).
        int rowBelow = row >= 0 ? row : 0;
        int rowAbove = (row >= 0 && row < SIZE - 1) ? row + 1 : -1;
        boolean isTopBorder = row == SIZE - 1;
        boolean isBottomBorder = row == -1;

        StringBuilder sb = new StringBuilder();
        sb.append(BOARD_ROW_INDENT);

        char hNorm = useStandard ? STD_H : sym[CUSTOM_SYMBOL_INDEX_HORIZONTAL];
        char hSel = useStandard ? STD_H_SEL : sym[CUSTOM_SYMBOL_INDEX_HORIZONTAL_SELECTED];

        char firstJunction;
        if (useStandard) {
            boolean leftEdgeSel = selectedField != null
                    && (isSelected(rowBelow, 0, selectedField)
                    || (rowAbove >= 0 && isSelected(rowAbove, 0, selectedField)));
            firstJunction = leftEdgeSel ? STD_CORNER_SEL : STD_CORNER;
        } else if (isTopBorder) {
            firstJunction = selectedField != null && isSelected(rowBelow, 0, selectedField)
                    ? sym[CUSTOM_SYMBOL_INDEX_TOP_LEFT_CORNER_SELECTED]
                    : sym[CUSTOM_SYMBOL_INDEX_TOP_LEFT_CORNER];
        } else if (isBottomBorder) {
            firstJunction = selectedField != null && isSelected(rowBelow, 0, selectedField)
                    ? sym[CUSTOM_SYMBOL_INDEX_BOTTOM_LEFT_CORNER_SELECTED]
                    : sym[CUSTOM_SYMBOL_INDEX_BOTTOM_LEFT_CORNER];
        } else {
            firstJunction = customLeftMidJunction(rowBelow, rowAbove, selectedField, sym);
        }
        sb.append(firstJunction);

        for (int c = 0; c < SIZE; c++) {
            boolean segSel = isSegmentSelected(rowBelow, rowAbove, c, selectedField, isTopBorder, isBottomBorder);
            sb.append(segSel ? hSel : hNorm).append(segSel ? hSel : hNorm).append(segSel ? hSel : hNorm);
            boolean junctionSel = useStandard && selectedField != null
                    && isStandardJunctionSelected(rowBelow, rowAbove, c, false, c == SIZE - 1, selectedField);
            char junction = useStandard
                    ? (junctionSel ? STD_CORNER_SEL : STD_CORNER)
                    : customJunctionAfterCell(rowBelow, rowAbove, c, selectedField, sym, isTopBorder, isBottomBorder);
            sb.append(junction);
        }
        return sb.toString();
    }

    private boolean isStandardJunctionSelected(int rowBelow, int rowAbove, int junctionCol, boolean isLeftEdge,
                                               boolean isRightEdge, Field selectedField) {
        if (selectedField == null) {
            return false;
        }
        if (isLeftEdge) {
            return isSelected(rowBelow, 0, selectedField) || (rowAbove >= 0 && isSelected(rowAbove, 0, selectedField));
        }
        if (isRightEdge) {
            return isSelected(rowBelow, SIZE - 1, selectedField)
                    || (rowAbove >= 0 && isSelected(rowAbove, SIZE - 1, selectedField));
        }
        return isSelected(rowBelow, junctionCol, selectedField)
                || isSelected(rowBelow, junctionCol + 1, selectedField)
                || (rowAbove >= 0 && isSelected(rowAbove, junctionCol, selectedField))
                || (rowAbove >= 0 && isSelected(rowAbove, junctionCol + 1, selectedField));
    }

    private boolean isSegmentSelected(int rowBelow, int rowAbove, int col, Field selectedField,
                                      boolean isTopBorder, boolean isBottomBorder) {
        if (selectedField == null) {
            return false;
        }
        if (isTopBorder || isBottomBorder) {
            return isSelected(rowBelow, col, selectedField);
        }
        return isSelected(rowBelow, col, selectedField) || isSelected(rowAbove, col, selectedField);
    }

    private char customLeftMidJunction(int rowBelow, int rowAbove, Field selectedField, char[] sym) {
        if (selectedField == null) {
            return sym[CUSTOM_SYMBOL_INDEX_LEFT_MID];
        }
        if (rowAbove >= 0 && isSelected(rowAbove, 0, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_LEFT_MID_SELECTED_ABOVE];
        }
        if (isSelected(rowBelow, 0, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_LEFT_MID_SELECTED_BELOW];
        }
        return sym[CUSTOM_SYMBOL_INDEX_LEFT_MID];
    }

    private char customRightMidJunction(int rowBelow, int rowAbove, Field selectedField, char[] sym) {
        if (selectedField == null) {
            return sym[CUSTOM_SYMBOL_INDEX_RIGHT_MID];
        }
        if (rowAbove >= 0 && isSelected(rowAbove, SIZE - 1, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_RIGHT_MID_SELECTED_ABOVE];
        }
        if (isSelected(rowBelow, SIZE - 1, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_RIGHT_MID_SELECTED_BELOW];
        }
        return sym[CUSTOM_SYMBOL_INDEX_RIGHT_MID];
    }

    private char customJunctionAfterCell(int rowBelow, int rowAbove, int c, Field selectedField, char[] sym,
                                        boolean isTopBorder, boolean isBottomBorder) {
        if (c == SIZE - 1) {
            if (isTopBorder) {
                return selectedField != null && isSelected(rowBelow, SIZE - 1, selectedField)
                        ? sym[CUSTOM_SYMBOL_INDEX_TOP_RIGHT_CORNER_SELECTED]
                        : sym[CUSTOM_SYMBOL_INDEX_TOP_RIGHT_CORNER];
            }
            if (isBottomBorder) {
                return selectedField != null && isSelected(rowBelow, SIZE - 1, selectedField)
                        ? sym[CUSTOM_SYMBOL_INDEX_BOTTOM_RIGHT_CORNER_SELECTED]
                        : sym[CUSTOM_SYMBOL_INDEX_BOTTOM_RIGHT_CORNER];
            }
            return customRightMidJunction(rowBelow, rowAbove, selectedField, sym);
        }
        if (isTopBorder) {
            return customTopMidJunction(rowBelow, c, selectedField, sym);
        }
        if (isBottomBorder) {
            return customBottomMidJunction(rowBelow, c, selectedField, sym);
        }
        return customCenterJunction(rowBelow, rowAbove, c, selectedField, sym);
    }

    private char customTopMidJunction(int row, int c, Field selectedField, char[] sym) {
        if (selectedField == null) {
            return sym[CUSTOM_SYMBOL_INDEX_TOP_MID];
        }
        if (isSelected(row, c, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_TOP_MID_SELECTED_LEFT];
        }
        if (isSelected(row, c + 1, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_TOP_MID_SELECTED_RIGHT];
        }
        return sym[CUSTOM_SYMBOL_INDEX_TOP_MID];
    }

    private char customBottomMidJunction(int row, int c, Field selectedField, char[] sym) {
        if (selectedField == null) {
            return sym[CUSTOM_SYMBOL_INDEX_BOTTOM_MID];
        }
        if (isSelected(row, c, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_BOTTOM_MID_SELECTED_LEFT];
        }
        if (isSelected(row, c + 1, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_BOTTOM_MID_SELECTED_RIGHT];
        }
        return sym[CUSTOM_SYMBOL_INDEX_BOTTOM_MID];
    }

    private char customCenterJunction(int rowBelow, int rowAbove, int c, Field selectedField, char[] sym) {
        if (selectedField == null) {
            return sym[CUSTOM_SYMBOL_INDEX_CENTER];
        }
        if (rowAbove >= 0 && isSelected(rowAbove, c, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_TOP_LEFT];
        }
        if (rowAbove >= 0 && isSelected(rowAbove, c + 1, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_TOP_RIGHT];
        }
        if (isSelected(rowBelow, c, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_BOTTOM_LEFT];
        }
        if (isSelected(rowBelow, c + 1, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_BOTTOM_RIGHT];
        }
        return sym[CUSTOM_SYMBOL_INDEX_CENTER];
    }

    private String buildCellLine(int r, Field selectedField, Team teamShownAsX, Team currentTeam,
                                 boolean useStandard, char[] sym) {
        StringBuilder sb = new StringBuilder();
        sb.append(r + 1).append(ROW_NUMBER_SEPARATOR);
        for (int c = 0; c < SIZE; c++) {
            boolean edgeSelected = isSelected(r, c, selectedField)
                    || (c > 0 && isSelected(r, c - 1, selectedField));
            char vChar;
            if (useStandard) {
                vChar = edgeSelected ? STD_V_SEL : STD_V;
            } else {
                vChar = edgeSelected && sym.length > CUSTOM_SYMBOL_INDEX_VERTICAL_SELECTED
                        ? sym[CUSTOM_SYMBOL_INDEX_VERTICAL_SELECTED] : sym[CUSTOM_SYMBOL_INDEX_VERTICAL];
            }
            sb.append(vChar);
            sb.append(cellContent(r, c, teamShownAsX, currentTeam));
        }
        boolean rightEdgeSelected = isSelected(r, SIZE - 1, selectedField);
        char lastV;
        if (useStandard) {
            lastV = rightEdgeSelected ? STD_V_SEL : STD_V;
        } else {
            lastV = rightEdgeSelected && sym.length > CUSTOM_SYMBOL_INDEX_VERTICAL_SELECTED
                    ? sym[CUSTOM_SYMBOL_INDEX_VERTICAL_SELECTED] : sym[CUSTOM_SYMBOL_INDEX_VERTICAL];
        }
        sb.append(lastV);
        return sb.toString();
    }

    /** Builds the column label line (A..G) with each letter centered under its column. */
    private String buildColumnLabelLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(COLUMN_LABEL_LEFT_PADDING);
        for (int c = 0; c < SIZE; c++) {
            if (c > 0) {
                sb.append(COLUMN_LABEL_SPACING);
            }
            sb.append((char) ('A' + c));
        }
        return sb.toString();
    }

    private String cellContent(int row, int col, Team teamShownAsX, Team currentTeam) {
        Field f = grid[row][col];
        Unit u = f.getUnit();
        if (u == null) {
            return EMPTY_CELL_CONTENT;
        }
        boolean ownForLetter = u.getTeam().equals(teamShownAsX);
        char letter = ownForLetter ? (u.isKing() ? 'X' : 'x') : (u.isKing() ? 'Y' : 'y');
        boolean canMove = u.getTeam().equals(currentTeam) && !u.hasMovedThisTurn();
        boolean block = u.isBlocked();
        char c1 = canMove ? '*' : ' ';
        char c2 = letter;
        char c3 = block ? 'b' : ' ';
        return "" + c1 + c2 + c3;
    }
}
