package edu.kit.kastel.model;

/**
 * Result of ending the current turn (yield). Used for command and AI output.
 *
 * @param discarded       unit discarded from hand, or null if none
 * @param yieldingTeam    team that ended the turn (the one that had the turn before switch)
 * @param newTeamDeckEmpty whether the new team's deck was empty and could not draw
 * @param winner          winning team if game over, otherwise null
 *
 * @author usylb
 */
public record YieldResult(Unit discarded, Team yieldingTeam,
                          boolean newTeamDeckEmpty, Team winner) { }

