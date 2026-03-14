package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.model.Game;



/**
 * Command "board": prints the current game board (and selected field highlight)
 * using the configured symbol set and verbosity mode.
 *
 * @author usylb
 */
public class BoardCommand extends Command {

    private static final String COMMAND_NAME = "board";
    private static final String COMMAND_REGEX = "(?i)^board$";

    /**
     * Creates the board command with the given handler.
     *
     * @param commandHandler the command handler
     */
    protected BoardCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Game game = commandHandler.getGame();
        if (game == null) {
            return;
        }
        ShowCommand.printBoard(game);
    }
}
