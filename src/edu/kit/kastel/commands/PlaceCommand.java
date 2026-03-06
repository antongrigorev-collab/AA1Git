package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.AlreadyPlacedException;
import edu.kit.kastel.exceptions.DuplicateHandIndexException;
import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.exceptions.InvalidHandIndexException;
import edu.kit.kastel.exceptions.NoFieldSelectedException;
import edu.kit.kastel.exceptions.PlaceFieldNotAdjacentToKingException;
import edu.kit.kastel.exceptions.PlaceOnEnemyFieldException;
import edu.kit.kastel.model.Compatibility;
import edu.kit.kastel.model.Field;
import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.Unit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Place command: place one or more units from hand onto the selected field.
 * Optional last argument can be a field (e.g. C2) to select that field before placing.
 *
 * @author usylb
 */
public class PlaceCommand extends Command {

    private static final String COMMAND_NAME = "place";
    /** place &lt;idx&gt; [&lt;idx&gt; ...] [&lt;field&gt;] e.g. place 3 or place 3 C2 */
    private static final String COMMAND_REGEX = "(?i)^place\\s+\\d+(\\s+(\\d+|[A-Ga-g][1-7]))*$";

    private static final int FIELD_ARGUMENT_LENGTH = 2;
    private static final char MIN_COLUMN_CHAR = 'A';
    private static final char MAX_COLUMN_CHAR = 'G';
    private static final char MIN_ROW_CHAR = '1';
    private static final char MAX_ROW_CHAR = '7';
    private static final int HAND_INDEX_OFFSET = 1;

    /** Index of first character in field string. */
    private static final int INDEX_FIRST_CHAR = 0;

    /** Index of second character in field string. */
    private static final int INDEX_SECOND_CHAR = 1;

    /** Index of row in parseField result array. */
    private static final int INDEX_ROW = 0;

    /** Index of column in parseField result array. */
    private static final int INDEX_COL = 1;

    /** Index of last element in list (offset from size). */
    private static final int INDEX_LAST_OFFSET = 1;

    /** Minimum loop index when iterating backwards. */
    private static final int MIN_LOOP_INDEX = 0;

    /** Message fragment: " places ". */
    private static final String MSG_PLACES = " places ";

    /** Message fragment: " on ". */
    private static final String MSG_ON = " on ";

    /** Message fragment: ".". */
    private static final String MSG_SENTENCE_END = ".";

    /** Message fragment: " and ". */
    private static final String MSG_AND = " and ";

    /** Message fragment: " join forces!". */
    private static final String MSG_JOIN_FORCES = " join forces!";

    /** Message: "Success!". */
    private static final String MSG_SUCCESS = "Success!";

    /** Message prefix: "Union failed. ". */
    private static final String MSG_UNION_FAILED = "Union failed. ";

    /** Message suffix: " was eliminated!". */
    private static final String MSG_WAS_ELIMINATED = " was eliminated!";

    /**
     * Creates the place command with the given handler.
     *
     * @param commandHandler the command handler
     */
    protected PlaceCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    private static boolean isFieldArg(String arg) {
        if (arg == null || arg.length() != FIELD_ARGUMENT_LENGTH) {
            return false;
        }
        char c0 = Character.toUpperCase(arg.charAt(INDEX_FIRST_CHAR));
        char c1 = arg.charAt(INDEX_SECOND_CHAR);
        return c0 >= MIN_COLUMN_CHAR && c0 <= MAX_COLUMN_CHAR && c1 >= MIN_ROW_CHAR && c1 <= MAX_ROW_CHAR;
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Game game = commandHandler.getGame();
        if (game == null || game.isGameOver()) {
            return;
        }
        List<String> argsList = new ArrayList<>(List.of(commandArguments));
        List<Integer> indices = validatePlaceCommand(game, argsList);
        if (indices.isEmpty()) {
            return;
        }
        var selected = game.getSelectedField();
        placeUnitsOnField(game, selected, indices);
        List<String> lines = game.getGameBoard().render(game.getSelectedField(), game.getTeam1(), game.getCurrentTeam());
        for (String line : lines) {
            System.out.println(line);
        }
        ShowCommand.printShow(game);
    }

    /**
     * Applies optional field argument to game selection, validates, and returns list of hand indices.
     * Throws on validation failure.
     */
    private List<Integer> validatePlaceCommand(Game game, List<String> argsList) throws GameException {
        if (!argsList.isEmpty() && isFieldArg(argsList.get(argsList.size() - INDEX_LAST_OFFSET))) {
            String fieldStr = argsList.remove(argsList.size() - INDEX_LAST_OFFSET).toUpperCase();
            int[] rc = Game.parseField(fieldStr);
            if (rc != null) {
                game.setSelectedField(game.getGameBoard().getField(rc[INDEX_ROW], rc[INDEX_COL]));
            }
        }
        var selected = game.getSelectedField();
        if (selected == null) {
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
            if (idx < HAND_INDEX_OFFSET || idx > handSize) {
                throw new InvalidHandIndexException(idx);
            }
            if (!seen.add(idx)) {
                throw new DuplicateHandIndexException(idx);
            }
            indices.add(idx);
        }
        return indices;
    }

    private void placeUnitsOnField(Game game, Field selected, List<Integer> indices) {
        List<Unit> toPlace = new ArrayList<>();
        for (int idx : indices) {
            toPlace.add(game.getCurrentTeam().getHand().get(idx));
        }
        for (int i = indices.size() - INDEX_LAST_OFFSET; i >= MIN_LOOP_INDEX; i--) {
            game.getCurrentTeam().getHand().remove(indices.get(i));
        }
        game.getCurrentTeam().getHand().markPlacedThisTurn();
        int r = selected.row();
        int c = selected.col();
        for (Unit unit : toPlace) {
            unit.setTeam(game.getCurrentTeam());
            Unit currentOnField = game.getGameBoard().getField(r, c).getUnit();
            if (currentOnField == null) {
                System.out.println(game.getCurrentTeam().getName() + MSG_PLACES + unit.getName() + MSG_ON
                        + selected.coordinate() + MSG_SENTENCE_END);
                game.getGameBoard().placeUnit(r, c, unit);
            } else {
                Compatibility.MergeStats stats = Compatibility.check(unit, currentOnField);
                System.out.println(game.getCurrentTeam().getName() + MSG_PLACES + unit.getName() + MSG_ON
                        + selected.coordinate() + MSG_SENTENCE_END);
                System.out.println(unit.getName() + MSG_AND + currentOnField.getName() + MSG_ON
                        + selected.coordinate() + MSG_JOIN_FORCES);
                if (stats != null) {
                    Unit merged = Game.createMergedUnit(unit, currentOnField, stats);
                    merged.setTeam(game.getCurrentTeam());
                    merged.setMovedThisTurn(false);
                    game.getGameBoard().getField(r, c).removeUnit();
                    game.getGameBoard().placeUnit(r, c, merged);
                    System.out.println(MSG_SUCCESS);
                } else {
                    game.getGameBoard().getField(r, c).removeUnit();
                    game.getGameBoard().placeUnit(r, c, unit);
                    System.out.println(MSG_UNION_FAILED + currentOnField.getName() + MSG_WAS_ELIMINATED);
                }
            }
            if (game.getBoardCount(game.getCurrentTeam()) > Game.MAX_NON_KING_UNITS_ON_BOARD) {
                Unit justPlaced = game.getGameBoard().getField(r, c).getUnit();
                game.getGameBoard().getField(r, c).removeUnit();
                System.out.println(justPlaced.getName() + MSG_WAS_ELIMINATED);
            }
        }
    }
}
