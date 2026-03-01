package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.GameException;
import Crown_of_Farmland.model.Game;
import Crown_of_Farmland.model.Team;

import java.util.List;

public class StateCommand extends Command {

    private static final String COMMAND_NAME = "state";
    private static final String COMMAND_REGEX = "(?i)^state$";
    private static final int STATE_LINE_LENGTH = 31;
    private static final int INITIAL_LP = 8000;
    private static final int DECK_SIZE = 40;
    private static final int MAX_BOARD_UNITS = 5;

    protected StateCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Game game = commandHandler.getGame();
        if (game == null) {
            return;
        }
        Team t1 = game.getTeam1();
        Team t2 = game.getTeam2();
        String prefix = "  ";
        printStateLine(prefix, t1.getName(), t2.getName());
        printStateLine(prefix, t1.getLifePoints() + "/" + INITIAL_LP + " LP",
                t2.getLifePoints() + "/" + INITIAL_LP + " LP");
        printStateLine(prefix, "DC: " + t1.getDeck().size() + "/" + DECK_SIZE,
                "DC: " + t2.getDeck().size() + "/" + DECK_SIZE);
        printStateLine(prefix, "BC: " + game.getBoardCount(t1) + "/" + MAX_BOARD_UNITS,
                "BC: " + game.getBoardCount(t2) + "/" + MAX_BOARD_UNITS);
        List<String> boardLines = game.getGameBoard().render(game.getSelectedField(), game.getTeam1(), game.getCurrentTeam());
        for (String line : boardLines) {
            System.out.println(line);
        }
        if (game.getSelectedField() != null) {
            ShowCommand.printShow(game);
        }
    }

    private void printStateLine(String prefix, String left, String right) {
        int padLen = STATE_LINE_LENGTH - prefix.length() - left.length() - right.length();
        String pad = padLen > 0 ? " ".repeat(padLen) : "";
        System.out.println(prefix + left + pad + right);
    }
}
