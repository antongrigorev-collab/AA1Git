package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.Team;

import java.util.List;

/**
 * Command "state": prints team names, life points, deck count, board count, the
 * board, and (if a field is selected) the show output for that field.
 *
 * @author usylb
 */
public class StateCommand extends Command {

    private static final String COMMAND_NAME = "state";
    private static final String COMMAND_REGEX = "(?i)^state$";
    private static final int STATE_LINE_LENGTH = 31;
    private static final int DECK_SIZE = 40;

    /** Indent for state output lines. */
    private static final String STATE_OUTPUT_INDENT = "  ";

    private static final String LIFE_POINTS_SEPARATOR = "/";
    private static final String LIFE_POINTS_SUFFIX = " LP";
    private static final String DECK_COUNT_PREFIX = "DC: ";
    private static final String DECK_COUNT_SEPARATOR = "/";
    private static final String BOARD_COUNT_PREFIX = "BC: ";
    private static final String BOARD_COUNT_SEPARATOR = "/";
    private static final String PADDING_SPACE = " ";

    /**
     * Creates the state command with the given handler.
     *
     * @param commandHandler the command handler
     */
    protected StateCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Game game = commandHandler.getGame();
        if (game == null) {
            return;
        }
        Team t1 = game.getTeam1();
        Team t2 = game.getTeam2();
        String prefix = STATE_OUTPUT_INDENT;
        printStateLine(prefix, t1.getName(), t2.getName());
        printStateLine(prefix,
                t1.getLifePoints() + LIFE_POINTS_SEPARATOR + Team.INITIAL_LIFE_POINTS + LIFE_POINTS_SUFFIX,
                t2.getLifePoints() + LIFE_POINTS_SEPARATOR + Team.INITIAL_LIFE_POINTS + LIFE_POINTS_SUFFIX);
        printStateLine(prefix,
                DECK_COUNT_PREFIX + t1.getDeck().size() + DECK_COUNT_SEPARATOR + DECK_SIZE,
                DECK_COUNT_PREFIX + t2.getDeck().size() + DECK_COUNT_SEPARATOR + DECK_SIZE);
        printStateLine(prefix,
                BOARD_COUNT_PREFIX + game.getBoardCount(t1) + BOARD_COUNT_SEPARATOR + Game.MAX_NON_KING_UNITS_ON_BOARD,
                BOARD_COUNT_PREFIX + game.getBoardCount(t2) + BOARD_COUNT_SEPARATOR + Game.MAX_NON_KING_UNITS_ON_BOARD);
        List<String> boardLines = game.getGameBoard().render(game.getSelectedField(), game.getTeam1(), game.getCurrentTeam());
        for (String line : boardLines) {
            System.out.println(line);
        }
        if (game.getSelectedField() != null) {
            ShowCommand.printShow(game);
        }
    }

    private void printStateLine(String prefix, String left, String right) {
        int padLen = STATE_LINE_LENGTH - prefix.length() - left.length() - right.length();
        String pad = padLen > 0 ? PADDING_SPACE.repeat(padLen) : "";
        System.out.println(prefix + left + pad + right);
    }
}
