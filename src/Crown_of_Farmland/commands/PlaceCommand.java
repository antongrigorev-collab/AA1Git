package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.AlreadyPlacedException;
import Crown_of_Farmland.exceptions.DuplicateHandIndexException;
import Crown_of_Farmland.exceptions.GameException;
import Crown_of_Farmland.exceptions.InvalidHandIndexException;
import Crown_of_Farmland.exceptions.InvalidHandIndexException;
import Crown_of_Farmland.exceptions.MustDiscardException;
import Crown_of_Farmland.exceptions.NoFieldSelectedException;
import Crown_of_Farmland.exceptions.PlaceFieldNotAdjacentToKingException;
import Crown_of_Farmland.exceptions.PlaceOnEnemyFieldException;
import Crown_of_Farmland.model.Compatibility;
import Crown_of_Farmland.model.Game;
import Crown_of_Farmland.model.Unit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Place command: place one or more units from hand onto the selected field.
 *
 * @author Programmieren-Team
 */
public class PlaceCommand extends Command {

    private static final String COMMAND_NAME = "place";
    private static final String COMMAND_REGEX = "(?i)^place\\s+\\d+(\\s+\\d+)*$";
    private static final int MAX_BOARD_UNITS = 5;

    protected PlaceCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Game game = commandHandler.getGame();
        if (game == null || game.isGameOver()) {
            return;
        }
        if (game.getCurrentTeam().getHand().size() == 5) {
            throw new MustDiscardException("place");
        }
        var selected = game.getSelectedField();
        if (selected == null || selected.isEmpty()) {
            throw new NoFieldSelectedException();
        }
        Unit onField = selected.getUnit();
        if (onField != null && !onField.getTeam().equals(game.getCurrentTeam())) {
            throw new PlaceOnEnemyFieldException(selected.coordinate());
        }
        if (game.getCurrentTeam().getHand().hasPlacedThisTurn()) {
            throw new AlreadyPlacedException();
        }
        int row = selected.row();
        int col = selected.col();
        if (!game.isAdjacentToKing(game.getCurrentTeam(), row, col)) {
            throw new PlaceFieldNotAdjacentToKingException(selected.coordinate());
        }
        int handSize = game.getCurrentTeam().getHand().size();
        Set<Integer> seen = new HashSet<>();
        List<Integer> indices = new ArrayList<>();
        for (String arg : commandArguments) {
            int idx = Integer.parseInt(arg);
            if (idx < 1 || idx > handSize) {
                throw new InvalidHandIndexException(idx);
            }
            if (!seen.add(idx)) {
                throw new DuplicateHandIndexException(idx);
            }
            indices.add(idx);
        }
        if (indices.isEmpty()) {
            return;
        }
        List<Unit> toPlace = new ArrayList<>();
        for (int idx : indices) {
            toPlace.add(game.getCurrentTeam().getHand().get(idx));
        }
        for (int i = indices.size() - 1; i >= 0; i--) {
            game.getCurrentTeam().getHand().remove(indices.get(i));
        }
        game.getCurrentTeam().getHand().markPlacedThisTurn();

        int r = selected.row();
        int c = selected.col();
        for (Unit unit : toPlace) {
            unit.setTeam(game.getCurrentTeam());
            Unit currentOnField = game.getGameBoard().getField(r, c).getUnit();
            if (currentOnField == null) {
                System.out.println(game.getCurrentTeam().getName() + " places " + unit.getName() + " on "
                        + selected.coordinate() + ".");
                game.getGameBoard().placeUnit(r, c, unit);
            } else {
                Compatibility.MergeStats stats = Compatibility.check(unit, currentOnField);
                System.out.println(game.getCurrentTeam().getName() + " places " + unit.getName() + " on "
                        + selected.coordinate() + ".");
                System.out.println(unit.getName() + " and " + currentOnField.getName() + " on "
                        + selected.coordinate() + " join forces!");
                if (stats != null) {
                    Unit merged = Game.createMergedUnit(unit, currentOnField, stats);
                    merged.setTeam(game.getCurrentTeam());
                    merged.setMovedThisTurn(false);
                    game.getGameBoard().getField(r, c).removeUnit();
                    game.getGameBoard().placeUnit(r, c, merged);
                    System.out.println("Success!");
                } else {
                    game.getGameBoard().getField(r, c).removeUnit();
                    game.getGameBoard().placeUnit(r, c, unit);
                    System.out.println("Union failed. " + currentOnField.getName() + " was eliminated.");
                }
            }
            if (game.getBoardCount(game.getCurrentTeam()) > MAX_BOARD_UNITS) {
                Unit justPlaced = game.getGameBoard().getField(r, c).getUnit();
                game.getGameBoard().getField(r, c).removeUnit();
                System.out.println(justPlaced.getName() + " was eliminated!");
            }
        }
        List<String> lines = game.getGameBoard().render(game.getSelectedField(), game.getCurrentTeam());
        for (String line : lines) {
            System.out.println(line);
        }
        ShowCommand.printShow(game);
    }
}
