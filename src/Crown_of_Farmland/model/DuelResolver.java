package Crown_of_Farmland.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves duels (A.1.4) for {@link Game}. Used by {@link Game#performDuel} to apply
 * damage, eliminations and movement; produces output lines and game-over winner.
 */
final class DuelResolver {

    /** Placeholder for unrevealed unit name in duel output (A.5.6). */
    private static final String HIDDEN_UNIT_DISPLAY_PLACEHOLDER = "???";

    private DuelResolver() { }

    /**
     * Performs a duel between attacker (on fromField) and defender (on toField).
     * Reveals both, applies damage/removals, moves attacker if it wins.
     *
     * @param game            game state (board, teams, checkGameOver)
     * @param attacker        attacking unit
     * @param defender        defending unit or king
     * @param defenderBlocked whether defender is in block
     * @param fromRow         attacker row
     * @param fromCol         attacker col
     * @param toRow           defender row
     * @param toCol           defender col
     * @return duel result with lines to print and winner if game over
     */
    static DuelResult performDuel(Game game, Unit attacker, Unit defender, boolean defenderBlocked,
                                  int fromRow, int fromCol, int toRow, int toCol) {
        List<String> lines = new ArrayList<>();
        GameBoard gameBoard = game.getGameBoard();
        Field fromField = gameBoard.getField(fromRow, fromCol);
        Field toField = gameBoard.getField(toRow, toCol);
        addDuelIntroLines(attacker, defender, fromField, toField, lines);
        if (defender.isKing()) {
            return resolveDuelVsKing(game, defender.getTeam(), attacker.getAtk(), lines);
        }
        if (defenderBlocked) {
            return resolveBlockedDuel(game, attacker, defender, fromField, toField, toRow, toCol, lines);
        }
        return resolveStandardDuel(game, attacker, defender, fromField, toField, toRow, toCol, lines);
    }

    private static void addDuelIntroLines(Unit attacker, Unit defender, Field fromField, Field toField,
                                          List<String> lines) {
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

    private static DuelResult resolveBlockedDuel(Game game, Unit attacker, Unit defender,
                                                 Field fromField, Field toField,
                                                 int toRow, int toCol, List<String> lines) {
        GameBoard gameBoard = game.getGameBoard();
        Team attackerTeam = attacker.getTeam();
        int atkA = attacker.getAtk();
        int defB = defender.getDef();
        if (atkA > defB) {
            toField.removeUnit();
            fromField.removeUnit();
            gameBoard.placeUnit(toRow, toCol, attacker);
            attacker.setMovedThisTurn(true);
            lines.add(defender.getName() + " was eliminated!");
            lines.add(attacker.getName() + " moves to " + toField.coordinate() + ".");
        } else if (atkA < defB) {
            int dmg = defB - atkA;
            attackerTeam.takeDamage(dmg);
            lines.add(attackerTeam.getName() + " takes " + dmg + " damage!");
            Team winner = game.checkGameOver();
            if (winner != null) {
                lines.add(attackerTeam.getName() + "'s life points dropped to 0!");
                lines.add(winner.getName() + " wins!");
            }
            return new DuelResult(lines, winner);
        }
        return new DuelResult(lines, null);
    }

    private static DuelResult resolveStandardDuel(Game game, Unit attacker, Unit defender,
                                                   Field fromField, Field toField,
                                                   int toRow, int toCol, List<String> lines) {
        GameBoard gameBoard = game.getGameBoard();
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
            gameBoard.placeUnit(toRow, toCol, attacker);
            attacker.setMovedThisTurn(true);
            lines.add(attacker.getName() + " moves to " + toField.coordinate() + ".");
            Team winner = game.checkGameOver();
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
            Team winner = game.checkGameOver();
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
