package Crown_of_Farmland.commands;

public class SymbolSet {

    /** Number of symbols required for custom board display (A.3.1). */
    private static final int CUSTOM_SYMBOL_SET_SIZE = 29;

    private final char[] symbols;

    public SymbolSet(char[] symbols) {
        this.symbols = symbols.clone();
    }

    public boolean isCustom() {
        return symbols.length == CUSTOM_SYMBOL_SET_SIZE;
    }
    public char[] raw() {
        return symbols.clone();
    }

    public static SymbolSet standard() {
        // Renderer kann daraus + - | etc. ableiten
        return new SymbolSet(new char[] {'+', '-', '|'});
    }
}
