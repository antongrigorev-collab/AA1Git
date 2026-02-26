package Crown_of_Farmland.commands;


import Crown_of_Farmland.exceptions.InvalidCommandArgumentsException;


public class BoardCommand extends Command {

    private static final String COMMAND_NAME = "board";


    private static final String COMMAND_REGEX = "(?i)^board$";


    protected BoardCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws InvalidCommandArgumentsException {

    }
}
