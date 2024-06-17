/*
 * Copyright (C) 2024 Oleksii PELYKH
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
package org.pcre4j;

import org.pcre4j.api.IPcre2;

import java.util.Arrays;
import java.util.Optional;

/**
 * Match options for {@link Pcre2Code#match}
 */
public enum Pcre2MatchOption {
    /**
     * Match only at the first position
     */
    ANCHORED(IPcre2.ANCHORED),

    /**
     * On success, make a private subject copy
     */
    COPY_MATCHED_SUBJECT(IPcre2.COPY_MATCHED_SUBJECT),

    /**
     * Pattern can match only at end of subject
     */
    ENDANCHORED(IPcre2.ENDANCHORED),

    /**
     * Subject string is not the beginning of a line
     */
    NOTBOL(IPcre2.NOTBOL),

    /**
     * Subject string is not the end of a line
     */
    NOTEOL(IPcre2.NOTEOL),

    /**
     * An empty string is not a valid match
     */
    NOTEMPTY(IPcre2.NOTEMPTY),

    /**
     * An empty string at the start of the subject is not a valid match
     */
    NOTEMPTY_ATSTART(IPcre2.NOTEMPTY_ATSTART),

    /**
     * Do not use JIT matching
     */
    NO_JIT(IPcre2.NO_JIT),

    /**
     * Do not check the subject for UTF validity (only relevant if PCRE2_UTF was set at compile time)
     */
    NO_UTF_CHECK(IPcre2.NO_UTF_CHECK),

    /**
     * Return {@link IPcre2#ERROR_PARTIAL} for a partial match even if there is a full match
     */
    PARTIAL_HARD(IPcre2.PARTIAL_HARD),

    /**
     * Return {@link IPcre2#ERROR_PARTIAL} for a partial match if no full matches are found
     */
    PARTIAL_SOFT(IPcre2.PARTIAL_SOFT);

    /**
     * The integer value of the option
     */
    private final int value;

    /**
     * Create a new enum value for the given option value.
     *
     * @param value the integer value of the option
     */
    private Pcre2MatchOption(int value) {
        this.value = value;
    }

    /**
     * Get the enum value by its option value.
     *
     * @param value the integer value of the option
     * @return the flag
     */
    public static Optional<Pcre2MatchOption> valueOf(int value) {
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
