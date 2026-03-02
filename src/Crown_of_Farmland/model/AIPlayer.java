package Crown_of_Farmland.model;

import Crown_of_Farmland.commands.ShowCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AI opponent for Team 2 (A.2). Runs one full turn: king move, place, unit moves, then yield.
 */
public final class AIPlayer {

    /** King move score: weight for enemy-adjacent penalty (A.2). */
    private static final int KING_SCORE_ENEMY_WEIGHT = 2;

    /** King move score: weight for fellow-on-field penalty (A.2). */
    private static final int KING_SCORE_FELLOW_PRESENT_WEIGHT = 3;

    /** Place phase score: weight for adjacent enemies (A.2). */
    private static final int PLACE_SCORE_ENEMY_WEIGHT = 2;

    /** Unit move advance score: multiplier for steps toward enemy king (A.2). */
    private static final int ADVANCE_SCORE_STEPS_MULTIPLIER = 10;

    /** Penalty for attacking a hidden (unrevealed) unit (A.2). */
    private static final int PENALTY_ATTACKING_HIDDEN_UNIT = 500;

    /** Standard duel score: multiplier for attack difference (A.2). */
    private static final int ATTACK_DIFFERENCE_MULTIPLIER = 2;

    /** Divisor for block/en-place score scaling from status values (A.2). */
    private static final int AI_SCORE_STATUS_DIVISOR = 100;

    /** Index of block option in move options list (after 4 cardinal directions). */
    private static final int BLOCK_OPTION_INDEX = 4;

    /** Index of en-place option in move options list. */
    private static final int EN_PLACE_OPTION_INDEX = 5;

    /** Hand size at which discard is required before yield (A.1.5). */
    private static final int MAX_HAND_SIZE_BEFORE_DISCARD = 5;

    private AIPlayer() { }

