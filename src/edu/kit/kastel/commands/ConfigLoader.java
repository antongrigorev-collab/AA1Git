package edu.kit.kastel.commands;

import edu.kit.kastel.exceptions.ConflictingDeckArgumentsException;
import edu.kit.kastel.exceptions.FileNotFoundException;
import edu.kit.kastel.exceptions.InvalidArgumentException;
import edu.kit.kastel.exceptions.InvalidBoardFileException;
import edu.kit.kastel.exceptions.InvalidDeckFileException;
import edu.kit.kastel.exceptions.InvalidIntegerException;
import edu.kit.kastel.exceptions.InvalidUnitsFileException;
import edu.kit.kastel.exceptions.MissingArgumentException;
import edu.kit.kastel.exceptions.StartupException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Loads and validates game configuration from key-value pairs (e.g. from command line).
 * Reads units file, deck file(s), optional board symbols, and builds a {@link GameConfig}.
 * File contents are printed to stdout before validation as specified.
 *
 * @author usylb
 */
public final class ConfigLoader {

    /** Maximum number of unit types in units file (A.3.2). */
    private static final int MAX_UNITS = 80;

    /** Total number of units per deck (A.1.6). */
    private static final int DECK_SIZE = 40;

    /** Maximum allowed length of a team name (A.3). */
    private static final int MAX_TEAM_NAME_LENGTH = 14;

    /** Default team 1 name when not specified (A.3). */
    private static final String DEFAULT_TEAM1_NAME = "Player";

    /** Default team 2 name when not specified (A.3). */
    private static final String DEFAULT_TEAM2_NAME = "Enemy";

    /** Default verbosity when not specified (A.3). */
    private static final String DEFAULT_VERBOSITY = "all";

    /** Expected number of symbols in custom board file (A.3.1). */
    private static final int EXPECTED_BOARD_SYMBOL_COUNT = 29;

    /** Expected number of semicolon-separated fields per unit line (qualifier;role;atk;def). */
    private static final int EXPECTED_UNIT_FIELDS_COUNT = 4;

    /** Delimiter for unit fields in units file. */
    private static final String UNIT_FIELD_DELIMITER = ";";

    /** Invalid content in units file (double space). */
    private static final String INVALID_DOUBLE_SPACE = "  ";

    /** Configuration key 'seed'. */
    private static final String KEY_SEED = "seed";

    /** Configuration key 'board'. */
    private static final String KEY_BOARD = "board";

    /** Configuration key 'units'. */
    private static final String KEY_UNITS = "units";

    /** Configuration key 'deck'. */
    private static final String KEY_DECK = "deck";

    /** Configuration key 'deck1'. */
    private static final String KEY_DECK1 = "deck1";

    /** Configuration key 'deck2'. */
    private static final String KEY_DECK2 = "deck2";

    /** Configuration key 'team1'. */
    private static final String KEY_TEAM1 = "team1";

    /** Configuration key 'team2'. */
    private static final String KEY_TEAM2 = "team2";

    /** Configuration key 'verbosity'. */
    private static final String KEY_VERBOSITY = "verbosity";

    /** Error prefix for too long team names. */
    private static final String ERROR_TEAM_NAME_PREFIX = "Team name for ";

    /** Error suffix for too long team names. */
    private static final String ERROR_TEAM_NAME_SUFFIX = " is too long";

    /** Error prefix for unknown startup parameters. */
    private static final String ERROR_UNKNOWN_PARAMETER_PREFIX = "Unknown parameter: ";

    /** Error message when deck and deck1/deck2 are used together. */
    private static final String ERROR_CONFLICTING_DECK_KEYS =
            "cannot use deck together with deck1/deck2";

    /** Error message when neither a shared nor two individual decks are specified correctly. */
    private static final String ERROR_INCOMPLETE_DECK_KEYS =
            "must specify either deck or both deck1 and deck2";

    /** Error message when units file defines no units. */
    private static final String ERROR_UNITS_FILE_EMPTY =
            "units file must define at least one unit";

    /** Error prefix when units file defines more than the allowed number of units. */
    private static final String ERROR_UNITS_FILE_TOO_MANY_PREFIX = "more than ";

    /** Error suffix for too many units in units file. */
    private static final String ERROR_UNITS_FILE_TOO_MANY_SUFFIX = " units";

    /** Error message for invalid units file line formatting. */
    private static final String ERROR_UNITS_FILE_LINE_FORMAT =
            "line must not end with semicolon or contain extra spaces";

    /** Error message when a units line does not have the expected number of fields. */
    private static final String ERROR_UNITS_FILE_FIELD_COUNT =
            "each line must have exactly 4 semicolon-separated fields";

    /** Error prefix for invalid verbosity specification. */
    private static final String ERROR_INVALID_VERBOSITY_PREFIX = "Invalid verbosity: ";

