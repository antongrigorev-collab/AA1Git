package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.DuplicateArgumentException;
import Crown_of_Farmland.exceptions.InvalidArgumentException;
import Crown_of_Farmland.exceptions.StartupException;

import java.util.HashMap;
import java.util.Map;

public class ArgumentParser {

    /** Key-value separator in program arguments (e.g. seed=123). */
    private static final char ARGUMENT_KEY_VALUE_SEPARATOR = '=';

    private ArgumentParser() { }

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
