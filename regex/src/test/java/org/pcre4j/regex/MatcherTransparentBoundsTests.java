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
 * Tests for transparent bounds behavior in {@link Matcher}.
 */
public class MatcherTransparentBoundsTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hasTransparentBoundsDefault(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Default should be false (opaque bounds)
        assertEquals(javaMatcher.hasTransparentBounds(), pcre4jMatcher.hasTransparentBounds());
        assertFalse(pcre4jMatcher.hasTransparentBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void useTransparentBoundsReturnsThis(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Should return this for method chaining
        var result = pcre4jMatcher.useTransparentBounds(true);
        assertEquals(pcre4jMatcher, result);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void useTransparentBoundsTrue(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.useTransparentBounds(true);
        pcre4jMatcher.useTransparentBounds(true);

        assertEquals(javaMatcher.hasTransparentBounds(), pcre4jMatcher.hasTransparentBounds());
        assertTrue(pcre4jMatcher.hasTransparentBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void useTransparentBoundsFalse(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set to true first, then back to false
        javaMatcher.useTransparentBounds(true).useTransparentBounds(false);
        pcre4jMatcher.useTransparentBounds(true).useTransparentBounds(false);

        assertEquals(javaMatcher.hasTransparentBounds(), pcre4jMatcher.hasTransparentBounds());
        assertFalse(pcre4jMatcher.hasTransparentBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsLookbehindCanSeeBeforeRegion(IPcre2 api) {
        // Test that lookbehind can see text before region start with transparent bounds enabled
        var regex = "(?<=foo)bar";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to start after "foo" (positions 3-9, which is "barXXX")
        javaMatcher.region(3, 9).useTransparentBounds(true);
        pcre4jMatcher.region(3, 9).useTransparentBounds(true);

        // With transparent bounds, lookbehind should see "foo" before the region
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);  // Should find "bar" preceded by "foo"
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsLookbehindCannotSeeBeforeRegionWhenOpaque(IPcre2 api) {
        // Test that lookbehind cannot see text before region start with opaque bounds (default)
        var regex = "(?<=foo)bar";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to start after "foo" (positions 3-9, which is "barXXX")
        // With opaque bounds (default), lookbehind cannot see "foo"
        javaMatcher.region(3, 9);
        pcre4jMatcher.region(3, 9);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(javaResult);  // Should NOT find "bar" because lookbehind can't see "foo"
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsLookaheadCanSeeAfterRegion(IPcre2 api) {
        // Test that lookahead can see text after region end with transparent bounds enabled
        var regex = "bar(?=XXX)";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to "foobar" (positions 0-6), not including "XXX"
        javaMatcher.region(0, 6).useTransparentBounds(true);
        pcre4jMatcher.region(0, 6).useTransparentBounds(true);

        // With transparent bounds, lookahead should see "XXX" after the region
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);  // Should find "bar" followed by "XXX"
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsLookaheadCannotSeeAfterRegionWhenOpaque(IPcre2 api) {
        // Test that lookahead cannot see text after region end with opaque bounds (default)
        var regex = "bar(?=XXX)";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to "foobar" (positions 0-6), not including "XXX"
        // With opaque bounds (default), lookahead cannot see "XXX"
        javaMatcher.region(0, 6);
        pcre4jMatcher.region(0, 6);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(javaResult);  // Should NOT find "bar" because lookahead can't see "XXX"
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWordBoundaryCanSeeBeforeRegion(IPcre2 api) {
        // Test that \b (word boundary) can see text before region with transparent bounds
        var regex = "\\bword";
        var input = "XXXword YYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region starting at "word" (position 3), with "XXX" before it
        javaMatcher.region(3, 8).useTransparentBounds(true);
        pcre4jMatcher.region(3, 8).useTransparentBounds(true);

        // With transparent bounds, \b should see that "X" is before "word" (no word boundary)
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        // "XXXword" - the X before 'w' is a letter, so \b should NOT match at position 3
        assertFalse(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWordBoundaryWithOpaqueRegion(IPcre2 api) {
        // Test that \b (word boundary) treats region start as word boundary with opaque bounds
        var regex = "\\bword";
        var input = "XXXword YYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region starting at "word" (position 3), with "XXX" before it
        // With opaque bounds (default), \b treats region start as word boundary
        javaMatcher.region(3, 8);
        pcre4jMatcher.region(3, 8);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        // With opaque bounds, \b sees region boundary as word boundary, so matches
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsPreservedAfterReset(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.useTransparentBounds(true);
        pcre4jMatcher.useTransparentBounds(true);

        javaMatcher.reset();
        pcre4jMatcher.reset();

        // Transparent bounds setting should be preserved after reset
        assertEquals(javaMatcher.hasTransparentBounds(), pcre4jMatcher.hasTransparentBounds());
        assertTrue(pcre4jMatcher.hasTransparentBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsPreservedAfterResetWithInput(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.useTransparentBounds(true);
        pcre4jMatcher.useTransparentBounds(true);

        javaMatcher.reset("newtest");
        pcre4jMatcher.reset("newtest");

        // Transparent bounds setting should be preserved after reset with new input
        assertEquals(javaMatcher.hasTransparentBounds(), pcre4jMatcher.hasTransparentBounds());
        assertTrue(pcre4jMatcher.hasTransparentBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsCombinedWithAnchoringBounds(IPcre2 api) {
        // Test that transparent bounds and anchoring bounds can be used together
        var regex = "(?<=foo)^bar$(?=XXX)";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to "bar" only (positions 3-6)
        // Enable transparent bounds (lookaround can see outside)
        // Enable anchoring bounds (^ and $ match at region boundaries)
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        // Should match: lookbehind sees "foo", ^ matches region start, $ matches region end,
        // lookahead sees "XXX"
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsLookingAtWithLookbehind(IPcre2 api) {
        // Test lookingAt() with lookbehind and transparent bounds
        var regex = "(?<=foo)bar";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 9).useTransparentBounds(true);
        pcre4jMatcher.region(3, 9).useTransparentBounds(true);

        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
        assertTrue(pcre4jMatcher.lookingAt());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMatchesWithLookaround(IPcre2 api) {
        // Test matches() with lookaround and transparent bounds
        var regex = "(?<=foo)bar(?=XXX)";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsNegativeLookbehind(IPcre2 api) {
        // Test negative lookbehind with transparent bounds
        var regex = "(?<!foo)bar";
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region starts at "bar" (position 3)
        javaMatcher.region(3, 9).useTransparentBounds(true);
        pcre4jMatcher.region(3, 9).useTransparentBounds(true);

        // With transparent bounds, negative lookbehind sees "XXX" (not "foo"), so matches
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsNegativeLookahead(IPcre2 api) {
        // Test negative lookahead with transparent bounds
        var regex = "bar(?!XXX)";
        var input = "foobarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "foobar" (positions 0-6), "YYY" is outside
        javaMatcher.region(0, 6).useTransparentBounds(true);
        pcre4jMatcher.region(0, 6).useTransparentBounds(true);

        // With transparent bounds, negative lookahead sees "YYY" (not "XXX"), so matches
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithFind(IPcre2 api) {
        // Test find() with transparent bounds
        var regex = "(?<=\\d)\\w+";
        var input = "123abcXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "abc" (positions 3-6), with "123" before it
        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        // find() should find "abc" preceded by a digit
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
        assertEquals("abc", pcre4jMatcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsUnicodeWithLookbehind(IPcre2 api) {
        // Test transparent bounds with Unicode and lookbehind
        var regex = "(?<=\uD83C\uDF10)test";
        var input = "\uD83C\uDF10testXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // \uD83C\uDF10 is 2 characters in Java string (surrogate pair), so region starts at position 2
        javaMatcher.region(2, 6).useTransparentBounds(true);
        pcre4jMatcher.region(2, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithUsePattern(IPcre2 api) {
        // Test that usePattern() works correctly with transparent bounds
        // This exercises the anchoringBoundsCode = null path in usePattern()
        var regex1 = "(?<=foo)bar";
        var regex2 = "(?<=XXX)YYY";
        var input = "foobarXXXYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex1).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex1).matcher(input);

        // Set up transparent bounds and match with first pattern
        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertTrue(pcre4jMatcher.hasMatch());

        // Switch to second pattern
        javaMatcher.usePattern(java.util.regex.Pattern.compile(regex2));
        pcre4jMatcher.usePattern(Pattern.compile(api, regex2));
        // usePattern calls reset(), set up new region
        javaMatcher.region(9, 12).useTransparentBounds(true);
        pcre4jMatcher.region(9, 12).useTransparentBounds(true);
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertTrue(pcre4jMatcher.hasMatch());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithCaseInsensitive(IPcre2 api) {
        // Test transparent bounds with CASE_INSENSITIVE flag
        var regex = "(?<=FOO)bar";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.CASE_INSENSITIVE).matcher(input);

        javaMatcher.region(3, 9).useTransparentBounds(true);
        pcre4jMatcher.region(3, 9).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithDotall(IPcre2 api) {
        // Test transparent bounds with DOTALL flag
        var regex = "(?<=foo).";
        var input = "foo\nXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.DOTALL).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.DOTALL).matcher(input);

        javaMatcher.region(3, 7).useTransparentBounds(true);
        pcre4jMatcher.region(3, 7).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
        assertEquals("\n", pcre4jMatcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithMultiline(IPcre2 api) {
        // Test transparent bounds with MULTILINE flag
        // MULTILINE affects ^ behavior - should still work with transparent bounds
        var regex = "^bar";
        var input = "foo\nbarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.MULTILINE).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.MULTILINE).matcher(input);

        // Region starts at "bar" (position 4)
        javaMatcher.region(4, 10).useTransparentBounds(true);
        pcre4jMatcher.region(4, 10).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMatchExtendsBeyondRegion(IPcre2 api) {
        // Test where greedy match would extend beyond region - should find shorter match
        var regex = "(?<=foo)\\w+";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "bar" only (positions 3-6), pattern would match "barXXX" without constraint
        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
        assertEquals("bar", pcre4jMatcher.group());  // Should be constrained to region
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsNoMatchWhenConstrainedToRegion(IPcre2 api) {
        // Test where match only exists beyond region - should not match
        var regex = "(?<=foo)XXX";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "bar" only (positions 3-6), "XXX" is outside
        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(javaResult);  // No match because XXX is outside region
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithAnchoringBoundsAndAnchors(IPcre2 api) {
        // Test transparent + anchoring bounds with ^ and $ in pattern
        var regex = "^bar$";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "bar" only (positions 3-6)
        // With anchoring bounds, ^ should match at region start, $ at region end
        // With transparent bounds, lookaround would see outside (though this pattern has none)
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMatchesConstrainedSubject(IPcre2 api) {
        // Test matches() with transparent bounds where match would extend beyond region
        var regex = "(?<=foo)\\w+(?=YYY)";
        var input = "foobarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "bar" (positions 3-6)
        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.matches();
        var pcre4jResult = pcre4jMatcher.matches();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithComments(IPcre2 api) {
        // Test transparent bounds with COMMENTS flag
        var regex = "(?<=foo) bar  # match bar after foo";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.COMMENTS).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.COMMENTS).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithUnicodeCharacterClass(IPcre2 api) {
        // Test transparent bounds with UNICODE_CHARACTER_CLASS flag
        var regex = "(?<=foo)\\w+";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.UNICODE_CHARACTER_CLASS)
                .matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.UNICODE_CHARACTER_CLASS).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithUnixLines(IPcre2 api) {
        // Test transparent bounds with UNIX_LINES flag
        var regex = "(?<=foo)bar";
        var input = "foobar\nXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.UNIX_LINES).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.UNIX_LINES).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsCachedTransformedPattern(IPcre2 api) {
        // Test that the transformed pattern is cached and reused
        var regex = "^bar$";
        var input = "XXXbarYYY";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // First match - creates the transformed pattern
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        assertTrue(pcre4jMatcher.find());

        // Reset and match again - should use cached transformed pattern
        pcre4jMatcher.reset();
        pcre4jMatcher.region(3, 6);
        assertTrue(pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsNoTransformationNeeded(IPcre2 api) {
        // Test pattern without ^ or $ (no transformation needed)
        var regex = "(?<=foo)bar(?=XXX)";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // This pattern has no ^ or $, so no transformation is needed
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringBoundsWithOnlyDollar(IPcre2 api) {
        // Test pattern with only $ (transformation removes $)
        var regex = "(?<=foo)bar$";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region ends at "bar" (position 6), $ should match at region end
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringBoundsWithOnlyCaret(IPcre2 api) {
        // Test pattern with only ^ (transformation replaces ^ with \G)
        var regex = "^bar";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region starts at "bar" (position 3), ^ should match at region start
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsEscapedAnchors(IPcre2 api) {
        // Test that escaped ^ and $ are not transformed
        var regex = "\\^bar\\$";
        var input = "foo^bar$XXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // \^ and \$ are literal characters, not anchors
        javaMatcher.region(3, 8).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 8).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchorsInCharacterClass(IPcre2 api) {
        // Test that ^ and $ inside character classes are not transformed
        var regex = "[^a]ar[$]";
        var input = "foobar$XXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // [^a] is negated char class, [$] matches literal $
        javaMatcher.region(3, 8).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 8).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMatchDoesNotEndAtRegionEnd(IPcre2 api) {
        // Test where anchored match doesn't end exactly at regionEnd
        var regex = "^ba$";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "bar" (positions 3-6), but ^ba$ only matches "ba" not "bar"
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(javaResult);  // "ba" doesn't match because $ requires end at position 6
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMultipleFlagsCombined(IPcre2 api) {
        // Test transparent bounds with multiple flags combined
        var regex = "(?<=FOO) bar  # comment";
        var input = "foobar\nXXX";
        int javaFlags = java.util.regex.Pattern.CASE_INSENSITIVE
                | java.util.regex.Pattern.COMMENTS
                | java.util.regex.Pattern.DOTALL;
        int pcre4jFlags = Pattern.CASE_INSENSITIVE | Pattern.COMMENTS | Pattern.DOTALL;

        var javaMatcher = java.util.regex.Pattern.compile(regex, javaFlags).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, pcre4jFlags).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsConstrainedMatchFailsAdvancesPosition(IPcre2 api) {
        // Test that when a match extends beyond regionEnd and the constrained match fails,
        // the search continues from the next position and eventually finds a valid match.
        // This exercises the PATH 3 branch where constrainedResult < 1 and we advance searchStart.
        var regex = "b+";  // Greedy quantifier that would extend beyond region
        var input = "XXXbbbYYYbb";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region from 3 to 5, input has "bbb" at 3-5 and "bb" at 9-11
        // With transparent bounds, the greedy b+ would want to match all 3 b's but regionEnd=5
        javaMatcher.region(3, 5).useTransparentBounds(true);
        pcre4jMatcher.region(3, 5).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void opaqueBoundsWithRegionStartGreaterThanZero(IPcre2 api) {
        // Test opaque bounds (default) with regionStart > 0 to exercise getRegionSubject() branch
        var regex = "bar";
        var input = "foobarbaz";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region from 3 to 6: "bar"
        javaMatcher.region(3, 6);
        pcre4jMatcher.region(3, 6);

        // Should find match at 3 (start of region)
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(pcre4jResult);
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(3, pcre4jMatcher.start());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void opaqueBoundsLookbehindBlocked(IPcre2 api) {
        // Test that with opaque bounds (default), lookbehind cannot see before region
        // This uses regionStart > 0 to exercise the substring branch in getRegionSubject()
        var regex = "(?<=foo)bar";
        var input = "foobar";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region from 3 to 6: "bar", but lookbehind needs to see "foo" before region
        javaMatcher.region(3, 6);  // Opaque bounds (default)
        pcre4jMatcher.region(3, 6);

        // With opaque bounds, lookbehind cannot see "foo" before region
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithLiteralFlag(IPcre2 api) {
        // Test transparent bounds with LITERAL flag - pattern treated as literal
        var regex = "^ba";  // With LITERAL, ^ and $ are literal characters, not anchors
        var input = "^ba$test";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.LITERAL).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.LITERAL).matcher(input);

        javaMatcher.useTransparentBounds(true);
        pcre4jMatcher.useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void nonAnchoringBoundsWithMiddleRegion(IPcre2 api) {
        // Test non-anchoring bounds with region in middle of input
        // This exercises getMatchOptions() with both NOTBOL and NOTEOL
        var regex = "test";
        var input = "XXXtestYYY";  // length 10
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 7) means regionStart=3 > 0, regionEnd=7 < 10
        // With non-anchoring bounds, both NOTBOL and NOTEOL should be set
        javaMatcher.region(3, 7).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 7).useAnchoringBounds(false);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(pcre4jResult);
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsSearchStartAdvancesOnConstraintFailure(IPcre2 api) {
        // Test the loop where searchStart advances after constrained match fails
        // Pattern: b+ (greedy) would match multiple b's
        // Input: "aabbbcc" with region (2, 4) meaning "bb" is in region but "bbb" available with transparent bounds
        var regex = "b+";
        var input = "aabbbcc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (2, 4) covers "bb", but transparent bounds can see "bbb"
        // The greedy b+ with transparent bounds sees 3 b's, tries to match, but extends past regionEnd=4
        // The constrained match at position 2 should find "bb" (2 chars within region)
        javaMatcher.region(2, 4).useTransparentBounds(true);
        pcre4jMatcher.region(2, 4).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
        // Match should be "bb" from position 2 to 4
        assertEquals(2, pcre4jMatcher.start());
        assertEquals(4, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringBoundsWithTransformFailedMatch(IPcre2 api) {
        // Test PATH 1 where transformed pattern matches but doesn't end at regionEnd,
        // then falls through to PATH 2 for normal matching
        var regex = "^ba";  // Only caret anchor, no dollar
        var input = "XXXbarYYY";  // "bar" from 3-6
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 6), pattern ^ba would transform to \Gba
        // \G matches at startOffset (3), so "ba" matches positions 3-5
        // Match ends at 5, not regionEnd=6, so falls through to normal matching
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void nonAnchoringBoundsOnlyNotbol(IPcre2 api) {
        // Test non-anchoring bounds with only NOTBOL (regionStart > 0, regionEnd == input.length())
        var regex = "test";
        var input = "XXXtest";  // length 7
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 7): regionStart=3 > 0 (NOTBOL), regionEnd=7 == input.length() (no NOTEOL)
        javaMatcher.region(3, 7).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 7).useAnchoringBounds(false);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void nonAnchoringBoundsOnlyNoteol(IPcre2 api) {
        // Test non-anchoring bounds with only NOTEOL (regionStart == 0, regionEnd < input.length())
        var regex = "test";
        var input = "testXXX";  // length 7
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (0, 4): regionStart=0 (no NOTBOL), regionEnd=4 < 7 (NOTEOL)
        javaMatcher.region(0, 4).useAnchoringBounds(false);
        pcre4jMatcher.region(0, 4).useAnchoringBounds(false);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void opaqueBoundsRegionStartZero(IPcre2 api) {
        // Test opaque bounds with regionStart == 0 to exercise else branch in getRegionSubject()
        var regex = "test";
        var input = "testXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (0, 4): regionStart=0, uses the else branch in getRegionSubject()
        javaMatcher.region(0, 4);
        pcre4jMatcher.region(0, 4);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(pcre4jResult);
        assertEquals(0, pcre4jMatcher.start());
        assertEquals(4, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithDollarAnchorAtRegionEnd(IPcre2 api) {
        // Test transparent + anchoring bounds with pattern that has $ and match ends at regionEnd
        var regex = "^bar$";
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 6): "bar", pattern ^bar$ should match with anchoring bounds
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(pcre4jResult);
        assertEquals(3, pcre4jMatcher.start());
        assertEquals(6, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsTransformedPatternNoMatch(IPcre2 api) {
        // Test PATH 1 where transformed pattern doesn't match, falls through to PATH 2
        var regex = "^xyz";  // Pattern with ^ that won't match at regionStart
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 6): "bar", pattern ^xyz won't match even with transformation
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMatchExtendsAndConstrainedFails(IPcre2 api) {
        // Test PATH 3 where match extends beyond regionEnd and constrained match also fails
        // Pattern: "bbb" requires 3 b's, but region only has 2 b's
        var regex = "bbb";
        var input = "aabbccc";  // "bb" at position 2-4
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (2, 4): "bb", but pattern needs "bbb"
        // Transparent bounds sees "bbc" but "bbb" doesn't exist
        javaMatcher.region(2, 4).useTransparentBounds(true);
        pcre4jMatcher.region(2, 4).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsSearchLoopExhaustsPositions(IPcre2 api) {
        // Test that search loop correctly terminates when all positions exhausted
        var regex = "xyz";  // Pattern that won't match anywhere in region
        var input = "XXXabcYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithDollarPatternNotEndingAtRegionEnd(IPcre2 api) {
        // Test pattern with $ where the match doesn't end at regionEnd
        // This exercises the path where originalHadDollar is true but lastMatchIndices[1] != regionEnd
        var regex = "^ba$";  // Pattern with $ anchor
        var input = "XXXbarYYY";  // "bar" at 3-6, pattern "^ba$" needs to end after "ba"
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 6): "bar", but ^ba$ would match at 3-5 (not at regionEnd=6)
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsPatternWithOnlyDollarAnchor(IPcre2 api) {
        // Test transparent + anchoring bounds with pattern that has only $ (no ^)
        // This exercises patternContainsDollarAnchor returning true for patterns without ^
        var regex = "bar$";
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 6): "bar", pattern bar$ should match with $ at regionEnd
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsGreedyQuantifierConstrainedMatch(IPcre2 api) {
        // Test greedy quantifier where transparent bounds sees more but constrained match works
        var regex = "a+";  // Greedy quantifier
        var input = "XXXaaaYYY";  // "aaa" at 3-6
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 5): only "aa" in region, but transparent bounds sees "aaa"
        // Constrained match should find "aa"
        javaMatcher.region(3, 5).useTransparentBounds(true);
        pcre4jMatcher.region(3, 5).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
        assertEquals(3, pcre4jMatcher.start());
        assertEquals(5, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithNestedCharacterClass(IPcre2 api) {
        // Test pattern with POSIX character class (nested brackets) containing $
        // This exercises the charClassDepth tracking in patternContainsDollarAnchor
        var regex = "[[:alpha:]$]+";  // POSIX class with $ inside
        var input = "XXXa$bYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMatchStartsAfterRegionStart(IPcre2 api) {
        // Test transparent bounds where match doesn't start at regionStart
        // This ensures PATH 1 is skipped when searchStart != regionStart
        var regex = "bar";
        var input = "XXXfoobarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 9): "foobar", "bar" starts at position 6
        javaMatcher.region(3, 9).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 9).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
        assertEquals(6, pcre4jMatcher.start());
        assertEquals(9, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void opaqueBoundsWithIndexAdjustment(IPcre2 api) {
        // Test that index adjustment works correctly for opaque bounds with regionStart > 0
        var regex = "(test)";  // Capturing group
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 7): "test"
        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        // Check that group indices are properly adjusted to input coordinates
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.start(1), pcre4jMatcher.start(1));
        assertEquals(javaMatcher.end(1), pcre4jMatcher.end(1));
        assertEquals(3, pcre4jMatcher.start(1));
        assertEquals(7, pcre4jMatcher.end(1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMultipleFinds(IPcre2 api) {
        // Test multiple find() calls with transparent bounds
        var regex = "a";
        var input = "XXXabaYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 6): "aba", should find 'a' at positions 3 and 5
        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        // First match
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(3, pcre4jMatcher.start());

        // Second match
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(5, pcre4jMatcher.start());

        // No more matches
        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsEmptyRegion(IPcre2 api) {
        // Test transparent bounds with empty region
        var regex = "";  // Empty pattern matches empty string
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Empty region at position 2
        javaMatcher.region(2, 2).useTransparentBounds(true);
        pcre4jMatcher.region(2, 2).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsFullInputRegion(IPcre2 api) {
        // Test transparent bounds when region covers full input
        var regex = "(?<=X)test(?=Y)";
        var input = "XtestY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region covers full input
        javaMatcher.region(0, input.length()).useTransparentBounds(true);
        pcre4jMatcher.region(0, input.length()).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringWithCaseInsensitiveFlag(IPcre2 api) {
        // Test transparent + anchoring bounds with CASE_INSENSITIVE flag
        // This exercises the CASELESS option in getOrCreateAnchoringBoundsCode()
        var regex = "^BAR$";
        var input = "XXXbarYYY";
        var javaPattern = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.CASE_INSENSITIVE);
        var javaMatcher = javaPattern.matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.CASE_INSENSITIVE).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringWithDotallFlag(IPcre2 api) {
        // Test transparent + anchoring bounds with DOTALL flag
        // This exercises the DOTALL option in getOrCreateAnchoringBoundsCode()
        var regex = "^b.r$";
        var input = "XXXb\nrYYY";  // newline in middle
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.DOTALL).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.DOTALL).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringWithUnicodeCharacterClassFlag(IPcre2 api) {
        // Test transparent + anchoring bounds with UNICODE_CHARACTER_CLASS flag
        // This exercises the UCP option in getOrCreateAnchoringBoundsCode()
        // Using a simpler pattern that works consistently
        var regex = "^test$";
        var input = "XXXtestYYY";
        int javaFlags = java.util.regex.Pattern.UNICODE_CHARACTER_CLASS;
        var javaMatcher = java.util.regex.Pattern.compile(regex, javaFlags).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.UNICODE_CHARACTER_CLASS).matcher(input);

        javaMatcher.region(3, 7).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 7).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringWithCommentsFlag(IPcre2 api) {
        // Test transparent + anchoring bounds with COMMENTS flag
        // This exercises the EXTENDED option in getOrCreateAnchoringBoundsCode()
        var regex = "^bar$  # comment";
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.COMMENTS).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.COMMENTS).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringWithUnixLinesFlag(IPcre2 api) {
        // Test transparent + anchoring bounds with UNIX_LINES flag
        // This exercises the LF newline option in getOrCreateAnchoringBoundsCode()
        var regex = "^bar$";
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.UNIX_LINES).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.UNIX_LINES).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsConstrainedMatchFailsAndAdvances(IPcre2 api) {
        // Test PATH 3 where match extends beyond regionEnd, constrained fails, and searchStart advances
        // Pattern "bb" would match at position 3 (matching "bb"), extending to position 5
        // But with region (3, 4), only one 'b' is in region, so constrained match fails
        // Then search should advance but find no more matches
        var regex = "bb";
        var input = "aaabbccc";  // "bb" at position 3-5
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 4): only one 'b' in region
        javaMatcher.region(3, 4).useTransparentBounds(true);
        pcre4jMatcher.region(3, 4).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void patternContainsDollarAnchorEscapedDollar(IPcre2 api) {
        // Test patternContainsDollarAnchor with escaped $ (should return false)
        // This exercises the escaped character handling path
        var regex = "bar\\$";  // Escaped $, not an anchor
        var input = "XXXbar$YYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 7).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void patternContainsDollarAnchorInCharClass(IPcre2 api) {
        // Test patternContainsDollarAnchor with $ inside character class (should return false)
        // This exercises the character class depth tracking
        var regex = "[a$]+bar";  // $ inside character class, not an anchor
        var input = "XXX$abarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 8).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 8).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsLoopAdvancesMultipleTimes(IPcre2 api) {
        // Test that search loop can advance multiple times before finding a match
        // Pattern needs to fail at multiple positions before succeeding
        var regex = "c+";  // Match one or more c's
        var input = "aabbccdd";  // "cc" at position 4-6
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (2, 5): "bbcc" - first c at position 4
        // Transparent bounds sees beyond but match starts at 4
        javaMatcher.region(2, 5).useTransparentBounds(true);
        pcre4jMatcher.region(2, 5).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        if (javaResult) {
            assertEquals(javaMatcher.start(), pcre4jMatcher.start());
            assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        }
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithAllFlagsAndAnchors(IPcre2 api) {
        // Test with multiple flags combined plus anchors
        int javaFlags = java.util.regex.Pattern.CASE_INSENSITIVE
                | java.util.regex.Pattern.DOTALL
                | java.util.regex.Pattern.COMMENTS;
        int pcre4jFlags = Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.COMMENTS;

        var regex = "^BAR$  # comment";
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex, javaFlags).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, pcre4jFlags).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

}
