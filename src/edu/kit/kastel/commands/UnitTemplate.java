package edu.kit.kastel.commands;

/**
 * Immutable template for a unit type (qualifier, role, ATK, DEF) as read from the units config file.
 *
 * @param qualifier first part of the unit name
 * @param role      second part of the unit name
 * @param atk       attack value
 * @param def       defense value
 * @author usylb
 */
public record UnitTemplate(String qualifier, String role, int atk, int def) { }
