package edu.kit.kastel.commands;

import java.util.List;

/**
 * Immutable configuration for Crown of Farmland loaded at startup. Contains seed,
 * team names, verbosity and symbol set, unit templates, and deck counts for both teams.
 *
 * @author usylb
 * @param seed               start value for the random number generator
 * @param team1Name          display name for team 1 (e.g. "Player")
 * @param team2Name          display name for team 2 (e.g. "Enemy")
 * @param verbosityMode      whether board output is full or compact
 * @param symbolSet          symbols used to draw the board (standard or custom)
 * @param units              list of unit templates (qualifier, role, atk, def)
 * @param deckCountsTeam1    count per unit type in team 1's deck (40 total)
 * @param deckCountsTeam2    count per unit type in team 2's deck (40 total)
 */
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

