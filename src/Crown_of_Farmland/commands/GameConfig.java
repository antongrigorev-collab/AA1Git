package Crown_of_Farmland.commands;

public record GameConfig(
        long seed,
        String team1Name,
        String team2Name,
        VerbosityMode verbosityMode,
        SymbolSet symbolSet
) { }

