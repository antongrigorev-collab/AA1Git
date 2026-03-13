package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.CannotDiscardException;
import edu.kit.kastel.exceptions.HandFullMustDiscardException;
import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.exceptions.InitializationException;
import edu.kit.kastel.exceptions.InvalidCommandArgumentsException;
import edu.kit.kastel.exceptions.InvalidHandIndexException;
import edu.kit.kastel.model.AIPlayer;
import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.YieldResult;

/**
 * Command "yield" [idx]: ends the current turn. If the hand has 5 cards, a 1-based
 * hand index must be given to discard one card. Switches to the other team, draws one
 * card for the new team (or ends the game if deck empty), then runs the AI turn if
 * the new team is team 2.
 *
 * @author usylb
 */
public class YieldCommand extends Command {

    private static final String COMMAND_NAME = "yield";
    private static final String COMMAND_REGEX = "(?i)^yield(\\s+\\d+)?$";
    private static final String INVALID_HAND_INDEX_PREFIX = "invalid hand index: ";
    private static final String DISCARDED_SUFFIX = " discarded ";
    private static final String STATS_SUFFIX = ").";
    private static final String STATS_PREFIX = " (";
    private static final String IT_IS_TURN = "It is ";
    private static final String TURN_SUFFIX = "'s turn!";
    private static final String NO_CARDS_IN_DECK = " has no cards left in the deck!";
    private static final String WINS = " wins!";

    /** Separator between ATK and DEF in stats output. */
    private static final String STATS_ATK_DEF_SEPARATOR = "/";

    private static final int FIRST_ARGUMENT_INDEX = 0;

    /**
     * Creates the yield command with the given handler.
     *
     * @param commandHandler the command handler
     */
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
        if (commandArguments.length > FIRST_ARGUMENT_INDEX) {
            try {
                discardIndex = Integer.parseInt(commandArguments[FIRST_ARGUMENT_INDEX]);
            } catch (NumberFormatException e) {
                throw new InvalidCommandArgumentsException(
                        INVALID_HAND_INDEX_PREFIX + commandArguments[FIRST_ARGUMENT_INDEX]);
            }
        }

        try {
            YieldResult result = game.endTurn(discardIndex);

            if (result.discarded() != null) {
                Unit u = result.discarded();
                System.out.println(result.yieldingTeam().getName() + DISCARDED_SUFFIX
                        + u.getName() + STATS_PREFIX + u.getAtk() + STATS_ATK_DEF_SEPARATOR + u.getDef() + STATS_SUFFIX);
            }
            System.out.println(IT_IS_TURN + game.getCurrentTeam().getName() + TURN_SUFFIX);
            if (result.newTeamDeckEmpty()) {
                System.out.println(game.getCurrentTeam().getName() + NO_CARDS_IN_DECK);
            }
            if (result.winner() != null) {
                System.out.println(result.winner().getName() + WINS);
                ShowCommand.printBoard(game);
            }
        } catch (HandFullMustDiscardException | CannotDiscardException | InvalidHandIndexException
                | InitializationException e) {
            System.out.println(e.getFormattedMessage());
        }
        if (game != null && !game.isGameOver() && game.getCurrentTeam().equals(game.getTeam2())) {
            AIPlayer.runTurn(game);
        }
    }
}
