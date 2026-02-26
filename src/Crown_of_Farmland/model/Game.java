package Crown_of_Farmland.model;

import Crown_of_Farmland.commands.GameConfig;
import Crown_of_Farmland.commands.UnitTemplate;
import Crown_of_Farmland.exceptions.CannotDiscardException;
import Crown_of_Farmland.exceptions.HandFullMustDiscardException;
import Crown_of_Farmland.exceptions.InvalidArgumentException;
import Crown_of_Farmland.exceptions.InvalidHandIndexException;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Game {

    /**
     * Result of ending the current turn (yield). Used for command output.
     *
     * @param discarded       unit discarded from hand, or null if none
     * @param yieldingTeam   team that ended the turn (the one that had the turn before switch)
     * @param newTeamDeckEmpty whether the new team's deck was empty and could not draw
     * @param winner         winning team if game over, otherwise null
     */
    public record YieldResult(Unit discarded, Team yieldingTeam,
                             boolean newTeamDeckEmpty, Team winner) { }

    private static final int INITIAL_HAND_SIZE = 4;
    private static final int MAX_HAND_SIZE = 5;
    private static final int CARDS_DRAWN_PER_TURN = 1;
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

    /**
     * Ends the current turn: resets selection, optionally discards one card,
     * switches to the other team, and has the new team draw one card (or sets game over if deck empty).
     *
     * @param discardHandIndex 1-based index of card to discard, or null if not discarding
     * @return result for command output (discarded unit, yielding team, deck empty, winner)
     * @throws HandFullMustDiscardException if hand has 5 cards and no index given
     * @throws CannotDiscardException      if index given but hand has fewer than 5 cards
     * @throws InvalidHandIndexException   if index is out of range
     */
    public YieldResult endTurn(Integer discardHandIndex)
            throws HandFullMustDiscardException, CannotDiscardException, InvalidHandIndexException {
        Team yieldingTeam = currentTeam;
        int handSize = yieldingTeam.getHand().size();

        if (handSize == MAX_HAND_SIZE && discardHandIndex == null) {
            throw new HandFullMustDiscardException(yieldingTeam.getName());
        }
        if (discardHandIndex != null && handSize < MAX_HAND_SIZE) {
            throw new CannotDiscardException();
        }
        if (discardHandIndex != null && (discardHandIndex < 1 || discardHandIndex > handSize)) {
            throw new InvalidHandIndexException(discardHandIndex);
        }

        selectedField = null;

        Unit discarded = null;
        if (discardHandIndex != null) {
            discarded = yieldingTeam.getHand().remove(discardHandIndex);
        }

        Team nextTeam = yieldingTeam == team1 ? team2 : team1;
        currentTeam = nextTeam;

        resetTurnStateFor(nextTeam);

        boolean newTeamDeckEmpty = nextTeam.getDeck().isEmpty();
        Team winner = null;
        if (newTeamDeckEmpty) {
            gameOver = true;
            winner = yieldingTeam;
        } else {
            drawToHand(nextTeam, CARDS_DRAWN_PER_TURN);
        }

        return new YieldResult(discarded, yieldingTeam, newTeamDeckEmpty, winner);
    }

    private void resetTurnStateFor(Team team) {
        team.getHand().resetTurn();
        for (int row = 0; row < GameBoard.SIZE; row++) {
            for (int col = 0; col < GameBoard.SIZE; col++) {
                Unit u = gameBoard.getField(row, col).getUnit();
                if (u != null && team.equals(u.getTeam())) {
                    u.setMovedThisTurn(false);
                }
            }
        }
    }
}
