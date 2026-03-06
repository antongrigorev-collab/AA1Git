package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.model.Game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Handles user input for Crown of Farmland (Krone von Ackerland). Creates and
 * initializes the game from configuration, then dispatches console commands
 * (select, board, move, flip, block, hand, place, show, yield, state, quit)
 * until the user quits or a startup error occurs.
 *
 * @author usylb
 */
public class CommandHandler {
    private static final String COMMAND_DELIMITER_REGEX = "\\s+";
    private static final String COMMAND_DELIMITER_REPLACEMENT = " ";
    private static final String COMMAND_NOT_FOUND_ERROR = "ERROR: Command '%s' not recognised by any pattern%n";
    private static final String HELP_MESSAGE = "Use one of the following commands: select, board, move, flip, "
            + "block, hand, place, show, yield, state, quit.";
    private static final int FIRST_ARGUMENT_INDEX = 1;

    private final GameConfig config;
    private final Map<String, Command> commands;
    private Game game;
    private boolean running = false;

    /**
     * Creates a new command handler object and initializes its commands.
     *
     * @param config the game configuration loaded at startup
     */
    public CommandHandler(GameConfig config) {
        this.config = config;
        this.commands = new HashMap<>();
        this.initCommands();
    }

    /**
     * Returns the current game instance.
     *
     * @return the game, or null if not yet initialized
     */
    public Game getGame() {
        return game;
    }

    /**
     * Requests the command loop to stop. Used by the quit command.
     */
    public void requestQuit() {
        this.running = false;
    }

    /**
     * Initializes the game from config, prints the help message, then reads and
     * executes commands from standard input until {@link #requestQuit()} is called.
     * Prints formatted error messages for game exceptions without terminating.
     */
    public void handleUserInput() {
        try {
            this.game = new Game(config);
            this.game.initFromConfig();
        } catch (GameException e) {
            System.out.println(e.getFormattedMessage());
            return;
        }

        System.out.println(HELP_MESSAGE);
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

        String[] commandArguments = Arrays.copyOfRange(splitCommand, FIRST_ARGUMENT_INDEX, splitCommand.length);
        for (Command command : commands.values()) {
            if (strippedInput.matches(command.getCommandRegex())) {
                try {
                    command.execute(commandArguments);
                } catch (GameException e) {
                    System.out.println(e.getFormattedMessage());
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
        this.addCommand(new HandCommand(this));
        this.addCommand(new MoveCommand(this));
        this.addCommand(new PlaceCommand(this));
        this.addCommand(new QuitCommand(this));
        this.addCommand(new ShowCommand(this));
        this.addCommand(new SelectCommand(this));
        this.addCommand(new StateCommand(this));
        this.addCommand(new YieldCommand(this));
    }

    private void addCommand(Command command) {
        this.commands.put(command.getCommandName(), command);
    }
}
