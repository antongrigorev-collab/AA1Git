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
    private static final int DIR_OFFSET_UP = 1;
    private static final int DIR_OFFSET_DOWN = -1;
    private static final int DIR_OFFSET_RIGHT = 1;
    private static final int DIR_OFFSET_LEFT = -1;
    private static final int[][] EIGHT_NEIGHBOR_OFFSETS = {
        {DIR_OFFSET_DOWN, DIR_OFFSET_DOWN}, {DIR_OFFSET_DOWN, MIN_BOARD_INDEX}, {DIR_OFFSET_DOWN, DIR_OFFSET_UP},
        {MIN_BOARD_INDEX, DIR_OFFSET_LEFT}, {MIN_BOARD_INDEX, DIR_OFFSET_RIGHT},
        {DIR_OFFSET_UP, DIR_OFFSET_DOWN}, {DIR_OFFSET_UP, MIN_BOARD_INDEX}, {DIR_OFFSET_UP, DIR_OFFSET_UP}
    };
    private static final int SINGLE_OPTION_INDEX = 0;
    private static final int SIZE_ONE = 1;
    private static final int INITIAL_SUM = 0;
    private static final int WEIGHT_FLOOR = 0;
    private static final int RANDOM_ORIGIN_INCLUSIVE = 1;
    private static final int LAST_INDEX_OFFSET = 1;
    private static final int FIRST_INDEX = 0;
    private static final int WEIGHT_TIE = 1;
    private static final int COUNT_ONE = 1;
    private static final int INITIAL_COUNT = 0;
    private static final int INITIAL_MAX_ATK = 0;
    private static final String FORMAT_NEWLINE = "%n";

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
     * Options and scores for one unit's possible moves. Order: up, right, down, left, block , en place (special -1,-1).
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
        if (weights.size() == SIZE_ONE) {
            return SINGLE_OPTION_INDEX;
        }
        int sum = INITIAL_SUM;
        for (Integer w : weights) {
            sum += Math.max(WEIGHT_FLOOR, w);
        }
        int chosenIndex;
        if (sum <= SCORE_ZERO) {
            chosenIndex = rnd.nextInt(MIN_BOARD_INDEX, weights.size());
        } else {
            int r = rnd.nextInt(RANDOM_ORIGIN_INCLUSIVE, sum + LAST_INDEX_OFFSET);
            int acc = INITIAL_SUM;
            chosenIndex = weights.size() - LAST_INDEX_OFFSET;
            for (int i = FIRST_INDEX; i < weights.size(); i++) {
                acc += Math.max(WEIGHT_FLOOR, weights.get(i));
                if (r <= acc) {
                    chosenIndex = i;
                    break;
                }
            }
        }
        return chosenIndex;
    }

    /**
     * Selects among indices with maximum score. On tie, each option has weight 1.
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
        for (int i = FIRST_INDEX; i < scores.size(); i++) {
            if (scores.get(i) == max) {
                tiedIndices.add(i);
            }
        }
        if (tiedIndices.size() == SIZE_ONE) {
            return tiedIndices.getFirst();
        }
        List<Integer> ones = new ArrayList<>();
        for (int i = FIRST_INDEX; i < tiedIndices.size(); i++) {
            ones.add(WEIGHT_TIE);
        }
        int sel = weightedSelect(ones, rnd);
        return tiedIndices.get(sel);
    }

    /**
     * Returns 1 if the cell (row, col) contains a non-king unit of the given team.
     *
     * @param game game state
     * @param row  row
     * @param col  column
     * @param team team to count
     * @return 0 or 1
     */
    private static int countAdjacentOneCell(Game game, int row, int col, Team team) {
        if (row < MIN_BOARD_INDEX || row >= GameBoard.SIZE || col < MIN_BOARD_INDEX || col >= GameBoard.SIZE) {
            return SCORE_ZERO;
        }
        Unit u = game.getGameBoard().getField(row, col).getUnit();
        if (u == null || !u.getTeam().equals(team)) {
            return SCORE_ZERO;
        }
        if (!u.isKing()) {
            return COUNT_ONE;
        }
        return SCORE_ZERO;
    }

    /**
     * Counts adjacent units in 8 directions (king is not counted).
     *
     * @param game game state
     * @param row  row
     * @param col  column
     * @param team team to count
     * @return count
     */
    static int countAdjacent(Game game, int row, int col, Team team) {
        int count = INITIAL_COUNT;
        for (int[] offset : EIGHT_NEIGHBOR_OFFSETS) {
            count += countAdjacentOneCell(game, row + offset[INDEX_ROW], col + offset[INDEX_COL], team);
        }
        return count;
    }

    private static final int[][] CARDINAL_OFFSETS = {{DIR_OFFSET_UP, MIN_BOARD_INDEX}, {DIR_OFFSET_DOWN, MIN_BOARD_INDEX}, {MIN_BOARD_INDEX, DIR_OFFSET_RIGHT}, {MIN_BOARD_INDEX, DIR_OFFSET_LEFT}};

    static int countAdjacent4(Game game, int row, int col, Team team) {
        int count = INITIAL_COUNT;
        for (int[] dx : CARDINAL_OFFSETS) {
            int r = row + dx[INDEX_ROW];
            int c = col + dx[INDEX_COL];
            if (r >= MIN_BOARD_INDEX && r < GameBoard.SIZE && c >= MIN_BOARD_INDEX && c < GameBoard.SIZE) {
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
        int max = INITIAL_MAX_ATK;
        for (int[] dx : CARDINAL_OFFSETS) {
            int r = row + dx[INDEX_ROW];
            int c = col + dx[INDEX_COL];
            while (r >= MIN_BOARD_INDEX && r < GameBoard.SIZE && c >= MIN_BOARD_INDEX && c < GameBoard.SIZE) {
                Unit u = game.getGameBoard().getField(r, c).getUnit();
                if (u == null) {
                    r += dx[INDEX_ROW];
                    c += dx[INDEX_COL];
                    continue;
                }
                if (u.getTeam().equals(enemy) && !u.isKing() && u.isRevealed()) {
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

    private static int scoreEmptyField(Game game, int tr, int tc, int ekr, int ekc, Team enemy) {
        int steps = Math.abs(tr - ekr) + Math.abs(tc - ekc);
        int enemies = countAdjacent4(game, tr, tc, enemy);
        return ADVANCE_SCORE_STEPS_MULTIPLIER * steps - enemies;
    }

    private static int scoreFriendlyField(Unit u, Unit target, Compatibility.MergeStats stats) {
        if (stats != null && !target.isKing()) {
            return stats.atk() + stats.def() - u.getAtk() - u.getDef();
        }
        return SCORE_ZERO - target.getAtk() - target.getDef();
    }

    private static int scoreEnemyField(Unit u, Unit target) {
        if (target.isKing()) {
            return u.getAtk();
        }
        if (!target.isRevealed()) {
            return u.getAtk() - PENALTY_ATTACKING_HIDDEN_UNIT;
        }
        if (target.isBlocked()) {
            return u.getAtk() - target.getDef();
        }
        return ATTACK_DIFFERENCE_MULTIPLIER * (u.getAtk() - target.getAtk());
    }

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
        int[][] dirs = {{ur + DIR_OFFSET_UP, uc}, {ur, uc + DIR_OFFSET_RIGHT}, {ur + DIR_OFFSET_DOWN, uc}, {ur, uc + DIR_OFFSET_LEFT}};
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
            int score;
            if (target == null) {
                score = scoreEmptyField(game, tr, tc, ekr, ekc, enemy);
            } else if (target.getTeam().equals(ai)) {
                score = scoreFriendlyField(u, target, Compatibility.check(u, target));
            } else {
                score = scoreEnemyField(u, target);
            }
            options.add(d);
            scores.add(score);
        }
        int blockScore = Math.max(MIN_BLOCK_SCORE, (u.getDef() - maxEnemyAtkInLine(game, ur, uc, enemy))
                / AI_SCORE_STATUS_DIVISOR);
        options.add(new int[] { ur, uc });
        scores.add(blockScore);
        int enPlaceScore = Math.max(MIN_EN_PLACE_SCORE, (u.getAtk() - maxEnemyAtkInLine(game, ur, uc, enemy))
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
            System.out.printf((NO_LONGER_BLOCKS_MESSAGE_FORMAT) + FORMAT_NEWLINE, ctx.unit().getName());
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
            System.out.printf((MOVES_TO_MESSAGE_FORMAT) + FORMAT_NEWLINE, ctx.unit().getName(), ctx.toField().coordinate());
            System.out.printf((JOIN_FORCES_MESSAGE_FORMAT) + FORMAT_NEWLINE, ctx.unit().getName(),
                    ctx.defender().getName(), ctx.toField().coordinate());
            System.out.println(SUCCESS_MESSAGE);
        } else {
            ctx.game().getGameBoard().getField(ctx.fromRow(), ctx.fromCol()).removeUnit();
            ctx.toField().removeUnit();
            ctx.game().getGameBoard().placeUnit(ctx.toRow(), ctx.toCol(), ctx.unit());
            ctx.unit().setMovedThisTurn(true);
            ctx.game().setSelectedField(ctx.toField());
            System.out.printf((MOVES_TO_MESSAGE_FORMAT) + FORMAT_NEWLINE, ctx.unit().getName(), ctx.toField().coordinate());
            System.out.printf((JOIN_FORCES_MESSAGE_FORMAT) + FORMAT_NEWLINE, ctx.unit().getName(),
                    ctx.defender().getName(), ctx.toField().coordinate());
            System.out.printf((UNION_FAILED_MESSAGE_FORMAT) + FORMAT_NEWLINE, ctx.defender().getName());
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
        System.out.printf((PLACES_ON_MESSAGE_FORMAT) + FORMAT_NEWLINE, game.getCurrentTeam().getName(), unit.getName(), field.coordinate());
        if (current == null) {
            game.getGameBoard().placeUnit(row, col, unit);
        } else {
            System.out.printf((JOIN_FORCES_MESSAGE_FORMAT) + FORMAT_NEWLINE, unit.getName(), current.getName(), field.coordinate());
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
                System.out.printf((UNION_FAILED_MESSAGE_FORMAT) + FORMAT_NEWLINE, current.getName());
            }
        }
        if (game.getBoardCount(game.getCurrentTeam()) > Game.MAX_NON_KING_UNITS_ON_BOARD) {
            Unit justPlaced = game.getGameBoard().getField(row, col).getUnit();
            game.getGameBoard().getField(row, col).removeUnit();
            System.out.printf((JUST_PLACED_ELIMINATED_MESSAGE_FORMAT) + FORMAT_NEWLINE, justPlaced.getName());
        }
    }
}
