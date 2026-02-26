package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.GameException;
import Crown_of_Farmland.model.Game;

import java.util.List;

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
        List<String> lines = game.getGameBoard().render(game.getSelectedField(), game.getCurrentTeam());
        for (String line : lines) {
            System.out.println(line);
        }
    }
}
