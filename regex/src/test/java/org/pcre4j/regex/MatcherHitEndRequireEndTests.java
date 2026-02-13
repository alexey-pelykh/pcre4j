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

/**
 * Tests for {@code hitEnd()} and {@code requireEnd()} behavior in {@link Matcher}.
 */
public class MatcherHitEndRequireEndTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndBeforeMatchOperation(IPcre2 api) {
        // Before any match operation, hitEnd should return false
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("testing");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("testing");

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertFalse(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndBeforeMatchOperation(IPcre2 api) {
        // Before any match operation, requireEnd should return false
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("testing");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("testing");

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertFalse(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndAfterPartialMatch(IPcre2 api) {
        // Pattern "AAB" against input "AA" - no full match, but partial match exists
        var javaMatcher = java.util.regex.Pattern.compile("AAB").matcher("AA");
        var pcre4jMatcher = Pattern.compile(api, "AAB").matcher("AA");

        assertFalse(javaMatcher.matches());
        assertFalse(pcre4jMatcher.matches());

        // hitEnd should be true because more input could lead to a match
        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertTrue(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndAfterSuccessfulMatchAtEnd(IPcre2 api) {
        // Pattern "test" matches exactly at end of input - hitEnd is false in Java
        // because the literal pattern matched completely
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertFalse(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndAfterSuccessfulMatchNotAtEnd(IPcre2 api) {
        // Pattern matches but not at the end - hitEnd should be false
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("test123");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("test123");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertFalse(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithNoMatchNoPartial(IPcre2 api) {
        // Pattern cannot possibly match - hitEnd is still true in Java because
        // the search engine had to examine the entire input to determine no match
        var javaMatcher = java.util.regex.Pattern.compile("xyz").matcher("abc");
        var pcre4jMatcher = Pattern.compile(api, "xyz").matcher("abc");

        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertTrue(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithDollarAnchor(IPcre2 api) {
        // Pattern with $ anchor matching at end - requireEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("test$").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test$").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertTrue(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithoutAnchor(IPcre2 api) {
        // Pattern without end anchor - requireEnd should be false even if match at end
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertFalse(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithBackslashZ(IPcre2 api) {
        // Pattern with \z anchor (absolute end) - requireEnd is FALSE in Java
        // because \z only matches at the absolute end, so more input cannot
        // invalidate the match (it would just not match the \z anymore)
        var javaMatcher = java.util.regex.Pattern.compile("test\\z").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test\\z").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertFalse(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithBackslashZUppercase(IPcre2 api) {
        // Pattern with \Z anchor (end before final newline) - requireEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("test\\Z").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test\\Z").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertTrue(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithLookingAt(IPcre2 api) {
        // Test hitEnd with lookingAt
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("test");

        assertTrue(javaMatcher.lookingAt());
        assertTrue(pcre4jMatcher.lookingAt());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithMatches(IPcre2 api) {
        // Test hitEnd with matches
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("test");

        assertTrue(javaMatcher.matches());
        assertTrue(pcre4jMatcher.matches());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndPersistsAfterReset(IPcre2 api) {
        // In Java, hitEnd persists after reset() - it is NOT cleared
        var javaMatcher = java.util.regex.Pattern.compile("test$").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test$").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        // Verify hitEnd is true after the match (pattern has $ anchor)
        assertTrue(javaMatcher.hitEnd());
        assertTrue(pcre4jMatcher.hitEnd());

        javaMatcher.reset();
        pcre4jMatcher.reset();

        // hitEnd persists after reset
        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertTrue(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndPersistsAfterReset(IPcre2 api) {
        // In Java, requireEnd persists after reset() - it is NOT cleared
        var javaMatcher = java.util.regex.Pattern.compile("test$").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test$").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        // Verify requireEnd is true after the match (pattern has $ anchor)
        assertTrue(javaMatcher.requireEnd());
        assertTrue(pcre4jMatcher.requireEnd());

        javaMatcher.reset();
        pcre4jMatcher.reset();

        // requireEnd persists after reset
        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertTrue(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithGreedyQuantifier(IPcre2 api) {
        // Greedy quantifier at end - hitEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("a+").matcher("aaa");
        var pcre4jMatcher = Pattern.compile(api, "a+").matcher("aaa");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithCharacterClass(IPcre2 api) {
        // Character class at end - hitEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("[a-z]+").matcher("abc");
        var pcre4jMatcher = Pattern.compile(api, "[a-z]+").matcher("abc");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithDot(IPcre2 api) {
        // Dot at end can match more - hitEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("a.").matcher("ab");
        var pcre4jMatcher = Pattern.compile(api, "a.").matcher("ab");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndAndRequireEndWithDollarNoMatch(IPcre2 api) {
        // Pattern ends with $ but doesn't match - requireEnd meaningless, hitEnd indicates partial
        var javaMatcher = java.util.regex.Pattern.compile("xyz$").matcher("abc");
        var pcre4jMatcher = Pattern.compile(api, "xyz$").matcher("abc");

        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithRegion(IPcre2 api) {
        // Test hitEnd with region
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("XXtestXX");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("XXtestXX");

        javaMatcher.region(2, 6);
        pcre4jMatcher.region(2, 6);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithDollarInCharacterClass(IPcre2 api) {
        // $ inside character class is literal, not anchor - requireEnd should be false
        var javaMatcher = java.util.regex.Pattern.compile("[$]").matcher("$");
        var pcre4jMatcher = Pattern.compile(api, "[$]").matcher("$");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertFalse(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndAfterMultipleFinds(IPcre2 api) {
        // Multiple finds - hitEnd should reflect the last find
        var javaMatcher = java.util.regex.Pattern.compile("a").matcher("aXa");
        var pcre4jMatcher = Pattern.compile(api, "a").matcher("aXa");

        // First find - not at end
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertFalse(pcre4jMatcher.hitEnd());

        // Second find - at end
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());

        // Third find - no match
        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());
        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithCharacterClassNoQuantifier(IPcre2 api) {
        // Character class without quantifier at end - matches exactly one char, hitEnd should be false
        var javaMatcher = java.util.regex.Pattern.compile("[a-z]").matcher("a");
        var pcre4jMatcher = Pattern.compile(api, "[a-z]").matcher("a");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertFalse(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithWordEscape(IPcre2 api) {
        // \w at end without quantifier - matches exactly one char
        var javaMatcher = java.util.regex.Pattern.compile("\\w").matcher("a");
        var pcre4jMatcher = Pattern.compile(api, "\\w").matcher("a");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithDigitEscape(IPcre2 api) {
        // \d at end without quantifier
        var javaMatcher = java.util.regex.Pattern.compile("\\d").matcher("5");
        var pcre4jMatcher = Pattern.compile(api, "\\d").matcher("5");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithSpaceEscape(IPcre2 api) {
        // \s at end without quantifier
        var javaMatcher = java.util.regex.Pattern.compile("\\s").matcher(" ");
        var pcre4jMatcher = Pattern.compile(api, "\\s").matcher(" ");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithLookingAtQuantifier(IPcre2 api) {
        // lookingAt with quantifier at end
        var javaMatcher = java.util.regex.Pattern.compile("a+").matcher("aaa");
        var pcre4jMatcher = Pattern.compile(api, "a+").matcher("aaa");

        assertTrue(javaMatcher.lookingAt());
        assertTrue(pcre4jMatcher.lookingAt());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithMatchesQuantifier(IPcre2 api) {
        // matches with quantifier
        var javaMatcher = java.util.regex.Pattern.compile("a+").matcher("aaa");
        var pcre4jMatcher = Pattern.compile(api, "a+").matcher("aaa");

        assertTrue(javaMatcher.matches());
        assertTrue(pcre4jMatcher.matches());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithMatchesDollar(IPcre2 api) {
        // matches() with $ - requireEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("test$").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test$").matcher("test");

        assertTrue(javaMatcher.matches());
        assertTrue(pcre4jMatcher.matches());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertTrue(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithLookingAtDollar(IPcre2 api) {
        // lookingAt() with $ - requireEnd should be true when matched at end
        var javaMatcher = java.util.regex.Pattern.compile("test$").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test$").matcher("test");

        assertTrue(javaMatcher.lookingAt());
        assertTrue(pcre4jMatcher.lookingAt());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithBraceQuantifier(IPcre2 api) {
        // Pattern with {n,} quantifier - hitEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("a{2,}").matcher("aaa");
        var pcre4jMatcher = Pattern.compile(api, "a{2,}").matcher("aaa");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithMatchNotAtEnd(IPcre2 api) {
        // Match found but not at end of input - hitEnd should be false
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("testXYZ");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("testXYZ");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertFalse(pcre4jMatcher.hitEnd());
    }

    // Empty region anchor semantics tests (regression tests for issue #69)

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyRegionAtEndWithAnchors_find(IPcre2 api) {
        // Regression test for issue #69: ^$ pattern should match empty region at end of input
        var regex = "^$";
        var input = "abc";

        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        javaMatcher.region(3, 3);

        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        pcre4jMatcher.region(3, 3);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult); // Java returns true for ^$ matching empty region
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyRegionAtEndWithAnchors_matches(IPcre2 api) {
        // Regression test for issue #69: ^$ pattern should match empty region at end of input
        var regex = "^$";
        var input = "abc";

        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        javaMatcher.region(3, 3);

        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        pcre4jMatcher.region(3, 3);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyRegionAtStartWithAnchors_find(IPcre2 api) {
        // Test ^$ pattern with empty region at start of input
        var regex = "^$";
        var input = "abc";

        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        javaMatcher.region(0, 0);

        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        pcre4jMatcher.region(0, 0);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyRegionInMiddleWithAnchors_find(IPcre2 api) {
        // Test ^$ pattern with empty region in middle of input
        var regex = "^$";
        var input = "abc";

        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        javaMatcher.region(1, 1);

        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        pcre4jMatcher.region(1, 1);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

}
