package edu.kit.kastel.model;

import edu.kit.kastel.exceptions.HandFullMustDiscardException;

import java.util.ArrayList;
import java.util.List;

/**
 * A team's hand. Holds up to 5 units. Tracks whether the team has placed this
 * turn (at most one place per turn). Indices in get/remove are 1-based.
 *
 * @author usylb
 */
public class Hand {
    private static final int MAX_SIZE = 5;

    private final List<Unit> units = new ArrayList<>();
    private boolean placedThisTurn;

    /**
     * Resets the "placed this turn" flag (called at start of team's turn).
     */
    public void resetTurn() {
        placedThisTurn = false;
    }

    /**
     * Returns whether the team has already placed a unit this turn.
     *
     * @return true if placed this turn
     */
    public boolean hasPlacedThisTurn() {
        return placedThisTurn;
    }

    /**
     * Marks that the team has placed a unit this turn.
     */
    public void markPlacedThisTurn() {
        placedThisTurn = true;
    }

    /**
     * Returns whether the hand has 5 cards (must discard before yield).
     *
     * @return true if full
     */
    public boolean isFull() {
        return units.size() >= MAX_SIZE;
    }

    /**
     * Returns the number of units in the hand.
     *
     * @return hand size
     */
    public int size() {
        return units.size();
    }

    /**
     * Adds a unit to the hand. Fails if the hand is already full.
     *
     * @param unit     the unit to add
     * @param teamName the team name (for error message)
     * @throws HandFullMustDiscardException if the hand already has 5 units
     */
    public void add(Unit unit, String teamName) throws HandFullMustDiscardException {
        if (isFull()) {
            throw new HandFullMustDiscardException(teamName);
        }
        units.add(unit);
    }

    /**
     * Returns the unit at the given 1-based index, or null if out of range.
     *
     * @param oneBasedIndex index 1..size()
     * @return the unit or null
     */
    public Unit get(int oneBasedIndex) {
        int idx = oneBasedIndex - 1;
        if (idx < 0 || idx >= units.size()) {
            return null;
        }
        return units.get(idx);
    }

    /**
     * Removes and returns the unit at the given 1-based index.
     *
     * @param oneBasedIndex index 1..size()
     * @return the removed unit or null if out of range
     */
    public Unit remove(int oneBasedIndex) {
        int idx = oneBasedIndex - 1;
        if (idx < 0 || idx >= units.size()) {
            return null;
        }
        return units.remove(idx);
    }

    /**
     * Returns an immutable copy of the current hand (for display or iteration).
     *
     * @return copy of the hand list
     */
    public List<Unit> snapshot() {
        return List.copyOf(units);
    }
}
