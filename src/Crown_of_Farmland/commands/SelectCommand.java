package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.GameException;
import Crown_of_Farmland.model.Game;

import java.util.List;

/**
 * Command "select &lt;field&gt;": selects the given field (A1–G7) and prints the board
 * and show output for that selection.
 */
public class SelectCommand extends Command {

    private static final String COMMAND_NAME = "select";
    private static final String COMMAND_REGEX = "(?i)^select\\s+[A-Ga-g][1-7]$";

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
        int col = fieldStr.charAt(0) - 'A';
        int row = Integer.parseInt(fieldStr.substring(1)) - 1;
        game.setSelectedField(game.getGameBoard().getField(row, col));
        List<String> lines = game.getGameBoard().render(game.getSelectedField(), game.getTeam1(), game.getCurrentTeam());
        for (String line : lines) {
            System.out.println(line);
        }
        ShowCommand.printShow(game);
    }
}
