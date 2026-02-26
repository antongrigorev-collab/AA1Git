package Crown_of_Farmland.commands;



import Crown_of_Farmland.exceptions.InvalidCommandArgumentsException;


public class BlockCommand extends Command {

    private static final String COMMAND_NAME = "block";


    private static final String COMMAND_REGEX = "(?i)^block$";


    protected BlockCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws InvalidCommandArgumentsException {



    }
}
