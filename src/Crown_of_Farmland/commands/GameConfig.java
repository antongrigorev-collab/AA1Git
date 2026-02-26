package Crown_of_Farmland.commands;

import java.util.List;

public record GameConfig(
        long seed,
        String team1Name,
        String team2Name,
        VerbosityMode verbosityMode,
        SymbolSet symbolSet,
        List<UnitTemplate> units,
        List<Integer> deckCountsTeam1,
        List<Integer> deckCountsTeam2
) { }

