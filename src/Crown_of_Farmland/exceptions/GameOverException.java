package Crown_of_Farmland.exceptions;

/**
 * Fehler: Das Spiel ist bereits beendet und es koennen keine weiteren Befehle ausgefuehrt werden.
 *
 * Tritt auf wenn:
 * - ein Team bereits gewonnen hat und weitere Spielbefehle eingegeben werden
 * - die Lebenspunkte eines Teams auf 0 gesunken sind
 * - der Stapel eines Teams leer ist und es nicht nachziehen kann
 *
 * @author Programmieren-Team
 */
public class GameOverException extends CommandException {

    /**
     * Erstellt eine neue GameOverException.
     */
    public GameOverException() {
        super("the game is already over");
    }
}
