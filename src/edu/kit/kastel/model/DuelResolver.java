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

    /**
     * Context for a single duel: game state, both units, blocked flag, and coordinates.
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

    private static void addDuelIntroLines(Unit attacker, Unit defender, Field fromField, Field toField, List<String> lines) {
        if (attacker.isBlocked()) {
            lines.add(attacker.getName() + " no longer blocks.");
            attacker.setBlocked(false);
        }
        int atkA = attacker.getAtk();
        int atkB = defender.getAtk();
        int defB = defender.getDef();
        String defenderDisplayName = defender.isRevealed() ? defender.getName() : HIDDEN_UNIT_DISPLAY_PLACEHOLDER;
        String defenderStats = defender.isRevealed() ? " (" + atkB + "/" + defB + ")" : "";
        lines.add(attacker.getName() + " (" + atkA + "/" + attacker.getDef() + ") attacks " + defenderDisplayName
                + defenderStats + " on " + toField.coordinate() + "!");
        if (!attacker.isRevealed()) {
            attacker.setRevealed(true);
            lines.add(attacker.getName() + " (" + attacker.getAtk() + "/" + attacker.getDef() + ") was flipped on "
                    + fromField.coordinate() + "!");
        }
        if (!defender.isRevealed()) {
            defender.setRevealed(true);
            lines.add(defender.getName() + " (" + defender.getAtk() + "/" + defender.getDef() + ") was flipped on "
                    + toField.coordinate() + "!");
        }
    }

    private static DuelResult resolveDuelVsKing(Game game, Team defenderTeam, int atkA, List<String> lines) {
        defenderTeam.takeDamage(atkA);
        lines.add(defenderTeam.getName() + " takes " + atkA + " damage!");
        Team winner = game.checkGameOver();
        if (winner != null) {
            lines.add(defenderTeam.getName() + "'s life points dropped to 0!");
            lines.add(winner.getName() + " wins!");
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
            lines.add(defender.getName() + " was eliminated!");
            lines.add(attacker.getName() + " moves to " + toField.coordinate() + ".");
        } else if (atkA < defB) {
            int dmg = defB - atkA;
            attackerTeam.takeDamage(dmg);
            lines.add(attackerTeam.getName() + " takes " + dmg + " damage!");
            Team winner = ctx.game().checkGameOver();
            if (winner != null) {
                lines.add(attackerTeam.getName() + "'s life points dropped to 0!");
                lines.add(winner.getName() + " wins!");
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
            lines.add(defender.getName() + " was eliminated!");
            lines.add(defenderTeam.getName() + " takes " + dmg + " damage!");
            fromField.removeUnit();
            gameBoard.placeUnit(ctx.toRow(), ctx.toCol(), attacker);
            attacker.setMovedThisTurn(true);
            lines.add(attacker.getName() + " moves to " + toField.coordinate() + ".");
            Team winner = ctx.game().checkGameOver();
            if (winner != null) {
                lines.add(defenderTeam.getName() + "'s life points dropped to 0!");
                lines.add(winner.getName() + " wins!");
            }
            return new DuelResult(lines, winner);
        }
        if (atkA < atkB) {
            int dmg = atkB - atkA;
            attackerTeam.takeDamage(dmg);
            lines.add(attacker.getName() + " was eliminated!");
            lines.add(attackerTeam.getName() + " takes " + dmg + " damage!");
            fromField.removeUnit();
            Team winner = ctx.game().checkGameOver();
            if (winner != null) {
                lines.add(attackerTeam.getName() + "'s life points dropped to 0!");
                lines.add(winner.getName() + " wins!");
            }
            return new DuelResult(lines, winner);
        }
        lines.add(defender.getName() + " was eliminated!");
        lines.add(attacker.getName() + " was eliminated!");
        fromField.removeUnit();
        toField.removeUnit();
        return new DuelResult(lines, null);
    }
}
