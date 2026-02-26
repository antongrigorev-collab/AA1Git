package Crown_of_Farmland.model;

public class BasicUnit extends Unit {
    private final String qualifier;
    private final String role;
    private final int atk;
    private final int def;

    public BasicUnit(String qualifier, String role, int atk, int def) {
        super();
        this.qualifier = qualifier;
        this.role = role;
        this.atk = atk;
        this.def = def;
    }

    @Override
    public String getQualifier() {
        return qualifier;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public int getAtk() {
        return atk;
    }

    @Override
    public int getDef() {
        return def;
    }
}
