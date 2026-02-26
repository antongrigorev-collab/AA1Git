import Crown_of_Farmland.commands.ConfigLoader;
import Crown_of_Farmland.commands.ArgumentParser;
import Crown_of_Farmland.commands.CommandHandler;
import Crown_of_Farmland.commands.GameConfig;
import Crown_of_Farmland.exceptions.GameException;

import java.util.Map;

/**
 * This is the main entry class for the program.
 *
 * @author Programmieren-Team
 */
public final class Main {

    private Main() {

    }

    /**
     * This is the main entry point for the program. There are no arguments expected.
     * If there are arguments, an error will be thrown.
     * @param args The command line arguments given at the start of the program
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