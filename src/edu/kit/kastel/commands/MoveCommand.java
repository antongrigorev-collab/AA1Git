package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.EmptyFieldException;
import edu.kit.kastel.exceptions.FieldTooFarException;
import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.exceptions.InvalidFieldException;
import edu.kit.kastel.exceptions.KingCannotAttackException;
import edu.kit.kastel.exceptions.MoveOntoOwnKingException;
import edu.kit.kastel.exceptions.NoFieldSelectedException;
import edu.kit.kastel.exceptions.NotOwnUnitException;
import edu.kit.kastel.exceptions.UnitAlreadyMovedException;
import edu.kit.kastel.model.Compatibility;
import edu.kit.kastel.model.DuelResult;
import edu.kit.kastel.model.Field;
import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.Unit;

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
        Game game = commandHandler.getGame();
        if (game == null || game.isGameOver()) {
            return;
        }
        var selected = game.getSelectedField();
        if (selected == null) {
            throw new NoFieldSelectedException();
        }
        if (selected.isEmpty()) {
            throw new EmptyFieldException(selected.coordinate());
        }
        Unit unit = selected.getUnit();
        if (!unit.getTeam().equals(game.getCurrentTeam())) {
            throw new NotOwnUnitException();
        }
        if (unit.hasMovedThisTurn()) {
            throw new UnitAlreadyMovedException(unit.getName());
        }
        String toStr = commandArguments[0].toUpperCase();
        int[] toRc = Game.parseField(toStr);
        if (toRc == null) {
            throw new InvalidFieldException(commandArguments[0]);
        }
        int toRow = toRc[0];
        int toCol = toRc[1];
        int fromRow = selected.row();
        int fromCol = selected.col();
        if (!Game.isAdjacent(fromRow, fromCol, toRow, toCol)) {
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
            System.out.println(unit.getName() + " no longer blocks.");
        }
        unit.setMovedThisTurn(true);
        System.out.println(unit.getName() + " moves to " + toField.coordinate() + ".");
        ShowCommand.printBoardAndShow(game);
    }

    private void executeMoveToEmpty(Game game, Unit unit, Field selected,
                                    int toRow, int toCol, Field toField) {
        if (unit.isBlocked()) {
            unit.setBlocked(false);
            System.out.println(unit.getName() + " no longer blocks.");
        }
        selected.removeUnit();
        game.getGameBoard().placeUnit(toRow, toCol, unit);
        unit.setMovedThisTurn(true);
        game.setSelectedField(toField);
        System.out.println(unit.getName() + " moves to " + toField.coordinate() + ".");
        ShowCommand.printBoardAndShow(game);
    }

    private void executeMoveToOccupied(MoveToOccupiedContext ctx) {
        Game game = ctx.game();
        Unit unit = ctx.unit();
        Unit defender = ctx.defender();
        Field selected = ctx.selected();
        Field toField = ctx.toField();
        int toRow = ctx.toRow();
        int toCol = ctx.toCol();
        if (!defender.getTeam().equals(game.getCurrentTeam())) {
            DuelResult result = game.performDuel(unit, defender, defender.isBlocked(),
                    ctx.fromRow(), ctx.fromCol(), toRow, toCol);
            for (String line : result.lines()) {
                System.out.println(line);
            }
            if (result.winner() != null) {
                return;
            }
            if (toField.getUnit() == unit) {
                unit.setMovedThisTurn(true);
                game.setSelectedField(toField);
            }
            ShowCommand.printBoardAndShow(game);
            return;
        }
        Compatibility.MergeStats stats = Compatibility.check(unit, defender);
        if (unit.isBlocked()) {
            unit.setBlocked(false);
            System.out.println(unit.getName() + " no longer blocks.");
        }
        if (stats != null) {
            Unit merged = Game.createMergedUnit(unit, defender, stats);
            merged.setTeam(game.getCurrentTeam());
            merged.setMovedThisTurn(false);
            selected.removeUnit();
            toField.removeUnit();
            game.getGameBoard().placeUnit(toRow, toCol, merged);
            game.setSelectedField(toField);
            System.out.println(unit.getName() + " moves to " + toField.coordinate() + ".");
            System.out.println(unit.getName() + " and " + defender.getName() + " on " + toField.coordinate()
                    + " join forces!");
            System.out.println("Success!");
        } else {
            selected.removeUnit();
            toField.removeUnit();
            game.getGameBoard().placeUnit(toRow, toCol, unit);
            unit.setMovedThisTurn(true);
            game.setSelectedField(toField);
            System.out.println(unit.getName() + " moves to " + toField.coordinate() + ".");
            System.out.println(unit.getName() + " and " + defender.getName() + " on " + toField.coordinate()
                    + " join forces!");
            System.out.println("Union failed. " + defender.getName() + " was eliminated.");
        }
        ShowCommand.printBoardAndShow(game);
    }
}
