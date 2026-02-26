package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.InvalidCommandArgumentsException;


public class SelectCommand extends Command {

    private static final String COMMAND_NAME = "add";

    private static final String COMMAND_REGEX = "(?i)^select\\s+[A-G][1-7]$";

    protected SelectCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }
    @Override
    public void execute(String[] commandArguments) throws InvalidCommandArgumentsException {


    }
}
