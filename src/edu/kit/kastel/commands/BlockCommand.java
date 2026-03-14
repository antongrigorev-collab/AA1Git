package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.CannotBlockKingException;
import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.model.Unit;

import java.util.List;

/**
 * Command "block": the selected unit starts blocking (counts as a move). The unit
 * must belong to the current team, must not be the King, and must not have moved yet this turn.
 *
 * @author usylb
 */
public class BlockCommand extends Command {

    private static final String COMMAND_NAME = "block";
    private static final String COMMAND_REGEX = "(?i)^block$";
    private static final String BLOCK_MESSAGE_COORDINATE_PREFIX = " (";
    private static final String BLOCK_MESSAGE_SUFFIX = ") blocks!";

    /**
     * Creates the block command with the given handler.
     *
     * @param commandHandler the command handler
     */
    protected BlockCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Command.SelectedUnitContext ctx = getSelectedOwnUnitNotMoved(commandHandler);
        if (ctx == null) {
            return;
        }
        Unit unit = ctx.unit();
        if (unit.isKing()) {
            throw new CannotBlockKingException();
        }
        unit.setBlocked(true);
        unit.setMovedThisTurn(true);
        System.out.println(unit.getName() + BLOCK_MESSAGE_COORDINATE_PREFIX
                + ctx.selected().coordinate() + BLOCK_MESSAGE_SUFFIX);
        List<String> lines = ctx.game().getGameBoard().render(
                ctx.game().getSelectedField(), ctx.game().getTeam1(), ctx.game().getCurrentTeam());
        for (String line : lines) {
            System.out.println(line);
        }
        ShowCommand.printShow(ctx.game());
    }
}
