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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for ReDoS protection via configurable match limits.
 */
public class MatchLimitTests {

    private String savedJitProperty;
    private String savedMatchLimitProperty;
    private String savedDepthLimitProperty;
    private String savedHeapLimitProperty;

    @BeforeEach
    void saveAndClearProperties() {
        savedJitProperty = System.getProperty("pcre2.regex.jit");
        savedMatchLimitProperty = System.getProperty(Matcher.MATCH_LIMIT_PROPERTY);
        savedDepthLimitProperty = System.getProperty(Matcher.DEPTH_LIMIT_PROPERTY);
        savedHeapLimitProperty = System.getProperty(Matcher.HEAP_LIMIT_PROPERTY);
        System.clearProperty(Matcher.MATCH_LIMIT_PROPERTY);
        System.clearProperty(Matcher.DEPTH_LIMIT_PROPERTY);
        System.clearProperty(Matcher.HEAP_LIMIT_PROPERTY);
    }

    @AfterEach
    void restoreProperties() {
        restoreProperty("pcre2.regex.jit", savedJitProperty);
        restoreProperty(Matcher.MATCH_LIMIT_PROPERTY, savedMatchLimitProperty);
        restoreProperty(Matcher.DEPTH_LIMIT_PROPERTY, savedDepthLimitProperty);
        restoreProperty(Matcher.HEAP_LIMIT_PROPERTY, savedHeapLimitProperty);
    }

