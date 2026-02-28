package Crown_of_Farmland.model;

import Crown_of_Farmland.commands.SymbolSet;
import Crown_of_Farmland.commands.VerbosityMode;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    public static final int SIZE = 7;

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

    public SymbolSet getSymbolSet() {
        return symbolSet;
    }

    public VerbosityMode getVerbosityMode() {
        return verbosityMode;
    }

    /**
     * Renders the board as a list of lines (no trailing spaces).
     * Row 7 at top, row 1 at bottom; columns A..G.
     *
     * @param selectedField the currently selected field, or null
     * @param currentTeam   the team whose turn it is (x/X = this team, y/Y = other)
     * @return lines to print
     */
    public List<String> render(Field selectedField, Team currentTeam) {
        List<String> out = new ArrayList<>();
        boolean compact = verbosityMode == VerbosityMode.COMPACT;
        boolean useStandard = !symbolSet.isCustom();
        char[] sym = symbolSet.raw();

        // Display from top (row 6 = spec row 7) down to bottom (row 0 = spec row 1)
        for (int r = SIZE - 1; r >= 0; r--) {
            if (!compact) {
                out.add(buildSeparatorLine(r, true, selectedField, useStandard, sym));
            }
            out.add(buildCellLine(r, selectedField, currentTeam, useStandard, sym));
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

    private String buildSeparatorLine(int row, boolean unused, Field selectedField,
                                      boolean useStandard, char[] sym) {
        // Draw the top edge of the given row. row 6 = top of grid, row 0 = top of bottom row. row=-1: bottom of row 0.
        int r = row >= 0 ? row : 0;
        char hNorm = useStandard ? STD_H : sym[8];
        char hSel = useStandard ? STD_H_SEL : (sym.length > 23 ? sym[23] : STD_H_SEL);
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for (int c = 0; c < SIZE; c++) {
            boolean cornerLeftSel = (c == 0 && isSelected(r, 0, selectedField))
                    || (c > 0 && (isSelected(r, c - 1, selectedField) || isSelected(r, c, selectedField)));
            boolean cornerRightSel = (c < SIZE - 1
                    && (isSelected(r, c, selectedField) || isSelected(r, c + 1, selectedField)))
                    || (c == SIZE - 1 && isSelected(r, SIZE - 1, selectedField));
            if (c == 0) {
                sb.append(cornerChar(true, true, isSelected(r, 0, selectedField), useStandard, sym));
            }
            boolean segSel = isSelected(r, c, selectedField);
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
        int selIdx = idx + 11;
        return selected && selIdx < sym.length ? sym[selIdx] : sym[idx];
    }

    private String buildCellLine(int r, Field selectedField, Team currentTeam, boolean useStandard, char[] sym) {
        StringBuilder sb = new StringBuilder();
        sb.append(r + 1).append(" ");
        for (int c = 0; c < SIZE; c++) {
            boolean edgeSelected = isSelected(r, c, selectedField)
                    || (c > 0 && isSelected(r, c - 1, selectedField));
            char vChar = useStandard
                    ? (edgeSelected ? STD_V_SEL : STD_V)
                    : (edgeSelected && sym.length > 24 ? sym[24] : sym[9]);
            sb.append(vChar);
            sb.append(cellContent(r, c, currentTeam));
        }
        boolean rightEdgeSelected = isSelected(r, SIZE - 1, selectedField);
        char lastV = useStandard
                ? (rightEdgeSelected ? STD_V_SEL : STD_V)
                : (rightEdgeSelected && sym.length > 24 ? sym[24] : sym[9]);
        sb.append(lastV);
        return sb.toString();
    }

    /** Builds the column label line (A..G) with each letter centered under its column. */
    private String buildColumnLabelLine() {
        StringBuilder sb = new StringBuilder();
        sb.append("    ");
        for (int c = 0; c < SIZE; c++) {
            if (c > 0) {
                sb.append("   ");
            }
            sb.append((char) ('A' + c));
        }
        return sb.toString();
    }

    private String cellContent(int row, int col, Team currentTeam) {
        Field f = grid[row][col];
        Unit u = f.getUnit();
        if (u == null) {
            return "   ";
        }
        boolean own = u.getTeam().equals(currentTeam);
        char letter = own ? (u.isKing() ? 'X' : 'x') : (u.isKing() ? 'Y' : 'y');
        boolean canMove = own && !u.hasMovedThisTurn();
        boolean block = u.isBlocked();
        char c1 = canMove ? '*' : ' ';
        char c2 = letter;
        char c3 = block ? 'b' : ' ';
        return "" + c1 + c2 + c3;
    }
}
