package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.CannotBlockKingException;
import Crown_of_Farmland.exceptions.GameException;
import Crown_of_Farmland.exceptions.MustDiscardException;
import Crown_of_Farmland.exceptions.NoFieldSelectedException;
import Crown_of_Farmland.exceptions.NotOwnUnitException;
import Crown_of_Farmland.exceptions.UnitAlreadyMovedException;
import Crown_of_Farmland.model.Game;
import Crown_of_Farmland.model.Unit;

import java.util.List;

public class BlockCommand extends Command {

    private static final String COMMAND_NAME = "block";
    private static final String COMMAND_REGEX = "(?i)^block$";

    protected BlockCommand(CommandHandler commandHandler) {
        super(COMMAND_NAME, COMMAND_REGEX, commandHandler);
    }

    @Override
    public void execute(String[] commandArguments) throws GameException {
        Game game = commandHandler.getGame();
        if (game == null || game.isGameOver()) {
            return;
        }
        if (game.getCurrentTeam().getHand().size() == 5) {
            throw new MustDiscardException("block");
        }
        var selected = game.getSelectedField();
        if (selected == null || selected.isEmpty()) {
            throw new NoFieldSelectedException();
        }
        Unit unit = selected.getUnit();
        if (!unit.getTeam().equals(game.getCurrentTeam())) {
            throw new NotOwnUnitException();
        }
        if (unit.isKing()) {
            throw new CannotBlockKingException();
        }
        if (unit.hasMovedThisTurn()) {
            throw new UnitAlreadyMovedException(unit.getName());
        }
        unit.setBlocked(true);
        unit.setMovedThisTurn(true);
        System.out.println(unit.getName() + " (" + selected.coordinate() + ") blocks!");
        List<String> lines = game.getGameBoard().render(game.getSelectedField(), game.getCurrentTeam());
        for (String line : lines) {
            System.out.println(line);
        }
        ShowCommand.printShow(game);
    }
}
