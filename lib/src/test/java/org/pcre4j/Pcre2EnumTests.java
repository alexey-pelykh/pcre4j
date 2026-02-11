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
import org.pcre4j.api.IPcre2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for enum classes and error types.
 */
public class Pcre2EnumTests {

    // === Pcre2Bsr ===

    @Test
    void bsrValueOfValid() {
        assertTrue(Pcre2Bsr.valueOf(IPcre2.BSR_UNICODE).isPresent());
        assertEquals(Pcre2Bsr.UNICODE, Pcre2Bsr.valueOf(IPcre2.BSR_UNICODE).get());

        assertTrue(Pcre2Bsr.valueOf(IPcre2.BSR_ANYCRLF).isPresent());
        assertEquals(Pcre2Bsr.ANYCRLF, Pcre2Bsr.valueOf(IPcre2.BSR_ANYCRLF).get());
    }

    @Test
    void bsrValueOfInvalid() {
        assertFalse(Pcre2Bsr.valueOf(-999).isPresent());
    }

    @Test
    void bsrValue() {
        assertEquals(IPcre2.BSR_UNICODE, Pcre2Bsr.UNICODE.value());
        assertEquals(IPcre2.BSR_ANYCRLF, Pcre2Bsr.ANYCRLF.value());
    }

    // === Pcre2Newline ===

    @Test
    void newlineValueOfValid() {
        assertTrue(Pcre2Newline.valueOf(IPcre2.NEWLINE_CR).isPresent());
        assertEquals(Pcre2Newline.CR, Pcre2Newline.valueOf(IPcre2.NEWLINE_CR).get());

        assertTrue(Pcre2Newline.valueOf(IPcre2.NEWLINE_LF).isPresent());
        assertEquals(Pcre2Newline.LF, Pcre2Newline.valueOf(IPcre2.NEWLINE_LF).get());

        assertTrue(Pcre2Newline.valueOf(IPcre2.NEWLINE_CRLF).isPresent());
        assertEquals(Pcre2Newline.CRLF, Pcre2Newline.valueOf(IPcre2.NEWLINE_CRLF).get());

        assertTrue(Pcre2Newline.valueOf(IPcre2.NEWLINE_ANY).isPresent());
        assertEquals(Pcre2Newline.ANY, Pcre2Newline.valueOf(IPcre2.NEWLINE_ANY).get());

        assertTrue(Pcre2Newline.valueOf(IPcre2.NEWLINE_ANYCRLF).isPresent());
        assertEquals(Pcre2Newline.ANYCRLF, Pcre2Newline.valueOf(IPcre2.NEWLINE_ANYCRLF).get());

        assertTrue(Pcre2Newline.valueOf(IPcre2.NEWLINE_NUL).isPresent());
        assertEquals(Pcre2Newline.NUL, Pcre2Newline.valueOf(IPcre2.NEWLINE_NUL).get());
    }

    @Test
    void newlineValueOfInvalid() {
        assertFalse(Pcre2Newline.valueOf(-999).isPresent());
    }

    @Test
    void newlineValues() {
        assertEquals(IPcre2.NEWLINE_CR, Pcre2Newline.CR.value());
        assertEquals(IPcre2.NEWLINE_LF, Pcre2Newline.LF.value());
        assertEquals(IPcre2.NEWLINE_CRLF, Pcre2Newline.CRLF.value());
        assertEquals(IPcre2.NEWLINE_ANY, Pcre2Newline.ANY.value());
        assertEquals(IPcre2.NEWLINE_ANYCRLF, Pcre2Newline.ANYCRLF.value());
        assertEquals(IPcre2.NEWLINE_NUL, Pcre2Newline.NUL.value());
    }

    // === Pcre2CompileOption ===

    @Test
    void compileOptionValues() {
        assertEquals(IPcre2.ANCHORED, Pcre2CompileOption.ANCHORED.value());
        assertEquals(IPcre2.CASELESS, Pcre2CompileOption.CASELESS.value());
        assertEquals(IPcre2.DOTALL, Pcre2CompileOption.DOTALL.value());
        assertEquals(IPcre2.MULTILINE, Pcre2CompileOption.MULTILINE.value());
    }

    // === Pcre2MatchOption ===

