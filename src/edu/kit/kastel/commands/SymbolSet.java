package edu.kit.kastel.commands;

/**
 * Holds the set of characters used to render the game board. Supports either
 * the standard set (+, -, |) or a custom set of 29 symbols read from a file.
 *
 * @author usylb
 */
public class SymbolSet {

    /** Number of symbols required for custom board display (A.3.1). */
    private static final int CUSTOM_SYMBOL_SET_SIZE = 29;

    private final char[] symbols;

    /**
     * Creates a symbol set from the given character array (copied).
     *
     * @param symbols the board symbols (3 for standard, 29 for custom)
     */
    public SymbolSet(char[] symbols) {
        this.symbols = symbols.clone();
    }

    /**
     * Returns whether this is a custom symbol set (29 symbols).
     *
     * @return true if custom, false if standard
     */
    public boolean isCustom() {
        return symbols.length == CUSTOM_SYMBOL_SET_SIZE;
    }

    /**
     * Returns a copy of the raw symbol array.
     *
     * @return the symbol array
     */
    public char[] raw() {
        return symbols.clone();
    }

    /**
     * Returns the standard symbol set (+, -, |) for ASCII board rendering.
     *
     * @return the standard symbol set
     */
    public static SymbolSet standard() {
        return new SymbolSet(new char[] {'+', '-', '|'});
    }
}
