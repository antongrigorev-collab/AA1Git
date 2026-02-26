package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.AlreadyPlacedException;
import Crown_of_Farmland.exceptions.DuplicateHandIndexException;
import Crown_of_Farmland.exceptions.GameException;
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
 * Optional last argument can be a field (e.g. C2) to select that field before placing.
 *
 * @author Programmieren-Team
 */
public class PlaceCommand extends Command {

    private static final String COMMAND_NAME = "place";
    /** place &lt;idx&gt; [&lt;idx&gt; ...] [&lt;field&gt;] e.g. place 3 or place 3 C2 */
    private static final String COMMAND_REGEX = "(?i)^place\\s+\\d+(\\s+(\\d+|[A-Ga-g][1-7]))*$";
    private static final int MAX_BOARD_UNITS = 5;

    protected PlaceCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    private static boolean isFieldArg(String arg) {
        if (arg == null || arg.length() != 2) {
            return false;
        }
        char c0 = Character.toUpperCase(arg.charAt(0));
        char c1 = arg.charAt(1);
        return c0 >= 'A' && c0 <= 'G' && c1 >= '1' && c1 <= '7';
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Game game = commandHandler.getGame();
        if (game == null || game.isGameOver()) {
            return;
        }
        if (game.getCurrentTeam().getHand().size() == 5) {
            throw new MustDiscardException("place a card");
        }
        List<String> argsList = new ArrayList<>(List.of(commandArguments));
        if (!argsList.isEmpty() && isFieldArg(argsList.get(argsList.size() - 1))) {
            String fieldStr = argsList.remove(argsList.size() - 1).toUpperCase();
            int[] rc = Game.parseField(fieldStr);
            if (rc != null) {
                game.setSelectedField(game.getGameBoard().getField(rc[0], rc[1]));
            }
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
        for (String arg : argsList) {
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
