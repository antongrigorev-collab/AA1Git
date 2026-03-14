package edu.kit.kastel.model;

import edu.kit.kastel.commands.ShowCommand;
import edu.kit.kastel.exceptions.CannotDiscardException;
import edu.kit.kastel.exceptions.HandFullMustDiscardException;
import edu.kit.kastel.exceptions.InitializationException;
import edu.kit.kastel.exceptions.InvalidHandIndexException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AI opponent for Team 2 (A.2). Runs one full turn: king move, place, unit moves, then yield.
 *
 * @author usylb
 */
public final class AIPlayer {

    private static final int KING_SCORE_ENEMY_WEIGHT = 2;
    private static final int KING_SCORE_FELLOW_PRESENT_WEIGHT = 3;
    private static final int PLACE_SCORE_ENEMY_WEIGHT = 2;
    private static final int[][] CLOCKWISE_DIRS = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
    private static final int BLOCK_OPTION_INDEX = 4;
    private static final int EN_PLACE_OPTION_INDEX = 5;
    private static final int MAX_HAND_SIZE_BEFORE_DISCARD = 5;
    private static final String BLOCKS_MESSAGE_FORMAT = "%s (%s) blocks!";
    private static final String NO_LONGER_BLOCKS_MESSAGE_FORMAT = "%s no longer blocks.";
    private static final String MOVES_TO_MESSAGE_FORMAT = "%s moves to %s.";
    private static final String DISCARDED_UNIT_MESSAGE_FORMAT = "%s discarded %s (%d/%d).";
    private static final String TURN_ANNOUNCEMENT_MESSAGE_FORMAT = "It is %s's turn!";
    private static final String NO_CARDS_LEFT_MESSAGE_FORMAT = "%s has no cards left in the deck!";
    private static final String WINS_MESSAGE_FORMAT = "%s wins!";
    private record PlacePhaseContext(Game game, Team ai, Team enemy, int kr, int kc, int ekr, int ekc, Random rnd) { }
    private record UnitMoveRoundData(List<Unit> movable, List<int[]> positions,
                                     List<Integer> unitTotalScores, List<List<int[]>> unitOptions,
                                     List<List<Integer>> unitOptionScores) { }
    private AIPlayer() { }

    /** Returns true if no move option (cardinal, block, en place) has positive score. */
    private static boolean hasNoPositiveMove(List<Integer> optScores) {
        return optScores.get(0) <= 0 && optScores.get(1) <= 0 && optScores.get(2) <= 0
                && optScores.get(3) <= 0 && optScores.get(EN_PLACE_OPTION_INDEX) <= 0;
    }

    /**
     * Runs one full turn for the current team (AI / team 2). Prints output and ends the turn.
     * @param game the game instance (current team must be team 2)
     */
    public static void runTurn(Game game) {
        if (game.isGameOver()) {
            return;
        }
        Team ai = game.getCurrentTeam();
        Team enemy = ai == game.getTeam1() ? game.getTeam2() : game.getTeam1();
        Random rnd = game.getRandom();
        int[] kingPos = game.getKingPosition(ai);
        if (kingPos == null) {
            return;
        }
        int kr = kingPos[0];
        int kc = kingPos[1];

        if (runKingMove(game, ai, enemy, kr, kc, rnd)) {
            if (!game.isGameOver()) {
                yieldTurn(game);
            }
            return;
        }
        kingPos = game.getKingPosition(ai);
        if (kingPos == null || game.isGameOver()) {
            yieldTurn(game);
            return;
        }
        kr = kingPos[0];
        kc = kingPos[1];
        int[] enemyKingPos = game.getKingPosition(enemy);
        int ekr = enemyKingPos != null ? enemyKingPos[0] : 0;
        int ekc = enemyKingPos != null ? enemyKingPos[1] : 0;
        runPlacePhase(new PlacePhaseContext(game, ai, enemy, kr, kc, ekr, ekc, rnd));
        runUnitMovesLoop(game, ai, enemy, ekr, ekc, rnd);
        if (!game.isGameOver()) {
            yieldTurn(game);
        }
    }

    private static boolean isValidKingMoveTarget(Game game, int row, int col, Team enemy) {
        if (row < 0 || row >= GameBoard.SIZE || col < 0 || col >= GameBoard.SIZE) {
            return false;
        }
        Unit onTo = game.getGameBoard().getField(row, col).getUnit();
        return onTo == null || !onTo.getTeam().equals(enemy);
    }

