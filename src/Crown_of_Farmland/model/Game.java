package Crown_of_Farmland.model;

import Crown_of_Farmland.commands.GameConfig;
import Crown_of_Farmland.exceptions.InvalidArgumentException;

import java.util.Objects;
import java.util.Random;

public class Game {

    private final GameConfig config;
    private final Random random;

    private final GameBoard gameBoard;
    private final Team team1;
    private final Team team2;

    private Team currentTeam;
    private Field selectedField;
    private boolean gameOver;


    public Game(GameConfig config) throws InvalidArgumentException {
        this.config = Objects.requireNonNull(config);
        this.random = new Random(config.seed());

        this.gameBoard = new GameBoard(config.symbolSet(), config.verbosityMode());
        this.team1 = Team.createTeam1(config.team1Name());
        this.team2 = Team.createTeam2(config.team2Name());

        this.currentTeam = team1;
        this.selectedField = null;
        this.gameOver = false;

    }
}
