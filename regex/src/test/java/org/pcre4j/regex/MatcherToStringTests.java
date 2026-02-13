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
package org.pcre4j.regex;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@code toString()} behavior in {@link Matcher}.
 */
public class MatcherToStringTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void toStringBeforeMatch(IPcre2 api) {
        var regex = "\\d+";
        var input = "abc123def";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var result = pcre4jMatcher.toString();

        assertTrue(result.startsWith("org.pcre4j.regex.Matcher["));
        assertTrue(result.contains("pattern=" + regex));
        assertTrue(result.contains("region=0," + input.length()));
        assertTrue(result.contains("lastMatchIndices=null"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void toStringAfterMatch(IPcre2 api) {
        var regex = "\\d+";
        var input = "abc123def";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        pcre4jMatcher.find();

        var result = pcre4jMatcher.toString();

        assertTrue(result.startsWith("org.pcre4j.regex.Matcher["));
        assertTrue(result.contains("pattern=" + regex));
        assertTrue(result.contains("region=0," + input.length()));
        assertTrue(result.contains("lastMatchIndices=[3, 6]"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void toStringWithRegion(IPcre2 api) {
        var regex = "\\d+";
        var input = "abc123def";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        pcre4jMatcher.region(3, 6);

        var result = pcre4jMatcher.toString();

        assertTrue(result.contains("region=3,6"));
    }

}
