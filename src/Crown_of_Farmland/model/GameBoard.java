package Crown_of_Farmland.model;

import Crown_of_Farmland.commands.SymbolSet;
import Crown_of_Farmland.commands.VerbosityMode;

public class GameBoard {
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

    public SymbolSet getSymbolSet() {
        return symbolSet;
    }

    public VerbosityMode getVerbosityMode() {
        return verbosityMode;
    }
}