    private static int computeKingMoveScore(Game game, int kr, int kc, int tr, int tc, Team ai, Team enemy) {
        int distance = (tr == kr && tc == kc) ? 0 : 1;
        int fellows = AIPlayerHelper.countAdjacent(game, tr, tc, ai);
        int enemies = AIPlayerHelper.countAdjacent(game, tr, tc, enemy);
        Unit onTo = game.getGameBoard().getField(tr, tc).getUnit();
        int fellowPresent = (onTo != null && onTo.getTeam().equals(ai) && !onTo.isKing()) ? 1 : 0;
        return fellows - KING_SCORE_ENEMY_WEIGHT * enemies - distance
                - KING_SCORE_FELLOW_PRESENT_WEIGHT * fellowPresent;
    }

    /** Returns true if turn should abort (game over or king gone after move). */
    private static boolean runKingMove(Game game, Team ai, Team enemy, int kr, int kc, Random rnd) {
        List<int[]> kingMoves = new ArrayList<>();
        kingMoves.add(new int[] { kr + 1, kc });
        kingMoves.add(new int[] { kr, kc + 1 });
        kingMoves.add(new int[] { kr - 1, kc });
        kingMoves.add(new int[] { kr, kc - 1 });
        kingMoves.add(new int[] { kr, kc });
        List<int[]> validKingMoves = new ArrayList<>();
        List<Integer> kingScores = new ArrayList<>();
        for (int[] to : kingMoves) {
            int tr = to[0];
            int tc = to[1];
            if (!isValidKingMoveTarget(game, tr, tc, enemy)) {
                continue;
            }
            validKingMoves.add(to);
            kingScores.add(computeKingMoveScore(game, kr, kc, tr, tc, ai, enemy));
        }
        if (validKingMoves.isEmpty()) {
            return false;
        }
        int idx = AIPlayerHelper.selectAmongMaxScore(kingScores, rnd);
        int[] to = validKingMoves.get(idx);
        executeMove(game, kr, kc, to[0], to[1]);
        if (!game.isGameOver()) {
            ShowCommand.printBoardAndShow(game);
        }
        return game.getKingPosition(ai) == null || game.isGameOver();
    }
    private static void runPlacePhase(PlacePhaseContext ctx) {
        List<int[]> placeFieldsOrdered = new ArrayList<>();
        List<Integer> placeScores = new ArrayList<>();
        for (int[] d : CLOCKWISE_DIRS) {
            int r = ctx.kr() + d[0];
            int c = ctx.kc() + d[1];
            if (r < 0 || r >= GameBoard.SIZE || c < 0 || c >= GameBoard.SIZE) {
                continue;
            }
            Field field = ctx.game().getGameBoard().getField(r, c);
            Unit occupant = field.getUnit();
            if (occupant != null && !occupant.getTeam().equals(ctx.ai())) {
                continue;
            }
            placeFieldsOrdered.add(new int[] { r, c });
            int steps = Math.abs(r - ctx.ekr()) + Math.abs(c - ctx.ekc());
            int enemies = AIPlayerHelper.countAdjacent4(ctx.game(), r, c, ctx.enemy());
            int fellows = AIPlayerHelper.countAdjacent4(ctx.game(), r, c, ctx.ai());
            placeScores.add(-steps + PLACE_SCORE_ENEMY_WEIGHT * enemies - fellows);
        }
        if (placeFieldsOrdered.isEmpty() || ctx.game().getCurrentTeam().getHand().size() <= 0) {
            return;
        }
        int fieldIdx = AIPlayerHelper.selectAmongMaxScore(placeScores, ctx.rnd());
        int[] pf = placeFieldsOrdered.get(fieldIdx);
        int pr = pf[0];
        int pc = pf[1];
        List<Unit> handUnits = ctx.game().getCurrentTeam().getHand().snapshot();
        List<Integer> atkWeights = new ArrayList<>();
        for (Unit u : handUnits) {
            atkWeights.add(Math.max(0, u.getAtk()));
        }
        int unitIdx = AIPlayerHelper.weightedSelect(atkWeights, ctx.rnd());
        int handIndex = unitIdx + 1;
        ctx.game().setSelectedField(ctx.game().getGameBoard().getField(pr, pc));
        AIPlayerHelper.placeUnit(ctx.game(), handIndex, pr, pc);
        ShowCommand.printBoardAndShow(ctx.game());
    }
    private static void runUnitMovesLoop(Game game, Team ai, Team enemy, int ekr, int ekc, Random rnd) {
        while (true) {
            if (!runOneUnitMoveRound(game, ai, enemy, ekr, ekc, rnd) || game.isGameOver()) {
                break;
            }
        }
    }

