package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.GameException;

/**
 * Quit command. Ends the program via normal control flow.
 *
 * @author Programmieren-Team
 */
public class QuitCommand extends Command {

    private static final String COMMAND_NAME = "quit";
    private static final String COMMAND_REGEX = "(?i)^quit$";

    protected QuitCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        commandHandler.requestQuit();
    }
}
