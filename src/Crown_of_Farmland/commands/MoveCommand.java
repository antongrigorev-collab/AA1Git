package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.InvalidCommandArgumentsException;

public class MoveCommand extends Command {

    private static final String COMMAND_NAME = "move";
    private static final String COMMAND_REGEX = "(?i)^move\\s+[A-G][1-7]$";


    protected MoveCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws InvalidCommandArgumentsException {

    }
}
