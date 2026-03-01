package Crown_of_Farmland.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helper for {@link AIPlayer} (AI opponent, A.2). Provides weighted random selection,
 * score-based selection and board counting utilities.
 */
final class AIPlayerHelper {

    private AIPlayerHelper() { }

    /**
     * Weighted random selection (A.1.12). Negative weights treated as 0.
     *
     * @param weights list of weights (order = option order)
     * @param rnd     random
     * @return index of selected option
     */
    static int weightedSelect(List<Integer> weights, Random rnd) {
        int sum = weights.stream().mapToInt(w -> Math.max(0, w)).sum();
        if (sum <= 0) {
            return rnd.nextInt(weights.size());
        }
        int r = rnd.nextInt(sum) + 1;
        int acc = 0;
        for (int i = 0; i < weights.size(); i++) {
            acc += Math.max(0, weights.get(i));
            if (r <= acc) {
                return i;
            }
        }
        return weights.size() - 1;
    }

    /**
     * Selects among indices with maximum score. Bei Gleichstand: Gewichte je 1.
     *
     * @param scores list of scores (order preserved)
     * @param rnd    random
     * @return index of selected item
     */
    static int selectAmongMaxScore(List<Integer> scores, Random rnd) {
        int max = scores.stream().mapToInt(Integer::intValue).max().orElse(0);
        List<Integer> tiedIndices = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) == max) {
                tiedIndices.add(i);
            }
        }
        if (tiedIndices.size() == 1) {
            return tiedIndices.get(0);
        }
        List<Integer> ones = new ArrayList<>();
        for (int i = 0; i < tiedIndices.size(); i++) {
            ones.add(1);
        }
        int sel = weightedSelect(ones, rnd);
        return tiedIndices.get(sel);
    }

    /**
     * Counts adjacent units in 8 directions.
     *
     * @param game        game state
     * @param row         row
     * @param col         column
     * @param team        team to count
     * @param includeKing whether to count the king
     * @return count
     */
    static int countAdjacent(Game game, int row, int col, Team team, boolean includeKing) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                int r = row + dr, c = col + dc;
                if (r >= 0 && r < GameBoard.SIZE && c >= 0 && c < GameBoard.SIZE) {
                    Unit u = game.getGameBoard().getField(r, c).getUnit();
                    if (u != null && u.getTeam().equals(team) && (includeKing || !u.isKing())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Counts adjacent units in 4 directions (cardinal).
     *
     * @param game game state
     * @param row  row
     * @param col  column
     * @param team team to count
     * @return count
     */
    static int countAdjacent4(Game game, int row, int col, Team team) {
        int count = 0;
        int[][] d = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dx : d) {
            int r = row + dx[0], c = col + dx[1];
            if (r >= 0 && r < GameBoard.SIZE && c >= 0 && c < GameBoard.SIZE) {
                Unit u = game.getGameBoard().getField(r, c).getUnit();
                if (u != null && u.getTeam().equals(team)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Maximum ATK among enemy units in straight line (4 directions) from (row, col).
     *
     * @param game  game state
     * @param row   row
     * @param col   column
     * @param enemy enemy team
     * @return max ATK or 0
     */
    static int maxEnemyAtkInLine(Game game, int row, int col, Team enemy) {
        int max = 0;
        int[][] d = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dx : d) {
            int r = row + dx[0], c = col + dx[1];
            while (r >= 0 && r < GameBoard.SIZE && c >= 0 && c < GameBoard.SIZE) {
                Unit u = game.getGameBoard().getField(r, c).getUnit();
                if (u == null) {
                    r += dx[0];
                    c += dx[1];
                    continue;
                }
                if (u.getTeam().equals(enemy) && !u.isKing()) {
                    max = Math.max(max, u.getAtk());
                }
                break;
            }
        }
        return max;
    }
}
