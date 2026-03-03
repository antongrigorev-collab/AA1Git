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

    /** King move score: weight for enemy-adjacent penalty (A.2). */
    private static final int KING_SCORE_ENEMY_WEIGHT = 2;

    /** King move score: weight for fellow-on-field penalty (A.2). */
    private static final int KING_SCORE_FELLOW_PRESENT_WEIGHT = 3;

    /** Place phase score: weight for adjacent enemies (A.2). */
    private static final int PLACE_SCORE_ENEMY_WEIGHT = 2;

    /** Index of block option in move options list (after 4 cardinal directions). */
    private static final int BLOCK_OPTION_INDEX = 4;

    /** Index of en-place option in move options list. */
    private static final int EN_PLACE_OPTION_INDEX = 5;

    /** Hand size at which discard is required before yield (A.1.5). */
    private static final int MAX_HAND_SIZE_BEFORE_DISCARD = 5;

    /** Context for the place phase (king position, enemy king, RNG). */
    private record PlacePhaseContext(Game game, Team ai, Team enemy, int kr, int kc, int ekr, int ekc, Random rnd) { }

    /** Context for merge/eliminate action (game, units, coordinates, target field). */
    private record MergeActionContext(Game game, Unit unit, Unit defender,
                                      int fromRow, int fromCol, int toRow, int toCol, Field toField) { }
    /** Collected data for one unit-move round: movable units, positions, and their option scores. */
    private record UnitMoveRoundData(List<Unit> movable, List<int[]> positions,
                                     List<Integer> unitTotalScores, List<List<int[]>> unitOptions,
                                     List<List<Integer>> unitOptionScores) { }
    private AIPlayer() { }

    /** Returns true if no move option (cardinal, block, en place) has positive score. */
    private static boolean hasNoPositiveMove(List<Integer> optScores) {
        return optScores.get(0) <= 0 && optScores.get(1) <= 0 && optScores.get(2) <= 0
                && optScores.get(3) <= 0 && optScores.get(EN_PLACE_OPTION_INDEX) <= 0;
    }

    /** Runs one full turn for the current team (AI / team 2). Prints output and ends the turn. @param game the game */
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
            yieldTurn(game);
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
        yieldTurn(game);
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
            if (tr < 0 || tr >= GameBoard.SIZE || tc < 0 || tc >= GameBoard.SIZE) {
                continue;
            }
            Unit onTo = game.getGameBoard().getField(tr, tc).getUnit();
            if (onTo != null && onTo.getTeam().equals(enemy)) {
                continue;
            }
            int distance = (tr == kr && tc == kc) ? 0 : 1;
            int fellows = AIPlayerHelper.countAdjacent(game, tr, tc, ai, false);
            int enemies = AIPlayerHelper.countAdjacent(game, tr, tc, enemy, false);
            int fellowPresent = (onTo != null && onTo.getTeam().equals(ai) && !onTo.isKing()) ? 1 : 0;
            int score = fellows - KING_SCORE_ENEMY_WEIGHT * enemies - distance - KING_SCORE_FELLOW_PRESENT_WEIGHT * fellowPresent;
            validKingMoves.add(to);
            kingScores.add(score);
        }
        if (validKingMoves.isEmpty()) {
            return false;
        }
        int idx = AIPlayerHelper.selectAmongMaxScore(kingScores, rnd);
        int[] to = validKingMoves.get(idx);
        executeMove(game, kr, kc, to[0], to[1]);
        ShowCommand.printBoardAndShow(game);
        return game.getKingPosition(ai) == null || game.isGameOver();
    }
    private static void runPlacePhase(PlacePhaseContext ctx) {
        int[][] clockwiseDirs = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        List<int[]> placeFieldsOrdered = new ArrayList<>();
        List<Integer> placeScores = new ArrayList<>();
        for (int[] d : clockwiseDirs) {
            int r = ctx.kr() + d[0];
            int c = ctx.kc() + d[1];
            if (r < 0 || r >= GameBoard.SIZE || c < 0 || c >= GameBoard.SIZE) {
                continue;
            }
            if (!ctx.game().getGameBoard().getField(r, c).isEmpty()) {
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
        placeUnit(ctx.game(), handIndex, pr, pc);
        ShowCommand.printBoardAndShow(ctx.game());
    }
    private static void runUnitMovesLoop(Game game, Team ai, Team enemy, int ekr, int ekc, Random rnd) {
        while (runOneUnitMoveRound(game, ai, enemy, ekr, ekc, rnd) && !game.isGameOver()) {
            // continue until no unit can move
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
            System.out.println(u.getName() + " (" + game.getGameBoard().getField(ur, uc).coordinate() + ") blocks!");
            ShowCommand.printBoardAndShow(game);
            return true;
        }
        int moveIdx = AIPlayerHelper.weightedSelect(optScores, rnd);
        Unit u = data.movable().get(chosenUnit);
        List<int[]> opts = data.unitOptions().get(chosenUnit);
        int tr = opts.get(moveIdx)[0];
        int tc = opts.get(moveIdx)[1];
        if (tr == -1 && tc == -1) {
            System.out.println(u.getName() + " moves to " + game.getGameBoard().getField(ur, uc).coordinate() + ".");
            u.setMovedThisTurn(true);
            ShowCommand.printBoardAndShow(game);
            return true;
        }
        if (tr == ur && tc == uc && moveIdx == BLOCK_OPTION_INDEX) {
            u.setBlocked(true);
            u.setMovedThisTurn(true);
            System.out.println(u.getName() + " (" + game.getGameBoard().getField(ur, uc).coordinate() + ") blocks!");
        } else {
            executeMove(game, ur, uc, tr, tc);
        }
        ShowCommand.printBoardAndShow(game);
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
    private static void yieldTurn(Game game) {
        int handSize = game.getCurrentTeam().getHand().size();
        Integer discardIdx = null;
        if (handSize == MAX_HAND_SIZE_BEFORE_DISCARD) {
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
            discardIdx = AIPlayerHelper.weightedSelect(inv, game.getRandom()) + 1;
        }
        try {
            Game.YieldResult result = game.endTurn(discardIdx);
            if (result.discarded() != null) {
                Unit u = result.discarded();
                System.out.println(result.yieldingTeam().getName() + " discarded "
                        + u.getName() + " (" + u.getAtk() + "/" + u.getDef() + ").");
            }
            System.out.println("It is " + game.getCurrentTeam().getName() + "'s turn!");
            if (result.newTeamDeckEmpty()) {
                System.out.println(game.getCurrentTeam().getName() + " has no cards left in the deck!");
            }
            if (result.winner() != null) {
                System.out.println(result.winner().getName() + " wins!");
            }
        } catch (HandFullMustDiscardException | CannotDiscardException | InvalidHandIndexException
                | InitializationException e) {
            System.out.println(e.getFormattedMessage());
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
            if (unit.isBlocked()) {
                unit.setBlocked(false);
                System.out.println(unit.getName() + " no longer blocks.");
            }
            unit.setMovedThisTurn(true);
            System.out.println(unit.getName() + " moves to " + toField.coordinate() + ".");
            return;
        }
        if (toField.isEmpty()) {
            if (unit.isBlocked()) {
                unit.setBlocked(false);
                System.out.println(unit.getName() + " no longer blocks.");
            }
            game.getGameBoard().getField(fromRow, fromCol).removeUnit();
            game.getGameBoard().placeUnit(toRow, toCol, unit);
            unit.setMovedThisTurn(true);
            game.setSelectedField(toField);
            System.out.println(unit.getName() + " moves to " + toField.coordinate() + ".");
            return;
        }
        Unit defender = toField.getUnit();
        if (!defender.getTeam().equals(game.getCurrentTeam())) {
            DuelResult result = game.performDuel(unit, defender, defender.isBlocked(), fromRow, fromCol, toRow, toCol);
            for (String line : result.lines()) {
                System.out.println(line);
            }
            if (toField.getUnit() == unit) {
                game.setSelectedField(toField);
                unit.setMovedThisTurn(true);
            }
            return;
        }
        executeMergeOrEliminate(new MergeActionContext(game, unit, defender, fromRow, fromCol, toRow, toCol, toField));
    }
    private static void executeMergeOrEliminate(MergeActionContext ctx) {
        if (ctx.unit().isBlocked()) {
            ctx.unit().setBlocked(false);
            System.out.println(ctx.unit().getName() + " no longer blocks.");
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
            System.out.println(ctx.unit().getName() + " moves to " + ctx.toField().coordinate() + ".");
            System.out.println(ctx.unit().getName() + " and " + ctx.defender().getName() + " on "
                    + ctx.toField().coordinate() + " join forces!");
            System.out.println("Success!");
        } else {
            ctx.game().getGameBoard().getField(ctx.fromRow(), ctx.fromCol()).removeUnit();
            ctx.toField().removeUnit();
            ctx.game().getGameBoard().placeUnit(ctx.toRow(), ctx.toCol(), ctx.unit());
            ctx.unit().setMovedThisTurn(true);
            ctx.game().setSelectedField(ctx.toField());
            System.out.println(ctx.unit().getName() + " moves to " + ctx.toField().coordinate() + ".");
            System.out.println(ctx.unit().getName() + " and " + ctx.defender().getName() + " on "
                    + ctx.toField().coordinate() + " join forces!");
            System.out.println("Union failed. " + ctx.defender().getName() + " was eliminated.");
        }
    }
    private static void placeUnit(Game game, int handIndex, int row, int col) {
        Unit unit = game.getCurrentTeam().getHand().get(handIndex);
        if (unit == null) {
            return;
        }
        game.getCurrentTeam().getHand().remove(handIndex);
        game.getCurrentTeam().getHand().markPlacedThisTurn();
        unit.setTeam(game.getCurrentTeam());
        Field field = game.getGameBoard().getField(row, col);
        Unit current = field.getUnit();
        System.out.println(game.getCurrentTeam().getName() + " places " + unit.getName() + " on "
                + field.coordinate() + ".");
        if (current == null) {
            game.getGameBoard().placeUnit(row, col, unit);
        } else {
            System.out.println(unit.getName() + " and " + current.getName() + " on " + field.coordinate()
                    + " join forces!");
            Compatibility.MergeStats stats = Compatibility.check(unit, current);
            if (stats != null) {
                Unit merged = Game.createMergedUnit(unit, current, stats);
                merged.setTeam(game.getCurrentTeam());
                merged.setMovedThisTurn(false);
                field.removeUnit();
                game.getGameBoard().placeUnit(row, col, merged);
                System.out.println("Success!");
            } else {
                field.removeUnit();
                game.getGameBoard().placeUnit(row, col, unit);
                System.out.println("Union failed. " + current.getName() + " was eliminated.");
            }
        }
        if (game.getBoardCount(game.getCurrentTeam()) > Game.MAX_NON_KING_UNITS_ON_BOARD) {
            Unit justPlaced = game.getGameBoard().getField(row, col).getUnit();
            game.getGameBoard().getField(row, col).removeUnit();
            System.out.println(justPlaced.getName() + " was eliminated!");
        }
    }
}
