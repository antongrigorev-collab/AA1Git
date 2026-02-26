package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.GameException;
import Crown_of_Farmland.model.Game;
import Crown_of_Farmland.model.Unit;

public class ShowCommand extends Command {

    private static final String COMMAND_NAME = "show";
    private static final String COMMAND_REGEX = "(?i)^show$";
    private static final String NO_UNIT = "<no unit>";

    protected ShowCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Game game = commandHandler.getGame();
        if (game == null) {
            return;
        }
        printShow(game);
    }

    /**
     * Prints the show output for the current selection. Used by show and select commands.
     *
     * @param game the game (must not be null)
     */
    public static void printShow(Game game) {
        var selected = game.getSelectedField();
        if (selected == null || selected.isEmpty()) {
            System.out.println(NO_UNIT);
            return;
        }
        Unit u = selected.getUnit();
        if (u.isKing()) {
            System.out.println(u.getTeam().getName() + "'s Farmer King");
            return;
        }
        if (!u.isRevealed() && !u.getTeam().equals(game.getCurrentTeam())) {
            System.out.println("??? (Team " + u.getTeam().getName() + ")");
            System.out.println("ATK: ???");
            System.out.println("DEF: ???");
            return;
        }
        System.out.println(u.getName() + " (Team " + u.getTeam().getName() + ")");
        System.out.println("ATK: " + u.getAtk());
        System.out.println("DEF: " + u.getDef());
    }
}
