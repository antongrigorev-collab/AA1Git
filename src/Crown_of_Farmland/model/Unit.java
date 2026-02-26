package Crown_of_Farmland.model;

public abstract class Unit {
    private Team team;

    private boolean revealed;
    private boolean blocked;
    private boolean movedThisTurn;

    protected Unit() {
        this.revealed = false;
        this.blocked = false;
        this.movedThisTurn = false;
    }

    public abstract String getQualifier();
    public abstract String getRole();

    public abstract int getAtk();
    public abstract int getDef();

    public String getName() {
        return getQualifier() + " " + getRole();
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
