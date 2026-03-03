package edu.kit.kastel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helper for {@link AIPlayer} (AI opponent, A.2). Provides weighted random selection,
 * score-based selection, board counting and unit move option scoring.
 *
 * @author usylb
 */
final class AIPlayerHelper {

    /** Unit move advance score: multiplier for steps toward enemy king (A.2). */
    private static final int ADVANCE_SCORE_STEPS_MULTIPLIER = 10;

    /** Penalty for attacking a hidden (unrevealed) unit (A.2). */
    private static final int PENALTY_ATTACKING_HIDDEN_UNIT = 500;

    /** Standard duel score: multiplier for attack difference (A.2). */
    private static final int ATTACK_DIFFERENCE_MULTIPLIER = 2;

    /** Divisor for block/en-place score scaling from status values (A.2). */
    private static final int AI_SCORE_STATUS_DIVISOR = 100;

    /**
     * Options (target coordinates) and scores for one unit's possible moves (A.2).
     * Order: up, right, down, left, block (same cell), en place (special -1,-1).
     *
     * @param options list of target coordinates per option
     * @param scores  list of scores in same order as options
     */
    record UnitMoveOption(List<int[]> options, List<Integer> scores) { }

    private AIPlayerHelper() { }

    /**
     * Weighted random selection (A.1.12). Negative weights treated as 0.
     *
     * @param weights list of weights (order = option order)
     * @param rnd     random
     * @return index of selected option
     */
    static int weightedSelect(List<Integer> weights, Random rnd) {
        int sum = 0;
        for (Integer w : weights) {
            sum += Math.max(0, w);
        }
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
        int max = Integer.MIN_VALUE;
        for (Integer s : scores) {
            if (s > max) {
                max = s;
            }
        }
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
                int r = row + dr;
                int c = col + dc;
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
            int r = row + dx[0];
            int c = col + dx[1];
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
            int r = row + dx[0];
            int c = col + dx[1];
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

    /**
     * Context for computing one unit's move options (A.2).
     *
     * @param game         the game state
     * @param unit         the unit for which options are computed
     * @param unitRow      row of the unit
     * @param unitCol      column of the unit
     * @param ai           the AI team
     * @param enemy        the enemy team
     * @param enemyKingRow row of the enemy king
     * @param enemyKingCol column of the enemy king
     */
    record UnitMoveContext(Game game, Unit unit, int unitRow, int unitCol,
                           Team ai, Team enemy, int enemyKingRow, int enemyKingCol) { }

    /**
     * Computes move options and scores for one unit (A.2): 4 directions, block, en place.
     *
     * @param ctx context (game, unit, positions, teams, enemy king position)
     * @return options and scores in standard order
     */
    static UnitMoveOption computeUnitMoveOptions(UnitMoveContext ctx) {
        Game game = ctx.game();
        Unit u = ctx.unit();
        int ur = ctx.unitRow();
        int uc = ctx.unitCol();
        Team ai = ctx.ai();
        Team enemy = ctx.enemy();
        int ekr = ctx.enemyKingRow();
        int ekc = ctx.enemyKingCol();
        List<int[]> options = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();
        int[][] dirs = {{ur + 1, uc}, {ur, uc + 1}, {ur - 1, uc}, {ur, uc - 1}};
        for (int[] d : dirs) {
            int tr = d[0];
            int tc = d[1];
            if (tr < 0 || tr >= GameBoard.SIZE || tc < 0 || tc >= GameBoard.SIZE) {
                options.add(d);
                scores.add(0);
                continue;
            }
            Field toField = game.getGameBoard().getField(tr, tc);
            Unit target = toField.getUnit();
            int score = 0;
            if (target == null) {
                int steps = Math.abs(tr - ekr) + Math.abs(tc - ekc);
                int enemies = countAdjacent4(game, tr, tc, enemy);
                score = ADVANCE_SCORE_STEPS_MULTIPLIER * steps - enemies;
            } else if (target.getTeam().equals(ai)) {
                Compatibility.MergeStats stats = Compatibility.check(u, target);
                if (stats != null && !target.isKing()) {
                    score = stats.atk() + stats.def() - u.getAtk() - u.getDef();
                } else {
                    score = -target.getAtk() - target.getDef();
                }
            } else {
                if (target.isKing()) {
                    score = u.getAtk();
                } else if (!target.isRevealed()) {
                    score = u.getAtk() - PENALTY_ATTACKING_HIDDEN_UNIT;
                } else if (target.isBlocked()) {
                    score = u.getAtk() - target.getDef();
                } else {
                    score = ATTACK_DIFFERENCE_MULTIPLIER * (u.getAtk() - target.getAtk());
                }
            }
            options.add(d);
            scores.add(score);
        }
        int blockScore = (int) Math.max(1, (u.getDef() - maxEnemyAtkInLine(game, ur, uc, enemy))
                / AI_SCORE_STATUS_DIVISOR);
        options.add(new int[] { ur, uc });
        scores.add(blockScore);
        int enPlaceScore = (int) Math.max(0, (u.getAtk() - maxEnemyAtkInLine(game, ur, uc, enemy))
                / AI_SCORE_STATUS_DIVISOR);
        options.add(new int[] { -1, -1 });
        scores.add(enPlaceScore);
        return new UnitMoveOption(options, scores);
    }
}
