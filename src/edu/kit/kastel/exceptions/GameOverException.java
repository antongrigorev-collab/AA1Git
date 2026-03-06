package edu.kit.kastel.exceptions;

/**
 * Thrown when a command is used after the game has ended (e.g. a team has won or a deck is empty).
 *
 * @author usylb
 */
public class GameOverException extends CommandException {

    private static final String GAME_ALREADY_OVER_MESSAGE = "the game is already over";

    /**
     * Constructs a new GameOverException.
     */
    public GameOverException() {
        super(GAME_ALREADY_OVER_MESSAGE);
    }
}
