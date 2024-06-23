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
 * The UTF character width.
 */
public enum Pcre2UtfWidth {
    /**
     * UTF-8
     */
    UTF8(0b1),

    /**
     * UTF-16
     */
    UTF16(0b10),

    /**
     * UTF-32
     */
    UTF32(0b1000);

    /**
     * The integer value
     */
    private final int value;

    /**
     * Create an enum entry with the given integer value.
     *
     * @param value the integer value
     */
    private Pcre2UtfWidth(int value) {
        this.value = value;
    }

    /**
     * Get the enum entry by its integer value.
     *
     * @param value the integer value
     * @return the enum entry
     */
    public static Optional<Pcre2UtfWidth> valueOf(int value) {
        return Arrays.stream(values())
                .filter(entry -> entry.value == value)
                .findFirst();
    }

    /**
     * Get the integer value.
     *
     * @return the integer value
     */
    public int value() {
        return value;
    }
}
