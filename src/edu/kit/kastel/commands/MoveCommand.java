package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.FieldTooFarException;
import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.exceptions.InvalidFieldException;
import edu.kit.kastel.exceptions.KingCannotAttackException;
import edu.kit.kastel.exceptions.MoveOntoOwnKingException;
import edu.kit.kastel.model.Compatibility;
import edu.kit.kastel.model.DuelResult;
import edu.kit.kastel.model.Field;
import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.BoardGeometry;

/**
 * Command "move &lt;field&gt;": moves the selected unit to the given adjacent field (A1–G7).
 * May trigger a duel (vs enemy or King), a merge (vs own unit), or a simple move.
 * The unit must belong to the current team and must not have moved this turn.
 *
 * @author usylb
 */
public class MoveCommand extends Command {

    /** Context for moving to an occupied field (duel or merge). */
    private record MoveToOccupiedContext(Game game, Unit unit, Unit defender,
                                        int fromRow, int fromCol, int toRow, int toCol,
                                        Field selected, Field toField) { }

    private static final String COMMAND_NAME = "move";
    private static final String COMMAND_REGEX = "(?i)^move\\s+[A-Ga-g][1-7]$";

    private static final String NO_LONGER_BLOCKS_SUFFIX = " no longer blocks.";
    private static final String MOVES_TO_MIDDLE = " moves to ";
    private static final String MOVES_TO_SUFFIX = ".";
    private static final String AND_CONNECTOR = " and ";
    private static final String ON_MIDDLE = " on ";
    private static final String JOIN_FORCES_SUFFIX = " join forces!";
    private static final String UNION_SUCCESS_MESSAGE = "Success!";
    private static final String UNION_FAILED_PREFIX = "Union failed. ";
    private static final String WAS_ELIMINATED_SUFFIX = " was eliminated!";

    /** Index of the first (and only) argument (target field). */
    private static final int INDEX_FIRST_ARG = 0;

    /** Index of row in parseField result array. */
    private static final int INDEX_ROW = 0;

    /** Index of column in parseField result array. */
    private static final int INDEX_COL = 1;

    /**
     * Creates the move command with the given handler.
     *
     * @param commandHandler the command handler
     */
    protected MoveCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Command.SelectedUnitContext ctx = getSelectedOwnUnitNotMoved(commandHandler);
        if (ctx == null) {
            return;
        }
        Game game = ctx.game();
        Field selected = ctx.selected();
        Unit unit = ctx.unit();
        String toStr = commandArguments[INDEX_FIRST_ARG].toUpperCase();
        int[] toRc = BoardGeometry.parseField(toStr);
        if (toRc == null) {
            throw new InvalidFieldException(commandArguments[INDEX_FIRST_ARG]);
        }
        int toRow = toRc[INDEX_ROW];
        int toCol = toRc[INDEX_COL];
        int fromRow = selected.row();
        int fromCol = selected.col();
        if (!BoardGeometry.isAdjacent(fromRow, fromCol, toRow, toCol)) {
            throw new FieldTooFarException(selected.coordinate(), toStr);
        }
        var toField = game.getGameBoard().getField(toRow, toCol);
        Unit toUnit = toField.getUnit();
        if (toUnit != null && toUnit.isKing() && toUnit.getTeam().equals(game.getCurrentTeam())) {
            throw new MoveOntoOwnKingException();
        }
        if (unit.isKing() && toUnit != null && !toUnit.getTeam().equals(game.getCurrentTeam())) {
            throw new KingCannotAttackException();
        }

