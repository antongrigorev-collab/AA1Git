package edu.kit.kastel.model;

/**
 * Compatibility check for merging two units (A.1.10).
 * Order: Symbiose, Gleichgesinntheit, Primkompatibilität.
 *
 * @author usylb
 */
public final class Compatibility {

    /** GGT threshold above which Gleichgesinntheit applies (ggt > 100). */
    private static final int CONSPIRATIVE_GGT_THRESHOLD = 100;

    /** Divisor for converting status values to prime-check index (A.1.10 Primkompatibilität). */
    private static final int STATUS_VALUE_SCALE = 100;

    private Compatibility() { }

    /**
     * Result of a compatibility check.
     *
     * @param atk ATK of the merged unit
     * @param def DEF of the merged unit
     */
    public record MergeStats(int atk, int def) { }


    /**
     * Returns merge stats if A and B are compatible (symbiosis, conspirative, or
     * prime compatibility per A.1.10), null otherwise. A is the unit that moved
     * or was placed onto B's field.
     *
     * @param unitA the moving/placing unit
     * @param unitB the unit on the target field
     * @return ATK/DEF for the merged unit, or null if incompatible or same name
     */
    public static MergeStats check(Unit unitA, Unit unitB) {
        if (unitA.getName().equals(unitB.getName())) {
            return null;
        }
        int atkA = unitA.getAtk();
        int defA = unitA.getDef();
        int atkB = unitB.getAtk();
        int defB = unitB.getDef();

        if (atkA > atkB && atkA == defB && atkB == defA) {
            return new MergeStats(atkA, defB);
        }
        int ggtAtk = gcd(atkA, atkB);
        int ggtDef = gcd(defA, defB);
        int ggt = Math.max(ggtAtk, ggtDef);
        if (ggt > CONSPIRATIVE_GGT_THRESHOLD) {
            return new MergeStats(atkA + atkB - ggt, defA + defB - ggt);
        }
        if (ggt == CONSPIRATIVE_GGT_THRESHOLD) {
            boolean atkPrime = atkA % STATUS_VALUE_SCALE == 0 && atkB % STATUS_VALUE_SCALE == 0
                    && isPrime(atkA / STATUS_VALUE_SCALE) && isPrime(atkB / STATUS_VALUE_SCALE);
            boolean defPrime = defA % STATUS_VALUE_SCALE == 0 && defB % STATUS_VALUE_SCALE == 0
                    && isPrime(defA / STATUS_VALUE_SCALE) && isPrime(defB / STATUS_VALUE_SCALE);
            if (atkPrime || defPrime) {
                return new MergeStats(atkA + atkB, defA + defB);
            }
        }
        return null;
    }

    private static int gcd(int a, int b) {
        int x = Math.abs(a);
        int y = Math.abs(b);
        while (y != 0) {
            int t = y;
            y = x % y;
            x = t;
        }
        return x;
    }

    private static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
