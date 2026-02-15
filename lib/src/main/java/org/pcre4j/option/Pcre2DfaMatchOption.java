/*
 * Copyright (C) 2024-2026 Oleksii PELYKH
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
 * Match options for {@link org.pcre4j.Pcre2Code#dfaMatch}.
 * <p>
 * These options control DFA matching behavior. DFA-specific options ({@link #RESTART} and {@link #SHORTEST}) are
 * only valid for DFA matching, while the remaining options are shared with standard matching.
 */
public enum Pcre2DfaMatchOption {
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
    PARTIAL_SOFT(IPcre2.PARTIAL_SOFT),

    /**
     * Restart DFA matching after a partial match.
     * <p>
     * When a previous DFA match returned a partial match, the matching can be continued with new data using this
     * option. The workspace must be preserved from the previous call.
     */
    RESTART(IPcre2.DFA_RESTART),

    /**
     * Return only the shortest match.
     * <p>
     * Without this option, DFA matching returns all possible match lengths at the same starting position (longest
     * first). With this option, only the shortest match is returned.
     */
    SHORTEST(IPcre2.DFA_SHORTEST);

    /**
     * The integer value of the option
     */
    private final int value;

    /**
     * Create a new enum value for the given option value.
     *
     * @param value the integer value of the option
     */
    private Pcre2DfaMatchOption(int value) {
        this.value = value;
    }

    /**
     * Get the enum value by its option value.
     *
     * @param value the integer value of the option
     * @return the flag
     */
    public static Optional<Pcre2DfaMatchOption> valueOf(int value) {
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