    /** Error message when board file is empty. */
    private static final String ERROR_EMPTY_BOARD_FILE = "Board file must not be empty";

    /** Error message when board symbol count is not as specified. */
    private static final String ERROR_INVALID_BOARD_SYMBOL_COUNT =
            "Board symbols must have 29 characters.";

    /** Error message when deck line count does not match number of units. */
    private static final String ERROR_DECK_LINE_COUNT_MISMATCH =
            "deck line count must match number of units";

    /** Error message prefix when deck does not contain the expected number of units. */
    private static final String ERROR_DECK_SIZE_PREFIX = "deck must contain exactly ";

    /** Error message suffix when deck does not contain the expected number of units. */
    private static final String ERROR_DECK_SIZE_SUFFIX = " units";

    /** Minimal allowed non-negative integer value for parsed numbers. */
    private static final int MIN_NON_NEGATIVE_VALUE = 0;

    /** Context label for attack value parsing. */
    private static final String CONTEXT_ATK = "ATK";

    /** Context label for defense value parsing. */
    private static final String CONTEXT_DEF = "DEF";

    /**
     * Allowed configuration keys as specified in A.3 (for unknown-parameter detection).
     */
    private static final Set<String> ALLOWED_KEYS = Set.of(
            KEY_SEED,
            KEY_BOARD,
            KEY_UNITS,
            KEY_DECK,
            KEY_DECK1,
            KEY_DECK2,
            KEY_TEAM1,
            KEY_TEAM2,
            KEY_VERBOSITY
    );

    private ConfigLoader() { }

    /**
     * Builds a game configuration from the given key-value map. Requires seed and
     * units; either deck or both deck1 and deck2; optional board, team1, team2, verbosity.
     * Validates keys, deck keys and file formats; throws on first error.
     *
     * @param kv key-value pairs (e.g. seed=123, units=units.txt, deck=deck.txt)
     * @return the validated game configuration
     * @throws MissingArgumentException if the "units" key is missing
     * @throws StartupException if a required key is missing, a file is invalid, or
     *                          deck arguments are conflicting
     */
    public static GameConfig load(Map<String, String> kv) throws StartupException {
        validateKnownKeys(kv);

        long seed = parseLongRequired(kv, KEY_SEED);

        if (!kv.containsKey(KEY_UNITS)) {
            throw new MissingArgumentException(KEY_UNITS);
        }
        validateDeckKeys(kv);

        String team1 = kv.getOrDefault(KEY_TEAM1, DEFAULT_TEAM1_NAME);
        String team2 = kv.getOrDefault(KEY_TEAM2, DEFAULT_TEAM2_NAME);
        String verbosityValue = kv.getOrDefault(KEY_VERBOSITY, DEFAULT_VERBOSITY);

        SymbolSet symbolSet = SymbolSet.standard();
        if (kv.containsKey(KEY_BOARD)) {
            symbolSet = readBoardSymbols(Path.of(kv.get(KEY_BOARD)));
        }

        List<UnitTemplate> units = readUnitsFile(Path.of(kv.get(KEY_UNITS)));
        List<Integer> deckCountsTeam1;
        List<Integer> deckCountsTeam2;

        if (kv.containsKey(KEY_DECK)) {
            List<Integer> counts = readDeckFile(Path.of(kv.get(KEY_DECK)), units.size());
            deckCountsTeam1 = counts;
            deckCountsTeam2 = counts;
        } else {
            deckCountsTeam1 = readDeckFile(Path.of(kv.get(KEY_DECK1)), units.size());
            deckCountsTeam2 = readDeckFile(Path.of(kv.get(KEY_DECK2)), units.size());
        }

        validateTeamNameLength(team1, KEY_TEAM1);
        validateTeamNameLength(team2, KEY_TEAM2);
        VerbosityMode mode = parseVerbosity(verbosityValue);

        return new GameConfig(seed, team1, team2, mode, symbolSet, units, deckCountsTeam1, deckCountsTeam2);
    }

    private static void validateTeamNameLength(String teamName, String key) throws InvalidArgumentException {
        if (teamName.length() > MAX_TEAM_NAME_LENGTH) {
            throw new InvalidArgumentException(ERROR_TEAM_NAME_PREFIX + key + ERROR_TEAM_NAME_SUFFIX);
        }
    }

    private static void validateKnownKeys(Map<String, String> kv) throws InvalidArgumentException {
        for (String key : kv.keySet()) {
            if (!ALLOWED_KEYS.contains(key)) {
                throw new InvalidArgumentException(ERROR_UNKNOWN_PARAMETER_PREFIX + key);
            }
        }
    }

