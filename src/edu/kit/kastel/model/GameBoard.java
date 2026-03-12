package edu.kit.kastel.model;

import edu.kit.kastel.commands.SymbolSet;
import edu.kit.kastel.commands.VerbosityMode;

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
        return BoardRenderer.render(this, selectedField, teamShownAsX, currentTeam);
    }
}
