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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.pcre4j.regex.MatcherTestUtils.assertMatchResultState;
import static org.pcre4j.regex.MatcherTestUtils.assertMatcherState;

/**
 * Tests for Unicode handling in {@link Matcher}.
 */
public class MatcherUnicodeTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicodeOneByte(IPcre2 api) {
        var regex = "Å";
        var input = "Å";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicodeTwoBytes(IPcre2 api) {
        var regex = "Ǎ";
        var input = "Ǎ";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicodeThreeBytes(IPcre2 api) {
        var regex = "•";
        var input = "•";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicodeFourBytes(IPcre2 api) {
        var regex = "\uD83C\uDF0D";
        var input = "\uD83C\uDF0D";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicode(IPcre2 api) {
        var regex = "ÅǍ•\uD83C\uDF0D!";
        var input = "ÅǍ•\uD83C\uDF0D!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicodeRegion(IPcre2 api) {
        var regex = "\uD83C\uDF0D";
        var input = "ÅǍ•\uD83C\uDF0D!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 5);
        pcre4jMatcher.region(3, 5);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

}
