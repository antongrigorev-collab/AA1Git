package Crown_of_Farmland.commands;

import Crown_of_Farmland.exceptions.FileNotFoundException;
import Crown_of_Farmland.exceptions.InvalidArgumentException;
import Crown_of_Farmland.exceptions.InvalidBoardFileException;
import Crown_of_Farmland.exceptions.InvalidIntegerException;
import Crown_of_Farmland.exceptions.MissingArgumentException;
import Crown_of_Farmland.exceptions.StartupException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ConfigLoader {
    public static GameConfig load(Map<String, String> kv) throws StartupException {
        // TODO: validate required keys: seed + units + (deck OR deck1+deck2)


        long seed = parseLongRequired(kv, "seed");

        String team1 = kv.getOrDefault("team1", "Player");
        String team2 = kv.getOrDefault("team2", "Enemy");

        VerbosityMode mode = parseVerbosity(kv.getOrDefault("verbosity", "all"));

        SymbolSet symbolSet = SymbolSet.standard();
        if (kv.containsKey("board")) {
            symbolSet = readBoardSymbols(Path.of(kv.get("board")));
        }

        return new GameConfig(seed, team1, team2, mode, symbolSet);
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
        try {
            // Spec: one line, 29 characters, UTF-8
            String line = Files.readAllLines(path).get(0);
            if (line.length() != 29) {
                throw new InvalidBoardFileException("Board symbols must have 29 characters.");
            }
            return new SymbolSet(line.toCharArray());
        } catch (InvalidBoardFileException | FileNotFoundException e) {
            throw e;
        } catch (Exception ex) {
            throw new FileNotFoundException(path.toString());
        }
    }

    private static long parseLongRequired(Map<String, String> kv, String key) throws MissingArgumentException, InvalidIntegerException {
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
