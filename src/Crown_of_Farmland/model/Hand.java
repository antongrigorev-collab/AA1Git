package Crown_of_Farmland.model;

import Crown_of_Farmland.exceptions.HandFullMustDiscardException;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private static final int MAX_SIZE = 5;

    private final List<Unit> units = new ArrayList<>();
    private boolean placedThisTurn;

    public void resetTurn() {
        placedThisTurn = false;
    }

    public boolean hasPlacedThisTurn() {
        return placedThisTurn;
    }

    public void markPlacedThisTurn() {
        placedThisTurn = true;
    }

    public boolean isFull() {
        return units.size() >= MAX_SIZE;
    }

    public int size() {
        return units.size();
    }

    public void add(Unit unit, String teamName) throws HandFullMustDiscardException {
        if (isFull()) {
            throw new HandFullMustDiscardException(teamName);
        }
        units.add(unit);
    }

    public Unit get(int oneBasedIndex) {
        int idx = oneBasedIndex - 1;
        if (idx < 0 || idx >= units.size()) {
            return null;
        }
        return units.get(idx);
    }

    public Unit remove(int oneBasedIndex) {
        int idx = oneBasedIndex - 1;
        if (idx < 0 || idx >= units.size()) {
            return null;
        }
        return units.remove(idx);
    }

    public List<Unit> snapshot() {
        return List.copyOf(units);
    }
}
