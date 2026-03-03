package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.EmptyFieldException;
import edu.kit.kastel.exceptions.FlipAlreadyFlippedException;
import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.exceptions.NoFieldSelectedException;
import edu.kit.kastel.exceptions.NotOwnUnitException;
import edu.kit.kastel.exceptions.UnitAlreadyMovedException;
import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.Unit;

import java.util.List;

/**
 * Command "flip": reveals the selected unit (shows name and ATK/DEF to both teams).
 * The unit must belong to the current team, must not have moved this turn, and must
 * not already be revealed. Does not count as a move.
 */
public class FlipCommand extends Command {

    private static final String COMMAND_NAME = "flip";
    private static final String COMMAND_REGEX = "(?i)^flip$";

    protected FlipCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Game game = commandHandler.getGame();
        if (game == null || game.isGameOver()) {
            return;
        }
        var selected = game.getSelectedField();
        if (selected == null) {
            throw new NoFieldSelectedException();
        }
        if (selected.isEmpty()) {
            throw new EmptyFieldException(selected.coordinate());
        }
        Unit unit = selected.getUnit();
        if (!unit.getTeam().equals(game.getCurrentTeam())) {
            throw new NotOwnUnitException();
        }
        if (unit.hasMovedThisTurn()) {
            throw new UnitAlreadyMovedException(unit.getName());
        }
        if (unit.isRevealed()) {
            throw new FlipAlreadyFlippedException(unit.getName());
        }
        unit.setRevealed(true);
        System.out.println(unit.getName() + " (" + unit.getAtk() + "/" + unit.getDef() + ") was flipped on "
                + selected.coordinate() + "!");
        List<String> lines = game.getGameBoard().render(game.getSelectedField(), game.getTeam1(), game.getCurrentTeam());
        for (String line : lines) {
            System.out.println(line);
        }
        ShowCommand.printShow(game);
    }
}
