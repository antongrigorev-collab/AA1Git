package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.InvalidCommandArgumentsException;

/**
 * Implements the assign command for lists.
 *
 * @author Programmieren-Team
 */
public class PlaceCommand extends Command {

    private static final String COMMAND_NAME = "place";

    private static final String COMMAND_REGEX = "(?i)^place\\s+\\d+(\\s+\\d+)*$";

    protected PlaceCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws InvalidCommandArgumentsException {

    }
}
