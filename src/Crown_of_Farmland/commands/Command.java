package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.GameException;


public abstract class Command {
    /**
     * This is the command handler.
     */
    protected final CommandHandler commandHandler;


    private final String commandName;
    private final String commandRegex;

    /**
     * Commands that have the same name name as regex, for example quit, do not have a seperate regex.
     *
     * @param commandName The name of the command
     * @param commandRegex The regex to match the command against
     * @param commandHandler The command handler
     */
    protected Command(String commandName, String commandRegex, CommandHandler commandHandler) {
        this.commandName = commandName;
        this.commandRegex = commandRegex;
        this.commandHandler = commandHandler;
    }

    /**
     * This returns the command name.
     *
     * @return The name of the command.
     */
    public final String getCommandName() {
        return commandName;
    }

    /**
     * This returns the regex that the input has to match against for the command to be executed.
     *
     * @return The pattern of the command.
     */
    public final String getCommandRegex() {
        return commandRegex;
    }

    /**
     * Executes a given command. The arguments are already split by the command handler.
     *
     * @param commandArguments The arguments the command needs to run. Can contain optional arguments
     * @throws GameException If the command fails (invalid arguments, invalid state, etc.)
     */
    public abstract void execute(String[] commandArguments) throws GameException;


}
