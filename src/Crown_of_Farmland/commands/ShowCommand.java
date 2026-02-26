package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.InvalidCommandArgumentsException;


public class ShowCommand extends Command {

    private static final String COMMAND_NAME = "show";

    private static final String COMMAND_REGEX = "(?i)^show$";


    protected ShowCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }
    @Override
    public void execute(String[] commandArguments) throws InvalidCommandArgumentsException {

    }
}
