package Crown_of_Farmland.model;

public class King extends Unit {
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
        return "Farmer";
    }

    @Override
    public String getRole() {
        return "King";
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
