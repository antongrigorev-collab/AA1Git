package Crown_of_Farmland.exceptions;

/**
 * Fehler: Es wurde ein Index zum Abwerfen angegeben, obwohl die Hand nicht voll ist.
 *
 * Tritt auf wenn:
 * - yield mit einem Index ausgefuehrt wird, aber das Team weniger als 5 Einheiten auf der Hand haelt
 * - das Abwerfen ist nur noetig und erlaubt, wenn die Hand voll ist
 *
 * @author Programmieren-Team
 */
public class CannotDiscardException extends CommandException {

    /**
     * Erstellt eine neue CannotDiscardException.
     */
    public CannotDiscardException() {
        super("cannot discard: hand is not full");
    }
}
