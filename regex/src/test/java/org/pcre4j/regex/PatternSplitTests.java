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
package org.pcre4j.regex;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Edge case tests for {@link Pattern#split(CharSequence)}, {@link Pattern#split(CharSequence, int)},
 * and {@link Pattern#splitWithDelimiters(CharSequence, int)}.
 */
public class PatternSplitTests {

    // --- limit=0 trailing empty strings removal ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitTrailingEmptyStringsRemovedWithDefaultLimit(IPcre2 api) {
        // split() with default limit (0) should remove trailing empty strings
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");
        var input = "a,b,c,,,";

        assertArrayEquals(javaPattern.split(input), pcre4jPattern.split(input));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitTrailingEmptyStringsRemovedWithZeroLimit(IPcre2 api) {
        // split(input, 0) should remove trailing empty strings
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");
        var input = "a,b,c,,,";

        assertArrayEquals(javaPattern.split(input, 0), pcre4jPattern.split(input, 0));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitAllEmptyWithZeroLimit(IPcre2 api) {
        // Input that results in all empty strings
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");
        var input = ",,,";

        assertArrayEquals(javaPattern.split(input, 0), pcre4jPattern.split(input, 0));
    }

    // --- Positive limit ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitPositiveLimitOne(IPcre2 api) {
        // limit=1 returns the entire input as a single element
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");
        var input = "a,b,c";

        assertArrayEquals(javaPattern.split(input, 1), pcre4jPattern.split(input, 1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitPositiveLimitExceedsMatches(IPcre2 api) {
        // limit greater than number of matches
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");
        var input = "a,b,c";

        assertArrayEquals(javaPattern.split(input, 10), pcre4jPattern.split(input, 10));
    }

    // --- Empty input and no-match ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitEmptyInput(IPcre2 api) {
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");

        assertArrayEquals(javaPattern.split(""), pcre4jPattern.split(""));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitNoMatch(IPcre2 api) {
        // No delimiter found — returns entire input
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");
        var input = "abc";

        assertArrayEquals(javaPattern.split(input), pcre4jPattern.split(input));
    }

    // --- splitWithDelimiters edge cases ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitWithDelimitersTrailingEmpties(IPcre2 api) {
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");
        var input = "a,b,c,,,";

        assertArrayEquals(
                javaPattern.splitWithDelimiters(input, 0),
                pcre4jPattern.splitWithDelimiters(input, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitWithDelimitersPositiveLimit(IPcre2 api) {
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");
        var input = "a,b,c,d";

        assertArrayEquals(
                javaPattern.splitWithDelimiters(input, 3),
                pcre4jPattern.splitWithDelimiters(input, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitWithDelimitersNoMatch(IPcre2 api) {
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");
        var input = "abc";

        assertArrayEquals(
                javaPattern.splitWithDelimiters(input, 0),
                pcre4jPattern.splitWithDelimiters(input, 0)
        );
    }

    // --- Regex-based delimiter edge cases ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitMultiCharDelimiter(IPcre2 api) {
        var javaPattern = java.util.regex.Pattern.compile("\\s*,\\s*");
        var pcre4jPattern = Pattern.compile(api, "\\s*,\\s*");
        var input = "a , b , c";

        assertArrayEquals(javaPattern.split(input), pcre4jPattern.split(input));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitDelimiterAtStartAndEnd(IPcre2 api) {
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");
        var input = ",a,b,c,";

        assertArrayEquals(javaPattern.split(input), pcre4jPattern.split(input));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitConsecutiveDelimiters(IPcre2 api) {
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");
        var input = "a,,b,,c";

        assertArrayEquals(javaPattern.split(input), pcre4jPattern.split(input));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitSingleCharInput(IPcre2 api) {
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");

        assertArrayEquals(javaPattern.split(","), pcre4jPattern.split(","));
    }

    // --- splitAsStream edge cases ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitAsStreamTrailingEmpties(IPcre2 api) {
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");
        var input = "a,b,c,,,";

        assertArrayEquals(
                javaPattern.splitAsStream(input).toArray(),
                pcre4jPattern.splitAsStream(input).toArray()
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitAsStreamEmptyInput(IPcre2 api) {
        var javaPattern = java.util.regex.Pattern.compile(",");
        var pcre4jPattern = Pattern.compile(api, ",");

        assertArrayEquals(
                javaPattern.splitAsStream("").toArray(),
                pcre4jPattern.splitAsStream("").toArray()
        );
    }
}
