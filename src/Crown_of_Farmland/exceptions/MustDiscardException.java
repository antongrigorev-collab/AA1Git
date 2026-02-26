package Crown_of_Farmland.exceptions;

/**
 * Fehler: Das Team muss zuerst eine Einheit abwerfen, bevor es andere Aktionen ausfuehren kann.
 *
 * Tritt auf wenn:
 * - nach einem fehlgeschlagenen yield-Befehl (Hand voll, kein Index) versucht wird,
 *   einen anderen Befehl als hand oder yield auszufuehren
 * - in der Beispielinteraktion: "cannot place a card, you must discard!"
 *
 * @author Programmieren-Team
 */
public class MustDiscardException extends CommandException {

    /**
     * Erstellt eine neue MustDiscardException.
     *
     * @param attemptedCommand der versuchte Befehl
     */
    public MustDiscardException(String attemptedCommand) {
        super("cannot " + attemptedCommand + ", you must discard!");
    }
}
