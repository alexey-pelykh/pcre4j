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
package org.pcre4j;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for error/exception class edge cases across the lib module.
 */
public class Pcre2ErrorClassTests {

    // === Pcre2CompileError getPatternRegion edge cases ===

    @Test
    void compileErrorShortPatternNoEllipsis() {
        // Pattern shorter than region size: no ellipsis on either side
        var error = new Pcre2CompileError("ab", 1, "test error");
        var message = error.getMessage();
        assertTrue(message.contains("\"ab\""), "Short pattern should appear without ellipsis: " + message);
    }

    @Test
    void compileErrorAtStartNoLeadingEllipsis() {
        // Error at offset 0: no leading ellipsis, may have trailing ellipsis
        var error = new Pcre2CompileError("abcdefghij", 0, "test error");
        var message = error.getMessage();
        // Region is pattern[0..3] = "abc", no leading ellipsis
        assertTrue(
                message.contains("\"abc\u2026\""),
                "Start of long pattern should have trailing ellipsis only: " + message
        );
    }

    @Test
    void compileErrorAtEndNoTrailingEllipsis() {
        // Error at the last character: no trailing ellipsis, may have leading ellipsis
        var error = new Pcre2CompileError("abcdefghij", 9, "test error");
        var message = error.getMessage();
        // Region is pattern[6..10] = "ghij", with leading ellipsis
        assertTrue(
                message.contains("\"\u2026ghij\""),
                "End of long pattern should have leading ellipsis only: " + message
        );
    }

    @Test
    void compileErrorMiddleBothEllipsis() {
        // Error in the middle of a long pattern: both leading and trailing ellipsis
        var error = new Pcre2CompileError("abcdefghijklmnop", 8, "test error");
        var message = error.getMessage();
        // Region is pattern[5..11] = "fghijk", with ellipsis on both sides
        assertTrue(message.contains("\u2026"), "Middle of long pattern should have ellipsis: " + message);
    }

    @Test
    void compileErrorEmptyPattern() {
        // Edge case: empty pattern
        var error = new Pcre2CompileError("", 0, "test error");
        var message = error.getMessage();
        assertTrue(message.contains("\"\""), "Empty pattern should produce empty quoted region: " + message);
    }

    @Test
    void compileErrorSingleCharPattern() {
        var error = new Pcre2CompileError("?", 0, "test error");
        assertEquals("?", error.pattern());
        assertEquals(0, error.offset());
        assertEquals("test error", error.message());
    }

    @Test
    void compileErrorExactlyRegionSizePattern() {
        // Pattern of exactly 2*PATTERN_REGION_SIZE (6 chars) with error in the middle
        var error = new Pcre2CompileError("abcdef", 3, "test error");
        var message = error.getMessage();
        // Region is [0..6] = entire pattern, no ellipsis
        assertTrue(message.contains("\"abcdef\""), "Pattern at region size should have no ellipsis: " + message);
    }

    @Test
    void compileErrorMessageFormat() {
        var error = new Pcre2CompileError("test", 2, "some error");
        assertEquals("Error in pattern at 2 \"test\": some error", error.getMessage());
    }

    // === Pcre2CompileError hierarchy and cause chain ===

    @Test
    void compileErrorIsIllegalArgumentException() {
        var error = new Pcre2CompileError("test", 0, "error");
        assertInstanceOf(IllegalArgumentException.class, error);
    }

    @Test
    void compileErrorCauseIsNullWhenNotProvided() {
        var error = new Pcre2CompileError("test", 0, "error");
        assertNull(error.getCause());
    }

    @Test
    void compileErrorCauseIsSetWhenProvided() {
        var cause = new RuntimeException("underlying");
        var error = new Pcre2CompileError("test", 0, "error", cause);
        assertEquals(cause, error.getCause());
    }

    @Test
    void compileErrorNullCauseMatchesSingleArgConstructor() {
        var single = new Pcre2CompileError("test", 0, "error");
        var nullCause = new Pcre2CompileError("test", 0, "error", null);
        assertEquals(single.getMessage(), nullCause.getMessage());
        assertNull(nullCause.getCause());
    }

    // === Pcre2SubstituteError edge cases ===

    @Test
    void substituteErrorIsRuntimeException() {
        var error = new Pcre2SubstituteError("test");
        assertInstanceOf(RuntimeException.class, error);
    }

    @Test
    void substituteErrorCauseIsNullWhenNotProvided() {
        var error = new Pcre2SubstituteError("test");
        assertNull(error.getCause());
    }

    @Test
    void substituteErrorNullCauseMatchesSingleArgConstructor() {
        var single = new Pcre2SubstituteError("test");
        var nullCause = new Pcre2SubstituteError("test", null);
        assertEquals(single.getMessage(), nullCause.getMessage());
        assertNull(nullCause.getCause());
    }

    @Test
    void substituteErrorMessageIsSameWithAndWithoutCause() {
        var without = new Pcre2SubstituteError("error msg");
        var with = new Pcre2SubstituteError("error msg", new RuntimeException("cause"));
        assertEquals(without.getMessage(), with.getMessage());
    }

    // === Pcre2NoSubstringError edge cases ===

    @Test
    void noSubstringErrorIsRuntimeException() {
        var error = new Pcre2NoSubstringError("test");
        assertInstanceOf(RuntimeException.class, error);
    }

    @Test
    void noSubstringErrorCauseIsNullWhenNotProvided() {
        var error = new Pcre2NoSubstringError("test");
        assertNull(error.getCause());
    }

    @Test
    void noSubstringErrorNullCauseMatchesSingleArgConstructor() {
        var single = new Pcre2NoSubstringError("test");
        var nullCause = new Pcre2NoSubstringError("test", null);
        assertEquals(single.getMessage(), nullCause.getMessage());
        assertNull(nullCause.getCause());
    }

    @Test
    void noSubstringErrorMessageIsSameWithAndWithoutCause() {
        var without = new Pcre2NoSubstringError("error msg");
        var with = new Pcre2NoSubstringError("error msg", new RuntimeException("cause"));
        assertEquals(without.getMessage(), with.getMessage());
    }

    // === Pcre2NoUniqueSubstringError edge cases ===

    @Test
    void noUniqueSubstringErrorIsRuntimeException() {
        var error = new Pcre2NoUniqueSubstringError("test");
        assertInstanceOf(RuntimeException.class, error);
    }

    @Test
    void noUniqueSubstringErrorCauseIsNullWhenNotProvided() {
        var error = new Pcre2NoUniqueSubstringError("test");
        assertNull(error.getCause());
    }

    @Test
    void noUniqueSubstringErrorNullCauseMatchesSingleArgConstructor() {
        var single = new Pcre2NoUniqueSubstringError("test");
        var nullCause = new Pcre2NoUniqueSubstringError("test", null);
        assertEquals(single.getMessage(), nullCause.getMessage());
        assertNull(nullCause.getCause());
    }

    @Test
    void noUniqueSubstringErrorMessageIsSameWithAndWithoutCause() {
        var without = new Pcre2NoUniqueSubstringError("error msg");
        var with = new Pcre2NoUniqueSubstringError("error msg", new RuntimeException("cause"));
        assertEquals(without.getMessage(), with.getMessage());
    }
}
