package Crown_of_Farmland.model;

import Crown_of_Farmland.commands.SymbolSet;
import Crown_of_Farmland.commands.VerbosityMode;

import java.util.ArrayList;
import java.util.List;

/**
 * The 7x7 game board for Crown of Farmland. Holds a grid of {@link Field}s and
 * renders the board (with optional selected field highlight) using the configured
 * symbol set and verbosity mode. Row 0 = bottom (spec row 1), column 0 = A.
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

    private String buildSeparatorLine(int row, boolean unused, Field selectedField, boolean useStandard, char[] sym) {
        // Draw the top edge of the given row. This same line is the BOTTOM edge of row r+1 (display).
        // So we must show selection for both row r (top of r) and row r+1 (bottom of r+1).
        // row=-1: bottom of row 0 only.
        int r = row >= 0 ? row : 0;
        int rAbove = (row >= 0 && row < SIZE - 1) ? row + 1 : -1; // row above in grid (display), or -1
        char hNorm = useStandard ? STD_H : sym[CUSTOM_SYMBOL_INDEX_HORIZONTAL];
        char hSel = useStandard ? STD_H_SEL : (sym.length > CUSTOM_SYMBOL_INDEX_HORIZONTAL_SELECTED ? sym[CUSTOM_SYMBOL_INDEX_HORIZONTAL_SELECTED] : STD_H_SEL);
        StringBuilder sb = new StringBuilder();
        sb.append(BOARD_ROW_INDENT);
        for (int c = 0; c < SIZE; c++) {
            boolean selR = isSelected(r, c, selectedField);
            boolean selRAbove = rAbove >= 0 && isSelected(rAbove, c, selectedField);
            boolean cornerLeftSel = (c == 0 && (selR || (rAbove >= 0 && isSelected(rAbove, 0, selectedField))))
                    || (c > 0 && (isSelected(r, c - 1, selectedField) || selR
                    || (rAbove >= 0 && (isSelected(rAbove, c - 1, selectedField) || isSelected(rAbove, c, selectedField)))));
            boolean cornerRightSel = (c < SIZE - 1
                    && (isSelected(r, c, selectedField) || isSelected(r, c + 1, selectedField)
                    || (rAbove >= 0 && (isSelected(rAbove, c, selectedField) || isSelected(rAbove, c + 1, selectedField)))))
                    || (c == SIZE - 1 && (selR || (rAbove >= 0 && isSelected(rAbove, SIZE - 1, selectedField))));
            if (c == 0) {
                sb.append(cornerChar(true, true, isSelected(r, 0, selectedField) || (rAbove >= 0 && isSelected(rAbove, 0, selectedField)), useStandard, sym));
            }
            boolean segSel = selR || selRAbove;
            sb.append(segSel ? hSel : hNorm).append(segSel ? hSel : hNorm).append(segSel ? hSel : hNorm);
            sb.append(cornerChar(true, false, cornerRightSel, useStandard, sym));
        }
        return sb.toString();
    }

    private char cornerChar(boolean top, boolean left, boolean selected, boolean useStandard, char[] sym) {
        if (useStandard) {
            return selected ? STD_CORNER_SEL : STD_CORNER;
        }
        int idx = top ? (left ? 0 : 1) : (left ? 2 : 3);
        int selIdx = idx + CUSTOM_CORNER_SELECTED_OFFSET;
        return selected && selIdx < sym.length ? sym[selIdx] : sym[idx];
    }

    private String buildCellLine(int r, Field selectedField, Team teamShownAsX, Team currentTeam,
                                 boolean useStandard, char[] sym) {
        StringBuilder sb = new StringBuilder();
        sb.append(r + 1).append(ROW_NUMBER_SEPARATOR);
        for (int c = 0; c < SIZE; c++) {
            boolean edgeSelected = isSelected(r, c, selectedField)
                    || (c > 0 && isSelected(r, c - 1, selectedField));
            char vChar = useStandard
                    ? (edgeSelected ? STD_V_SEL : STD_V)
                    : (edgeSelected && sym.length > CUSTOM_SYMBOL_INDEX_VERTICAL_SELECTED ? sym[CUSTOM_SYMBOL_INDEX_VERTICAL_SELECTED] : sym[CUSTOM_SYMBOL_INDEX_VERTICAL]);
            sb.append(vChar);
            sb.append(cellContent(r, c, teamShownAsX, currentTeam));
        }
        boolean rightEdgeSelected = isSelected(r, SIZE - 1, selectedField);
        char lastV = useStandard
                ? (rightEdgeSelected ? STD_V_SEL : STD_V)
                : (rightEdgeSelected && sym.length > CUSTOM_SYMBOL_INDEX_VERTICAL_SELECTED ? sym[CUSTOM_SYMBOL_INDEX_VERTICAL_SELECTED] : sym[CUSTOM_SYMBOL_INDEX_VERTICAL]);
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
