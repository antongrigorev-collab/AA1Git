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

/**
 * Loads and validates game configuration from key-value pairs (e.g. from command line).
 * Reads units file, deck file(s), optional board symbols, and builds a {@link GameConfig}.
 * File contents are printed to stdout before validation as specified.
 *
 * @author Programmieren-Team
 */
public class ConfigLoader {

    /** Maximum number of unit types in units file (A.3.2). */
    private static final int MAX_UNITS = 80;

    /** Total number of units per deck (A.1.6). */
    private static final int DECK_SIZE = 40;

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

    /**
     * Builds a game configuration from the given key-value map. Requires seed and
     * units; either deck or both deck1 and deck2; optional board, team1, team2, verbosity.
     * Validates deck keys and file formats; throws on first error.
     *
     * @param kv key-value pairs (e.g. seed=123, units=units.txt, deck=deck.txt)
     * @return the validated game configuration
     * @throws StartupException if a required key is missing, a file is invalid, or
     *                          deck arguments are conflicting
     */
    public static GameConfig load(Map<String, String> kv) throws StartupException {
        long seed = parseLongRequired(kv, "seed");

        if (!kv.containsKey("units")) {
            throw new MissingArgumentException("units");
        }
        validateDeckKeys(kv);

        String team1 = kv.getOrDefault("team1", DEFAULT_TEAM1_NAME);
        String team2 = kv.getOrDefault("team2", DEFAULT_TEAM2_NAME);

        VerbosityMode mode = parseVerbosity(kv.getOrDefault("verbosity", DEFAULT_VERBOSITY));

        SymbolSet symbolSet = SymbolSet.standard();
        if (kv.containsKey("board")) {
            symbolSet = readBoardSymbols(Path.of(kv.get("board")));
        }

        List<UnitTemplate> units = readUnitsFile(Path.of(kv.get("units")));
        List<Integer> deckCountsTeam1;
        List<Integer> deckCountsTeam2;
        if (kv.containsKey("deck")) {
            List<Integer> counts = readDeckFile(Path.of(kv.get("deck")), units.size());
            deckCountsTeam1 = counts;
            deckCountsTeam2 = counts;
        } else {
            deckCountsTeam1 = readDeckFile(Path.of(kv.get("deck1")), units.size());
            deckCountsTeam2 = readDeckFile(Path.of(kv.get("deck2")), units.size());
        }

        return new GameConfig(seed, team1, team2, mode, symbolSet, units, deckCountsTeam1, deckCountsTeam2);
    }

    private static void validateDeckKeys(Map<String, String> kv) throws ConflictingDeckArgumentsException {
        boolean hasDeck = kv.containsKey("deck");
        boolean hasDeck1 = kv.containsKey("deck1");
        boolean hasDeck2 = kv.containsKey("deck2");
        if (hasDeck && (hasDeck1 || hasDeck2)) {
            throw new ConflictingDeckArgumentsException("cannot use deck together with deck1/deck2");
        }
        if (!hasDeck && !(hasDeck1 && hasDeck2)) {
            throw new ConflictingDeckArgumentsException("must specify either deck or both deck1 and deck2");
        }
    }

    private static List<UnitTemplate> readUnitsFile(Path path) throws StartupException {
        List<String> lines = readAndOutputFile(path);
        if (lines.size() > MAX_UNITS) {
            throw new InvalidUnitsFileException("more than " + MAX_UNITS + " units");
        }
        List<UnitTemplate> units = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.strip();
            if (trimmed.endsWith(UNIT_FIELD_DELIMITER) || trimmed.contains(INVALID_DOUBLE_SPACE)) {
                throw new InvalidUnitsFileException("line must not end with semicolon or contain extra spaces");
            }
            String[] parts = trimmed.split(UNIT_FIELD_DELIMITER, -1);
            if (parts.length != EXPECTED_UNIT_FIELDS_COUNT) {
                throw new InvalidUnitsFileException("each line must have exactly 4 semicolon-separated fields");
            }
            String qualifier = parts[0].strip();
            String role = parts[1].strip();
            int atk = parseNonNegativeInt(parts[2].strip(), "ATK");
            int def = parseNonNegativeInt(parts[3].strip(), "DEF");
            units.add(new UnitTemplate(qualifier, role, atk, def));
        }
        return List.copyOf(units);
    }

    private static List<Integer> readDeckFile(Path path, int expectedLines) throws StartupException {
        List<String> lines = readAndOutputFile(path);
        if (lines.size() != expectedLines) {
            throw new InvalidDeckFileException("deck line count must match number of units");
        }
        List<Integer> counts = new ArrayList<>();
        int sum = 0;
        for (String line : lines) {
            int count = parseNonNegativeInt(line.strip(), "deck count");
            counts.add(count);
            sum += count;
        }
        if (sum != DECK_SIZE) {
            throw new InvalidDeckFileException("deck must contain exactly " + DECK_SIZE + " units");
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
            if (n < 0) {
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
            default -> throw new InvalidArgumentException("Invalid verbosity: " + v);
        };
    }

    private static SymbolSet readBoardSymbols(Path path) throws InvalidBoardFileException, FileNotFoundException {
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            throw new FileNotFoundException(path.toString());
        }
        if (lines.isEmpty()) {
            throw new InvalidBoardFileException("Board file must not be empty");
        }
        String line = lines.get(0);
        if (line.length() != EXPECTED_BOARD_SYMBOL_COUNT) {
            throw new InvalidBoardFileException("Board symbols must have 29 characters.");
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
