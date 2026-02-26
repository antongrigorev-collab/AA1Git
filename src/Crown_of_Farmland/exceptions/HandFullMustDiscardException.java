package Crown_of_Farmland.exceptions;

/**
 * Fehler: Die Hand ist voll (5 Einheiten) und es muss eine Einheit abgeworfen werden.
 *
 * Tritt auf wenn:
 * - yield ohne Index ausgefuehrt wird, aber das Team 5 Einheiten auf der Hand haelt
 * - in der Beispielinteraktion: "Player's hand is full!"
 *
 * @author Programmieren-Team
 */
public class HandFullMustDiscardException extends CommandException {

    /**
     * Erstellt eine neue HandFullMustDiscardException.
     *
     * @param teamName der Name des betroffenen Teams
     */
    public HandFullMustDiscardException(String teamName) {
        super(teamName + "'s hand is full!");
    }
}
