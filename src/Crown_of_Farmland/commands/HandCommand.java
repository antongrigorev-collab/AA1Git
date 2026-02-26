package Crown_of_Farmland.commands;


import Crown_of_Farmland.exceptions.InvalidCommandArgumentsException;

public class HandCommand extends Command {

    private static final String COMMAND_NAME = "hand";

    private static final String COMMAND_REGEX = "(?i)^hand$";


    protected HandCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws InvalidCommandArgumentsException {

    }
}
