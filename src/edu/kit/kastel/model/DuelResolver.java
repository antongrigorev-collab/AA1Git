package edu.kit.kastel.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves duels (A.1.4) for {@link Game}. Used by {@link Game#performDuel} to apply
 * damage, eliminations and movement; produces output lines and game-over winner.
 *
 * @author usylb
 */
final class DuelResolver {

    /** Placeholder for unrevealed unit name in duel output (A.5.6). */
    private static final String HIDDEN_UNIT_DISPLAY_PLACEHOLDER = "???";

    /** Format: unit no longer blocks. */
    private static final String FORMAT_NO_LONGER_BLOCKS = "%s no longer blocks.";

    /** Format: attacker (atk/def) attacks defender [stats] on field. */
    private static final String FORMAT_ATTACKS_ON_FIELD = "%s (%d/%d) attacks %s%s on %s!";

    /** Format: stats in parentheses for display. */
    private static final String FORMAT_STATS = " (%d/%d)";

    /** Format: unit was flipped on field. */
    private static final String FORMAT_WAS_FLIPPED_ON = "%s (%d/%d) was flipped on %s!";

    /** Format: team takes damage. */
    private static final String FORMAT_TAKES_DAMAGE = "%s takes %d damage!";

    /** Format: team's life points dropped to 0. */
    private static final String FORMAT_LIFE_POINTS_ZERO = "%s's life points dropped to 0!";

    /** Format: team wins. */
    private static final String FORMAT_WINS = "%s wins!";

    /** Format: unit was eliminated. */
    private static final String FORMAT_WAS_ELIMINATED = "%s was eliminated!";

    /** Format: unit moves to field. */
    private static final String FORMAT_MOVES_TO = "%s moves to %s.";

    /**
     * Context for a single duel: game state, both units, blocked flag, and coordinates.
     *
     * @param game           the game state
     * @param attacker       the attacking unit
     * @param defender       the defending unit
     * @param defenderBlocked whether the defender is blocked
     * @param fromRow        row of the source field
     * @param fromCol        column of the source field
     * @param toRow          row of the target field
     * @param toCol          column of the target field
     */
    record DuelContext(Game game, Unit attacker, Unit defender, boolean defenderBlocked,
                       int fromRow, int fromCol, int toRow, int toCol) { }

    private DuelResolver() { }

    /**
     * Performs a duel between attacker (on fromField) and defender (on toField).
     * Reveals both, applies damage/removals, moves attacker if it wins.
     *
     * @param ctx duel context (game, units, coordinates)
     * @return duel result with lines to print and winner if game over
     */
    static DuelResult performDuel(DuelContext ctx) {
        List<String> lines = new ArrayList<>();
        GameBoard gameBoard = ctx.game().getGameBoard();
        Field fromField = gameBoard.getField(ctx.fromRow(), ctx.fromCol());
        Field toField = gameBoard.getField(ctx.toRow(), ctx.toCol());
        addDuelIntroLines(ctx.attacker(), ctx.defender(), fromField, toField, lines);
        if (ctx.defender().isKing()) {
            return resolveDuelVsKing(ctx.game(), ctx.defender().getTeam(), ctx.attacker().getAtk(), lines);
        }
        if (ctx.defenderBlocked()) {
            return resolveBlockedDuel(ctx, fromField, toField, lines);
        }
        return resolveStandardDuel(ctx, fromField, toField, lines);
    }

    private static void addDuelIntroLines(Unit attacker, Unit defender, Field fromField, Field toField,
                                          List<String> lines) {
        if (attacker.isBlocked()) {
            lines.add(String.format(FORMAT_NO_LONGER_BLOCKS, attacker.getName()));
            attacker.setBlocked(false);
        }
        int atkA = attacker.getAtk();
        int atkB = defender.getAtk();
        int defB = defender.getDef();
        String defenderDisplayName;
        String defenderStats;
        if (defender.isKing()) {
            defenderDisplayName = defender.getName();
            defenderStats = "";
        } else if (defender.isRevealed()) {
            defenderDisplayName = defender.getName();
            defenderStats = String.format(FORMAT_STATS, atkB, defB);
        } else {
            defenderDisplayName = HIDDEN_UNIT_DISPLAY_PLACEHOLDER;
            defenderStats = "";
        }
        lines.add(String.format(FORMAT_ATTACKS_ON_FIELD, attacker.getName(), atkA, attacker.getDef(),
                defenderDisplayName, defenderStats, toField.coordinate()));
        if (!attacker.isRevealed()) {
            attacker.setRevealed(true);
            lines.add(String.format(FORMAT_WAS_FLIPPED_ON, attacker.getName(), attacker.getAtk(), attacker.getDef(),
                    fromField.coordinate()));
        }
        if (!defender.isRevealed()) {
            defender.setRevealed(true);
            lines.add(String.format(FORMAT_WAS_FLIPPED_ON, defender.getName(), defender.getAtk(), defender.getDef(),
                    toField.coordinate()));
        }
    }

    private static DuelResult resolveDuelVsKing(Game game, Team defenderTeam, int atkA, List<String> lines) {
        defenderTeam.takeDamage(atkA);
        lines.add(String.format(FORMAT_TAKES_DAMAGE, defenderTeam.getName(), atkA));
        Team winner = game.checkGameOver();
        if (winner != null) {
            lines.add(String.format(FORMAT_LIFE_POINTS_ZERO, defenderTeam.getName()));
            lines.add(String.format(FORMAT_WINS, winner.getName()));
        }
        return new DuelResult(lines, winner);
    }

    private static DuelResult resolveBlockedDuel(DuelContext ctx, Field fromField, Field toField,
                                                 List<String> lines) {
        GameBoard gameBoard = ctx.game().getGameBoard();
        Unit attacker = ctx.attacker();
        Unit defender = ctx.defender();
        Team attackerTeam = attacker.getTeam();
        int atkA = attacker.getAtk();
        int defB = defender.getDef();
        if (atkA > defB) {
            toField.removeUnit();
            fromField.removeUnit();
            gameBoard.placeUnit(ctx.toRow(), ctx.toCol(), attacker);
            attacker.setMovedThisTurn(true);
            lines.add(String.format(FORMAT_WAS_ELIMINATED, defender.getName()));
            lines.add(String.format(FORMAT_MOVES_TO, attacker.getName(), toField.coordinate()));
        } else if (atkA < defB) {
            int dmg = defB - atkA;
            attackerTeam.takeDamage(dmg);
            lines.add(String.format(FORMAT_TAKES_DAMAGE, attackerTeam.getName(), dmg));
            Team winner = ctx.game().checkGameOver();
            if (winner != null) {
                lines.add(String.format(FORMAT_LIFE_POINTS_ZERO, attackerTeam.getName()));
                lines.add(String.format(FORMAT_WINS, winner.getName()));
            }
            return new DuelResult(lines, winner);
        }
        return new DuelResult(lines, null);
    }

    private static DuelResult resolveStandardDuel(DuelContext ctx, Field fromField, Field toField,
                                                   List<String> lines) {
        GameBoard gameBoard = ctx.game().getGameBoard();
        Unit attacker = ctx.attacker();
        Unit defender = ctx.defender();
        Team attackerTeam = attacker.getTeam();
        Team defenderTeam = defender.getTeam();
        int atkA = attacker.getAtk();
        int atkB = defender.getAtk();
        if (atkA > atkB) {
            int dmg = atkA - atkB;
            defenderTeam.takeDamage(dmg);
            lines.add(String.format(FORMAT_WAS_ELIMINATED, defender.getName()));
            lines.add(String.format(FORMAT_TAKES_DAMAGE, defenderTeam.getName(), dmg));
            fromField.removeUnit();
            gameBoard.placeUnit(ctx.toRow(), ctx.toCol(), attacker);
            attacker.setMovedThisTurn(true);
            lines.add(String.format(FORMAT_MOVES_TO, attacker.getName(), toField.coordinate()));
            Team winner = ctx.game().checkGameOver();
            if (winner != null) {
                lines.add(String.format(FORMAT_LIFE_POINTS_ZERO, defenderTeam.getName()));
                lines.add(String.format(FORMAT_WINS, winner.getName()));
            }
            return new DuelResult(lines, winner);
        }
        if (atkA < atkB) {
            int dmg = atkB - atkA;
            attackerTeam.takeDamage(dmg);
            lines.add(String.format(FORMAT_WAS_ELIMINATED, attacker.getName()));
            lines.add(String.format(FORMAT_TAKES_DAMAGE, attackerTeam.getName(), dmg));
            fromField.removeUnit();
            Team winner = ctx.game().checkGameOver();
            if (winner != null) {
                lines.add(String.format(FORMAT_LIFE_POINTS_ZERO, attackerTeam.getName()));
                lines.add(String.format(FORMAT_WINS, winner.getName()));
            }
            return new DuelResult(lines, winner);
        }
        lines.add(String.format(FORMAT_WAS_ELIMINATED, defender.getName()));
        lines.add(String.format(FORMAT_WAS_ELIMINATED, attacker.getName()));
        fromField.removeUnit();
        toField.removeUnit();
        return new DuelResult(lines, null);
    }
}
