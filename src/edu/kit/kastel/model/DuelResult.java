package edu.kit.kastel.model;

import java.util.List;

/**
 * Result of a duel (A.1.4). Contains lines to print and whether the game ended.
 *
 * @param lines   output lines in order
 * @param winner  winning team if game over (LP dropped to 0), else null
 * @author usylb
 */
public record DuelResult(List<String> lines, Team winner) { }
