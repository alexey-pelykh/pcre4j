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
import org.pcre4j.exception.Pcre2CompileError;
import org.pcre4j.exception.Pcre2CompileException;
import org.pcre4j.exception.Pcre2Exception;
import org.pcre4j.exception.Pcre2InternalException;
import org.pcre4j.exception.Pcre2MatchException;
import org.pcre4j.exception.Pcre2MatchLimitException;
import org.pcre4j.exception.Pcre2NoSubstringError;
import org.pcre4j.exception.Pcre2NoSubstringException;
import org.pcre4j.exception.Pcre2NoUniqueSubstringError;
import org.pcre4j.exception.Pcre2NoUniqueSubstringException;
import org.pcre4j.exception.Pcre2PatternInfoSizeError;
import org.pcre4j.exception.Pcre2PatternInfoSizeException;
import org.pcre4j.exception.Pcre2SubstituteError;
import org.pcre4j.exception.Pcre2SubstituteException;
import org.pcre4j.exception.Pcre2SubstringException;
import org.pcre4j.option.Pcre2PatternInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for exception class hierarchy and edge cases across the lib module.
 */
@SuppressWarnings("deprecation")
public class Pcre2ErrorClassTests {

    // === Pcre2Exception hierarchy ===

    @Test
    void pcre2ExceptionIsRuntimeException() {
        var ex = new Pcre2Exception("test", 42);
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void pcre2ExceptionCarriesErrorCode() {
        var ex = new Pcre2Exception("test", 42);
        assertEquals(42, ex.errorCode());
    }

    @Test
    void pcre2ExceptionZeroErrorCode() {
        var ex = new Pcre2Exception("test", 0);
        assertEquals(0, ex.errorCode());
    }

    @Test
    void pcre2ExceptionCauseIsNullWhenNotProvided() {
        var ex = new Pcre2Exception("test", 0);
        assertNull(ex.getCause());
    }

    @Test
    void pcre2ExceptionCauseIsSetWhenProvided() {
        var cause = new RuntimeException("underlying");
        var ex = new Pcre2Exception("test", 0, cause);
        assertEquals(cause, ex.getCause());
    }

    // === Pcre2CompileException hierarchy and edge cases ===

    @Test
    void compileExceptionIsPcre2Exception() {
        var ex = new Pcre2CompileException("test", 0, "error", 42);
        assertInstanceOf(Pcre2Exception.class, ex);
    }

    @Test
    void compileExceptionCarriesErrorCode() {
        var ex = new Pcre2CompileException("test", 0, "error", 42);
        assertEquals(42, ex.errorCode());
    }

    @Test
    void compileExceptionShortPatternNoEllipsis() {
        var ex = new Pcre2CompileException("ab", 1, "test error", 0);
        var message = ex.getMessage();
        assertTrue(message.contains("\"ab\""), "Short pattern should appear without ellipsis: " + message);
    }

    @Test
    void compileExceptionAtStartNoLeadingEllipsis() {
        var ex = new Pcre2CompileException("abcdefghij", 0, "test error", 0);
        var message = ex.getMessage();
        assertTrue(
                message.contains("\"abc\u2026\""),
                "Start of long pattern should have trailing ellipsis only: " + message
        );
    }

    @Test
    void compileExceptionAtEndNoTrailingEllipsis() {
        var ex = new Pcre2CompileException("abcdefghij", 9, "test error", 0);
        var message = ex.getMessage();
        assertTrue(
                message.contains("\"\u2026ghij\""),
                "End of long pattern should have leading ellipsis only: " + message
        );
    }

    @Test
    void compileExceptionMiddleBothEllipsis() {
        var ex = new Pcre2CompileException("abcdefghijklmnop", 8, "test error", 0);
        var message = ex.getMessage();
        assertTrue(message.contains("\u2026"), "Middle of long pattern should have ellipsis: " + message);
    }

    @Test
    void compileExceptionEmptyPattern() {
        var ex = new Pcre2CompileException("", 0, "test error", 0);
        var message = ex.getMessage();
        assertTrue(message.contains("\"\""), "Empty pattern should produce empty quoted region: " + message);
    }

    @Test
    void compileExceptionSingleCharPattern() {
        var ex = new Pcre2CompileException("?", 0, "test error", 0);
        assertEquals("?", ex.pattern());
        assertEquals(0, ex.offset());
        assertEquals("test error", ex.message());
    }

    @Test
    void compileExceptionExactlyRegionSizePattern() {
        var ex = new Pcre2CompileException("abcdef", 3, "test error", 0);
        var message = ex.getMessage();
        assertTrue(message.contains("\"abcdef\""), "Pattern at region size should have no ellipsis: " + message);
    }

    @Test
    void compileExceptionMessageFormat() {
        var ex = new Pcre2CompileException("test", 2, "some error", 0);
        assertEquals("Error in pattern at 2 \"test\": some error", ex.getMessage());
    }

    @Test
    void compileExceptionCauseIsNullWhenNotProvided() {
        var ex = new Pcre2CompileException("test", 0, "error", 0);
        assertNull(ex.getCause());
    }

    @Test
    void compileExceptionCauseIsSetWhenProvided() {
        var cause = new RuntimeException("underlying");
        var ex = new Pcre2CompileException("test", 0, "error", 0, cause);
        assertEquals(cause, ex.getCause());
    }

    // === Deprecated Pcre2CompileError backwards compatibility ===

    @Test
    void compileErrorIsPcre2CompileException() {
        var error = new Pcre2CompileError("test", 0, "error");
        assertInstanceOf(Pcre2CompileException.class, error);
    }

    @Test
    void compileErrorIsPcre2Exception() {
        var error = new Pcre2CompileError("test", 0, "error");
        assertInstanceOf(Pcre2Exception.class, error);
    }

    @Test
    void compileErrorNullCauseMatchesSingleArgConstructor() {
        var single = new Pcre2CompileError("test", 0, "error");
        var nullCause = new Pcre2CompileError("test", 0, "error", null);
        assertEquals(single.getMessage(), nullCause.getMessage());
        assertNull(nullCause.getCause());
    }

    // === Pcre2SubstituteException ===

    @Test
    void substituteExceptionIsPcre2Exception() {
        var ex = new Pcre2SubstituteException("test", 42);
        assertInstanceOf(Pcre2Exception.class, ex);
    }

    @Test
    void substituteExceptionCarriesErrorCode() {
        var ex = new Pcre2SubstituteException("test", 42);
        assertEquals(42, ex.errorCode());
    }

    @Test
    void substituteExceptionCauseIsNullWhenNotProvided() {
        var ex = new Pcre2SubstituteException("test", 0);
        assertNull(ex.getCause());
    }

    @Test
    void substituteExceptionNullCauseMatchesSingleArgConstructor() {
        var single = new Pcre2SubstituteException("test", 0);
        var nullCause = new Pcre2SubstituteException("test", 0, null);
        assertEquals(single.getMessage(), nullCause.getMessage());
        assertNull(nullCause.getCause());
    }

    // === Deprecated Pcre2SubstituteError backwards compatibility ===

    @Test
    void substituteErrorIsPcre2SubstituteException() {
        var error = new Pcre2SubstituteError("test");
        assertInstanceOf(Pcre2SubstituteException.class, error);
    }

    @Test
    void substituteErrorIsPcre2Exception() {
        var error = new Pcre2SubstituteError("test");
        assertInstanceOf(Pcre2Exception.class, error);
    }

    @Test
    void substituteErrorNullCauseMatchesSingleArgConstructor() {
        var single = new Pcre2SubstituteError("test");
        var nullCause = new Pcre2SubstituteError("test", null);
        assertEquals(single.getMessage(), nullCause.getMessage());
        assertNull(nullCause.getCause());
    }

    // === Pcre2NoSubstringException ===

    @Test
    void noSubstringExceptionIsPcre2SubstringException() {
        var ex = new Pcre2NoSubstringException("test", 0);
        assertInstanceOf(Pcre2SubstringException.class, ex);
    }

    @Test
    void noSubstringExceptionIsPcre2Exception() {
        var ex = new Pcre2NoSubstringException("test", 0);
        assertInstanceOf(Pcre2Exception.class, ex);
    }

    @Test
    void noSubstringExceptionCauseIsNullWhenNotProvided() {
        var ex = new Pcre2NoSubstringException("test", 0);
        assertNull(ex.getCause());
    }

    @Test
    void noSubstringExceptionNullCauseMatchesSingleArgConstructor() {
        var single = new Pcre2NoSubstringException("test", 0);
        var nullCause = new Pcre2NoSubstringException("test", 0, null);
        assertEquals(single.getMessage(), nullCause.getMessage());
        assertNull(nullCause.getCause());
    }

    // === Deprecated Pcre2NoSubstringError backwards compatibility ===

    @Test
    void noSubstringErrorIsPcre2NoSubstringException() {
        var error = new Pcre2NoSubstringError("test");
        assertInstanceOf(Pcre2NoSubstringException.class, error);
    }

    @Test
    void noSubstringErrorIsPcre2Exception() {
        var error = new Pcre2NoSubstringError("test");
        assertInstanceOf(Pcre2Exception.class, error);
    }

    // === Pcre2NoUniqueSubstringException ===

    @Test
    void noUniqueSubstringExceptionIsPcre2SubstringException() {
        var ex = new Pcre2NoUniqueSubstringException("test", 0);
        assertInstanceOf(Pcre2SubstringException.class, ex);
    }

    @Test
    void noUniqueSubstringExceptionIsPcre2Exception() {
        var ex = new Pcre2NoUniqueSubstringException("test", 0);
        assertInstanceOf(Pcre2Exception.class, ex);
    }

    @Test
    void noUniqueSubstringExceptionCauseIsNullWhenNotProvided() {
        var ex = new Pcre2NoUniqueSubstringException("test", 0);
        assertNull(ex.getCause());
    }

    @Test
    void noUniqueSubstringExceptionNullCauseMatchesSingleArgConstructor() {
        var single = new Pcre2NoUniqueSubstringException("test", 0);
        var nullCause = new Pcre2NoUniqueSubstringException("test", 0, null);
        assertEquals(single.getMessage(), nullCause.getMessage());
        assertNull(nullCause.getCause());
    }

    // === Deprecated Pcre2NoUniqueSubstringError backwards compatibility ===

    @Test
    void noUniqueSubstringErrorIsPcre2NoUniqueSubstringException() {
        var error = new Pcre2NoUniqueSubstringError("test");
        assertInstanceOf(Pcre2NoUniqueSubstringException.class, error);
    }

    @Test
    void noUniqueSubstringErrorIsPcre2Exception() {
        var error = new Pcre2NoUniqueSubstringError("test");
        assertInstanceOf(Pcre2Exception.class, error);
    }

    // === Pcre2MatchException ===

    @Test
    void matchExceptionIsPcre2Exception() {
        var ex = new Pcre2MatchException("test", 42);
        assertInstanceOf(Pcre2Exception.class, ex);
    }

    @Test
    void matchExceptionCarriesErrorCode() {
        var ex = new Pcre2MatchException("test", 42);
        assertEquals(42, ex.errorCode());
    }

    // === Pcre2MatchLimitException ===

    @Test
    void matchLimitExceptionIsPcre2MatchException() {
        var ex = new Pcre2MatchLimitException("test", 42);
        assertInstanceOf(Pcre2MatchException.class, ex);
    }

    @Test
    void matchLimitExceptionIsPcre2Exception() {
        var ex = new Pcre2MatchLimitException("test", 42);
        assertInstanceOf(Pcre2Exception.class, ex);
    }

    @Test
    void matchLimitExceptionCarriesErrorCode() {
        var ex = new Pcre2MatchLimitException("test", 42);
        assertEquals(42, ex.errorCode());
    }

    // === Pcre2InternalException ===

    @Test
    void internalExceptionIsPcre2Exception() {
        var ex = new Pcre2InternalException("test", 0);
        assertInstanceOf(Pcre2Exception.class, ex);
    }

    // === Pcre2PatternInfoSizeException ===

    @Test
    void patternInfoSizeExceptionIsPcre2InternalException() {
        var ex = new Pcre2PatternInfoSizeException(Pcre2PatternInfo.INFO_SIZE, 42);
        assertInstanceOf(Pcre2InternalException.class, ex);
    }

    @Test
    void patternInfoSizeExceptionIsPcre2Exception() {
        var ex = new Pcre2PatternInfoSizeException(Pcre2PatternInfo.INFO_SIZE, 42);
        assertInstanceOf(Pcre2Exception.class, ex);
    }

    // === Cross-hierarchy catch test ===

    @Test
    void allExceptionsAreCatchableAsPcre2Exception() {
        assertInstanceOf(Pcre2Exception.class, new Pcre2CompileException("p", 0, "m", 0));
        assertInstanceOf(Pcre2Exception.class, new Pcre2MatchException("m", 0));
        assertInstanceOf(Pcre2Exception.class, new Pcre2MatchLimitException("m", 0));
        assertInstanceOf(Pcre2Exception.class, new Pcre2SubstituteException("m", 0));
        assertInstanceOf(Pcre2Exception.class, new Pcre2SubstringException("m", 0));
        assertInstanceOf(Pcre2Exception.class, new Pcre2NoSubstringException("m", 0));
        assertInstanceOf(Pcre2Exception.class, new Pcre2NoUniqueSubstringException("m", 0));
        assertInstanceOf(Pcre2Exception.class, new Pcre2InternalException("m", 0));
        assertInstanceOf(Pcre2Exception.class, new Pcre2PatternInfoSizeException(Pcre2PatternInfo.INFO_SIZE, 0));
    }

    @Test
    void deprecatedAliasesAreCatchableAsPcre2Exception() {
        assertInstanceOf(Pcre2Exception.class, new Pcre2CompileError("p", 0, "m"));
        assertInstanceOf(Pcre2Exception.class, new Pcre2SubstituteError("m"));
        assertInstanceOf(Pcre2Exception.class, new Pcre2NoSubstringError("m"));
        assertInstanceOf(Pcre2Exception.class, new Pcre2NoUniqueSubstringError("m"));
        assertInstanceOf(Pcre2Exception.class, new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_SIZE, 0));
    }
}
