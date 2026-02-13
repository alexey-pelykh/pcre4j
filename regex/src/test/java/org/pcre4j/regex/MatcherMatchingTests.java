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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.pcre4j.regex.MatcherTestUtils.assertGroups;
import static org.pcre4j.regex.MatcherTestUtils.assertMatchResultState;
import static org.pcre4j.regex.MatcherTestUtils.assertMatcherState;
import static org.pcre4j.regex.MatcherTestUtils.assertNoMatchResultState;
import static org.pcre4j.regex.MatcherTestUtils.assertNoMatchState;

/**
 * Tests for core matching operations ({@code matches()}, {@code lookingAt()}, {@code find()}) in {@link Matcher}.
 */
public class MatcherMatchingTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchesTrue(IPcre2 api) {
        var regex = "42";
        var input = "42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchesFalse(IPcre2 api) {
        var regex = "42";
        var input = "42!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertNoMatchState(javaMatcher, pcre4jMatcher);

        assertNoMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchesTrueInRegion(IPcre2 api) {
        var regex = "42";
        var input = "[42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(1, 3);
        pcre4jMatcher.region(1, 3);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchesFalseRegion(IPcre2 api) {
        var regex = "42";
        var input = "[42!]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(1, 4);
        pcre4jMatcher.region(1, 4);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertNoMatchState(javaMatcher, pcre4jMatcher);

        assertNoMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void lookingAtTrue(IPcre2 api) {
        var regex = "42";
        var input = "42!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void lookingAtFalse(IPcre2 api) {
        var regex = "42";
        var input = "!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
        assertNoMatchState(javaMatcher, pcre4jMatcher);

        assertNoMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void lookingAtTrueInRegion(IPcre2 api) {
        var regex = "42";
        var input = "[42!]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(1, 4);
        pcre4jMatcher.region(1, 4);

        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void lookingAtFalseRegion(IPcre2 api) {
        var regex = "42";
        var input = "[!42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(1, 4);
        pcre4jMatcher.region(1, 4);

        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
        assertNoMatchState(javaMatcher, pcre4jMatcher);

        assertNoMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findTrue(IPcre2 api) {
        var regex = "42";
        var input = "42!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findFalse(IPcre2 api) {
        var regex = "42!";
        var input = "42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertNoMatchState(javaMatcher, pcre4jMatcher);

        assertNoMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findTrueInRegion(IPcre2 api) {
        var regex = "42";
        var input = "[42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(1, 3);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findFalseInRegion(IPcre2 api) {
        var regex = "42!";
        var input = "[42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(1, 3);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertNoMatchState(javaMatcher, pcre4jMatcher);

        assertNoMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findTrueAtOffset(IPcre2 api) {
        var regex = "42";
        var input = "!!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(2), pcre4jMatcher.find(2));
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findFalseAtOffset(IPcre2 api) {
        var regex = "42";
        var input = "!!test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(2), pcre4jMatcher.find(2));
        assertNoMatchState(javaMatcher, pcre4jMatcher);

        assertNoMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findMultiple(IPcre2 api) {
        var regex = "42";
        var input = "42!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findMultipleWithinRegion(IPcre2 api) {
        var regex = "42";
        var input = "42!42!42!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(2, 8);
        pcre4jMatcher.region(2, 8);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertMatcherState(javaMatcher, pcre4jMatcher);

        assertMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findMultipleOutsideRegion(IPcre2 api) {
        var regex = "42";
        var input = "42!__!__!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(2, 8);
        pcre4jMatcher.region(2, 8);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertNoMatchState(javaMatcher, pcre4jMatcher);

        assertNoMatchResultState(javaMatcher.toMatchResult(), pcre4jMatcher.toMatchResult());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void captureGroups(IPcre2 api) {
        var regex = "(?<four>4)(.*)(?<two>2)";
        var input = "4test2";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
        assertGroups(javaMatcher, pcre4jMatcher);
        assertEquals(javaMatcher.start("four"), pcre4jMatcher.start("four"));
        assertEquals(javaMatcher.start("two"), pcre4jMatcher.start("two"));
        assertEquals(javaMatcher.end("four"), pcre4jMatcher.end("four"));
        assertEquals(javaMatcher.end("two"), pcre4jMatcher.end("two"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyGroup(IPcre2 api) {
        var regex = "!*";
        var input = "42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
        assertGroups(javaMatcher, pcre4jMatcher);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void singleUnmatchedGroup(IPcre2 api) {
        var regex = "(42)?";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
        assertGroups(javaMatcher, pcre4jMatcher);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unmatchedGroups(IPcre2 api) {
        var regex = "42((?<exclamation>!)|(?<question>\\?))";
        var input = "42!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
        assertGroups(javaMatcher, pcre4jMatcher);
        assertEquals(javaMatcher.start("exclamation"), pcre4jMatcher.start("exclamation"));
        assertEquals(javaMatcher.start("question"), pcre4jMatcher.start("question"));
        assertEquals(javaMatcher.end("exclamation"), pcre4jMatcher.end("exclamation"));
        assertEquals(javaMatcher.end("question"), pcre4jMatcher.end("question"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void positiveLookaround(IPcre2 api) {
        var regex = "(?<=(?<lWrapper>\\W))?(\\d+)(?=(?<rWrapper>\\W))?";
        var input = "(42)";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
        assertGroups(javaMatcher, pcre4jMatcher);
        assertEquals(javaMatcher.start("lWrapper"), pcre4jMatcher.start("lWrapper"));
        assertEquals(javaMatcher.start("rWrapper"), pcre4jMatcher.start("rWrapper"));
        assertEquals(javaMatcher.end("lWrapper"), pcre4jMatcher.end("lWrapper"));
        assertEquals(javaMatcher.end("rWrapper"), pcre4jMatcher.end("rWrapper"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void positiveUnmatchedLookaround(IPcre2 api) {
        var regex = "(?<=(?<lWrapper>\\W))?(\\d+)(?=(?<rWrapper>\\W))?";
        var input = "42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
        assertGroups(javaMatcher, pcre4jMatcher);
        assertEquals(javaMatcher.start("lWrapper"), pcre4jMatcher.start("lWrapper"));
        assertEquals(javaMatcher.start("rWrapper"), pcre4jMatcher.start("rWrapper"));
        assertEquals(javaMatcher.end("lWrapper"), pcre4jMatcher.end("lWrapper"));
        assertEquals(javaMatcher.end("rWrapper"), pcre4jMatcher.end("rWrapper"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyStringMatches(IPcre2 api) {
        var regex = "^$";
        var input = "";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyStringFind(IPcre2 api) {
        var regex = "^$";
        var input = "";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findAtEndOfString(IPcre2 api) {
        var regex = "$";
        var input = "abc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // find(start) at end of string should work for $ pattern
        assertEquals(javaMatcher.find(input.length()), pcre4jMatcher.find(input.length()));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findExhaustedInRegion(IPcre2 api) {
        // Test find() behavior when iterating through all matches in a region
        var regex = "a";
        var input = "aaa";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to first two characters only
        javaMatcher.region(0, 2);
        pcre4jMatcher.region(0, 2);

        // First find() should succeed (matches 'a' at position 0)
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        // Second find() should succeed (matches 'a' at position 1)
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        // Third find() should fail - no more matches in region
        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findWithZeroWidthMatchExhaustsRegion(IPcre2 api) {
        // Test that after a zero-width match at regionEnd, the next find() returns false
        // This exercises the start > regionEnd branch in find():
        // After zero-width match at [1,1], start becomes 1, but since start == lastMatchIndices[0],
        // it's incremented to 2. If regionEnd is 1, then start (2) > regionEnd (1) is true.
        var regex = "$";  // Zero-width match at end
        var input = "ab";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to just "a" (positions 0-1)
        javaMatcher.region(0, 1);
        pcre4jMatcher.region(0, 1);

        // First find() matches $ at position 1 (end of region) - zero-width match at [1,1]
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(1, javaMatcher.start());
        assertEquals(1, pcre4jMatcher.start());
        assertEquals(1, javaMatcher.end());
        assertEquals(1, pcre4jMatcher.end());

        // Second find(): lastMatchIndices = [1,1], so start = 1
        // Since start (1) == lastMatchIndices[0] (1), start is incremented to 2
        // Now start (2) > regionEnd (1) is TRUE, triggering the early return
        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());
    }

}
