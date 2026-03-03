package edu.kit.kastel.commands;

/**
 * Controls how the board is printed: full grid with separator lines (ALL) or
 * compact lines without separator lines (COMPACT).
 */
public enum VerbosityMode {
    /** Full board output with all connector lines. */
    ALL,
    /** Compact board output without connector lines. */
    COMPACT
}
