package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.CannotDiscardException;
import Crown_of_Farmland.exceptions.HandFullMustDiscardException;
import Crown_of_Farmland.exceptions.GameException;
import Crown_of_Farmland.exceptions.InvalidCommandArgumentsException;
import Crown_of_Farmland.exceptions.InvalidHandIndexException;
import Crown_of_Farmland.model.AIPlayer;
import Crown_of_Farmland.model.Game;
import Crown_of_Farmland.model.Unit;

public class YieldCommand extends Command {

    private static final String COMMAND_NAME = "yield";
    private static final String COMMAND_REGEX = "(?i)^yield(\\s+\\d+)?$";

    protected YieldCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Game game = commandHandler.getGame();
        if (game == null) {
            return;
        }
        if (game.isGameOver()) {
            return;
        }

        Integer discardIndex = null;
        if (commandArguments.length > 0) {
            try {
                discardIndex = Integer.parseInt(commandArguments[0]);
            } catch (NumberFormatException e) {
                throw new InvalidCommandArgumentsException("invalid hand index: " + commandArguments[0]);
            }
        }

        try {
            Game.YieldResult result = game.endTurn(discardIndex);

            if (result.discarded() != null) {
                Unit u = result.discarded();
                System.out.println(result.yieldingTeam().getName() + " discarded "
                        + u.getName() + " (" + u.getAtk() + "/" + u.getDef() + ").");
            }
            System.out.println("It is " + game.getCurrentTeam().getName() + "'s turn!");
            if (result.newTeamDeckEmpty()) {
                System.out.println(game.getCurrentTeam().getName() + " has no cards left in the deck!");
            }
            if (result.winner() != null) {
                System.out.println(result.winner().getName() + " wins!");
            }
        } catch (HandFullMustDiscardException | CannotDiscardException | InvalidHandIndexException e) {
            System.out.println(e.getFormattedMessage());
        }
        if (game != null && !game.isGameOver() && game.getCurrentTeam().equals(game.getTeam2())) {
            AIPlayer.runTurn(game);
        }
    }
}