    @Test
    void matchOptionValues() {
        assertEquals(IPcre2.NOTBOL, Pcre2MatchOption.NOTBOL.value());
        assertEquals(IPcre2.NOTEOL, Pcre2MatchOption.NOTEOL.value());
        assertEquals(IPcre2.NOTEMPTY, Pcre2MatchOption.NOTEMPTY.value());
        assertEquals(IPcre2.PARTIAL_SOFT, Pcre2MatchOption.PARTIAL_SOFT.value());
        assertEquals(IPcre2.PARTIAL_HARD, Pcre2MatchOption.PARTIAL_HARD.value());
        assertEquals(IPcre2.COPY_MATCHED_SUBJECT, Pcre2MatchOption.COPY_MATCHED_SUBJECT.value());
    }

    // === Pcre2SubstituteOption ===

    @Test
    void substituteOptionValues() {
        assertEquals(IPcre2.SUBSTITUTE_GLOBAL, Pcre2SubstituteOption.GLOBAL.value());
        assertEquals(IPcre2.SUBSTITUTE_EXTENDED, Pcre2SubstituteOption.EXTENDED.value());
    }

    // === Pcre2JitOption ===

    @Test
    void jitOptionValues() {
        assertEquals(IPcre2.JIT_COMPLETE, Pcre2JitOption.COMPLETE.value());
        assertEquals(IPcre2.JIT_PARTIAL_SOFT, Pcre2JitOption.PARTIAL_SOFT.value());
        assertEquals(IPcre2.JIT_PARTIAL_HARD, Pcre2JitOption.PARTIAL_HARD.value());
    }

    // === Pcre2CompileExtraOption ===

    @Test
    void compileExtraOptionValues() {
        for (var option : Pcre2CompileExtraOption.values()) {
            assertTrue(option.value() != 0, "Extra option " + option + " should have non-zero value");
        }
    }

    // === Pcre2PatternInfo ===

    @Test
    void patternInfoValueOfValid() {
        assertTrue(Pcre2PatternInfo.valueOf(IPcre2.INFO_BACKREFMAX).isPresent());
        assertTrue(Pcre2PatternInfo.valueOf(IPcre2.INFO_CAPTURECOUNT).isPresent());
        assertTrue(Pcre2PatternInfo.valueOf(IPcre2.INFO_BSR).isPresent());
    }

    @Test
    void patternInfoValueOfInvalid() {
        assertFalse(Pcre2PatternInfo.valueOf(-999).isPresent());
    }

    // === Pcre2CompileError ===

    @Test
    void compileErrorFields() {
        var error = new Pcre2CompileError("test?", 4, "quantifier does not follow a repeatable item");
        assertEquals("test?", error.pattern());
        assertEquals(4, error.offset());
        assertEquals("quantifier does not follow a repeatable item", error.message());
        assertNotNull(error.getMessage());
    }

    @Test
    void compileErrorWithCause() {
        var cause = new RuntimeException("underlying");
        var error = new Pcre2CompileError("test?", 4, "compile error", cause);
        assertEquals(cause, error.getCause());
    }

    @Test
    void compileErrorPatternRegionTruncation() {
        // Test with error at the beginning (should not have leading ellipsis)
        var error1 = new Pcre2CompileError("?test", 0, "error");
        assertNotNull(error1.getMessage());
        assertFalse(error1.getMessage().contains("\u2026?"));

        // Test with error in the middle of a long pattern (should have ellipsis on both sides)
        var error2 = new Pcre2CompileError("abcdefghijklmnop", 8, "error");
        assertNotNull(error2.getMessage());

        // Test with error at the end
        var error3 = new Pcre2CompileError("test?", 4, "error");
        assertNotNull(error3.getMessage());
    }

    // === Pcre2SubstituteError ===

    @Test
    void substituteErrorMessage() {
        var error = new Pcre2SubstituteError("substitution error");
        assertEquals("substitution error", error.getMessage());
    }

    // === Pcre2NoSubstringError ===

    @Test
    void noSubstringErrorMessage() {
        var error = new Pcre2NoSubstringError("no substring");
        assertEquals("no substring", error.getMessage());
    }

    // === Pcre2NoUniqueSubstringError ===

    @Test
    void noUniqueSubstringErrorMessage() {
        var error = new Pcre2NoUniqueSubstringError("not unique");
        assertEquals("not unique", error.getMessage());
    }
}
