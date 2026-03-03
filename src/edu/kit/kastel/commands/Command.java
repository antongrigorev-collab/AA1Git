package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.GameException;


/**
 * Base class for console commands in Crown of Farmland. Each command has a name,
 * a regex pattern for matching user input, and an execute method that receives
 * pre-split arguments.
 *
 * @author usylb
 */
public abstract class Command {
    /** The command handler that owns this command and provides game access. */
    protected final CommandHandler commandHandler;


    private final String commandName;
    private final String commandRegex;

    /**
     * Constructs a command with the given name, regex, and handler.
     *
     * @param commandName   the command name (e.g. "quit")
     * @param commandRegex  the regex the user input must match (may equal the name)
     * @param commandHandler the command handler
     */
    protected Command(String commandName, String commandRegex, CommandHandler commandHandler) {
        this.commandName = commandName;
        this.commandRegex = commandRegex;
        this.commandHandler = commandHandler;
    }

    /**
     * Returns the command name.
     *
     * @return the command name
     */
    public final String getCommandName() {
        return commandName;
    }

    /**
     * Returns the regex pattern used to match user input for this command.
     *
     * @return the command regex pattern
     */
    public final String getCommandRegex() {
        return commandRegex;
    }

    /**
     * Executes this command with the given pre-split arguments.
     *
     * @param commandArguments the arguments for the command (may include optional parts)
     * @throws GameException if the command fails (invalid arguments or invalid game state)
     */
    public abstract void execute(String[] commandArguments) throws GameException;


}
