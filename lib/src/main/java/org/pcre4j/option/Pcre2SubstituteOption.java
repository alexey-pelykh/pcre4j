/*
 * Copyright (C) 2026 Oleksii PELYKH
 *
 * This file is a part of the PCRE4J. The PCRE4J is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see
 * <https://www.gnu.org/licenses/>.
 */
package org.pcre4j.option;

import org.pcre4j.api.IPcre2;

import java.util.Arrays;
import java.util.Optional;

/**
 * Substitute options for {@link org.pcre4j.Pcre2Code#substitute}
 */
public enum Pcre2SubstituteOption {
    /**
     * Replace all occurrences throughout subject
     */
    GLOBAL(IPcre2.SUBSTITUTE_GLOBAL),

    /**
     * Enable extended replacement processing
     */
    EXTENDED(IPcre2.SUBSTITUTE_EXTENDED),

    /**
     * Insert empty string for unset groups
     */
    UNSET_EMPTY(IPcre2.SUBSTITUTE_UNSET_EMPTY),

    /**
     * Handle undefined groups as unset
     */
    UNKNOWN_UNSET(IPcre2.SUBSTITUTE_UNKNOWN_UNSET),

    /**
     * Calculate needed length on buffer overflow
     */
    OVERFLOW_LENGTH(IPcre2.SUBSTITUTE_OVERFLOW_LENGTH),

    /**
     * Treat replacement as literal text
     */
    LITERAL(IPcre2.SUBSTITUTE_LITERAL),

    /**
     * Use pre-existing match data for initial match
     */
    MATCHED(IPcre2.SUBSTITUTE_MATCHED),

    /**
     * Return only replacement string(s)
     */
    REPLACEMENT_ONLY(IPcre2.SUBSTITUTE_REPLACEMENT_ONLY);

    /**
     * The integer value of the option
     */
    private final int value;

    /**
     * Create a new enum value for the given option value.
     *
     * @param value the integer value of the option
     */
    private Pcre2SubstituteOption(int value) {
        this.value = value;
    }

    /**
     * Get the enum value by its option value.
     *
     * @param value the integer value of the option
     * @return the flag
     */
    public static Optional<Pcre2SubstituteOption> valueOf(int value) {
        return Arrays.stream(values())
                .filter(flag -> flag.value == value)
                .findFirst();
    }

    /**
     * Get the option value of the enum value.
     *
     * @return the integer value of the option
     */
    public int value() {
        return value;
    }
}
