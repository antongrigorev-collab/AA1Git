package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.EmptyFieldException;
import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.exceptions.NoFieldSelectedException;
import edu.kit.kastel.exceptions.NotOwnUnitException;
import edu.kit.kastel.exceptions.UnitAlreadyMovedException;
import edu.kit.kastel.model.Field;
import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.Unit;


/**
 * Base class for console commands in Crown of Farmland. Each command has a name,
 * a regex pattern for matching user input, and an execute method that receives
 * pre-split arguments.
 *
 * @author usylb
 */
public abstract class Command {

    /**
     * Context after validating: game exists and not over, selected field, own unit not yet moved.
     *
     * @param game     the game instance
     * @param selected the selected field
     * @param unit     the unit on the selected field
     */
    public record SelectedUnitContext(Game game, Field selected, Unit unit) { }

    /** The command handler that owns this command and provides game access. */
    protected final CommandHandler commandHandler;


    private final String commandName;
    private final String commandRegex;

    /**
     * Constructs a command with the given name, regex, and handler.
     *
     * @param commandName   the command name (e.g. "quit")
     * @param commandRegex  the regex the user input must match (may equal the name)
     * @param commandHandler the command handler
     */
    protected Command(String commandName, String commandRegex, CommandHandler commandHandler) {
        this.commandName = commandName;
        this.commandRegex = commandRegex;
        this.commandHandler = commandHandler;
    }

    /**
     * Returns the command name.
     *
     * @return the command name
     */
    public final String getCommandName() {
        return commandName;
    }

    /**
     * Returns the regex pattern used to match user input for this command.
     *
     * @return the command regex pattern
     */
    public final String getCommandRegex() {
        return commandRegex;
    }

    /**
     * Executes this command with the given pre-split arguments.
     *
     * @param commandArguments the arguments for the command (may include optional parts)
     * @throws GameException if the command fails (invalid arguments or invalid game state)
     */
    public abstract void execute(String[] commandArguments) throws GameException;

    /**
     * Validates that the handler has a running game, a selected non-empty field with an own unit
     * that has not moved this turn. Returns null if game is null or game over; throws on invalid
     * selection; otherwise returns the context.
     *
     * @param commandHandler the command handler
     * @return the context, or null if game is null or game over
     * @throws GameException             if the validation fails
     * @throws NoFieldSelectedException  if no field is selected
     * @throws EmptyFieldException        if the selected field is empty
     * @throws NotOwnUnitException       if the unit does not belong to the current team
     * @throws UnitAlreadyMovedException if the unit has already moved this turn
     */
    protected static SelectedUnitContext getSelectedOwnUnitNotMoved(CommandHandler commandHandler)
            throws GameException {
        Game game = commandHandler.getGame();
        if (game == null || game.isGameOver()) {
            return null;
        }
        Field selected = game.getSelectedField();
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
        return new SelectedUnitContext(game, selected, unit);
    }
}
