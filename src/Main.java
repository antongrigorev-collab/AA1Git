import Crown_of_Farmland.commands.ConfigLoader;
import Crown_of_Farmland.commands.ArgumentParser;
import Crown_of_Farmland.commands.CommandHandler;
import Crown_of_Farmland.commands.GameConfig;
import Crown_of_Farmland.exceptions.GameException;

import java.util.Map;

/**
 * Main entry point for the text-based game Crown of Farmland (Krone von Ackerland).
 * Initializes configuration from command-line arguments, loads the game, and runs
 * the command loop until the user quits or the game ends.
 *
 * @author Programmieren-Team
 */
public final class Main {
    private Main() {

    }

    /**
     * Runs the game. Parses key-value arguments, loads configuration (units, decks,
     * board symbols), creates the game and command handler, then processes user input.
     * Prints a formatted error message if a game or startup exception occurs.
     *
     * @param args command-line key-value pairs (e.g. seed=123, units=units.txt)
     */
    public static void main(String[] args) {
        try {
            Map<String, String> kv = ArgumentParser.parse(args);
            GameConfig config = ConfigLoader.load(kv);
            CommandHandler handler = new CommandHandler(config);
            handler.handleUserInput();
        } catch (GameException e) {
            System.out.println(e.getFormattedMessage());
        }
    }
}