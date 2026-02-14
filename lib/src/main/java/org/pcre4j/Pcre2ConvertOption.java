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
package org.pcre4j;

import org.pcre4j.api.IPcre2;

import java.util.Arrays;
import java.util.Optional;

/**
 * Options for pattern conversion via {@link Pcre2PatternConverter}.
 * <p>
 * These options control the behavior of the PCRE2 pattern conversion functions, which convert
 * glob patterns or POSIX regular expressions into PCRE2-compatible patterns.
 */
public enum Pcre2ConvertOption {

    /**
     * Treat the input pattern as a UTF string
     */
    UTF(IPcre2.CONVERT_UTF),

    /**
     * Skip UTF validity check on the input pattern
     */
    NO_UTF_CHECK(IPcre2.CONVERT_NO_UTF_CHECK),

    /**
     * Convert a POSIX basic regular expression
     */
    POSIX_BASIC(IPcre2.CONVERT_POSIX_BASIC),

    /**
     * Convert a POSIX extended regular expression
     */
    POSIX_EXTENDED(IPcre2.CONVERT_POSIX_EXTENDED),

    /**
     * Convert a glob pattern
     */
    GLOB(IPcre2.CONVERT_GLOB),

    /**
     * Convert a glob pattern where wildcards do not match the path separator
     */
    GLOB_NO_WILD_SEPARATOR(IPcre2.CONVERT_GLOB_NO_WILD_SEPARATOR),

    /**
     * Convert a glob pattern with the double-star ({@code **}) feature disabled
     */
    GLOB_NO_STARSTAR(IPcre2.CONVERT_GLOB_NO_STARSTAR);

    /**
     * The integer value of the option
     */
    private final int value;

    /**
     * Create a new enum value for the given option value.
     *
     * @param value the integer value of the option
     */
    private Pcre2ConvertOption(int value) {
        this.value = value;
    }

    /**
     * Get the enum value by its option value.
     *
     * @param value the integer value of the option
     * @return the option
     */
    public static Optional<Pcre2ConvertOption> valueOf(int value) {
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
