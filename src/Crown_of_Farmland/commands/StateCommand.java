package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.InvalidCommandArgumentsException;

public class StateCommand extends Command {

    private static final String COMMAND_NAME = "state";

    private static final String COMMAND_REGEX = "(?i)^state$";

    protected StateCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws InvalidCommandArgumentsException {

    }
}
