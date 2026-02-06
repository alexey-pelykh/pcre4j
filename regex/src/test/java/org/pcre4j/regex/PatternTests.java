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

import static org.junit.jupiter.api.Assertions.*;

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

    @ParameterizedTest
    @MethodSource("parameters")
    void withoutUnicodeCharacterClass(IPcre2 api) {
        var regex = "\\w";
        var input = "Ǎ";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void withUnicodeCharacterClass(IPcre2 api) {
        var regex = "\\w";
        var input = "Ǎ";
        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.UNICODE_CHARACTER_CLASS
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.UNICODE_CHARACTER_CLASS
        ).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void withoutUnixNewline(IPcre2 api) {
        var regex = "^A$";
        var input = "A\u0085B";
        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.MULTILINE
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.MULTILINE
        ).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void withUnixNewline(IPcre2 api) {
        var regex = "^A$";
        var input = "A\u0085B";
        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.MULTILINE | java.util.regex.Pattern.UNIX_LINES
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.MULTILINE | Pattern.UNIX_LINES
        ).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertThrows(IllegalStateException.class, javaMatcher::group);
        assertThrows(IllegalStateException.class, pcre4jMatcher::group);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void splitAsStream(IPcre2 api) {
        //test cases
        Object[][] cases = new Object[][]{
            {",", "a,b,c"},
            {",", "a,b,c,"},
            {",", ""},
            {",", "abc"},
            {"\\d", "a1b2c"}
        };

        for (Object[] c : cases) {
            String regex = (String) c[0];
            String input = (String) c[1];

            var javaPattern = java.util.regex.Pattern.compile(regex);
            var pcre4jPattern = Pattern.compile(api, regex);

            assertArrayEquals(
                javaPattern.splitAsStream(input).toArray(),
                pcre4jPattern.splitAsStream(input).toArray(),
                "Mismatch for regex=" + regex + ", input=" + input
            );
        }
    }
}
