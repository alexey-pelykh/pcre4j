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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for non-JIT (interpreter) code paths in {@link Matcher}.
 * <p>
 * These tests explicitly disable JIT compilation via the {@code pcre2.regex.jit} system property to exercise
 * the fallback code paths in {@link Matcher#matches()}, {@link Matcher#lookingAt()}, and {@link Matcher#find()}.
 */
public class NonJitMatcherTests {

    private String savedJitProperty;

    @BeforeEach
    void disableJit() {
        savedJitProperty = System.getProperty("pcre2.regex.jit");
        System.setProperty("pcre2.regex.jit", "false");
    }

    @AfterEach
    void restoreJit() {
        if (savedJitProperty != null) {
            System.setProperty("pcre2.regex.jit", savedJitProperty);
        } else {
            System.clearProperty("pcre2.regex.jit");
        }
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchesTrue(IPcre2 api) {
        var regex = "42";
        var input = "42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchesFalse(IPcre2 api) {
        var regex = "42";
        var input = "42!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertFalse(pcre4jMatcher.matches());
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
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchesFalseInRegion(IPcre2 api) {
        var regex = "42";
        var input = "[42!]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(1, 4);
        pcre4jMatcher.region(1, 4);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertFalse(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchesWithGroups(IPcre2 api) {
        var regex = "(\\d+)-(\\w+)";
        var input = "42-abc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.group(1), pcre4jMatcher.group(1));
        assertEquals(javaMatcher.group(2), pcre4jMatcher.group(2));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchesWithNamedGroups(IPcre2 api) {
        var regex = "(?<num>\\d+)-(?<word>\\w+)";
        var input = "42-abc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
        assertEquals(javaMatcher.group("num"), pcre4jMatcher.group("num"));
        assertEquals(javaMatcher.group("word"), pcre4jMatcher.group("word"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void lookingAtTrue(IPcre2 api) {
        var regex = "42";
        var input = "42!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void lookingAtFalse(IPcre2 api) {
        var regex = "42";
        var input = "!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
        assertFalse(pcre4jMatcher.lookingAt());
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
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void lookingAtWithGroups(IPcre2 api) {
        var regex = "(\\d+)";
        var input = "42abc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.lookingAt());
        assertTrue(pcre4jMatcher.lookingAt());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.group(1), pcre4jMatcher.group(1));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findTrue(IPcre2 api) {
        var regex = "\\d+";
        var input = "abc42def";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findFalse(IPcre2 api) {
        var regex = "\\d+";
        var input = "abcdef";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertFalse(pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findMultiple(IPcre2 api) {
        var regex = "\\d+";
        var input = "a1b22c333";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // First match
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());

        // Second match
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());

        // Third match
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());

        // No more matches
        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findWithGroups(IPcre2 api) {
        var regex = "(\\w+)=(\\d+)";
        var input = "a=1 b=2";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.group(1), pcre4jMatcher.group(1));
        assertEquals(javaMatcher.group(2), pcre4jMatcher.group(2));

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.group(1), pcre4jMatcher.group(1));
        assertEquals(javaMatcher.group(2), pcre4jMatcher.group(2));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findInRegion(IPcre2 api) {
        var regex = "\\d+";
        var input = "a1b2c3d";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(2, 5);
        pcre4jMatcher.region(2, 5);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsEnabledCaretMatchesAtRegionStart(IPcre2 api) {
        var regex = "^test";
        var input = "xxxtestxxx";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        javaMatcher.useAnchoringBounds(true);
        pcre4jMatcher.useAnchoringBounds(true);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsDisabledCaretMatchesAtInputStart(IPcre2 api) {
        var regex = "^test";
        var input = "xxxtestxxx";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        javaMatcher.useAnchoringBounds(false);
        pcre4jMatcher.useAnchoringBounds(false);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertFalse(pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsEnabled(IPcre2 api) {
        var regex = "(?<=x)test";
        var input = "xxxtestxxx";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        javaMatcher.useTransparentBounds(true);
        pcre4jMatcher.useTransparentBounds(true);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsDisabled(IPcre2 api) {
        var regex = "(?<=x)test";
        var input = "xxxtestxxx";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        javaMatcher.useTransparentBounds(false);
        pcre4jMatcher.useTransparentBounds(false);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertFalse(pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchesWithTransparentBounds(IPcre2 api) {
        var regex = "test";
        var input = "xxxtestxxx";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        javaMatcher.useTransparentBounds(true);
        pcre4jMatcher.useTransparentBounds(true);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void groupExtractionIdenticalToJit(IPcre2 api) {
        var regex = "(\\d{4})-(\\d{2})-(\\d{2})";
        var input = "date: 2024-01-15 end";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.group(1), pcre4jMatcher.group(1));
        assertEquals(javaMatcher.group(2), pcre4jMatcher.group(2));
        assertEquals(javaMatcher.group(3), pcre4jMatcher.group(3));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.start(1), pcre4jMatcher.start(1));
        assertEquals(javaMatcher.end(1), pcre4jMatcher.end(1));
        assertEquals(javaMatcher.start(2), pcre4jMatcher.start(2));
        assertEquals(javaMatcher.end(2), pcre4jMatcher.end(2));
        assertEquals(javaMatcher.start(3), pcre4jMatcher.start(3));
        assertEquals(javaMatcher.end(3), pcre4jMatcher.end(3));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultFromMatches(IPcre2 api) {
        var regex = "(\\w+)@(\\w+)";
        var input = "user@host";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.matches());
        assertTrue(pcre4jMatcher.matches());

        var javaResult = javaMatcher.toMatchResult();
        var pcre4jResult = pcre4jMatcher.toMatchResult();

        assertEquals(javaResult.start(), pcre4jResult.start());
        assertEquals(javaResult.end(), pcre4jResult.end());
        assertEquals(javaResult.group(), pcre4jResult.group());
        assertEquals(javaResult.group(1), pcre4jResult.group(1));
        assertEquals(javaResult.group(2), pcre4jResult.group(2));
        assertEquals(javaResult.groupCount(), pcre4jResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultFromLookingAt(IPcre2 api) {
        var regex = "(\\d+)";
        var input = "42abc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.lookingAt());
        assertTrue(pcre4jMatcher.lookingAt());

        var javaResult = javaMatcher.toMatchResult();
        var pcre4jResult = pcre4jMatcher.toMatchResult();

        assertEquals(javaResult.start(), pcre4jResult.start());
        assertEquals(javaResult.end(), pcre4jResult.end());
        assertEquals(javaResult.group(), pcre4jResult.group());
        assertEquals(javaResult.group(1), pcre4jResult.group(1));
        assertEquals(javaResult.groupCount(), pcre4jResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultFromFind(IPcre2 api) {
        var regex = "(\\d+)";
        var input = "abc42def";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        var javaResult = javaMatcher.toMatchResult();
        var pcre4jResult = pcre4jMatcher.toMatchResult();

        assertEquals(javaResult.start(), pcre4jResult.start());
        assertEquals(javaResult.end(), pcre4jResult.end());
        assertEquals(javaResult.group(), pcre4jResult.group());
        assertEquals(javaResult.group(1), pcre4jResult.group(1));
        assertEquals(javaResult.groupCount(), pcre4jResult.groupCount());
    }
}
