package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.InvalidCommandArgumentsException;


public class FlipCommand extends Command {

    private static final String COMMAND_NAME = "flip";

    private static final String COMMAND_REGEX = "(?i)^flip$";



    protected FlipCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws InvalidCommandArgumentsException {


    }
}
