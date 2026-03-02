package Crown_of_Farmland.model;

public class King extends Unit {

    /** Display qualifier for Farmer King (A.1.3). */
    private static final String KING_QUALIFIER = "Farmer";

    /** Display role for Farmer King (A.1.3). */
    private static final String KING_ROLE = "King";

    private King() {
        super();
    }

    public static King forTeam1() {
        return new King();
    }

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
        return 0;
    }

    @Override
    public int getDef() {
        return 0;
    }
}
