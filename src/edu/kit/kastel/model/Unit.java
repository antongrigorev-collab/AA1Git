package edu.kit.kastel.model;

/**
 * Base type for units on the board or in hand: name (qualifier + role), ATK/DEF,
 * team, and state (revealed, blocked, moved this turn). Implementations are
 * {@link BasicUnit} and {@link King}.
 *
 * @author usylb
 */
public abstract class Unit {
    /** Separator between qualifier and role in unit name (A.1.2). */
    private static final String QUALIFIER_ROLE_SEPARATOR = " ";

    private Team team;

    private boolean revealed;
    private boolean blocked;
    private boolean movedThisTurn;

    /** Constructor for subclasses; initialises revealed, blocked and movedThisTurn to false. */
    protected Unit() {
        this.revealed = false;
        this.blocked = false;
        this.movedThisTurn = false;
    }

    /**
     * Returns the qualifier part of the unit name (e.g. "Daisy").
     *
     * @return the qualifier
     */
    public abstract String getQualifier();

    /**
     * Returns the role part of the unit name (e.g. "Farmer").
     *
     * @return the role
     */
    public abstract String getRole();

    /**
     * Returns the attack value (0 for King).
     *
     * @return ATK
     */
    public abstract int getAtk();

    /**
     * Returns the defense value (0 for King).
     *
     * @return DEF
     */
    public abstract int getDef();

    /**
     * Returns the full display name (qualifier + " " + role).
     *
     * @return the unit name
     */
    public String getName() {
        return getQualifier() + QUALIFIER_ROLE_SEPARATOR + getRole();
    }

    /**
     * Returns whether this unit is the team's Farmer King.
     *
     * @return true only for {@link King}
     */
    public boolean isKing() {
        return false;
    }

    /**
     * Returns the team this unit belongs to.
     *
     * @return the team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Sets the team this unit belongs to.
     *
     * @param team the team
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Returns whether this unit has been revealed (flipped).
     *
     * @return true if revealed
     */
    public boolean isRevealed() {
        return revealed;
    }

    /**
     * Sets whether this unit is revealed.
     *
     * @param revealed true to mark as revealed
     */
    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    /**
     * Returns whether this unit is currently blocking.
     *
     * @return true if blocking
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * Sets whether this unit is blocking.
     *
     * @param blocked true to set blocking
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    /**
     * Returns whether this unit has moved in the current turn.
     *
     * @return true if it has moved this turn
     */
    public boolean hasMovedThisTurn() {
        return movedThisTurn;
    }

    /**
     * Sets whether this unit has moved in the current turn.
     *
     * @param movedThisTurn true if it has moved this turn
     */
    public void setMovedThisTurn(boolean movedThisTurn) {
        this.movedThisTurn = movedThisTurn;
    }
}
