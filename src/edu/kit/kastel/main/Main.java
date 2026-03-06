package edu.kit.kastel.main;

import edu.kit.kastel.commands.ConfigLoader;
import edu.kit.kastel.commands.ArgumentParser;
import edu.kit.kastel.commands.CommandHandler;
import edu.kit.kastel.commands.GameConfig;
import edu.kit.kastel.exceptions.GameException;

import java.util.Map;

/**
 * Main entry point for the text-based game Crown of Farmland (Krone von Ackerland).
 * Initializes configuration from command-line arguments, loads the game, and runs
 * the command loop until the user quits or the game ends.
 *
 * @author usylb
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
            System.err.println(e.getFormattedMessage());
        }
    }
}
