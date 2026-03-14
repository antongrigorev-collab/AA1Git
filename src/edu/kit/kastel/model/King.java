package edu.kit.kastel.model;

/**
 * The Farmer King unit. Represents the team on the board (D1 for team 1, D7 for
 * team 2). Has no ATK/DEF, cannot initiate duels or block; moving onto an own unit
 * eliminates that unit.
 *
 * @author usylb
 */
public final class King extends Unit {

    /** Display qualifier for Farmer King (A.1.3). */
    private static final String KING_QUALIFIER = "Farmer";

    /** Display role for Farmer King (A.1.3). */
    private static final String KING_ROLE = "King";

    /** King has no attack value (A.1.3). */
    private static final int KING_ATK = 0;

    /** King has no defense value (A.1.3). */
    private static final int KING_DEF = 0;

    private King() {
    }

    /**
     * Creates the King instance for team 1.
     *
     * @return the team 1 king
     */
    public static King forTeam1() {
        return new King();
    }

    /**
     * Creates the King instance for team 2.
     *
     * @return the team 2 king
     */
    public static King forTeam2() {
        return new King();
    }

    @Override
    public boolean isKing() {
        return true;
    }

    @Override
    public String getQualifier() {
        return KING_QUALIFIER;
    }

    @Override
    public String getRole() {
        return KING_ROLE;
    }

    @Override
    public int getAtk() {
        return KING_ATK;
    }

    @Override
    public int getDef() {
        return KING_DEF;
    }
}
