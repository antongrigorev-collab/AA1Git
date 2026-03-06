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

    /** Remainder meaning divisible (for modulo checks). */
    private static final int REMAINDER_ZERO = 0;

    /** Smallest prime number (used in prime check). */
    private static final int SMALLEST_PRIME = 2;

    /** GCD loop terminates when remainder is this. */
    private static final int GCD_TERMINATE_WHEN = 0;

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
            boolean atkPrime = atkA % STATUS_VALUE_SCALE == REMAINDER_ZERO && atkB % STATUS_VALUE_SCALE == REMAINDER_ZERO
                    && isPrime(atkA / STATUS_VALUE_SCALE) && isPrime(atkB / STATUS_VALUE_SCALE);
            boolean defPrime = defA % STATUS_VALUE_SCALE == REMAINDER_ZERO && defB % STATUS_VALUE_SCALE == REMAINDER_ZERO
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
        while (y != GCD_TERMINATE_WHEN) {
            int t = y;
            y = x % y;
            x = t;
        }
        return x;
    }

    private static boolean isPrime(int n) {
        if (n < SMALLEST_PRIME) {
            return false;
        }
        for (int i = SMALLEST_PRIME; i * i <= n; i++) {
            if (n % i == REMAINDER_ZERO) {
                return false;
            }
        }
        return true;
    }
}
