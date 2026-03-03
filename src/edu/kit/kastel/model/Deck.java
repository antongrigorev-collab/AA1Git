package edu.kit.kastel.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A team's draw pile. Holds up to 40 units; cards are drawn from the top (index 0).
 * Shuffling uses the game's shared {@link java.util.Random} for reproducibility.
 *
 * @author usylb
 */
public class Deck {
    private final List<Unit> cards = new ArrayList<>();

    /**
     * Adds a unit to the bottom of the deck.
     *
     * @param unit the unit to add
     */
    public void add(Unit unit) {
        cards.add(unit);
    }

    /**
     * Shuffles the deck using the given random generator.
     *
     * @param random the random generator (e.g. from game config seed)
     */
    public void shuffle(Random random) {
        Collections.shuffle(cards, random);
    }

    /**
     * Returns whether the deck has no cards.
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Returns the number of cards in the deck.
     *
     * @return deck size
     */
    public int size() {
        return cards.size();
    }

    /**
     * Draws and removes the top card.
     *
     * @return the drawn unit, or null if the deck is empty
     */
    public Unit draw() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }
}
