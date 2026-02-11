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
package org.pcre4j.test;

import org.junit.jupiter.api.Test;
import org.pcre4j.api.IPcre2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract tests for PCRE2 DFA (Deterministic Finite Automaton) matching operations.
 *
 * @param <T> the PCRE2 API implementation type
 */
public interface Pcre2DfaMatchingContractTest<T extends IPcre2> {

    /**
     * Returns the PCRE2 API implementation to test.
     *
     * @return the PCRE2 API implementation
     */
    T getApi();

    @Test
    default void dfaMatchBasic() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        // DFA matching requires a workspace array
        final var workspace = new int[100];
        final var result = api.dfaMatch(code, "hello world", 0, 0, matchData, 0, workspace, workspace.length);

        // DFA match should succeed and return 1 for a simple match
        assertTrue(result > 0, "DFA match should succeed (result=" + result + ")");

        // Verify match position using ovector
        final var ovector = new long[2];
        api.getOvector(matchData, ovector);
        assertEquals(0, ovector[0], "Match should start at position 0");
        assertEquals(5, ovector[1], "Match should end at position 5");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void dfaMatchNoMatch() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("xyz", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];
        final var result = api.dfaMatch(code, "hello world", 0, 0, matchData, 0, workspace, workspace.length);

        assertEquals(IPcre2.ERROR_NOMATCH, result, "DFA match should return NOMATCH");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void dfaMatchWithStartOffset() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("world", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];
        // Start matching from offset 6
        final var result = api.dfaMatch(code, "hello world", 6, 0, matchData, 0, workspace, workspace.length);

        assertTrue(result > 0, "DFA match should succeed (result=" + result + ")");

        // Verify match position
        final var ovector = new long[2];
        api.getOvector(matchData, ovector);
        assertEquals(6, ovector[0], "Match should start at position 6");
        assertEquals(11, ovector[1], "Match should end at position 11");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void dfaMatchAlternatives() {
        var api = getApi();
        // DFA matching can find all alternative matches - use a pattern with alternation
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        // Pattern matches "ab", "abc", or "abcd"
        final var code = api.compile("ab(c(d)?)?", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        // Create match data with enough space for multiple matches
        // DFA can return multiple matches, so we need space in ovector
        final var matchData = api.matchDataCreate(10, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];
        final var result = api.dfaMatch(code, "abcd", 0, 0, matchData, 0, workspace, workspace.length);

        // DFA should find multiple matches (ab, abc, abcd)
        assertTrue(result >= 1, "DFA match should find at least one match (result=" + result + ")");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void dfaMatchWorkspaceTooSmall() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("(a+)+", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        // Use a very small workspace
        final var workspace = new int[1];
        final var result = api.dfaMatch(code, "aaaaaaaaaa", 0, 0, matchData, 0, workspace, workspace.length);

        // Should return DFA workspace too small error
        assertEquals(IPcre2.ERROR_DFA_WSSIZE, result, "Should return DFA_WSSIZE error");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void dfaMatchNullSubject() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];

        assertThrows(IllegalArgumentException.class,
                () -> api.dfaMatch(code, null, 0, 0, matchData, 0, workspace, workspace.length));

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void dfaMatchNullWorkspace() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        assertThrows(IllegalArgumentException.class,
                () -> api.dfaMatch(code, "hello world", 0, 0, matchData, 0, null, 100));

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void dfaMatchNegativeWscount() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];

        assertThrows(IllegalArgumentException.class,
                () -> api.dfaMatch(code, "hello world", 0, 0, matchData, 0, workspace, -1));

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void dfaMatchWscountTooLarge() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];

        // wscount larger than workspace array
        assertThrows(IllegalArgumentException.class,
                () -> api.dfaMatch(code, "hello world", 0, 0, matchData, 0, workspace, 200));

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void dfaMatchUnicode() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("🌐+", IPcre2.UTF, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];
        final var result = api.dfaMatch(code, "hello 🌐🌐🌐 world", 0, 0, matchData, 0, workspace, workspace.length);

        assertTrue(result > 0, "DFA match should succeed (result=" + result + ")");

        // Verify match position (UTF-8 encoding: each emoji is 4 bytes)
        final var ovector = new long[2];
        api.getOvector(matchData, ovector);
        assertEquals(6, ovector[0], "Match should start at byte offset 6");
        assertEquals(18, ovector[1], "Match should end at byte offset 18 (6 + 3*4)");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void dfaMatchShortest() {
        var api = getApi();
        // Test DFA_SHORTEST option which returns shortest match
        // DFA_SHORTEST only has effect with patterns that have alternatives of different lengths
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        // Pattern with explicit alternatives of different lengths
        final var code = api.compile("a|aa|aaa|aaaa", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreate(10, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];

        // Without DFA_SHORTEST, DFA returns all matches with longest first (rc=count of matches)
        final var resultLongest = api.dfaMatch(code, "aaaa", 0, 0, matchData, 0, workspace, workspace.length);
        assertEquals(4, resultLongest, "DFA without DFA_SHORTEST should return all 4 alternative matches");

        final var ovectorLongest = new long[2];
        api.getOvector(matchData, ovectorLongest);
        assertEquals(4, ovectorLongest[1], "Without DFA_SHORTEST, first match should be longest (4 a's)");

        // With DFA_SHORTEST, DFA returns only the shortest match
        final var resultShortest = api.dfaMatch(
                code, "aaaa", 0, IPcre2.DFA_SHORTEST, matchData, 0, workspace, workspace.length);
        assertEquals(1, resultShortest, "DFA with DFA_SHORTEST should return only 1 match");

        final var ovectorShortest = new long[2];
        api.getOvector(matchData, ovectorShortest);
        assertEquals(1, ovectorShortest[1], "With DFA_SHORTEST, should match only 1 'a'");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }
}
