package Crown_of_Farmland.model;

/**
 * Compatibility check for merging two units (A.1.10).
 * Order: Symbiose, Gleichgesinntheit, Primkompatibilität.
 */
public final class Compatibility {

    private Compatibility() { }

    /**
     * Result of a compatibility check.
     *
     * @param atk ATK of the merged unit
     * @param def DEF of the merged unit
     */
    public record MergeStats(int atk, int def) { }


    /**
     * Returns merge stats if A and B are compatible, null otherwise.
     * A is the unit that moved/placed onto B's field.
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
        if (ggt > 100) {
            return new MergeStats(atkA + atkB - ggt, defA + defB - ggt);
        }
        if (ggt == 100) {
            boolean atkPrime = atkA % 100 == 0 && atkB % 100 == 0
                    && isPrime(atkA / 100) && isPrime(atkB / 100);
            boolean defPrime = defA % 100 == 0 && defB % 100 == 0
                    && isPrime(defA / 100) && isPrime(defB / 100);
            if (atkPrime || defPrime) {
                return new MergeStats(atkA + atkB, defA + defB);
            }
        }
        return null;
    }

    private static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
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
