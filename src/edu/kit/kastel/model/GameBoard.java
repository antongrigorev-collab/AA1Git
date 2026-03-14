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
     * Row 7 at top, row 1 at bottom; columns from A to G.
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