    private static void validateDeckKeys(Map<String, String> kv) throws ConflictingDeckArgumentsException {
        boolean hasDeck = kv.containsKey(KEY_DECK);
        boolean hasDeck1 = kv.containsKey(KEY_DECK1);
        boolean hasDeck2 = kv.containsKey(KEY_DECK2);
        if (hasDeck && (hasDeck1 || hasDeck2)) {
            throw new ConflictingDeckArgumentsException(ERROR_CONFLICTING_DECK_KEYS);
        }
        if (!hasDeck && !(hasDeck1 && hasDeck2)) {
            throw new ConflictingDeckArgumentsException(ERROR_INCOMPLETE_DECK_KEYS);
        }
    }

    private static List<UnitTemplate> readUnitsFile(Path path) throws StartupException {
        List<String> lines = readAndOutputFile(path);
        if (lines.isEmpty()) {
            throw new InvalidUnitsFileException(ERROR_UNITS_FILE_EMPTY);
        }
        if (lines.size() > MAX_UNITS) {
            throw new InvalidUnitsFileException(
                    ERROR_UNITS_FILE_TOO_MANY_PREFIX + MAX_UNITS + ERROR_UNITS_FILE_TOO_MANY_SUFFIX
            );
        }
        List<UnitTemplate> units = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.strip();
            if (trimmed.endsWith(UNIT_FIELD_DELIMITER) || trimmed.contains(INVALID_DOUBLE_SPACE)) {
                throw new InvalidUnitsFileException(ERROR_UNITS_FILE_LINE_FORMAT);
            }
            String[] parts = trimmed.split(UNIT_FIELD_DELIMITER, -1);
            if (parts.length != EXPECTED_UNIT_FIELDS_COUNT) {
                throw new InvalidUnitsFileException(ERROR_UNITS_FILE_FIELD_COUNT);
            }
            String qualifier = parts[0].strip();
            String role = parts[1].strip();
            int atk = parseNonNegativeInt(parts[2].strip(), CONTEXT_ATK);
            int def = parseNonNegativeInt(parts[3].strip(), CONTEXT_DEF);
            units.add(new UnitTemplate(qualifier, role, atk, def));
        }
        return List.copyOf(units);
    }

    private static List<Integer> readDeckFile(Path path, int expectedLines) throws StartupException {
        List<String> lines = readAndOutputFile(path);
        if (lines.size() != expectedLines) {
            throw new InvalidDeckFileException(ERROR_DECK_LINE_COUNT_MISMATCH);
        }
        List<Integer> counts = new ArrayList<>();
        int sum = 0;
        for (String line : lines) {
            int count = parseNonNegativeInt(line.strip(), "deck count");
            counts.add(count);
            sum += count;
        }
        if (sum != DECK_SIZE) {
            throw new InvalidDeckFileException(
                    ERROR_DECK_SIZE_PREFIX + DECK_SIZE + ERROR_DECK_SIZE_SUFFIX
            );
        }
        return List.copyOf(counts);
    }

    private static List<String> readAndOutputFile(Path path) throws FileNotFoundException {
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            throw new FileNotFoundException(path.toString());
        }
        for (String line : lines) {
            System.out.println(line);
        }
        return lines;
    }

    private static int parseNonNegativeInt(String value, String context) throws InvalidIntegerException {
        try {
            int n = Integer.parseInt(value);
            if (n < MIN_NON_NEGATIVE_VALUE) {
                throw new InvalidIntegerException(value);
            }
            return n;
        } catch (NumberFormatException e) {
            throw new InvalidIntegerException(value);
        }
    }

    private static VerbosityMode parseVerbosity(String v) throws InvalidArgumentException {
        String x = v.toLowerCase();
        return switch (x) {
            case "all" -> VerbosityMode.ALL;
            case "compact" -> VerbosityMode.COMPACT;
            default -> throw new InvalidArgumentException(ERROR_INVALID_VERBOSITY_PREFIX + v);
        };
    }

    private static SymbolSet readBoardSymbols(Path path) throws InvalidBoardFileException, FileNotFoundException {
        List<String> lines = readAndOutputFile(path);
        if (lines.isEmpty()) {
            throw new InvalidBoardFileException(ERROR_EMPTY_BOARD_FILE);
        }
        String line = lines.get(0);
        if (line.length() != EXPECTED_BOARD_SYMBOL_COUNT) {
            throw new InvalidBoardFileException(ERROR_INVALID_BOARD_SYMBOL_COUNT);
        }
        return new SymbolSet(line.toCharArray());
    }

    private static long parseLongRequired(Map<String, String> kv, String key)
            throws MissingArgumentException, InvalidIntegerException {
        if (!kv.containsKey(key)) {
            throw new MissingArgumentException(key);
        }
        try {
            return Long.parseLong(kv.get(key));
        } catch (NumberFormatException ex) {
            throw new InvalidIntegerException(kv.get(key));
        }
    }

}
