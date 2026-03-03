package edu.kit.kastel.commands;

/**
 * Immutable template for a unit type (qualifier, role, ATK, DEF) as read from the units config file.
 */
public record UnitTemplate(String qualifier, String role, int atk, int def) { }
