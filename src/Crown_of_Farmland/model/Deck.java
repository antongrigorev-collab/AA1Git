package Crown_of_Farmland.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
    private final List<Unit> cards = new ArrayList<>();

    public void add(Unit unit) {
        cards.add(unit);
    }

    public void shuffle(Random random) {
        Collections.shuffle(cards, random);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

    public Unit draw() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }
}