    /** Runs one round of unit moves. @return true if at least one unit moved or blocked */
    private static boolean runOneUnitMoveRound(Game game, Team ai, Team enemy, int ekr, int ekc, Random rnd) {
        UnitMoveRoundData data = computeUnitMoveRoundData(game, ai, enemy, ekr, ekc);
        if (data.movable().isEmpty()) {
            return false;
        }
        int chosenUnit = AIPlayerHelper.selectAmongMaxScore(data.unitTotalScores(), rnd);
        int ur = data.positions().get(chosenUnit)[0];
        int uc = data.positions().get(chosenUnit)[1];
        game.setSelectedField(game.getGameBoard().getField(ur, uc));

        List<Integer> optScores = data.unitOptionScores().get(chosenUnit);
        boolean noPositiveMove = hasNoPositiveMove(optScores);
        if (noPositiveMove) {
            Unit u = data.movable().get(chosenUnit);
            u.setBlocked(true);
            u.setMovedThisTurn(true);
            System.out.printf((BLOCKS_MESSAGE_FORMAT) + "%n", u.getName(),
                    game.getGameBoard().getField(ur, uc).coordinate());
            ShowCommand.printBoardAndShow(game);
            return true;
        }
        int moveIdx = AIPlayerHelper.weightedSelect(optScores, rnd);
        Unit u = data.movable().get(chosenUnit);
        List<int[]> opts = data.unitOptions().get(chosenUnit);
        int tr = opts.get(moveIdx)[0];
        int tc = opts.get(moveIdx)[1];
        if (tr == AIPlayerHelper.EN_PLACE_SENTINEL_ROW && tc == AIPlayerHelper.EN_PLACE_SENTINEL_COL) {
            System.out.printf((MOVES_TO_MESSAGE_FORMAT) + "%n", u.getName(),
                    game.getGameBoard().getField(ur, uc).coordinate());
            u.setMovedThisTurn(true);
            ShowCommand.printBoardAndShow(game);
            return true;
        }
        if (tr == ur && tc == uc && moveIdx == BLOCK_OPTION_INDEX) {
            u.setBlocked(true);
            u.setMovedThisTurn(true);
            System.out.printf((BLOCKS_MESSAGE_FORMAT) + "%n", u.getName(),
                    game.getGameBoard().getField(ur, uc).coordinate());
        } else {
            executeMove(game, ur, uc, tr, tc);
        }
        if (!game.isGameOver()) {
            ShowCommand.printBoardAndShow(game);
        }
        return true;
    }
    private static UnitMoveRoundData computeUnitMoveRoundData(Game game, Team ai, Team enemy, int ekr, int ekc) {
        List<Unit> movable = new ArrayList<>();
        List<int[]> positions = new ArrayList<>();
        for (int r = 0; r < GameBoard.SIZE; r++) {
            for (int c = 0; c < GameBoard.SIZE; c++) {
                Unit u = game.getGameBoard().getField(r, c).getUnit();
                if (u != null && u.getTeam().equals(ai) && !u.isKing() && !u.hasMovedThisTurn()) {
                    movable.add(u);
                    positions.add(new int[] { r, c });
                }
            }
        }
        List<Integer> unitTotalScores = new ArrayList<>();
        List<List<int[]>> unitOptions = new ArrayList<>();
        List<List<Integer>> unitOptionScores = new ArrayList<>();
        for (int i = 0; i < movable.size(); i++) {
            Unit u = movable.get(i);
            int ur = positions.get(i)[0];
            int uc = positions.get(i)[1];
            AIPlayerHelper.UnitMoveContext moveCtx = new AIPlayerHelper.UnitMoveContext(game, u, ur, uc,
                    ai, enemy, ekr, ekc);
            AIPlayerHelper.UnitMoveOption opt = AIPlayerHelper.computeUnitMoveOptions(moveCtx);
            int total = 0;
            for (Integer score : opt.scores()) {
                total += score;
            }
            unitTotalScores.add(total);
            unitOptions.add(opt.options());
            unitOptionScores.add(opt.scores());
        }
        return new UnitMoveRoundData(movable, positions, unitTotalScores, unitOptions, unitOptionScores);
    }
    /** Returns null if hand not full; otherwise 1-based index for inverse-weighted discard (A.2). */
    private static Integer computeDiscardHandIndex(Game game) {
        if (game.getCurrentTeam().getHand().size() != MAX_HAND_SIZE_BEFORE_DISCARD) {
            return null;
        }
        List<Unit> hand = game.getCurrentTeam().getHand().snapshot();
        List<Integer> weights = new ArrayList<>();
        for (Unit u : hand) {
            weights.add(u.getAtk() + u.getDef());
        }
        int max = 0;
        for (Integer w : weights) {
            if (w > max) {
                max = w;
            }
        }
        List<Integer> inv = new ArrayList<>();
        for (int w : weights) {
            inv.add(Math.max(0, max - w));
        }
        return AIPlayerHelper.weightedSelect(inv, game.getRandom()) + 1;
    }

