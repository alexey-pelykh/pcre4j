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
package org.pcre4j;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Pcre2CodeDfaMatchTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchBasic(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        var result = code.dfaMatch("hello world");

        assertNotNull(result, "DFA match should succeed");
        assertEquals(0, result.start(), "Match should start at position 0");
        assertEquals(5, result.longestEnd(), "Match should end at position 5");
        assertEquals("hello", result.longestMatch());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchNoMatch(IPcre2 api) {
        var code = new Pcre2Code(api, "xyz");
        var result = code.dfaMatch("hello world");

        assertNull(result, "DFA match should return null when no match");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchWithStartOffset(IPcre2 api) {
        var code = new Pcre2Code(api, "world");
        var result = code.dfaMatch("hello world", 6, null, null, 1000);

        assertNotNull(result, "DFA match should succeed");
        assertEquals(6, result.start(), "Match should start at position 6");
        assertEquals(11, result.longestEnd(), "Match should end at position 11");
        assertEquals("world", result.longestMatch());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchMultipleAlternatives(IPcre2 api) {
        // Pattern with explicit alternatives of different lengths
        var code = new Pcre2Code(api, "a|aa|aaa|aaaa");
        var result = code.dfaMatch("aaaa", 0, null, null, 1000);

        assertNotNull(result, "DFA match should succeed");
        assertEquals(0, result.start(), "Match should start at position 0");
        assertTrue(result.count() >= 1, "DFA should find at least one match");
        assertEquals(4, result.longestEnd(), "Longest match should be 4 a's");
        assertEquals(1, result.shortestEnd(), "Shortest match should be 1 a");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchShortestOption(IPcre2 api) {
        // With DFA_SHORTEST, only the shortest match is returned
        var code = new Pcre2Code(api, "a|aa|aaa|aaaa");
        var result = code.dfaMatch("aaaa", 0, EnumSet.of(Pcre2DfaMatchOption.SHORTEST), null, 1000);

        assertNotNull(result, "DFA match should succeed");
        assertEquals(1, result.count(), "DFA with SHORTEST should return only 1 match");
        assertEquals(1, result.longestEnd(), "Shortest match should be 1 'a'");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchWithOptionalGroups(IPcre2 api) {
        // Pattern matches "ab", "abc", or "abcd" - DFA returns multiple match lengths
        var code = new Pcre2Code(api, "ab(c(d)?)?");
        var result = code.dfaMatch("abcd", 0, null, null, 1000);

        assertNotNull(result, "DFA match should succeed");
        assertEquals(0, result.start(), "Match should start at position 0");
        assertTrue(result.count() >= 1, "DFA should find at least one match");
        assertEquals(4, result.longestEnd(), "Longest match should be 'abcd' (end=4)");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchUnicode(IPcre2 api) {
        var code = new Pcre2Code(api, "\\x{1F310}+", EnumSet.of(Pcre2CompileOption.UTF));
        var result = code.dfaMatch("hello \uD83C\uDF10\uD83C\uDF10\uD83C\uDF10 world");

        assertNotNull(result, "DFA match should succeed for Unicode");
        assertEquals("hello ".length(), result.start(), "Match should start after 'hello '");
        assertEquals("hello \uD83C\uDF10\uD83C\uDF10\uD83C\uDF10".length(), result.longestEnd(),
                "Match should end after all emojis");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchNullSubjectThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        assertThrows(IllegalArgumentException.class, () -> code.dfaMatch(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchNegativeStartOffsetThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        assertThrows(IllegalArgumentException.class, () ->
                code.dfaMatch("hello", -1, null, null, 1000));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchStartOffsetPastEndThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        assertThrows(IllegalArgumentException.class, () ->
                code.dfaMatch("abc", 4, null, null, 1000));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchZeroWorkspaceSizeThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        assertThrows(IllegalArgumentException.class, () ->
                code.dfaMatch("hello", 0, null, null, 0));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchResultMethods(IPcre2 api) {
        var code = new Pcre2Code(api, "a|aa|aaa");
        var result = code.dfaMatch("aaa", 0, null, null, 1000);

        assertNotNull(result, "DFA match should succeed");
        assertEquals("aaa", result.subject());
        assertEquals(0, result.start());

        // Verify the match(index) method works for all results
        for (int i = 0; i < result.count(); i++) {
            var matched = result.match(i);
            assertNotNull(matched, "match(" + i + ") should not be null");
            assertTrue(matched.startsWith("a"), "match(" + i + ") should start with 'a'");
        }

        // Longest and shortest accessors
        assertEquals(result.match(0), result.longestMatch());
        assertEquals(result.match(result.count() - 1), result.shortestMatch());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchPartialSoft(IPcre2 api) {
        var code = new Pcre2Code(api, "hello world");
        var result = code.dfaMatch("hello", 0, EnumSet.of(Pcre2DfaMatchOption.PARTIAL_SOFT), null, 1000);

        // Partial match: "hello" is a prefix of "hello world"
        assertNotNull(result, "Partial DFA match should return a result");
        assertTrue(result.isPartial(), "Result should be marked as partial");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchWithMatchContext(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        var matchContext = new Pcre2MatchContext(api, null);
        var result = code.dfaMatch("hello world", 0, null, matchContext, 1000);

        assertNotNull(result, "DFA match with match context should succeed");
        assertEquals("hello", result.longestMatch());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchUnsupportedPatternThrows(IPcre2 api) {
        // Backreferences are not supported in DFA matching
        var code = new Pcre2Code(api, "(a)\\1");
        assertThrows(Pcre2MatchException.class, () -> code.dfaMatch("aa"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchNotPartialByDefault(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        var result = code.dfaMatch("hello");

        assertNotNull(result, "DFA match should succeed");
        assertEquals(false, result.isPartial(), "Full match should not be partial");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchStartOffsetAtEnd(IPcre2 api) {
        // Pattern $ matches at end of string
        var code = new Pcre2Code(api, "$");
        var subject = "abc";
        var result = code.dfaMatch(subject, subject.length(), null, null, 1000);

        assertNotNull(result, "Pattern $ should match at end of string with DFA");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchEmptySubject(IPcre2 api) {
        var code = new Pcre2Code(api, "^$");
        var result = code.dfaMatch("");

        assertNotNull(result, "Empty string should match pattern ^$ with DFA");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dfaMatchEnumOptions(IPcre2 api) {
        // Verify DFA options combine correctly
        var code = new Pcre2Code(api, "hello");
        var options = EnumSet.of(Pcre2DfaMatchOption.ANCHORED, Pcre2DfaMatchOption.NOTBOL);

        // ANCHORED + NOTBOL: match only at first position but not at beginning of line
        // For pattern "hello" in "hello", ANCHORED works but NOTBOL shouldn't affect this
        var result = code.dfaMatch("hello", 0, options, null, 1000);
        assertNotNull(result, "DFA match with combined options should succeed");
    }
}
