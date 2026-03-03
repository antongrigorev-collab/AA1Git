package edu.kit.kastel.model;

/**
 * A regular unit with qualifier, role, and non-negative ATK and DEF. Used for
 * all units drawn from the deck and for merged units.
 *
 * @author usylb
 */
public class BasicUnit extends Unit {
    private final String qualifier;
    private final String role;
    private final int atk;
    private final int def;

    /**
     * Creates a basic unit with the given name parts and stats.
     *
     * @param qualifier the first part of the name (e.g. "Daisy")
     * @param role      the second part (e.g. "Farmer")
     * @param atk       attack value (non-negative)
     * @param def       defense value (non-negative)
     */
    public BasicUnit(String qualifier, String role, int atk, int def) {
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
