package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.model.Game;

import java.util.List;

/**
 * Command "select &lt;field&gt;": selects the given field (A1–G7) and prints the board
 * and show output for that selection.
 *
 * @author usylb
 */
public class SelectCommand extends Command {

    private static final String COMMAND_NAME = "select";
    private static final String COMMAND_REGEX = "(?i)^select\\s+[A-Ga-g][1-7]$";

    private static final char MIN_COLUMN_CHAR = 'A';
    private static final int ROW_INDEX_OFFSET = 1;

    /**
     * Creates the select command with the given handler.
     *
     * @param commandHandler the command handler
     */
    protected SelectCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Game game = commandHandler.getGame();
        if (game == null) {
            return;
        }
        String fieldStr = commandArguments[0].toUpperCase();
        int col = fieldStr.charAt(0) - MIN_COLUMN_CHAR;
        int row = Integer.parseInt(fieldStr.substring(1)) - ROW_INDEX_OFFSET;
        game.setSelectedField(game.getGameBoard().getField(row, col));
        List<String> lines = game.getGameBoard().render(game.getSelectedField(), game.getTeam1(), game.getCurrentTeam());
        for (String line : lines) {
            System.out.println(line);
        }
        ShowCommand.printShow(game);
    }
}
