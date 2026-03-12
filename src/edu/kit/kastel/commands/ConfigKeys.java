package edu.kit.kastel.commands;

import java.util.Set;

/**
 * Configuration keys, defaults and allowed-key set for {@link ConfigLoader}.
 *
 * @author usylb
 */
final class ConfigKeys {

    /** Configuration key 'seed'. */
    static final String KEY_SEED = "seed";

    /** Configuration key 'board'. */
    static final String KEY_BOARD = "board";

    /** Configuration key 'units'. */
    static final String KEY_UNITS = "units";

    /** Configuration key 'deck'. */
    static final String KEY_DECK = "deck";

    /** Configuration key 'deck1'. */
    static final String KEY_DECK1 = "deck1";

    /** Configuration key 'deck2'. */
    static final String KEY_DECK2 = "deck2";

    /** Configuration key 'team1'. */
    static final String KEY_TEAM1 = "team1";

    /** Configuration key 'team2'. */
    static final String KEY_TEAM2 = "team2";

    /** Configuration key 'verbosity'. */
    static final String KEY_VERBOSITY = "verbosity";

    /** Default team 1 name when not specified (A.3). */
    static final String DEFAULT_TEAM1_NAME = "Player";

    /** Default team 2 name when not specified (A.3). */
    static final String DEFAULT_TEAM2_NAME = "Enemy";

    /** Default verbosity when not specified (A.3). */
    static final String DEFAULT_VERBOSITY = "all";

    /**
     * Allowed configuration keys as specified in A.3 (for unknown-parameter detection).
     */
    static final Set<String> ALLOWED_KEYS = Set.of(
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

    private ConfigKeys() {
    }
}

