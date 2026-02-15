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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;
import org.pcre4j.api.Pcre2UtfWidth;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Extended tests for {@link Pcre4jUtils} covering configuration queries,
 * error messages, group extraction, and ovector conversion edge cases.
 */
public class Pcre4jUtilsExtendedTests {

    // === Configuration query methods ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getVersion(IPcre2 api) {
        var version = Pcre4jUtils.getVersion(api);
        assertNotNull(version);
        assertFalse(version.isEmpty());
        assertTrue(version.contains("."), "Version should contain a dot: " + version);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getVersionNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.getVersion(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void isVersionAtLeastExactVersion(IPcre2 api) {
        var version = Pcre4jUtils.getVersion(api);
        var versionPart = version.contains(" ") ? version.substring(0, version.indexOf(' ')) : version;
        var parts = versionPart.split("\\.");
        var actualMajor = Integer.parseInt(parts[0]);
        var actualMinor = Integer.parseInt(parts[1]);

        // Exact version match should return true
        assertTrue(Pcre4jUtils.isVersionAtLeast(api, actualMajor, actualMinor));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void isVersionAtLeastLowerMajor(IPcre2 api) {
        var version = Pcre4jUtils.getVersion(api);
        var versionPart = version.contains(" ") ? version.substring(0, version.indexOf(' ')) : version;
        var parts = versionPart.split("\\.");
        var actualMajor = Integer.parseInt(parts[0]);

        // Lower major version should return true
        assertTrue(Pcre4jUtils.isVersionAtLeast(api, actualMajor - 1, 0));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void isVersionAtLeastHigherMajor(IPcre2 api) {
        var version = Pcre4jUtils.getVersion(api);
        var versionPart = version.contains(" ") ? version.substring(0, version.indexOf(' ')) : version;
        var parts = versionPart.split("\\.");
        var actualMajor = Integer.parseInt(parts[0]);

        // Higher major version should return false
        assertFalse(Pcre4jUtils.isVersionAtLeast(api, actualMajor + 1, 0));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void isVersionAtLeastSameMajorLowerMinor(IPcre2 api) {
        var version = Pcre4jUtils.getVersion(api);
        var versionPart = version.contains(" ") ? version.substring(0, version.indexOf(' ')) : version;
        var parts = versionPart.split("\\.");
        var actualMajor = Integer.parseInt(parts[0]);

        // Same major, lower minor should return true
        assertTrue(Pcre4jUtils.isVersionAtLeast(api, actualMajor, 0));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void isVersionAtLeastSameMajorHigherMinor(IPcre2 api) {
        var version = Pcre4jUtils.getVersion(api);
        var versionPart = version.contains(" ") ? version.substring(0, version.indexOf(' ')) : version;
        var parts = versionPart.split("\\.");
        var actualMajor = Integer.parseInt(parts[0]);
        var actualMinor = Integer.parseInt(parts[1]);

        // Same major, higher minor should return false
        assertFalse(Pcre4jUtils.isVersionAtLeast(api, actualMajor, actualMinor + 1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void isVersionAtLeastNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.isVersionAtLeast(null, 10, 0));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getUnicodeVersion(IPcre2 api) {
        var version = Pcre4jUtils.getUnicodeVersion(api);
        assertNotNull(version);
        assertFalse(version.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getUnicodeVersionNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.getUnicodeVersion(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void isUnicodeSupported(IPcre2 api) {
        // Standard PCRE2 builds support Unicode
        assertTrue(Pcre4jUtils.isUnicodeSupported(api));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void isUnicodeSupportedNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.isUnicodeSupported(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getDefaultParenthesesNestingLimit(IPcre2 api) {
        var limit = Pcre4jUtils.getDefaultParenthesesNestingLimit(api);
        assertTrue(limit > 0, "Default parens nesting limit should be positive");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getDefaultParenthesesNestingLimitNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.getDefaultParenthesesNestingLimit(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getDefaultNewline(IPcre2 api) {
        var newline = Pcre4jUtils.getDefaultNewline(api);
        assertNotNull(newline);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getDefaultNewlineNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.getDefaultNewline(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void isBackslashCDisabled(IPcre2 api) {
        // Just verify it doesn't throw
        Pcre4jUtils.isBackslashCDisabled(api);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void isBackslashCDisabledNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.isBackslashCDisabled(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getDefaultMatchLimit(IPcre2 api) {
        var limit = Pcre4jUtils.getDefaultMatchLimit(api);
        assertTrue(limit > 0, "Default match limit should be positive");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getDefaultMatchLimitNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.getDefaultMatchLimit(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getInternalLinkSize(IPcre2 api) {
        var linkSize = Pcre4jUtils.getInternalLinkSize(api);
        assertTrue(linkSize > 0, "Internal link size should be positive");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getInternalLinkSizeNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.getInternalLinkSize(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getJitTarget(IPcre2 api) {
        var target = Pcre4jUtils.getJitTarget(api);
        // May return null if JIT not supported, or a non-empty string
        if (target != null) {
            assertFalse(target.isEmpty());
        }
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getJitTargetNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.getJitTarget(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void isJitSupported(IPcre2 api) {
        // Just verify it doesn't throw; JIT support depends on build
        Pcre4jUtils.isJitSupported(api);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void isJitSupportedNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.isJitSupported(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getDefaultHeapLimit(IPcre2 api) {
        var limit = Pcre4jUtils.getDefaultHeapLimit(api);
        assertTrue(limit >= 0, "Default heap limit should be non-negative");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getDefaultHeapLimitNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.getDefaultHeapLimit(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getDefaultDepthLimit(IPcre2 api) {
        var limit = Pcre4jUtils.getDefaultDepthLimit(api);
        assertTrue(limit > 0, "Default depth limit should be positive");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getDefaultDepthLimitNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.getDefaultDepthLimit(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getCompiledWidths(IPcre2 api) {
        var widths = Pcre4jUtils.getCompiledWidths(api);
        assertNotNull(widths);
        assertFalse(widths.isEmpty(), "Should have at least one compiled width");
        assertTrue(widths.contains(Pcre2UtfWidth.UTF8), "Must support UTF-8");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getCompiledWidthsNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.getCompiledWidths(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getDefaultBsr(IPcre2 api) {
        var bsr = Pcre4jUtils.getDefaultBsr(api);
        assertNotNull(bsr);
        assertTrue(bsr == Pcre2Bsr.UNICODE || bsr == Pcre2Bsr.ANYCRLF);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getDefaultBsrNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.getDefaultBsr(null));
    }

    // === getErrorMessage ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getErrorMessageValidCode(IPcre2 api) {
        // ERROR_NOMATCH is -1
        var msg = Pcre4jUtils.getErrorMessage(api, IPcre2.ERROR_NOMATCH);
        assertNotNull(msg);
        assertFalse(msg.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getErrorMessageNullApiThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.getErrorMessage(null, 0));
    }

    // === convertCharacterIndexToByteOffset ===

    @Test
    void convertCharacterIndexToByteOffsetAscii() {
        assertEquals(0, Pcre4jUtils.convertCharacterIndexToByteOffset("hello", 0));
        assertEquals(3, Pcre4jUtils.convertCharacterIndexToByteOffset("hello", 3));
        assertEquals(5, Pcre4jUtils.convertCharacterIndexToByteOffset("hello", 5));
    }

    @Test
    void convertCharacterIndexToByteOffsetMultiByte() {
        // "café" - é is 2 bytes in UTF-8
        var subject = "caf\u00e9";
        assertEquals(0, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 0));
        assertEquals(3, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 3));
        assertEquals(5, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 4)); // é = 2 bytes
    }

    @Test
    void convertCharacterIndexToByteOffsetThreeByte() {
        // Chinese characters are 3 bytes each in UTF-8
        var subject = "\u4e16\u754c"; // "世界"
        assertEquals(0, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 0));
        assertEquals(3, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 1));
        assertEquals(6, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 2));
    }

    @Test
    void convertCharacterIndexToByteOffsetSurrogate() {
        // Emoji U+1F600 (😀) uses a surrogate pair in Java, 4 bytes in UTF-8
        var subject = "\uD83D\uDE00";
        assertEquals(0, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 0));
        assertEquals(2, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 1));
        assertEquals(4, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 2));
    }

    @Test
    void convertCharacterIndexToByteOffsetNullThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.convertCharacterIndexToByteOffset(null, 0));
    }

    @Test
    void convertCharacterIndexToByteOffsetNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.convertCharacterIndexToByteOffset("test", -1));
    }

    // === convertOvectorToStringIndices ===

    @Test
    void convertOvectorToStringIndicesAscii() {
        var subject = "hello world";
        // Full match "hello" at bytes 0-5
        var ovector = new long[]{0, 5};
        assertArrayEquals(new int[]{0, 5}, Pcre4jUtils.convertOvectorToStringIndices(subject, ovector));
    }

    @Test
    void convertOvectorToStringIndicesWithGroups() {
        var subject = "hello world";
        // Full match + 2 groups
        var ovector = new long[]{0, 11, 0, 5, 6, 11};
        assertArrayEquals(
                new int[]{0, 11, 0, 5, 6, 11},
                Pcre4jUtils.convertOvectorToStringIndices(subject, ovector)
        );
    }

    @Test
    void convertOvectorToStringIndicesUnmatchedGroup() {
        var subject = "hello";
        // Full match + group that didn't match
        var ovector = new long[]{0, 5, -1, -1};
        assertArrayEquals(
                new int[]{0, 5, -1, -1},
                Pcre4jUtils.convertOvectorToStringIndices(subject, ovector)
        );
    }

    @Test
    void convertOvectorToStringIndicesMultiByte() {
        // "café" - é is 2 bytes in UTF-8
        var subject = "caf\u00e9";
        // Match "é" at byte offset 3-5
        var ovector = new long[]{3, 5};
        var result = Pcre4jUtils.convertOvectorToStringIndices(subject, ovector);
        assertEquals(3, result[0]); // char index 3
        assertEquals(4, result[1]); // char index 4
    }

    @Test
    void convertOvectorToStringIndicesAllUnmatched() {
        var subject = "hello";
        var ovector = new long[]{-1, -1};
        assertArrayEquals(new int[]{-1, -1}, Pcre4jUtils.convertOvectorToStringIndices(subject, ovector));
    }

    @Test
    void convertOvectorToStringIndicesNullSubjectThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.convertOvectorToStringIndices(null, new long[]{0, 5}));
    }

    @Test
    void convertOvectorToStringIndicesNullOvectorThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.convertOvectorToStringIndices("test", null));
    }

    @Test
    void convertOvectorToStringIndicesTooSmallThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.convertOvectorToStringIndices("test", new long[]{0}));
    }

    @Test
    void convertOvectorToStringIndicesOddLengthThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.convertOvectorToStringIndices("test", new long[]{0, 5, 1}));
    }

    // === getGroupNames ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getGroupNamesNoNames(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)");
        var names = Pcre4jUtils.getGroupNames(code);
        assertEquals(2, names.length);
        // All unnamed groups should be null
        for (var name : names) {
            assertEquals(null, name);
        }
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getGroupNamesWithNames(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>a)(?<second>b)");
        var names = Pcre4jUtils.getGroupNames(code);
        assertEquals(2, names.length);
        assertEquals("first", names[0]);
        assertEquals("second", names[1]);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getGroupNamesMixedNaming(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>a)(b)(?<third>c)");
        var names = Pcre4jUtils.getGroupNames(code);
        assertEquals(3, names.length);
        assertEquals("first", names[0]);
        assertEquals(null, names[1]);
        assertEquals("third", names[2]);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getGroupNamesNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> Pcre4jUtils.getGroupNames(null));
    }

    // === getMatchGroups ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getMatchGroupsWithMatchData(IPcre2 api) {
        var code = new Pcre2Code(api, "(hello) (world)");
        var matchData = new Pcre2MatchData(code);
        code.match("hello world", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);

        var groups = Pcre4jUtils.getMatchGroups(code, "hello world", matchData);
        assertEquals(3, groups.length); // full match + 2 groups
        assertEquals("hello world", groups[0]);
        assertEquals("hello", groups[1]);
        assertEquals("world", groups[2]);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getMatchGroupsWithOvector(IPcre2 api) {
        var code = new Pcre2Code(api, "(hello) (world)");
        var matchData = new Pcre2MatchData(code);
        code.match("hello world", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        var ovector = matchData.ovector();

        var groups = Pcre4jUtils.getMatchGroups(code, "hello world", ovector);
        assertEquals(3, groups.length);
        assertEquals("hello world", groups[0]);
        assertEquals("hello", groups[1]);
        assertEquals("world", groups[2]);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getMatchGroupsNullCodeThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.getMatchGroups(null, "test", new long[]{0, 4}));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getMatchGroupsNullSubjectThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.getMatchGroups(code, null, new long[]{0, 4}));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getMatchGroupsNullOvectorThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.getMatchGroups(code, "test", (long[]) null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getMatchGroupsNullMatchDataThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.getMatchGroups(code, "test", (Pcre2MatchData) null));
    }

    // === getNamedMatchGroups ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getNamedMatchGroupsNullMatchDataThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<greeting>hello)");
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.getNamedMatchGroups(code, "hello", (Pcre2MatchData) null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getNamedMatchGroupsNullCodeThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.getNamedMatchGroups(null, "test", new long[]{0, 4}));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getNamedMatchGroupsNullSubjectThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.getNamedMatchGroups(code, null, new long[]{0, 4}));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getNamedMatchGroupsNullOvectorThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalArgumentException.class, () ->
                Pcre4jUtils.getNamedMatchGroups(code, "test", (long[]) null));
    }

    // === JIT target / JIT support consistency ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getJitTargetConsistentWithIsJitSupported(IPcre2 api) {
        var jitSupported = Pcre4jUtils.isJitSupported(api);
        var jitTarget = Pcre4jUtils.getJitTarget(api);
        if (jitSupported) {
            assertNotNull(jitTarget, "JIT target should be non-null when JIT is supported");
            assertFalse(jitTarget.isEmpty(), "JIT target should be non-empty when JIT is supported");
        } else {
            assertNull(jitTarget, "JIT target should be null when JIT is not supported");
        }
    }

    // === getErrorMessage with various error codes ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getErrorMessageMultipleCodes(IPcre2 api) {
        // Test several well-known error codes produce non-empty messages
        int[] errorCodes = {
                IPcre2.ERROR_NOMATCH,
                IPcre2.ERROR_NULL,
                IPcre2.ERROR_BADOPTION,
                IPcre2.ERROR_NOMEMORY
        };
        for (var code : errorCodes) {
            var msg = Pcre4jUtils.getErrorMessage(api, code);
            assertNotNull(msg, "Error message for code " + code + " should not be null");
            assertFalse(msg.isEmpty(), "Error message for code " + code + " should not be empty");
        }
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getErrorMessageDistinctMessages(IPcre2 api) {
        // Different error codes should produce different messages
        var noMatchMsg = Pcre4jUtils.getErrorMessage(api, IPcre2.ERROR_NOMATCH);
        var nullMsg = Pcre4jUtils.getErrorMessage(api, IPcre2.ERROR_NULL);
        assertFalse(noMatchMsg.equals(nullMsg), "Different error codes should produce different messages");
    }

    // === getGroupNames with many named groups ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getGroupNamesManyNamedGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<a>.)(?<b>.)(?<c>.)(?<d>.)(?<e>.)");
        var names = Pcre4jUtils.getGroupNames(code);
        assertEquals(5, names.length);
        assertEquals("a", names[0]);
        assertEquals("b", names[1]);
        assertEquals("c", names[2]);
        assertEquals("d", names[3]);
        assertEquals("e", names[4]);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getGroupNamesNoGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        var names = Pcre4jUtils.getGroupNames(code);
        assertEquals(0, names.length);
    }

    // === getMatchGroups with unmatched optional groups ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getMatchGroupsUnmatchedOptionalGroup(IPcre2 api) {
        // Pattern with an optional group that won't match
        var code = new Pcre2Code(api, "(hello)(?: (world))?", EnumSet.of(Pcre2CompileOption.UTF));
        var matchData = new Pcre2MatchData(code);
        var subject = "hello";
        code.match(subject, 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        var ovector = matchData.ovector();

        // The second group should be unmatched (-1)
        assertEquals(-1L, ovector[4]);
        assertEquals(-1L, ovector[5]);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getMatchGroupsAtStringBoundaries(IPcre2 api) {
        // Match at start and end boundaries
        var code = new Pcre2Code(api, "^(hello)$");
        var matchData = new Pcre2MatchData(code);
        var subject = "hello";
        code.match(subject, 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);

        var groups = Pcre4jUtils.getMatchGroups(code, subject, matchData);
        assertEquals(2, groups.length);
        assertEquals("hello", groups[0]);
        assertEquals("hello", groups[1]);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getMatchGroupsMultiByteSubject(IPcre2 api) {
        // Match within a multi-byte UTF-8 subject
        var code = new Pcre2Code(api, "(café)", EnumSet.of(Pcre2CompileOption.UTF));
        var matchData = new Pcre2MatchData(code);
        var subject = "café";
        code.match(subject, 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);

        var groups = Pcre4jUtils.getMatchGroups(code, subject, matchData);
        assertEquals(2, groups.length);
        assertEquals("café", groups[0]);
        assertEquals("café", groups[1]);
    }

}
