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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@code toMatchResult()} snapshot behavior in {@link Matcher}.
 */
public class MatcherMatchResultTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultSnapshotDoesNotChangeWhenMatcherAdvances(IPcre2 api) {
        var regex = "\\w+";
        var input = "hello world";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        var javaSnapshot = javaMatcher.toMatchResult();
        var pcre4jSnapshot = pcre4jMatcher.toMatchResult();

        assertEquals(javaSnapshot.group(), pcre4jSnapshot.group());
        assertEquals(javaSnapshot.start(), pcre4jSnapshot.start());
        assertEquals(javaSnapshot.end(), pcre4jSnapshot.end());

        // Advance the matcher to next match
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        // Snapshot should still reflect the first match
        assertEquals(javaSnapshot.group(), pcre4jSnapshot.group());
        assertEquals(javaSnapshot.start(), pcre4jSnapshot.start());
        assertEquals(javaSnapshot.end(), pcre4jSnapshot.end());

        // But the matcher itself has moved forward
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals("world", pcre4jMatcher.group());
        assertEquals("hello", pcre4jSnapshot.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultNamedGroupAccessors(IPcre2 api) {
        var regex = "(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})";
        var input = "date: 2024-01-15";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        var javaResult = javaMatcher.toMatchResult();
        var pcre4jResult = pcre4jMatcher.toMatchResult();

        // Test group(String)
        assertEquals(javaResult.group("year"), pcre4jResult.group("year"));
        assertEquals(javaResult.group("month"), pcre4jResult.group("month"));
        assertEquals(javaResult.group("day"), pcre4jResult.group("day"));

        // Test start(String)
        assertEquals(javaResult.start("year"), pcre4jResult.start("year"));
        assertEquals(javaResult.start("month"), pcre4jResult.start("month"));
        assertEquals(javaResult.start("day"), pcre4jResult.start("day"));

        // Test end(String)
        assertEquals(javaResult.end("year"), pcre4jResult.end("year"));
        assertEquals(javaResult.end("month"), pcre4jResult.end("month"));
        assertEquals(javaResult.end("day"), pcre4jResult.end("day"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultByGroupNumber(IPcre2 api) {
        var regex = "(\\w+)\\s+(\\w+)\\s+(\\w+)";
        var input = "one two three";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        var javaResult = javaMatcher.toMatchResult();
        var pcre4jResult = pcre4jMatcher.toMatchResult();

        // Test group(int)
        for (int i = 0; i <= javaResult.groupCount(); i++) {
            assertEquals(javaResult.group(i), pcre4jResult.group(i));
            assertEquals(javaResult.start(i), pcre4jResult.start(i));
            assertEquals(javaResult.end(i), pcre4jResult.end(i));
        }

        assertEquals(javaResult.groupCount(), pcre4jResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultThrowsIllegalStateExceptionWhenNoMatch(IPcre2 api) {
        var regex = "\\d+";
        var input = "hello";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // No find() call, so no match has occurred
        var result = pcre4jMatcher.toMatchResult();

        assertThrows(IllegalStateException.class, () -> result.start());
        assertThrows(IllegalStateException.class, () -> result.end());
        assertThrows(IllegalStateException.class, () -> result.group());
        assertThrows(IllegalStateException.class, () -> result.start(0));
        assertThrows(IllegalStateException.class, () -> result.end(0));
        assertThrows(IllegalStateException.class, () -> result.group(0));
        assertThrows(IllegalStateException.class, () -> result.start("name"));
        assertThrows(IllegalStateException.class, () -> result.end("name"));
        assertThrows(IllegalStateException.class, () -> result.group("name"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultThrowsIndexOutOfBoundsForInvalidGroup(IPcre2 api) {
        var regex = "(\\w+)";
        var input = "hello";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(pcre4jMatcher.find());
        var result = pcre4jMatcher.toMatchResult();

        // Group count is 1, so group 2 should throw
        assertThrows(IndexOutOfBoundsException.class, () -> result.start(5));
        assertThrows(IndexOutOfBoundsException.class, () -> result.end(5));
        assertThrows(IndexOutOfBoundsException.class, () -> result.group(5));

        // Negative group
        assertThrows(IndexOutOfBoundsException.class, () -> result.start(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> result.end(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> result.group(-1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultThrowsForInvalidNamedGroup(IPcre2 api) {
        var regex = "(?<word>\\w+)";
        var input = "hello";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(pcre4jMatcher.find());
        var result = pcre4jMatcher.toMatchResult();

        assertThrows(IllegalArgumentException.class, () -> result.start("nonexistent"));
        assertThrows(IllegalArgumentException.class, () -> result.end("nonexistent"));
        assertThrows(IllegalArgumentException.class, () -> result.group("nonexistent"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultNamedGroups(IPcre2 api) {
        var regex = "(?<first>\\w+) (?<second>\\w+)";
        var input = "hello world";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        var javaResult = javaMatcher.toMatchResult();
        var pcre4jResult = pcre4jMatcher.toMatchResult();

        assertEquals(javaResult.namedGroups(), pcre4jResult.namedGroups());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultHasMatchFalseWhenNoMatch(IPcre2 api) {
        var regex = "xyz";
        var input = "hello";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertFalse(pcre4jMatcher.find());
        var result = pcre4jMatcher.toMatchResult();

        assertFalse(result.hasMatch());
        assertEquals(0, result.groupCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultHasMatchTrueAfterMatch(IPcre2 api) {
        var regex = "\\w+";
        var input = "hello";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(pcre4jMatcher.find());
        var result = pcre4jMatcher.toMatchResult();

        assertTrue(result.hasMatch());
    }

}
