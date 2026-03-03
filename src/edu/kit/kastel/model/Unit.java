package edu.kit.kastel.model;

/**
 * Base type for units on the board or in hand: name (qualifier + role), ATK/DEF,
 * team, and state (revealed, blocked, moved this turn). Implementations are
 * {@link BasicUnit} and {@link King}.
 */
public abstract class Unit {
    /** Separator between qualifier and role in unit name (A.1.2). */
    private static final String QUALIFIER_ROLE_SEPARATOR = " ";

    private Team team;

    private boolean revealed;
    private boolean blocked;
    private boolean movedThisTurn;

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

    public boolean isKing() {
        return false;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean hasMovedThisTurn() {
        return movedThisTurn;
    }

    public void setMovedThisTurn(boolean movedThisTurn) {
        this.movedThisTurn = movedThisTurn;
    }
}
