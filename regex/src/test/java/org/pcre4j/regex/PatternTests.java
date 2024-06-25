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
package org.pcre4j.regex;

import org.junit.jupiter.api.Test;
import org.pcre4j.Pcre4j;
import org.pcre4j.jna.Pcre2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests to ensure API likeness of the {@link Pattern} to the {@link java.util.regex.Pattern}.
 */
public class PatternTests {

    static {
        Pcre4j.setup(new Pcre2());
    }

    @Test
    void namedGroups() {
        var regex = "(?<number>42)";
        var javaPattern = java.util.regex.Pattern.compile(regex);
        var pcre4jPattern = Pattern.compile(regex);

        assertEquals(javaPattern.namedGroups(), pcre4jPattern.namedGroups());
    }

    @Test
    void split() {
        var regex = "\\D+";
        var input = "0, 1, 1, 2, 3, 5, 8, ..., 144, ...";
        var javaPattern = java.util.regex.Pattern.compile(regex);
        var pcre4jPattern = Pattern.compile(regex);

        assertArrayEquals(javaPattern.split(input), pcre4jPattern.split(input));
        assertArrayEquals(javaPattern.split(input, 2), pcre4jPattern.split(input, 2));
        assertArrayEquals(javaPattern.splitWithDelimiters(input, 0), pcre4jPattern.splitWithDelimiters(input, 0));
    }

    @Test
    void unicodeSplit() {
        var regex = "\\D+";
        var input = "0 ⇾ 1 ⇾ 1 ⇾ 2 ⇾ 3 ⇾ 5 ⇾ 8 ⇾ … ⇾ 144 ⇾ …";
        var javaPattern = java.util.regex.Pattern.compile(regex);
        var pcre4jPattern = Pattern.compile(regex);

        assertArrayEquals(javaPattern.split(input), pcre4jPattern.split(input));
        assertArrayEquals(javaPattern.split(input, 2), pcre4jPattern.split(input, 2));
        assertArrayEquals(javaPattern.splitWithDelimiters(input, 0), pcre4jPattern.splitWithDelimiters(input, 0));
    }
}
