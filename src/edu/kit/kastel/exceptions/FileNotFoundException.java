package edu.kit.kastel.exceptions;

/**
 * Thrown when a config file path (board, units, deck, deck1, deck2) does not exist.
 *
 * @author usylb
 */
public class FileNotFoundException extends StartupException {

    /**
     * Constructs a new FileNotFoundException.
     *
     * @param filePath the path that was not found
     */
    public FileNotFoundException(String filePath) {
        super("file not found: " + filePath);
    }
}
