package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.EmptyFieldException;
import Crown_of_Farmland.exceptions.FlipAlreadyFlippedException;
import Crown_of_Farmland.exceptions.GameException;
import Crown_of_Farmland.exceptions.NoFieldSelectedException;
import Crown_of_Farmland.exceptions.NotOwnUnitException;
import Crown_of_Farmland.exceptions.UnitAlreadyMovedException;
import Crown_of_Farmland.model.Game;
import Crown_of_Farmland.model.Unit;

import java.util.List;

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
