package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.GameException;
import Crown_of_Farmland.model.Game;
import Crown_of_Farmland.model.Unit;

/**
 * Command "show": prints information about the currently selected field (unit name,
 * team, ATK/DEF, or "&lt;no unit&gt;" / Farmer King / ??? for hidden opponent units).
 */
public class ShowCommand extends Command {

    private static final String COMMAND_NAME = "show";
    private static final String COMMAND_REGEX = "(?i)^show$";
    private static final String NO_UNIT = "<no unit>";

    /** Placeholder for hidden unit name/stats when not revealed to current player (A.5.6). */
    private static final String HIDDEN_UNIT_PLACEHOLDER = "???";

    /** Suffix for Farmer King display (e.g. "Player's Farmer King"). */
    private static final String FARMER_KING_DISPLAY_SUFFIX = "'s Farmer King";

    /** Prefix for unit team display. */
    private static final String TEAM_DISPLAY_PREFIX = " (Team ";

    /** Prefix for ATK display line. */
    private static final String ATK_PREFIX = "ATK: ";

    /** Prefix for DEF display line. */
    private static final String DEF_PREFIX = "DEF: ";

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
     * Prints the show output for the current selection. Displays unit name, team,
     * ATK and DEF, or placeholder for empty/hidden/King. Reused by show and select.
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
            System.out.println(u.getTeam().getName() + FARMER_KING_DISPLAY_SUFFIX);
            return;
        }
        if (!u.isRevealed() && !u.getTeam().equals(game.getCurrentTeam())) {
            System.out.println(HIDDEN_UNIT_PLACEHOLDER + TEAM_DISPLAY_PREFIX + u.getTeam().getName() + ")");
            System.out.println(ATK_PREFIX + HIDDEN_UNIT_PLACEHOLDER);
            System.out.println(DEF_PREFIX + HIDDEN_UNIT_PLACEHOLDER);
            return;
        }
        System.out.println(u.getName() + TEAM_DISPLAY_PREFIX + u.getTeam().getName() + ")");
        System.out.println(ATK_PREFIX + u.getAtk());
        System.out.println(DEF_PREFIX + u.getDef());
    }
}
