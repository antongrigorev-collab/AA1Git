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
 * @author usylb
 */
public final class ArgumentParser {

    /** Key-value separator in program arguments (e.g. seed=123). */
    private static final char ARGUMENT_KEY_VALUE_SEPARATOR = '=';

    /** Minimal index at which a key may end (at least one character). */
    private static final int MIN_KEY_END_INDEX = 1;

    /** Minimal required length of the value part after the '=' separator. */
    private static final int MIN_VALUE_LENGTH = 1;

    /** Start index for key substring (from beginning of argument). */
    private static final int INDEX_KEY_START = 0;

    /** Offset after '=' to start of value substring. */
    private static final int VALUE_START_OFFSET = 1;

    /** Prefix for invalid-argument error messages. */
    private static final String INVALID_ARGUMENT_PREFIX = "Invalid argument: ";

    private ArgumentParser() { }

    /**
     * Parses the given argument strings into a map. Each element must contain
     * exactly one '=' with non-empty key and value.
     *
     * @param args the raw command-line arguments
     * @return a map from key to value (no duplicates)
     * @throws StartupException            if argument format is invalid or a key is duplicated
     * @throws InvalidArgumentException   if an argument has invalid format
     * @throws DuplicateArgumentException if a key appears more than once
     */
    public static Map<String, String> parse(String[] args) throws StartupException {
        Map<String, String> kv = new HashMap<>();
        for (String arg : args) {
            int eq = arg.indexOf(ARGUMENT_KEY_VALUE_SEPARATOR);
            if (eq < MIN_KEY_END_INDEX || arg.length() - eq <= MIN_VALUE_LENGTH) {
                throw new InvalidArgumentException(INVALID_ARGUMENT_PREFIX + arg);
            }
            String key = arg.substring(INDEX_KEY_START, eq);
            String value = arg.substring(eq + VALUE_START_OFFSET);
            if (kv.containsKey(key)) {
                throw new DuplicateArgumentException(key);
            }
            kv.put(key, value);
        }
        return kv;
    }

}
