package edu.kit.kastel.model;

import edu.kit.kastel.commands.GameConfig;
import edu.kit.kastel.commands.UnitTemplate;
import edu.kit.kastel.exceptions.CannotDiscardException;
import edu.kit.kastel.exceptions.HandFullMustDiscardException;
import edu.kit.kastel.exceptions.InitializationException;
import edu.kit.kastel.exceptions.InvalidArgumentException;
import edu.kit.kastel.exceptions.InvalidHandIndexException;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Holds the core game state for Crown of Farmland (Krone von Ackerland): board,
 * teams, current turn, selection, and duel/merge logic. Initializes from
 * {@link edu.kit.kastel.commands.GameConfig} and provides methods for commands
 * and the AI opponent.
 *
 * @author usylb
 */
public class Game {

    /** Maximum non-King units per team on the board (6th unit is eliminated, A.1.5). */
    public static final int MAX_NON_KING_UNITS_ON_BOARD = 5;

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

    /** Separator between name parts in merged unit (A.1.9). */
    private static final String MERGED_NAME_PART_SEPARATOR = " ";

    private final GameConfig config;
    private final Random random;

    private final GameBoard gameBoard;
    private final Team team1;
    private final Team team2;

    private Team currentTeam;
    private Field selectedField;
    private boolean gameOver;

