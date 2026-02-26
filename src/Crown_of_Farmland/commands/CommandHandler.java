package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.InvalidCommandArgumentsException;
import Crown_of_Farmland.model.Game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Implement the command handler in a similar fashion as on earlier tasks.
 *
 * @author Programmieren-Team
 */
public class CommandHandler {
    private static final String COMMAND_DELIMITER_REGEX = "\\s+";
    private static final String COMMAND_DELIMITER_REPLACEMENT = " ";
    private static final String COMMAND_NOT_FOUND_ERROR = "ERROR: Command '%s' not recognised by any pattern%n";
    private final Map<String, Command> commands;
    private boolean running = false;

    /**
     * Creates a new command handler object and initisalizes its program.commands.
     *
     */
    public CommandHandler() {
        this.commands = new HashMap<>();
        this.initCommands();
    }

    /**
     * This method handles the input of the user.
     * The input is taken so long, as this (command handler) was not stopped by the quit command.
     */
    public void handleUserInput() {
        this.running = true;

        try (Scanner scanner = new Scanner(System.in)) {
            while (running) {
                executeCommand(scanner.nextLine());
            }

        }
    }


    private void executeCommand(String inputString) {

        String strippedInput = inputString.strip().replaceAll(COMMAND_DELIMITER_REGEX, COMMAND_DELIMITER_REPLACEMENT);
        String[] splitCommand = strippedInput.split(COMMAND_DELIMITER_REGEX);

        String[] commandArguments = Arrays.copyOfRange(splitCommand, 1, splitCommand.length);
        for (Command command : commands.values()) {
            if (strippedInput.matches(command.getCommandRegex())) {
                try {
                    command.execute(commandArguments);
                } catch (InvalidCommandArgumentsException e) {
                    System.err.println(e.getMessage());
                }
                return;
            }
        }
        System.out.printf(COMMAND_NOT_FOUND_ERROR, inputString);
    }

    private void initCommands() {
        this.addCommand(new BlockCommand(this));
        this.addCommand(new BoardCommand(this));
        this.addCommand(new FlipCommand(this));
        this.addCommand(new MoveCommand(this));
        this.addCommand(new PlaceCommand(this));
        this.addCommand(new ShowCommand(this));
        this.addCommand(new SelectCommand(this));
        this.addCommand(new StateCommand(this));
        this.addCommand(new YieldCommand(this));
        this.addCommand(new HandCommand(this));
    }

    private void addCommand(Command command) {
        this.commands.put(command.getCommandName(), command);
    }
}
