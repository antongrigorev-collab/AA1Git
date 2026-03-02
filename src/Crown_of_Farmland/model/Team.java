package Crown_of_Farmland.model;

import Crown_of_Farmland.exceptions.InvalidArgumentException;

public class Team {
    /** Initial life points for each team at game start (A.1.7). */
    public static final int INITIAL_LIFE_POINTS = 8000;

    private final String name;
    private int lifePoints;

    private final Deck deck;
    private final Hand hand;

    private final King king;

    private Team(String name, King king) {
        this.name = name;
        this.lifePoints = INITIAL_LIFE_POINTS;

        this.deck = new Deck();
        this.hand = new Hand();
        this.king = king;
        this.king.setTeam(this);
        this.king.setRevealed(true);
    }

    public static Team createTeam1(String name) throws InvalidArgumentException {
        try {
            return new Team(name, King.forTeam1());
        }
        catch (Exception e) {
            throw new InvalidArgumentException(e.getMessage());
        }
    }

    public static Team createTeam2(String name) throws InvalidArgumentException {
        try {
            return new Team(name, King.forTeam2());
        }
        catch (Exception e) {
            throw new InvalidArgumentException(e.getMessage());
        }
    }

    public String getName() {
        return name;
    }

    public int getLifePoints() {
        return lifePoints;
    }

    public void takeDamage(int amount) {
        if (amount <= 0) {
            return;
        }
        lifePoints -= amount;
    }

    public boolean isDead() {
        return lifePoints <= 0;
    }

    public Deck getDeck() {
        return deck;
    }

    public Hand getHand() {
        return hand;
    }

    public King getKing() {
        return king;
    }
}
