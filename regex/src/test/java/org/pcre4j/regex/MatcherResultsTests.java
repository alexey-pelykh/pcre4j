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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@code results()} stream operation in {@link Matcher}.
 */
public class MatcherResultsTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void resultsBasic(IPcre2 api) {
        var regex = "\\d+";
        var input = "a1b22c333d";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        for (int i = 0; i < javaResults.size(); i++) {
            assertEquals(javaResults.get(i).group(), pcre4jResults.get(i).group());
            assertEquals(javaResults.get(i).start(), pcre4jResults.get(i).start());
            assertEquals(javaResults.get(i).end(), pcre4jResults.get(i).end());
        }
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void resultsNoMatches(IPcre2 api) {
        var regex = "xyz";
        var input = "hello world";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        assertTrue(pcre4jResults.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void resultsSingleMatch(IPcre2 api) {
        var regex = "world";
        var input = "hello world!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        assertEquals(1, pcre4jResults.size());
        assertEquals(javaResults.get(0).group(), pcre4jResults.get(0).group());
        assertEquals(javaResults.get(0).start(), pcre4jResults.get(0).start());
        assertEquals(javaResults.get(0).end(), pcre4jResults.get(0).end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void resultsWithGroups(IPcre2 api) {
        var regex = "(\\w)(\\d)";
        var input = "a1 b2 c3";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        for (int i = 0; i < javaResults.size(); i++) {
            assertEquals(javaResults.get(i).groupCount(), pcre4jResults.get(i).groupCount());
            assertEquals(javaResults.get(i).group(), pcre4jResults.get(i).group());
            assertEquals(javaResults.get(i).group(1), pcre4jResults.get(i).group(1));
            assertEquals(javaResults.get(i).group(2), pcre4jResults.get(i).group(2));
        }
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void resultsImmutableSnapshots(IPcre2 api) {
        var regex = "\\w+";
        var input = "one two three";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Collect all results first
        var results = pcre4jMatcher.results().toList();

        // Results should be independent snapshots
        assertEquals(3, results.size());
        assertEquals("one", results.get(0).group());
        assertEquals("two", results.get(1).group());
        assertEquals("three", results.get(2).group());

        // Each should have correct positions
        assertEquals(0, results.get(0).start());
        assertEquals(3, results.get(0).end());
        assertEquals(4, results.get(1).start());
        assertEquals(7, results.get(1).end());
        assertEquals(8, results.get(2).start());
        assertEquals(13, results.get(2).end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void resultsDoesNotReset(IPcre2 api) {
        var regex = "\\w+";
        var input = "one two three";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Call find() first to advance the matcher
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals("one", javaMatcher.group());
        assertEquals("one", pcre4jMatcher.group());

        // results() should NOT reset and should continue from current position
        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        assertEquals(2, pcre4jResults.size());  // "two" and "three" only
        assertEquals("two", pcre4jResults.get(0).group());
        assertEquals("three", pcre4jResults.get(1).group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void resultsZeroWidthMatches(IPcre2 api) {
        var regex = "(?=\\d)";  // Zero-width positive lookahead for digit
        var input = "a1b2c3";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        for (int i = 0; i < javaResults.size(); i++) {
            assertEquals(javaResults.get(i).start(), pcre4jResults.get(i).start());
            assertEquals(javaResults.get(i).end(), pcre4jResults.get(i).end());
            // Zero-width matches have start == end
            assertEquals(pcre4jResults.get(i).start(), pcre4jResults.get(i).end());
        }
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void resultsEmptyString(IPcre2 api) {
        var regex = ".*";
        var input = "";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void resultsUnicode(IPcre2 api) {
        var regex = "\\p{L}+";
        var input = "hello \u043C\u0438\u0440 \u4E16\u754C";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        for (int i = 0; i < javaResults.size(); i++) {
            assertEquals(javaResults.get(i).group(), pcre4jResults.get(i).group());
            assertEquals(javaResults.get(i).start(), pcre4jResults.get(i).start());
            assertEquals(javaResults.get(i).end(), pcre4jResults.get(i).end());
        }
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void resultsStreamOperations(IPcre2 api) {
        var regex = "\\d+";
        var input = "a1b22c333d4444";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Test that stream operations work correctly
        var javaSum = javaMatcher.results()
                .mapToInt(mr -> Integer.parseInt(mr.group()))
                .sum();
        var pcre4jSum = pcre4jMatcher.results()
                .mapToInt(mr -> Integer.parseInt(mr.group()))
                .sum();

        assertEquals(javaSum, pcre4jSum);
        assertEquals(1 + 22 + 333 + 4444, pcre4jSum);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void resultsCount(IPcre2 api) {
        var regex = "a";
        var input = "abracadabra";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.results().count(), pcre4jMatcher.results().count());
        // Need to create new matchers since results() consumes the matcher state
        javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        assertEquals(5, pcre4jMatcher.results().count());
    }

}