    /**
     * Constructs a new game from the given configuration. Creates the board, both
     * teams, and sets the current team to team 1. Does not fill decks or place units;
     * call {@link #initFromConfig()} to complete initialization.
     *
     * @param config the game configuration (seed, team names, units, decks, etc.)
     * @throws InvalidArgumentException if config is invalid
     */
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
     *
     * @throws InitializationException      if hand is unexpectedly full during init
     * @throws HandFullMustDiscardException if hand would be full when drawing
     */
    public void initFromConfig() throws InitializationException, HandFullMustDiscardException {
        fillDeck(team1, config.deckCountsTeam1());
        fillDeck(team2, config.deckCountsTeam2());

        team1.getDeck().shuffle(random);
        team2.getDeck().shuffle(random);

        drawToHand(team1, INITIAL_HAND_SIZE);
        drawToHand(team2, INITIAL_HAND_SIZE);

        gameBoard.placeUnit(KING_TEAM1_ROW, KING_COL, team1.getKing());
        gameBoard.placeUnit(KING_TEAM2_ROW, KING_COL, team2.getKing());

        // Zu Beginn jedes eigenen Zuges eine weitere (A.1.5): Team 1 zieht zu Beginn des ersten Zugs.
        if (!team1.getDeck().isEmpty()) {
            drawToHand(team1, CARDS_DRAWN_PER_TURN);
        }
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

    private void drawToHand(Team team, int count)
            throws InitializationException, HandFullMustDiscardException {
        for (int i = 0; i < count; i++) {
            Unit unit = team.getDeck().draw();
            if (unit != null) {
                unit.setTeam(team);
                if (team.getHand().isFull()) {
                    throw new InitializationException("Initial hand draw failed");
                }
                team.getHand().add(unit, team.getName());
            }
        }
    }

    /**
     * Returns the game board.
     * @return the game board
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * Returns team 1 (the player team).
     * @return team 1
     */
    public Team getTeam1() {
        return team1;
    }

    /**
     * Returns team 2 (the opponent team).
     * @return team 2
     */
    public Team getTeam2() {
        return team2;
    }

    /**
     * Returns the team that is currently allowed to act.
     * @return the current team
     */
    public Team getCurrentTeam() {
        return currentTeam;
    }

    /**
     * Returns the shared random number generator used for shuffling and AI decisions.
     *
     * @return the random instance
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Returns the currently selected field (for move, place, flip, block, show).
     *
     * @return the selected field, or null if none
     */
    public Field getSelectedField() {
        return selectedField;
    }

    /**
     * Sets the currently selected field.
     *
     * @param selectedField the field to select, or null to clear selection
     */
    public void setSelectedField(Field selectedField) {
        this.selectedField = selectedField;
    }

    /**
     * Returns whether the game has ended (one team has won).
     *
     * @return true if the game is over
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Returns the number of units (excluding the King) of the given team on the board.
     *
     * @param team the team
     * @return count of that team's non-King units on the board
     */
    public int getBoardCount(Team team) {
        int count = 0;
        for (int row = 0; row < GameBoard.SIZE; row++) {
            for (int col = 0; col < GameBoard.SIZE; col++) {
                Unit u = gameBoard.getField(row, col).getUnit();
                if (u != null && team.equals(u.getTeam()) && !u.isKing()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Ends the current turn: resets selection, optionally discards one card,
     * switches to the other team, and has the new team draw one card (or sets game over if deck empty).
     *
     * @param discardHandIndex 1-based index of card to discard, or null if not discarding
     * @return result for command output (discarded unit, yielding team, deck empty, winner)
     * @throws HandFullMustDiscardException if hand has 5 cards and no index given, or if draw fails because hand full
     * @throws CannotDiscardException      if index given but hand has fewer than 5 cards
     * @throws InvalidHandIndexException   if index is out of range
     * @throws InitializationException     if draw after turn switch fails
     */
    public YieldResult endTurn(Integer discardHandIndex)
            throws HandFullMustDiscardException, CannotDiscardException, InvalidHandIndexException,
            InitializationException {
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

    /**
     * Performs a duel between attacker (on fromField) and defender (on toField).
     * Reveals both, applies damage/removals, moves attacker if it wins.
     *
     * @param attacker        the attacking unit
     * @param defender       the defending unit or king
     * @param defenderBlocked whether the defender is in block state
     * @param fromRow        row of the attacker
     * @param fromCol        column of the attacker
     * @param toRow          row of the defender (target field)
     * @param toCol          column of the defender (target field)
     * @return duel result with lines to print and winner if game over
     */
    public DuelResult performDuel(Unit attacker, Unit defender, boolean defenderBlocked,
                                  int fromRow, int fromCol, int toRow, int toCol) {
        DuelResolver.DuelContext ctx = new DuelResolver.DuelContext(this, attacker, defender,
                defenderBlocked, fromRow, fromCol, toRow, toCol);
        return DuelResolver.performDuel(ctx);
    }

    /**
     * Called by {@link DuelResolver} after damage; sets gameOver and returns winner if a team is dead.
     *
     * @return the winning team if a team has 0 LP, otherwise null
     */
    Team checkGameOver() {
        if (team1.isDead()) {
            gameOver = true;
            return team2;
        }
        if (team2.isDead()) {
            gameOver = true;
            return team1;
        }
        return null;
    }

    /**
     * Creates a merged unit from A (moving/placing) and B (on field). Stats from MergeStats.
     * Name: Qualifier_B Qualifier_A Role_B. Revealed if both were revealed.
     *
     * @param unitA  the unit that is moving or being placed
     * @param unitB  the unit already on the field
     * @param stats  merge stats (ATK/DEF) from compatibility check
     * @return the new merged unit
     */
    public static Unit createMergedUnit(Unit unitA, Unit unitB, Compatibility.MergeStats stats) {
        String qualifier = unitB.getQualifier() + MERGED_NAME_PART_SEPARATOR + unitA.getQualifier();
        String role = unitB.getRole();
        BasicUnit merged = new BasicUnit(qualifier, role, stats.atk(), stats.def());
        merged.setRevealed(unitA.isRevealed() && unitB.isRevealed());
        return merged;
    }

    /**
     * Manhattan distance 1 (including en place).
     *
     * @param fromRow row of source field
     * @param fromCol column of source field
     * @param toRow   row of target field
     * @param toCol   column of target field
     * @return true if the two fields are adjacent or the same
     */
    public static boolean isAdjacent(int fromRow, int fromCol, int toRow, int toCol) {
        return Math.abs(fromRow - toRow) + Math.abs(fromCol - toCol) <= 1;
    }

    /**
     * True if (row, col) is adjacent to the given team's king (8 directions).
     *
     * @param team the team whose king position is used
     * @param row  row index of the field
     * @param col  column index of the field
     * @return true if the field is adjacent to that team's king
     */
    public boolean isAdjacentToKing(Team team, int row, int col) {
        int[] kingPos = getKingPosition(team);
        if (kingPos == null) {
            return false;
        }
        int kr = kingPos[0];
        int kc = kingPos[1];
        return Math.abs(row - kr) <= 1 && Math.abs(col - kc) <= 1;
    }

    /**
     * Parses "A1"-"G7" to row,col. Row 1 = index 0.
     *
     * @param coord field identifier A1 to G7
     * @return int array [row, col] or null if invalid
     */
    public static int[] parseField(String coord) {
        String u = coord.strip().toUpperCase();
        if (u.length() != 2 || u.charAt(0) < 'A' || u.charAt(0) > 'G' || u.charAt(1) < '1' || u.charAt(1) > '7') {
            return null;
        }
        int col = u.charAt(0) - 'A';
        int row = u.charAt(1) - '1';
        return new int[] { row, col };
    }

    /**
     * Returns current [row, col] of the given team's king, or null if not found.
     *
     * @param team the team whose king position is returned
     * @return int array [row, col] or null if not found
     */
    public int[] getKingPosition(Team team) {
        Unit king = team.getKing();
        for (int r = 0; r < GameBoard.SIZE; r++) {
            for (int c = 0; c < GameBoard.SIZE; c++) {
                Unit u = gameBoard.getField(r, c).getUnit();
                if (u == king) {
                    return new int[] { r, c };
                }
            }
        }
        return null;
    }
}
