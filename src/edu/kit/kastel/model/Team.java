package edu.kit.kastel.model;

/**
 * Represents one team in Crown of Farmland: name, life points, deck, hand, and
 * the Farmer King. Created via {@link #createTeam1(String)} or
 * {@link #createTeam2(String)}.
 *
 * @author usylb
 */
public final class Team {
    /** Initial life points for each team at game start (A.1.7). */
    public static final int INITIAL_LIFE_POINTS = 8000;

    /** Threshold: team is dead when life points are at or below this value. */
    private static final int LIFE_THRESHOLD_DEAD = 0;

    /** Minimum damage amount to apply (non-positive damage is ignored). */
    private static final int MIN_DAMAGE_TO_APPLY = 0;

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

    /**
     * Creates team 1 (player) with the given display name.
     *
     * @param name the team name (e.g. "Player")
     * @return the new team
     */
    public static Team createTeam1(String name) {
        return new Team(name, King.forTeam1());
    }

    /**
     * Creates team 2 (opponent) with the given display name.
     *
     * @param name the team name (e.g. "Enemy")
     * @return the new team
     */
    public static Team createTeam2(String name) {
        return new Team(name, King.forTeam2());
    }

    /**
     * Returns the team's display name.
     *
     * @return the team name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current life points.
     *
     * @return life points (0 when dead)
     */
    public int getLifePoints() {
        return lifePoints;
    }

    /**
     * Applies damage to this team (reduces life points). Ignores non-positive values.
     *
     * @param amount the damage to apply
     */
    public void takeDamage(int amount) {
        if (amount <= MIN_DAMAGE_TO_APPLY) {
            return;
        }
        lifePoints -= amount;
    }

    /**
     * Returns whether this team has lost (life points &lt;= 0).
     *
     * @return true if the team is dead
     */
    public boolean isDead() {
        return lifePoints <= LIFE_THRESHOLD_DEAD;
    }

    /**
     * Returns this team's deck (draw pile).
     *
     * @return the deck
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Returns this team's hand (up to 5 units).
     *
     * @return the hand
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Returns this team's Farmer King unit.
     *
     * @return the king
     */
    public King getKing() {
        return king;
    }
}
