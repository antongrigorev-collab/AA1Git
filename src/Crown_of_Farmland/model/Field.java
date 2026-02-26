package Crown_of_Farmland.model;

public final class Field {

    private final int row;
    private final int col;

    private Unit unit;

    public Field(int row, int col) {
        this.row = row;
        this.col = col;
        this.unit = null;
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public boolean isEmpty() {
        return unit == null;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void removeUnit() {
        this.unit = null;
    }

    public String coordinate() {
        char c = (char) ('A' + col);
        int r = row + 1;
        return "" + c + r;
    }
}