    private static void restoreProperty(String name, String savedValue) {
        if (savedValue != null) {
            System.setProperty(name, savedValue);
        } else {
            System.clearProperty(name);
        }
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchLimitThrowsMatchLimitException(IPcre2 api) {
        // Set a very low match limit
        System.setProperty(Matcher.MATCH_LIMIT_PROPERTY, "100");

        // Use a pattern that causes catastrophic backtracking with optimizations disabled
        // The (*NO_AUTO_POSSESS) and (*NO_START_OPT) directives prevent PCRE2 from
        // optimizing away the backtracking, ensuring the match limit is hit
        var pattern = Pattern.compile(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$");
        var matcher = pattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");

        var exception = assertThrows(MatchLimitException.class, matcher::find);
        assertEquals(IPcre2.ERROR_MATCHLIMIT, exception.getErrorCode());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void depthLimitThrowsMatchLimitException(IPcre2 api) {
        // Depth limits are only enforced by the interpreter, not the JIT matcher
        System.setProperty("pcre2.regex.jit", "false");

        // Set a very low depth limit with a high match limit to ensure depth limit is hit first
        System.setProperty(Matcher.DEPTH_LIMIT_PROPERTY, "10");
        System.setProperty(Matcher.MATCH_LIMIT_PROPERTY, "100000000");

        // Use a pattern that causes deep recursion with optimizations disabled
        var pattern = Pattern.compile(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$");
        var matcher = pattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");

        var exception = assertThrows(MatchLimitException.class, matcher::find);
        assertEquals(IPcre2.ERROR_DEPTHLIMIT, exception.getErrorCode());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void heapLimitThrowsMatchLimitException(IPcre2 api) {
        // Heap limits are only enforced by the interpreter, not the JIT matcher
        System.setProperty("pcre2.regex.jit", "false");

        // Set a very low heap limit with high match/depth limits to ensure heap limit is hit first
        System.setProperty(Matcher.HEAP_LIMIT_PROPERTY, "1");
        System.setProperty(Matcher.MATCH_LIMIT_PROPERTY, "100000000");
        System.setProperty(Matcher.DEPTH_LIMIT_PROPERTY, "100000");

        // Use a pattern that requires heap memory during matching with optimizations disabled
        var pattern = Pattern.compile(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$");
        var matcher = pattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");

        var exception = assertThrows(MatchLimitException.class, matcher::find);
        assertEquals(IPcre2.ERROR_HEAPLIMIT, exception.getErrorCode());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchLimitAppliesToMatches(IPcre2 api) {
        // Verify that match limit also applies to matches() method
        System.setProperty(Matcher.MATCH_LIMIT_PROPERTY, "100");

        var pattern = Pattern.compile(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$");
        var matcher = pattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");

        assertThrows(MatchLimitException.class, matcher::matches);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchLimitAppliesToLookingAt(IPcre2 api) {
        // Verify that match limit also applies to lookingAt() method
        System.setProperty(Matcher.MATCH_LIMIT_PROPERTY, "100");

        var pattern = Pattern.compile(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$");
        var matcher = pattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");

        assertThrows(MatchLimitException.class, matcher::lookingAt);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void noLimitByDefaultAllowsNormalMatching(IPcre2 api) {
        // Without setting system properties, normal matching should work fine
        var pattern = Pattern.compile(api, "\\d+");
        var matcher = pattern.matcher("abc123def");

        assertTrue(matcher.find());
        assertEquals("123", matcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void highLimitAllowsNormalMatching(IPcre2 api) {
        // A high match limit should not interfere with normal patterns
        System.setProperty(Matcher.MATCH_LIMIT_PROPERTY, "10000000");
        System.setProperty(Matcher.DEPTH_LIMIT_PROPERTY, "250");
        System.setProperty(Matcher.HEAP_LIMIT_PROPERTY, "20000");

        var pattern = Pattern.compile(api, "\\d+");
        var matcher = pattern.matcher("abc123def");

        assertTrue(matcher.find());
        assertEquals("123", matcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchLimitAppliesToUsePattern(IPcre2 api) {
        // Verify that match limits are reconfigured when usePattern() is called
        System.setProperty(Matcher.MATCH_LIMIT_PROPERTY, "100");

        var pattern1 = Pattern.compile(api, "\\d+");
        var pattern2 = Pattern.compile(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$");

        // Start with a simple pattern
        var matcher = pattern1.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");

        // Switch to the backtracking-heavy pattern
        matcher.usePattern(pattern2);

        assertThrows(MatchLimitException.class, matcher::find);
    }

    // --- Per-pattern match limit tests (Pattern.Builder) ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderMatchLimitThrowsMatchLimitException(IPcre2 api) {
        // Per-pattern match limit via builder
        var pattern = Pattern.builder(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$")
                .matchLimit(100)
                .compile();
        var matcher = pattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");

        var exception = assertThrows(MatchLimitException.class, matcher::find);
        assertEquals(IPcre2.ERROR_MATCHLIMIT, exception.getErrorCode());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderDepthLimitThrowsMatchLimitException(IPcre2 api) {
        // Depth limits are only enforced by the interpreter, not the JIT matcher
        System.setProperty("pcre2.regex.jit", "false");

        // Per-pattern depth limit via builder with high match limit
        var pattern = Pattern.builder(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$")
                .matchLimit(100_000_000)
                .depthLimit(10)
                .compile();
        var matcher = pattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");

        var exception = assertThrows(MatchLimitException.class, matcher::find);
        assertEquals(IPcre2.ERROR_DEPTHLIMIT, exception.getErrorCode());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderHeapLimitThrowsMatchLimitException(IPcre2 api) {
        // Heap limits are only enforced by the interpreter, not the JIT matcher
        System.setProperty("pcre2.regex.jit", "false");

        // Per-pattern heap limit via builder with high match/depth limits
        var pattern = Pattern.builder(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$")
                .matchLimit(100_000_000)
                .depthLimit(100_000)
                .heapLimit(1)
                .compile();
        var matcher = pattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");

        var exception = assertThrows(MatchLimitException.class, matcher::find);
        assertEquals(IPcre2.ERROR_HEAPLIMIT, exception.getErrorCode());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderWithFlagsCompiles(IPcre2 api) {
        // Builder with compile flags
        var pattern = Pattern.builder(api, "hello")
                .flags(Pattern.CASE_INSENSITIVE)
                .compile();
        var matcher = pattern.matcher("HELLO");

        assertTrue(matcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderWithoutLimitsUsesDefaults(IPcre2 api) {
        // Builder without limits should behave like compile()
        var pattern = Pattern.builder(api, "\\d+")
                .compile();
        var matcher = pattern.matcher("abc123def");

        assertTrue(matcher.find());
        assertEquals("123", matcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderMatchLimitOverridesSystemProperty(IPcre2 api) {
        // System property sets a high limit that would allow matching
        System.setProperty(Matcher.MATCH_LIMIT_PROPERTY, "100000000");

        // Per-pattern limit overrides system property with a low limit
        var pattern = Pattern.builder(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$")
                .matchLimit(100)
                .compile();
        var matcher = pattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");

        assertThrows(MatchLimitException.class, matcher::find);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderWithoutMatchLimitFallsBackToSystemProperty(IPcre2 api) {
        // System property sets a low match limit
        System.setProperty(Matcher.MATCH_LIMIT_PROPERTY, "100");

        // Builder without match limit should fall back to system property
        var pattern = Pattern.builder(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$")
                .compile();
        var matcher = pattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");

        assertThrows(MatchLimitException.class, matcher::find);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderMatchLimitAppliesToMatches(IPcre2 api) {
        // Per-pattern match limit applies to matches() method
        var pattern = Pattern.builder(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$")
                .matchLimit(100)
                .compile();
        var matcher = pattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");

        assertThrows(MatchLimitException.class, matcher::matches);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderMatchLimitAppliesToLookingAt(IPcre2 api) {
        // Per-pattern match limit applies to lookingAt() method
        var pattern = Pattern.builder(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$")
                .matchLimit(100)
                .compile();
        var matcher = pattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");

        assertThrows(MatchLimitException.class, matcher::lookingAt);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderMatchLimitAppliesToUsePattern(IPcre2 api) {
        // Per-pattern match limits are reconfigured when usePattern() is called
        var pattern1 = Pattern.compile(api, "\\d+");
        var pattern2 = Pattern.builder(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$")
                .matchLimit(100)
                .compile();

        var matcher = pattern1.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");
        matcher.usePattern(pattern2);

        assertThrows(MatchLimitException.class, matcher::find);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderHighLimitAllowsNormalMatching(IPcre2 api) {
        // High per-pattern limits should not interfere with normal matching
        var pattern = Pattern.builder(api, "\\d+")
                .matchLimit(10_000_000)
                .depthLimit(250)
                .heapLimit(20_000)
                .compile();
        var matcher = pattern.matcher("abc123def");

        assertTrue(matcher.find());
        assertEquals("123", matcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderNegativeMatchLimitThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () ->
                Pattern.builder(api, "test").matchLimit(-1)
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderNegativeDepthLimitThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () ->
                Pattern.builder(api, "test").depthLimit(-1)
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderNegativeHeapLimitThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () ->
                Pattern.builder(api, "test").heapLimit(-1)
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderNullApiThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () ->
                Pattern.builder(null, "test")
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void builderNullRegexThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () ->
                Pattern.builder(api, null)
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void usePatternClearsPerPatternLimits(IPcre2 api) {
        // Start with a pattern that has a low per-pattern match limit
        var limitedPattern = Pattern.builder(api, "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$")
                .matchLimit(100)
                .compile();

        // Create matcher with limited pattern and input that triggers backtracking - should throw
        var matcher = limitedPattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaab");
        assertThrows(MatchLimitException.class, matcher::find);

        // Switch to a simple pattern without per-pattern limits - should match normally
        var simplePattern = Pattern.compile(api, "\\d+");
        matcher.usePattern(simplePattern);
        matcher.reset("abc123def");
        assertDoesNotThrow(() -> matcher.find());
    }
}
