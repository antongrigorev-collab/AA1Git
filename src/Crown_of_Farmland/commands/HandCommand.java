package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.GameException;
import Crown_of_Farmland.model.Game;
import Crown_of_Farmland.model.Unit;

import java.util.List;

public class HandCommand extends Command {

    private static final String COMMAND_NAME = "hand";
    private static final String COMMAND_REGEX = "(?i)^hand$";

    /** Format for hand list entry: [idx] name (atk/def). */
    private static final String HAND_ENTRY_INDEX_PREFIX = "[";
    private static final String HAND_ENTRY_INDEX_SUFFIX = "] ";
    private static final String UNIT_STATS_PREFIX = " (";
    private static final String UNIT_STATS_SEPARATOR = "/";
    private static final String UNIT_STATS_SUFFIX = ")";

    protected HandCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Game game = commandHandler.getGame();
        if (game == null || game.isGameOver()) {
            return;
        }
        List<Unit> handUnits = game.getCurrentTeam().getHand().snapshot();
        for (int i = 0; i < handUnits.size(); i++) {
            Unit u = handUnits.get(i);
            int idx = i + 1;
            System.out.println(HAND_ENTRY_INDEX_PREFIX + idx + HAND_ENTRY_INDEX_SUFFIX + u.getName()
                    + UNIT_STATS_PREFIX + u.getAtk() + UNIT_STATS_SEPARATOR + u.getDef() + UNIT_STATS_SUFFIX);
        }
    }
}
