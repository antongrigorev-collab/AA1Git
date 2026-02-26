package Crown_of_Farmland.commands;

public class SymbolSet {
    private final char[] symbols;
    public SymbolSet(char[] symbols) {
        this.symbols = symbols.clone();
    }
    public boolean isCustom() {
        return symbols.length == 29;
    }
    public char[] raw() {
        return symbols.clone();
    }

    public static SymbolSet standard() {
        // Renderer kann daraus + - | etc. ableiten
        return new SymbolSet(new char[] {'+', '-', '|'});
    }
}