        if (fromRow == toRow && fromCol == toCol) {
            executeMoveEnPlace(game, unit, toField);
            return;
        }
        if (toField.isEmpty()) {
            executeMoveToEmpty(game, unit, selected, toRow, toCol, toField);
            return;
        }
        Unit defender = toField.getUnit();
        executeMoveToOccupied(new MoveToOccupiedContext(
                game, unit, defender, fromRow, fromCol, toRow, toCol, selected, toField));
    }

    private void executeMoveEnPlace(Game game, Unit unit, Field toField) {
        if (unit.isBlocked()) {
            unit.setBlocked(false);
            System.out.println(unit.getName() + NO_LONGER_BLOCKS_SUFFIX);
        }
        unit.setMovedThisTurn(true);
        System.out.println(unit.getName() + MOVES_TO_MIDDLE + toField.coordinate() + MOVES_TO_SUFFIX);
        ShowCommand.printBoardAndShow(game);
    }

    private void executeMoveToEmpty(Game game, Unit unit, Field selected,
                                    int toRow, int toCol, Field toField) {
        if (unit.isBlocked()) {
            unit.setBlocked(false);
            System.out.println(unit.getName() + NO_LONGER_BLOCKS_SUFFIX);
        }
        selected.removeUnit();
        game.getGameBoard().placeUnit(toRow, toCol, unit);
        unit.setMovedThisTurn(true);
        game.setSelectedField(toField);
        System.out.println(unit.getName() + MOVES_TO_MIDDLE + toField.coordinate() + MOVES_TO_SUFFIX);
        ShowCommand.printBoardAndShow(game);
    }

    private void executeMoveToOccupied(MoveToOccupiedContext ctx) {
        if (!ctx.defender().getTeam().equals(ctx.game().getCurrentTeam())) {
            executeMoveToOccupiedVsEnemy(ctx);
        } else {
            executeMoveToOccupiedVsAlly(ctx);
        }
    }

    private void executeMoveToOccupiedVsEnemy(MoveToOccupiedContext ctx) {
        DuelResult result = ctx.game().performDuel(ctx.unit(), ctx.defender(), ctx.defender().isBlocked(),
                ctx.fromRow(), ctx.fromCol(), ctx.toRow(), ctx.toCol());
        for (String line : result.lines()) {
            System.out.println(line);
        }
        if (result.winner() != null) {
            if (ctx.toField().getUnit() == ctx.unit()) {
                ctx.unit().setMovedThisTurn(true);
                ctx.game().setSelectedField(ctx.toField());
            } else if (ctx.selected().getUnit() == ctx.unit()) {
                ctx.unit().setMovedThisTurn(true);
                ctx.game().setSelectedField(ctx.selected());
            } else {
                ctx.game().setSelectedField(null);
            }
            ShowCommand.printBoardAndShow(ctx.game());
            return;
        }
        if (ctx.toField().getUnit() == ctx.unit()) {
            ctx.unit().setMovedThisTurn(true);
            ctx.game().setSelectedField(ctx.toField());
        }
        ShowCommand.printBoardAndShow(ctx.game());
    }

    private void executeMoveToOccupiedVsAlly(MoveToOccupiedContext ctx) {
        Compatibility.MergeStats stats = Compatibility.check(ctx.unit(), ctx.defender());
        if (ctx.unit().isBlocked()) {
            ctx.unit().setBlocked(false);
            System.out.println(ctx.unit().getName() + NO_LONGER_BLOCKS_SUFFIX);
        }
        if (stats != null) {
            Unit merged = Game.createMergedUnit(ctx.unit(), ctx.defender(), stats);
            merged.setTeam(ctx.game().getCurrentTeam());
            merged.setMovedThisTurn(false);
            ctx.selected().removeUnit();
            ctx.toField().removeUnit();
            ctx.game().getGameBoard().placeUnit(ctx.toRow(), ctx.toCol(), merged);
            ctx.game().setSelectedField(ctx.toField());
            System.out.println(ctx.unit().getName() + MOVES_TO_MIDDLE + ctx.toField().coordinate() + MOVES_TO_SUFFIX);
            System.out.println(ctx.unit().getName() + AND_CONNECTOR + ctx.defender().getName() + ON_MIDDLE
                    + ctx.toField().coordinate() + JOIN_FORCES_SUFFIX);
            System.out.println(UNION_SUCCESS_MESSAGE);
        } else {
            ctx.selected().removeUnit();
            ctx.toField().removeUnit();
            ctx.game().getGameBoard().placeUnit(ctx.toRow(), ctx.toCol(), ctx.unit());
            ctx.unit().setMovedThisTurn(true);
            ctx.game().setSelectedField(ctx.toField());
            System.out.println(ctx.unit().getName() + MOVES_TO_MIDDLE + ctx.toField().coordinate() + MOVES_TO_SUFFIX);
            System.out.println(ctx.unit().getName() + AND_CONNECTOR + ctx.defender().getName() + ON_MIDDLE
                    + ctx.toField().coordinate() + JOIN_FORCES_SUFFIX);
            System.out.println(UNION_FAILED_PREFIX + ctx.defender().getName() + WAS_ELIMINATED_SUFFIX);
        }
        ShowCommand.printBoardAndShow(ctx.game());
    }
}
