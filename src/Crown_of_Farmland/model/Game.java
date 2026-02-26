package Crown_of_Farmland.model;

import Crown_of_Farmland.commands.GameConfig;
import Crown_of_Farmland.commands.UnitTemplate;
import Crown_of_Farmland.exceptions.InvalidArgumentException;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Game {

    private static final int INITIAL_HAND_SIZE = 4;
    private static final int KING_TEAM1_ROW = 0;
    private static final int KING_TEAM2_ROW = 6;
    private static final int KING_COL = 3;

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

    /**
     * Initializes decks from config (fill, shuffle), draws 4 cards into each hand,
     * and places both kings on the board (D1 and D7).
     */
    public void initFromConfig() {
        fillDeck(team1, config.deckCountsTeam1());
        fillDeck(team2, config.deckCountsTeam2());

        team1.getDeck().shuffle(random);
        team2.getDeck().shuffle(random);

        drawToHand(team1, INITIAL_HAND_SIZE);
        drawToHand(team2, INITIAL_HAND_SIZE);

        gameBoard.placeUnit(KING_TEAM1_ROW, KING_COL, team1.getKing());
        gameBoard.placeUnit(KING_TEAM2_ROW, KING_COL, team2.getKing());
    }

    private void fillDeck(Team team, List<Integer> counts) {
        var units = config.units();
        for (int i = 0; i < units.size(); i++) {
            UnitTemplate t = units.get(i);
            int n = counts.get(i);
            for (int k = 0; k < n; k++) {
                team.getDeck().add(new BasicUnit(t.qualifier(), t.role(), t.atk(), t.def()));
            }
        }
    }

    private void drawToHand(Team team, int count) {
        for (int i = 0; i < count; i++) {
            Unit unit = team.getDeck().draw();
            if (unit != null) {
                unit.setTeam(team);
                try {
                    team.getHand().add(unit, team.getName());
                } catch (Exception e) {
                    throw new IllegalStateException("Initial hand draw failed", e);
                }
            }
        }
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }

    public Random getRandom() {
        return random;
    }

    public Field getSelectedField() {
        return selectedField;
    }

    public void setSelectedField(Field selectedField) {
        this.selectedField = selectedField;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
