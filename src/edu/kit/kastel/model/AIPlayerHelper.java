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

    static final int EN_PLACE_SENTINEL_ROW = -1;
    static final int EN_PLACE_SENTINEL_COL = -1;

    private static final int ADVANCE_SCORE_STEPS_MULTIPLIER = 10;
    private static final int PENALTY_ATTACKING_HIDDEN_UNIT = 500;
    private static final int ATTACK_DIFFERENCE_MULTIPLIER = 2;
    private static final int AI_SCORE_STATUS_DIVISOR = 100;
    private static final String NO_LONGER_BLOCKS_MESSAGE_FORMAT = "%s no longer blocks.";
    private static final String MOVES_TO_MESSAGE_FORMAT = "%s moves to %s.";
    private static final String JOIN_FORCES_MESSAGE_FORMAT = "%s and %s on %s join forces!";
    private static final String SUCCESS_MESSAGE = "Success!";
    private static final String UNION_FAILED_MESSAGE_FORMAT = "Union failed. %s was eliminated.";
    private static final String PLACES_ON_MESSAGE_FORMAT = "%s places %s on %s.";
    private static final String JUST_PLACED_ELIMINATED_MESSAGE_FORMAT = "%s was eliminated!";
    private static final int INDEX_ROW = 0;
    private static final int INDEX_COL = 1;
    private static final int MIN_BOARD_INDEX = 0;
    private static final int SCORE_ZERO = 0;
    private static final int MIN_BLOCK_SCORE = 1;
    private static final int MIN_EN_PLACE_SCORE = 0;

    /**
     * Context for merge/eliminate action (game, units, coordinates, target field).
     *
     * @param game     the game state
     * @param unit     the moving/attacking unit
     * @param defender the unit on the target field (same team, for merge/eliminate)
     * @param fromRow  row of the moving unit
     * @param fromCol  column of the moving unit
     * @param toRow    row of the target field
     * @param toCol    column of the target field
     * @param toField  the target field
     */
    record MergeActionContext(Game game, Unit unit, Unit defender,
                             int fromRow, int fromCol, int toRow, int toCol, Field toField) { }

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
        int chosenIndex;
        if (sum <= 0) {
            chosenIndex = rnd.nextInt(0, weights.size());
        } else {
            int r = rnd.nextInt(1, sum + 1);
            int acc = 0;
            chosenIndex = weights.size() - 1;
            for (int i = 0; i < weights.size(); i++) {
                acc += Math.max(0, weights.get(i));
                if (r <= acc) {
                    chosenIndex = i;
                    break;
                }
            }
        }
        return chosenIndex;
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
            int tr = d[INDEX_ROW];
            int tc = d[INDEX_COL];
            if (tr < MIN_BOARD_INDEX || tr >= GameBoard.SIZE || tc < MIN_BOARD_INDEX || tc >= GameBoard.SIZE) {
                options.add(d);
                scores.add(SCORE_ZERO);
                continue;
            }
            Field toField = game.getGameBoard().getField(tr, tc);
            Unit target = toField.getUnit();
            int score = SCORE_ZERO;
            if (target == null) {
                int steps = Math.abs(tr - ekr) + Math.abs(tc - ekc);
                int enemies = countAdjacent4(game, tr, tc, enemy);
                score = ADVANCE_SCORE_STEPS_MULTIPLIER * steps - enemies;
            } else if (target.getTeam().equals(ai)) {
                Compatibility.MergeStats stats = Compatibility.check(u, target);
                if (stats != null && !target.isKing()) {
                    score = stats.atk() + stats.def() - u.getAtk() - u.getDef();
                } else {
                    score = SCORE_ZERO - target.getAtk() - target.getDef();
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
        int blockScore = (int) Math.max(MIN_BLOCK_SCORE, (u.getDef() - maxEnemyAtkInLine(game, ur, uc, enemy))
                / AI_SCORE_STATUS_DIVISOR);
        options.add(new int[] { ur, uc });
        scores.add(blockScore);
        int enPlaceScore = (int) Math.max(MIN_EN_PLACE_SCORE, (u.getAtk() - maxEnemyAtkInLine(game, ur, uc, enemy))
                / AI_SCORE_STATUS_DIVISOR);
        options.add(new int[] { EN_PLACE_SENTINEL_ROW, EN_PLACE_SENTINEL_COL });
        scores.add(enPlaceScore);
        return new UnitMoveOption(options, scores);
    }

    /**
     * Executes merge (if compatible) or eliminate defender on same-team field.
     *
     * @param ctx context (game, unit, defender, coordinates, target field)
     */
    static void executeMergeOrEliminate(MergeActionContext ctx) {
        if (ctx.unit().isBlocked()) {
            ctx.unit().setBlocked(false);
            System.out.println(String.format(NO_LONGER_BLOCKS_MESSAGE_FORMAT, ctx.unit().getName()));
        }
        Compatibility.MergeStats stats = Compatibility.check(ctx.unit(), ctx.defender());
        if (stats != null) {
            Unit merged = Game.createMergedUnit(ctx.unit(), ctx.defender(), stats);
            merged.setTeam(ctx.game().getCurrentTeam());
            merged.setMovedThisTurn(false);
            ctx.game().getGameBoard().getField(ctx.fromRow(), ctx.fromCol()).removeUnit();
            ctx.toField().removeUnit();
            ctx.game().getGameBoard().placeUnit(ctx.toRow(), ctx.toCol(), merged);
            ctx.game().setSelectedField(ctx.toField());
            System.out.println(String.format(MOVES_TO_MESSAGE_FORMAT, ctx.unit().getName(), ctx.toField().coordinate()));
            System.out.println(String.format(JOIN_FORCES_MESSAGE_FORMAT, ctx.unit().getName(),
                    ctx.defender().getName(), ctx.toField().coordinate()));
            System.out.println(SUCCESS_MESSAGE);
        } else {
            ctx.game().getGameBoard().getField(ctx.fromRow(), ctx.fromCol()).removeUnit();
            ctx.toField().removeUnit();
            ctx.game().getGameBoard().placeUnit(ctx.toRow(), ctx.toCol(), ctx.unit());
            ctx.unit().setMovedThisTurn(true);
            ctx.game().setSelectedField(ctx.toField());
            System.out.println(String.format(MOVES_TO_MESSAGE_FORMAT, ctx.unit().getName(), ctx.toField().coordinate()));
            System.out.println(String.format(JOIN_FORCES_MESSAGE_FORMAT, ctx.unit().getName(),
                    ctx.defender().getName(), ctx.toField().coordinate()));
            System.out.println(String.format(UNION_FAILED_MESSAGE_FORMAT, ctx.defender().getName()));
        }
    }
    /**
     * Places one unit from the current team's hand onto the given field (merge or eliminate as per A.2).
     *
     * @param game      game state
     * @param handIndex 1-based hand index
     * @param row       row
     * @param col       column
     */
    static void placeUnit(Game game, int handIndex, int row, int col) {
        Unit unit = game.getCurrentTeam().getHand().get(handIndex);
        if (unit == null) {
            return;
        }
        game.getCurrentTeam().getHand().remove(handIndex);
        game.getCurrentTeam().getHand().markPlacedThisTurn();
        unit.setTeam(game.getCurrentTeam());
        Field field = game.getGameBoard().getField(row, col);
        Unit current = field.getUnit();
        System.out.println(String.format(PLACES_ON_MESSAGE_FORMAT, game.getCurrentTeam().getName(), unit.getName(), field.coordinate()));
        if (current == null) {
            game.getGameBoard().placeUnit(row, col, unit);
        } else {
            System.out.println(String.format(JOIN_FORCES_MESSAGE_FORMAT, unit.getName(), current.getName(), field.coordinate()));
            Compatibility.MergeStats stats = Compatibility.check(unit, current);
            if (stats != null) {
                Unit merged = Game.createMergedUnit(unit, current, stats);
                merged.setTeam(game.getCurrentTeam());
                merged.setMovedThisTurn(false);
                field.removeUnit();
                game.getGameBoard().placeUnit(row, col, merged);
                System.out.println(SUCCESS_MESSAGE);
            } else {
                field.removeUnit();
                game.getGameBoard().placeUnit(row, col, unit);
                System.out.println(String.format(UNION_FAILED_MESSAGE_FORMAT, current.getName()));
            }
        }
        if (game.getBoardCount(game.getCurrentTeam()) > Game.MAX_NON_KING_UNITS_ON_BOARD) {
            Unit justPlaced = game.getGameBoard().getField(row, col).getUnit();
            game.getGameBoard().getField(row, col).removeUnit();
            System.out.println(String.format(JUST_PLACED_ELIMINATED_MESSAGE_FORMAT, justPlaced.getName()));
        }
    }
}
