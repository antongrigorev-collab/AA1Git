package Crown_of_Farmland.commands;

public class YieldCommand extends Command {

    private static final String COMMAND_NAME = "yield";


    private static final String COMMAND_REGEX = "(?i)^yield(\\s+\\d+)?$";

    protected YieldCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) {

    }
}
