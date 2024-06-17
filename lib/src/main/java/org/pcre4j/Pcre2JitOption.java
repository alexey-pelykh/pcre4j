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

public enum Pcre2JitOption {
    /**
     * Compile code for full matching
     */
    COMPLETE(IPcre2.JIT_COMPLETE),

    /**
     * Compile code for soft partial matching
     */
    PARTIAL_SOFT(IPcre2.JIT_PARTIAL_SOFT),

    /**
     * Compile code for hard partial matching
     */
    PARTIAL_HARD(IPcre2.JIT_PARTIAL_HARD),

    /**
     * @deprecated Use {@link Pcre2CompileOption#MATCH_INVALID_UTF}
     */
    @Deprecated INVALID_UTF(IPcre2.JIT_INVALID_UTF);

    /**
     * The integer value of the option
     */
    private final int value;

    /**
     * Create a new enum value for the given option value.
     *
     * @param value the integer value of the option
     */
    private Pcre2JitOption(int value) {
        this.value = value;
    }

    /**
     * Get the enum value by its option value.
     *
     * @param value the integer value of the option
     * @return the flag
     */
    public static Optional<Pcre2JitOption> valueOf(int value) {
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
