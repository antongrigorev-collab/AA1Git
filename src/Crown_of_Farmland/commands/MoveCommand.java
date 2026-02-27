package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.EmptyFieldException;
import Crown_of_Farmland.exceptions.FieldTooFarException;
import Crown_of_Farmland.exceptions.GameException;
import Crown_of_Farmland.exceptions.InvalidFieldException;
import Crown_of_Farmland.exceptions.KingCannotAttackException;
import Crown_of_Farmland.exceptions.MoveOntoOwnKingException;
import Crown_of_Farmland.exceptions.NoFieldSelectedException;
import Crown_of_Farmland.exceptions.NotOwnUnitException;
import Crown_of_Farmland.exceptions.UnitAlreadyMovedException;
import Crown_of_Farmland.model.Compatibility;
import Crown_of_Farmland.model.DuelResult;
import Crown_of_Farmland.model.Game;
import Crown_of_Farmland.model.Unit;

import java.util.List;

public class MoveCommand extends Command {

    private static final String COMMAND_NAME = "move";
    private static final String COMMAND_REGEX = "(?i)^move\\s+[A-Ga-g][1-7]$";

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
            if (unit.isBlocked()) {
                unit.setBlocked(false);
                System.out.println(unit.getName() + " no longer blocks.");
            }
            unit.setMovedThisTurn(true);
            System.out.println(unit.getName() + " moves to " + toField.coordinate() + ".");
            printBoardAndShow(game);
            return;
        }

        if (toField.isEmpty()) {
            if (unit.isBlocked()) {
                unit.setBlocked(false);
                System.out.println(unit.getName() + " no longer blocks.");
            }
            selected.removeUnit();
            game.getGameBoard().placeUnit(toRow, toCol, unit);
            unit.setMovedThisTurn(true);
            game.setSelectedField(toField);
            System.out.println(unit.getName() + " moves to " + toField.coordinate() + ".");
            printBoardAndShow(game);
            return;
        }

        Unit defender = toField.getUnit();
        if (!defender.getTeam().equals(game.getCurrentTeam())) {
            DuelResult result = game.performDuel(unit, defender, defender.isBlocked(), fromRow, fromCol, toRow, toCol);
            for (String line : result.lines()) {
                System.out.println(line);
            }
            if (result.winner() != null) {
                return;
            }
            if (toField.getUnit() == unit) {
                game.setSelectedField(toField);
            }
            printBoardAndShow(game);
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
            String joinMsg = unit.getName() + " and " + defender.getName() + " on " + toField.coordinate()
                    + " join forces!";
            System.out.println(joinMsg);
            System.out.println("Success!");
        } else {
            selected.removeUnit();
            toField.removeUnit();
            game.getGameBoard().placeUnit(toRow, toCol, unit);
            unit.setMovedThisTurn(true);
            game.setSelectedField(toField);
            System.out.println(unit.getName() + " moves to " + toField.coordinate() + ".");
            String joinMsg = unit.getName() + " and " + defender.getName() + " on " + toField.coordinate()
                    + " join forces!";
            System.out.println(joinMsg);
            System.out.println("Union failed. " + defender.getName() + " was eliminated.");
        }
        printBoardAndShow(game);
    }

    private void printBoardAndShow(Game game) {
        List<String> lines = game.getGameBoard().render(game.getSelectedField(), game.getCurrentTeam());
        for (String line : lines) {
            System.out.println(line);
        }
        ShowCommand.printShow(game);
    }
}