    private static void printYieldResult(Game game, YieldResult result) {
        if (result.discarded() != null) {
            Unit u = result.discarded();
            System.out.printf((DISCARDED_UNIT_MESSAGE_FORMAT) + "%n",
                    result.yieldingTeam().getName(), u.getName(), u.getAtk(), u.getDef());
        }
        System.out.printf((TURN_ANNOUNCEMENT_MESSAGE_FORMAT) + "%n", game.getCurrentTeam().getName());
        if (result.newTeamDeckEmpty()) {
            System.out.printf((NO_CARDS_LEFT_MESSAGE_FORMAT) + "%n",
                    game.getCurrentTeam().getName());
        }
        if (result.winner() != null) {
            System.out.printf((WINS_MESSAGE_FORMAT) + "%n", result.winner().getName());
        }
    }

    private static void yieldTurn(Game game) {
        if (game.isGameOver()) {
            return;
        }
        Integer discardIdx = computeDiscardHandIndex(game);
        try {
            YieldResult result = game.endTurn(discardIdx);
            printYieldResult(game, result);
        } catch (HandFullMustDiscardException | CannotDiscardException | InvalidHandIndexException
                | InitializationException e) {
            System.out.println(e.getFormattedMessage());
        }
    }
    private static void executeEnPlaceMove(Game game, Unit unit, Field toField) {
        if (unit.isBlocked()) {
            unit.setBlocked(false);
            System.out.printf((NO_LONGER_BLOCKS_MESSAGE_FORMAT) + "%n", unit.getName());
        }
        unit.setMovedThisTurn(true);
        System.out.printf((MOVES_TO_MESSAGE_FORMAT) + "%n", unit.getName(), toField.coordinate());
    }

    private static void executeMoveToEmpty(Game game, Unit unit, int fromRow, int fromCol, int toRow, int toCol) {
        Field toField = game.getGameBoard().getField(toRow, toCol);
        if (unit.isBlocked()) {
            unit.setBlocked(false);
            System.out.printf((NO_LONGER_BLOCKS_MESSAGE_FORMAT) + "%n", unit.getName());
        }
        game.getGameBoard().getField(fromRow, fromCol).removeUnit();
        game.getGameBoard().placeUnit(toRow, toCol, unit);
        unit.setMovedThisTurn(true);
        game.setSelectedField(toField);
        System.out.printf((MOVES_TO_MESSAGE_FORMAT) + "%n", unit.getName(), toField.coordinate());
    }

    private static void executeDuelMove(Game game, Unit unit, Unit defender, int fromRow, int fromCol, int toRow,
                                       int toCol) {
        Field toField = game.getGameBoard().getField(toRow, toCol);
        DuelResult result = game.performDuel(unit, defender, defender.isBlocked(), fromRow, fromCol, toRow, toCol);
        for (String line : result.lines()) {
            System.out.println(line);
        }
        if (result.winner() != null) {
            if (toField.getUnit() == unit) {
                unit.setMovedThisTurn(true);
                game.setSelectedField(toField);
            } else if (game.getGameBoard().getField(fromRow, fromCol).getUnit() == unit) {
                unit.setMovedThisTurn(true);
                game.setSelectedField(game.getGameBoard().getField(fromRow, fromCol));
            }
            ShowCommand.printBoardAndShow(game);
            return;
        }
        if (toField.getUnit() == unit) {
            game.setSelectedField(toField);
            unit.setMovedThisTurn(true);
        }
    }

    private static void executeMove(Game game, int fromRow, int fromCol, int toRow, int toCol) {
        game.setSelectedField(game.getGameBoard().getField(fromRow, fromCol));
        Unit unit = game.getGameBoard().getField(fromRow, fromCol).getUnit();
        if (unit == null) {
            return;
        }
        Field toField = game.getGameBoard().getField(toRow, toCol);
        if (fromRow == toRow && fromCol == toCol) {
            executeEnPlaceMove(game, unit, toField);
            return;
        }
        if (toField.isEmpty()) {
            executeMoveToEmpty(game, unit, fromRow, fromCol, toRow, toCol);
            return;
        }
        Unit defender = toField.getUnit();
        if (!defender.getTeam().equals(game.getCurrentTeam())) {
            executeDuelMove(game, unit, defender, fromRow, fromCol, toRow, toCol);
            return;
        }
        AIPlayerHelper.executeMergeOrEliminate(new AIPlayerHelper.MergeActionContext(game, unit,
                defender, fromRow, fromCol, toRow, toCol, toField));
    }
}
