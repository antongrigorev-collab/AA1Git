package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.DuplicateArgumentException;
import edu.kit.kastel.exceptions.InvalidArgumentException;
import edu.kit.kastel.exceptions.StartupException;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses command-line arguments into key-value pairs. Each argument must be of
 * the form key=value; duplicate keys are rejected.
 *
 * @author Programmieren-Team
 */
public class ArgumentParser {

    /** Key-value separator in program arguments (e.g. seed=123). */
    private static final char ARGUMENT_KEY_VALUE_SEPARATOR = '=';

    private ArgumentParser() { }

    /**
     * Parses the given argument strings into a map. Each element must contain
     * exactly one '=' with non-empty key and value.
     *
     * @param args the raw command-line arguments
     * @return a map from key to value (no duplicates)
     * @throws InvalidArgumentException   if an argument has invalid format
     * @throws DuplicateArgumentException if a key appears more than once
     */
    public static Map<String, String> parse(String[] args) throws StartupException {
        Map<String, String> kv = new HashMap<>();
        for (String arg : args) {
            int eq = arg.indexOf(ARGUMENT_KEY_VALUE_SEPARATOR);
            if (eq <= 0 || eq == arg.length() - 1) {
                throw new InvalidArgumentException("Invalid argument: " + arg);
            }
            String key = arg.substring(0, eq);
            String value = arg.substring(eq + 1);
            if (kv.containsKey(key)) {
                throw new DuplicateArgumentException(key);
            }
            kv.put(key, value);
        }
        return kv;
    }

}
