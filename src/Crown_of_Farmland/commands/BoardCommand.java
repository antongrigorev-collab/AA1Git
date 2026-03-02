package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.GameException;
import Crown_of_Farmland.model.Game;

import java.util.List;

/**
 * Command "board": prints the current game board (and selected field highlight)
 * using the configured symbol set and verbosity mode.
 */
public class BoardCommand extends Command {

    private static final String COMMAND_NAME = "board";
    private static final String COMMAND_REGEX = "(?i)^board$";

    protected BoardCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Game game = commandHandler.getGame();
        if (game == null) {
            return;
        }
        List<String> lines = game.getGameBoard().render(game.getSelectedField(), game.getTeam1(), game.getCurrentTeam());
        for (String line : lines) {
            System.out.println(line);
        }
    }
}
