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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests to ensure API likeness of the {@link Pattern} to the {@link java.util.regex.Pattern}.
 */
public class PatternTests {

    private static final IPcre2 JNA_PCRE2 = new org.pcre4j.jna.Pcre2();
    private static final IPcre2 FFM_PCRE2 = new org.pcre4j.ffm.Pcre2();

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(JNA_PCRE2),
                Arguments.of(FFM_PCRE2)
        );
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void namedGroups(IPcre2 api) {
        var regex = "(?<number>42)";
        var javaPattern = java.util.regex.Pattern.compile(regex);
        var pcre4jPattern = Pattern.compile(api, regex);

        assertEquals(javaPattern.namedGroups(), pcre4jPattern.namedGroups());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void split(IPcre2 api) {
        var regex = "\\D+";
        var input = "0, 1, 1, 2, 3, 5, 8, ..., 144, ...";
        var javaPattern = java.util.regex.Pattern.compile(regex);
        var pcre4jPattern = Pattern.compile(api, regex);

        assertArrayEquals(javaPattern.split(input), pcre4jPattern.split(input));
        assertArrayEquals(javaPattern.split(input, 2), pcre4jPattern.split(input, 2));
        assertArrayEquals(javaPattern.splitWithDelimiters(input, 0), pcre4jPattern.splitWithDelimiters(input, 0));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void unicodeSplit(IPcre2 api) {
        var regex = "\\D+";
        var input = "0 ⇾ 1 ⇾ 1 ⇾ 2 ⇾ 3 ⇾ 5 ⇾ 8 ⇾ … ⇾ 144 ⇾ …";
        var javaPattern = java.util.regex.Pattern.compile(regex);
        var pcre4jPattern = Pattern.compile(api, regex);

        assertArrayEquals(javaPattern.split(input), pcre4jPattern.split(input));
        assertArrayEquals(javaPattern.split(input, 2), pcre4jPattern.split(input, 2));
        assertArrayEquals(javaPattern.splitWithDelimiters(input, 0), pcre4jPattern.splitWithDelimiters(input, 0));
    }

}
