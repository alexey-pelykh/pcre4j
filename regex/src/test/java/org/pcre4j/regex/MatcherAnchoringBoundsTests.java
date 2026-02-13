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
 * Tests for anchoring bounds behavior in {@link Matcher}.
 */
public class MatcherAnchoringBoundsTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hasAnchoringBoundsDefault(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Default should be true (anchoring enabled)
        assertEquals(javaMatcher.hasAnchoringBounds(), pcre4jMatcher.hasAnchoringBounds());
        assertTrue(pcre4jMatcher.hasAnchoringBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void useAnchoringBoundsReturnsThis(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Should return this for method chaining
        var result = pcre4jMatcher.useAnchoringBounds(false);
        assertEquals(pcre4jMatcher, result);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void useAnchoringBoundsFalse(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.useAnchoringBounds(false);
        pcre4jMatcher.useAnchoringBounds(false);

        assertEquals(javaMatcher.hasAnchoringBounds(), pcre4jMatcher.hasAnchoringBounds());
        assertFalse(pcre4jMatcher.hasAnchoringBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void useAnchoringBoundsTrue(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set to false first, then back to true
        javaMatcher.useAnchoringBounds(false).useAnchoringBounds(true);
        pcre4jMatcher.useAnchoringBounds(false).useAnchoringBounds(true);

        assertEquals(javaMatcher.hasAnchoringBounds(), pcre4jMatcher.hasAnchoringBounds());
        assertTrue(pcre4jMatcher.hasAnchoringBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsCaretWithRegion(IPcre2 api) {
        // Test that ^ matches at region start with anchoring bounds enabled (default)
        var regex = "^test";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        // With anchoring bounds (default), ^ should match at region start
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);  // Java behavior: ^ matches at region start with anchoring bounds
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsCaretWithRegionDisabled(IPcre2 api) {
        // Test that ^ does NOT match at region start with anchoring bounds disabled
        var regex = "^test";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 7).useAnchoringBounds(false);

        // With non-anchoring bounds, ^ should NOT match at region start (only at true input start)
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertFalse(pcre4jMatcher.hasAnchoringBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsDollarWithRegion(IPcre2 api) {
        // Test that $ matches at region end with anchoring bounds enabled (default)
        var regex = "test$";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        // With anchoring bounds (default), $ should match at region end
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsDollarWithRegionDisabled(IPcre2 api) {
        // Test that $ does NOT match at region end with anchoring bounds disabled
        var regex = "test$";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 7).useAnchoringBounds(false);

        // With non-anchoring bounds, $ should NOT match at region end (only at true input end)
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsLookingAtWithRegion(IPcre2 api) {
        // Test lookingAt() with ^ pattern in a region
        var regex = "^test";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        // With anchoring bounds (default), ^ should match at region start in lookingAt
        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsLookingAtWithRegionDisabled(IPcre2 api) {
        // Test lookingAt() with ^ pattern in a region with anchoring bounds disabled
        var regex = "^test";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 7).useAnchoringBounds(false);

        // With non-anchoring bounds, ^ should NOT match at region start in lookingAt
        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsMatchesWithRegion(IPcre2 api) {
        // Test matches() with ^ and $ pattern in a region
        var regex = "^test$";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        // With anchoring bounds (default), ^test$ should match the region content
        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsMatchesWithRegionDisabled(IPcre2 api) {
        // Test matches() with ^ and $ pattern in a region with anchoring bounds disabled
        var regex = "^test$";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 7).useAnchoringBounds(false);

        // With non-anchoring bounds, ^test$ should NOT match (anchors won't match at region boundaries)
        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsPreservedAfterReset(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.useAnchoringBounds(false);
        pcre4jMatcher.useAnchoringBounds(false);

        javaMatcher.reset();
        pcre4jMatcher.reset();

        // Anchoring bounds setting should be preserved after reset
        assertEquals(javaMatcher.hasAnchoringBounds(), pcre4jMatcher.hasAnchoringBounds());
        assertFalse(pcre4jMatcher.hasAnchoringBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsPreservedAfterResetWithInput(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.useAnchoringBounds(false);
        pcre4jMatcher.useAnchoringBounds(false);

        javaMatcher.reset("newtest");
        pcre4jMatcher.reset("newtest");

        // Anchoring bounds setting should be preserved after reset with new input
        assertEquals(javaMatcher.hasAnchoringBounds(), pcre4jMatcher.hasAnchoringBounds());
        assertFalse(pcre4jMatcher.hasAnchoringBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsWithFullInput(IPcre2 api) {
        // When region covers full input, anchoring bounds should have no effect
        var regex = "^test$";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Test with anchoring bounds enabled (default)
        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());

        // Test with anchoring bounds disabled - should still match since region = full input
        javaMatcher.reset().useAnchoringBounds(false);
        pcre4jMatcher.reset().useAnchoringBounds(false);
        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsWordBoundaryWithRegion(IPcre2 api) {
        // Test that \b (word boundary) behavior with regions
        var regex = "\\bword\\b";
        var input = "XXXword YYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 8);
        pcre4jMatcher.region(3, 8);

        // With anchoring bounds, \b should match at region boundaries
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsWordBoundaryWithRegionDisabled(IPcre2 api) {
        // Test that \b (word boundary) behavior with regions and non-anchoring bounds.
        // Note: In Java, useAnchoringBounds(false) only affects ^ and $.
        // Word boundaries (\b) are controlled by useTransparentBounds() which is NOT yet implemented.
        // With transparent bounds disabled (the default), \b treats region boundaries as word boundaries
        // regardless of the anchoringBounds setting.
        var regex = "\\bword\\b";
        var input = "XXXword YYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 8).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 8).useAnchoringBounds(false);

        // With transparent bounds disabled (default), \b sees region boundaries as word boundaries,
        // so \bword\b matches at region start even though "XXX" is adjacent in full input.
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

}