    /**
     * Runs one full turn for the current team (must be the AI / team 2).
     * Prints all output and ends the turn (yield with optional discard).
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
        runPlacePhase(game, ai, enemy, kr, kc, ekr, ekc, rnd);
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
            int tr = to[0], tc = to[1];
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
        printBoardAndShow(game);
        return game.getKingPosition(ai) == null || game.isGameOver();
    }

    private static void runPlacePhase(Game game, Team ai, Team enemy, int kr, int kc, int ekr, int ekc, Random rnd) {
        int[][] clockwiseDirs = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        List<int[]> placeFieldsOrdered = new ArrayList<>();
        List<Integer> placeScores = new ArrayList<>();
        for (int[] d : clockwiseDirs) {
            int r = kr + d[0], c = kc + d[1];
            if (r < 0 || r >= GameBoard.SIZE || c < 0 || c >= GameBoard.SIZE) {
                continue;
            }
            if (!game.getGameBoard().getField(r, c).isEmpty()) {
                continue;
            }
            placeFieldsOrdered.add(new int[] { r, c });
            int steps = Math.abs(r - ekr) + Math.abs(c - ekc);
            int enemies = AIPlayerHelper.countAdjacent4(game, r, c, enemy);
            int fellows = AIPlayerHelper.countAdjacent4(game, r, c, ai);
            placeScores.add(-steps + PLACE_SCORE_ENEMY_WEIGHT * enemies - fellows);
        }
        if (placeFieldsOrdered.isEmpty() || game.getCurrentTeam().getHand().size() <= 0) {
            return;
        }
        int fieldIdx = AIPlayerHelper.selectAmongMaxScore(placeScores, rnd);
        int[] pf = placeFieldsOrdered.get(fieldIdx);
        int pr = pf[0], pc = pf[1];
        List<Unit> handUnits = game.getCurrentTeam().getHand().snapshot();
        List<Integer> atkWeights = new ArrayList<>();
        for (Unit u : handUnits) {
            atkWeights.add(Math.max(0, u.getAtk()));
        }
        int unitIdx = AIPlayerHelper.weightedSelect(atkWeights, rnd);
        int handIndex = unitIdx + 1;
        game.setSelectedField(game.getGameBoard().getField(pr, pc));
        placeUnit(game, handIndex, pr, pc);
        printBoardAndShow(game);
    }

    private static void runUnitMovesLoop(Game game, Team ai, Team enemy, int ekr, int ekc, Random rnd) {
        boolean anyMoved = true;
        while (anyMoved && !game.isGameOver()) {
            anyMoved = false;
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
            if (movable.isEmpty()) {
                break;
            }
            List<Integer> unitTotalScores = new ArrayList<>();
            List<List<int[]>> unitOptions = new ArrayList<>();
            List<List<Integer>> unitOptionScores = new ArrayList<>();
            for (int i = 0; i < movable.size(); i++) {
                Unit u = movable.get(i);
                int ur = positions.get(i)[0], uc = positions.get(i)[1];
                UnitMoveOption opt = computeUnitMoveOptions(game, u, ur, uc, ai, enemy, ekr, ekc);
                int total = opt.scores().stream().mapToInt(Integer::intValue).sum();
                unitTotalScores.add(total);
                unitOptions.add(opt.options());
                unitOptionScores.add(opt.scores());
            }
            int chosenUnit = AIPlayerHelper.selectAmongMaxScore(unitTotalScores, rnd);
            int ur = positions.get(chosenUnit)[0], uc = positions.get(chosenUnit)[1];
            game.setSelectedField(game.getGameBoard().getField(ur, uc));

            List<Integer> optScores = unitOptionScores.get(chosenUnit);
            boolean noPositiveMove = optScores.get(0) <= 0 && optScores.get(1) <= 0 && optScores.get(2) <= 0 && optScores.get(3) <= 0 && optScores.get(EN_PLACE_OPTION_INDEX) <= 0;
            if (noPositiveMove) {
                Unit u = movable.get(chosenUnit);
                u.setBlocked(true);
                u.setMovedThisTurn(true);
                System.out.println(u.getName() + " (" + game.getGameBoard().getField(ur, uc).coordinate() + ") blocks!");
                anyMoved = true;
                printBoardAndShow(game);
                continue;
            }
            int moveIdx = AIPlayerHelper.weightedSelect(optScores, rnd);
            Unit u = movable.get(chosenUnit);
            List<int[]> opts = unitOptions.get(chosenUnit);
            int tr = opts.get(moveIdx)[0], tc = opts.get(moveIdx)[1];
            if (tr == -1 && tc == -1) {
                System.out.println(u.getName() + " moves to " + game.getGameBoard().getField(ur, uc).coordinate() + ".");
                u.setMovedThisTurn(true);
                anyMoved = true;
                printBoardAndShow(game);
                continue;
            }
            if (tr == ur && tc == uc && moveIdx == BLOCK_OPTION_INDEX) {
                u.setBlocked(true);
                u.setMovedThisTurn(true);
                System.out.println(u.getName() + " (" + game.getGameBoard().getField(ur, uc).coordinate() + ") blocks!");
            } else {
                executeMove(game, ur, uc, tr, tc);
            }
            anyMoved = true;
            printBoardAndShow(game);
        }
    }

    private record UnitMoveOption(List<int[]> options, List<Integer> scores) { }

    private static UnitMoveOption computeUnitMoveOptions(Game game, Unit u, int ur, int uc, Team ai, Team enemy, int ekr, int ekc) {
        List<int[]> options = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();
        int[][] dirs = {{ur + 1, uc}, {ur, uc + 1}, {ur - 1, uc}, {ur, uc - 1}};
        for (int[] d : dirs) {
            int tr = d[0], tc = d[1];
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
                int enemies = AIPlayerHelper.countAdjacent4(game, tr, tc, enemy);
                score = 10 * steps - enemies;
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
                    score = 2 * (u.getAtk() - target.getAtk());
                }
            }
            options.add(d);
            scores.add(score);
        }
        int blockScore = (int) Math.max(1, (u.getDef() - AIPlayerHelper.maxEnemyAtkInLine(game, ur, uc, enemy)) / AI_SCORE_STATUS_DIVISOR);
        options.add(new int[] { ur, uc });
        scores.add(blockScore);
        int enPlaceScore = (int) Math.max(0, (u.getAtk() - AIPlayerHelper.maxEnemyAtkInLine(game, ur, uc, enemy)) / AI_SCORE_STATUS_DIVISOR);
        options.add(new int[] { -1, -1 });
        scores.add(enPlaceScore);
        return new UnitMoveOption(options, scores);
    }

    private static void yieldTurn(Game game) {
        int handSize = game.getCurrentTeam().getHand().size();
        Integer discardIdx = null;
        if (handSize == 5) {
            List<Unit> hand = game.getCurrentTeam().getHand().snapshot();
            List<Integer> weights = new ArrayList<>();
            for (Unit u : hand) {
                weights.add(u.getAtk() + u.getDef());
            }
            int max = weights.stream().mapToInt(Integer::intValue).max().orElse(0);
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
        } catch (Exception ignored) {
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
        executeMergeOrEliminate(game, unit, defender, fromRow, fromCol, toRow, toCol, toField);
    }

    private static void executeMergeOrEliminate(Game game, Unit unit, Unit defender,
                                                int fromRow, int fromCol, int toRow, int toCol, Field toField) {
        if (unit.isBlocked()) {
            unit.setBlocked(false);
            System.out.println(unit.getName() + " no longer blocks.");
        }
        Compatibility.MergeStats stats = Compatibility.check(unit, defender);
        if (stats != null) {
            Unit merged = Game.createMergedUnit(unit, defender, stats);
            merged.setTeam(game.getCurrentTeam());
            merged.setMovedThisTurn(false);
            game.getGameBoard().getField(fromRow, fromCol).removeUnit();
            toField.removeUnit();
            game.getGameBoard().placeUnit(toRow, toCol, merged);
            game.setSelectedField(toField);
            System.out.println(unit.getName() + " moves to " + toField.coordinate() + ".");
            System.out.println(unit.getName() + " and " + defender.getName() + " on " + toField.coordinate()
                    + " join forces!");
            System.out.println("Success!");
        } else {
            game.getGameBoard().getField(fromRow, fromCol).removeUnit();
            toField.removeUnit();
            game.getGameBoard().placeUnit(toRow, toCol, unit);
            unit.setMovedThisTurn(true);
            game.setSelectedField(toField);
            System.out.println(unit.getName() + " moves to " + toField.coordinate() + ".");
            System.out.println(unit.getName() + " and " + defender.getName() + " on " + toField.coordinate()
                    + " join forces!");
            System.out.println("Union failed. " + defender.getName() + " was eliminated.");
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

    private static void printBoardAndShow(Game game) {
        List<String> lines = game.getGameBoard().render(game.getSelectedField(), game.getTeam1(), game.getCurrentTeam());
        for (String line : lines) {
            System.out.println(line);
        }
        ShowCommand.printShow(game);
    }
}
