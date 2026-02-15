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
import org.pcre4j.api.IPcre2;
import org.pcre4j.exception.Pcre2CompileError;
import org.pcre4j.exception.Pcre2ConvertException;
import org.pcre4j.exception.Pcre2NoSubstringError;
import org.pcre4j.exception.Pcre2NoUniqueSubstringError;
import org.pcre4j.exception.Pcre2PatternInfoSizeError;
import org.pcre4j.exception.Pcre2SubstituteError;
import org.pcre4j.option.Pcre2Bsr;
import org.pcre4j.option.Pcre2CompileExtraOption;
import org.pcre4j.option.Pcre2CompileOption;
import org.pcre4j.option.Pcre2ConvertOption;
import org.pcre4j.option.Pcre2JitOption;
import org.pcre4j.option.Pcre2MatchOption;
import org.pcre4j.option.Pcre2Newline;
import org.pcre4j.option.Pcre2PatternInfo;
import org.pcre4j.option.Pcre2SubstituteOption;

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
        assertEquals(IPcre2.NO_UTF_CHECK, Pcre2CompileOption.NO_UTF_CHECK.value());
        assertEquals(IPcre2.ENDANCHORED, Pcre2CompileOption.ENDANCHORED.value());
        assertEquals(IPcre2.ALLOW_EMPTY_CLASS, Pcre2CompileOption.ALLOW_EMPTY_CLASS.value());
        assertEquals(IPcre2.ALT_BSUX, Pcre2CompileOption.ALT_BSUX.value());
        assertEquals(IPcre2.AUTO_CALLOUT, Pcre2CompileOption.AUTO_CALLOUT.value());
        assertEquals(IPcre2.CASELESS, Pcre2CompileOption.CASELESS.value());
        assertEquals(IPcre2.DOLLAR_ENDONLY, Pcre2CompileOption.DOLLAR_ENDONLY.value());
        assertEquals(IPcre2.DOTALL, Pcre2CompileOption.DOTALL.value());
        assertEquals(IPcre2.DUPNAMES, Pcre2CompileOption.DUPNAMES.value());
        assertEquals(IPcre2.EXTENDED, Pcre2CompileOption.EXTENDED.value());
        assertEquals(IPcre2.FIRSTLINE, Pcre2CompileOption.FIRSTLINE.value());
        assertEquals(IPcre2.MATCH_UNSET_BACKREF, Pcre2CompileOption.MATCH_UNSET_BACKREF.value());
        assertEquals(IPcre2.MULTILINE, Pcre2CompileOption.MULTILINE.value());
        assertEquals(IPcre2.NEVER_UCP, Pcre2CompileOption.NEVER_UCP.value());
        assertEquals(IPcre2.NEVER_UTF, Pcre2CompileOption.NEVER_UTF.value());
        assertEquals(IPcre2.NO_AUTO_CAPTURE, Pcre2CompileOption.NO_AUTO_CAPTURE.value());
        assertEquals(IPcre2.NO_AUTO_POSSESS, Pcre2CompileOption.NO_AUTO_POSSESS.value());
        assertEquals(IPcre2.NO_DOTSTAR_ANCHOR, Pcre2CompileOption.NO_DOTSTAR_ANCHOR.value());
        assertEquals(IPcre2.NO_START_OPTIMIZE, Pcre2CompileOption.NO_START_OPTIMIZE.value());
        assertEquals(IPcre2.UCP, Pcre2CompileOption.UCP.value());
        assertEquals(IPcre2.UNGREEDY, Pcre2CompileOption.UNGREEDY.value());
        assertEquals(IPcre2.UTF, Pcre2CompileOption.UTF.value());
        assertEquals(IPcre2.NEVER_BACKSLASH_C, Pcre2CompileOption.NEVER_BACKSLASH_C.value());
        assertEquals(IPcre2.ALT_CIRCUMFLEX, Pcre2CompileOption.ALT_CIRCUMFLEX.value());
        assertEquals(IPcre2.ALT_VERBNAMES, Pcre2CompileOption.ALT_VERBNAMES.value());
        assertEquals(IPcre2.USE_OFFSET_LIMIT, Pcre2CompileOption.USE_OFFSET_LIMIT.value());
        assertEquals(IPcre2.EXTENDED_MORE, Pcre2CompileOption.EXTENDED_MORE.value());
        assertEquals(IPcre2.LITERAL, Pcre2CompileOption.LITERAL.value());
        assertEquals(IPcre2.MATCH_INVALID_UTF, Pcre2CompileOption.MATCH_INVALID_UTF.value());
    }

    @Test
    void compileOptionValueOfValid() {
        for (var option : Pcre2CompileOption.values()) {
            var result = Pcre2CompileOption.valueOf(option.value());
            assertTrue(result.isPresent(), "valueOf should find " + option);
            assertEquals(option, result.get());
        }
    }

    @Test
    void compileOptionValueOfInvalid() {
        assertFalse(Pcre2CompileOption.valueOf(-999).isPresent());
    }

    // === Pcre2MatchOption ===

    @Test
    void matchOptionValues() {
        assertEquals(IPcre2.ANCHORED, Pcre2MatchOption.ANCHORED.value());
        assertEquals(IPcre2.COPY_MATCHED_SUBJECT, Pcre2MatchOption.COPY_MATCHED_SUBJECT.value());
        assertEquals(IPcre2.ENDANCHORED, Pcre2MatchOption.ENDANCHORED.value());
        assertEquals(IPcre2.NOTBOL, Pcre2MatchOption.NOTBOL.value());
        assertEquals(IPcre2.NOTEOL, Pcre2MatchOption.NOTEOL.value());
        assertEquals(IPcre2.NOTEMPTY, Pcre2MatchOption.NOTEMPTY.value());
        assertEquals(IPcre2.NOTEMPTY_ATSTART, Pcre2MatchOption.NOTEMPTY_ATSTART.value());
        assertEquals(IPcre2.NO_JIT, Pcre2MatchOption.NO_JIT.value());
        assertEquals(IPcre2.NO_UTF_CHECK, Pcre2MatchOption.NO_UTF_CHECK.value());
        assertEquals(IPcre2.PARTIAL_HARD, Pcre2MatchOption.PARTIAL_HARD.value());
        assertEquals(IPcre2.PARTIAL_SOFT, Pcre2MatchOption.PARTIAL_SOFT.value());
    }

    @Test
    void matchOptionValueOfValid() {
        for (var option : Pcre2MatchOption.values()) {
            var result = Pcre2MatchOption.valueOf(option.value());
            assertTrue(result.isPresent(), "valueOf should find " + option);
            assertEquals(option, result.get());
        }
    }

    @Test
    void matchOptionValueOfInvalid() {
        assertFalse(Pcre2MatchOption.valueOf(-999).isPresent());
    }

    // === Pcre2SubstituteOption ===

    @Test
    void substituteOptionValues() {
        assertEquals(IPcre2.SUBSTITUTE_GLOBAL, Pcre2SubstituteOption.GLOBAL.value());
        assertEquals(IPcre2.SUBSTITUTE_EXTENDED, Pcre2SubstituteOption.EXTENDED.value());
        assertEquals(IPcre2.SUBSTITUTE_UNSET_EMPTY, Pcre2SubstituteOption.UNSET_EMPTY.value());
        assertEquals(IPcre2.SUBSTITUTE_UNKNOWN_UNSET, Pcre2SubstituteOption.UNKNOWN_UNSET.value());
        assertEquals(IPcre2.SUBSTITUTE_OVERFLOW_LENGTH, Pcre2SubstituteOption.OVERFLOW_LENGTH.value());
        assertEquals(IPcre2.SUBSTITUTE_LITERAL, Pcre2SubstituteOption.LITERAL.value());
        assertEquals(IPcre2.SUBSTITUTE_MATCHED, Pcre2SubstituteOption.MATCHED.value());
        assertEquals(IPcre2.SUBSTITUTE_REPLACEMENT_ONLY, Pcre2SubstituteOption.REPLACEMENT_ONLY.value());
    }

    @Test
    void substituteOptionValueOfValid() {
        for (var option : Pcre2SubstituteOption.values()) {
            var result = Pcre2SubstituteOption.valueOf(option.value());
            assertTrue(result.isPresent(), "valueOf should find " + option);
            assertEquals(option, result.get());
        }
    }

    @Test
    void substituteOptionValueOfInvalid() {
        assertFalse(Pcre2SubstituteOption.valueOf(-999).isPresent());
    }

    // === Pcre2JitOption ===

    @Test
    void jitOptionValues() {
        assertEquals(IPcre2.JIT_COMPLETE, Pcre2JitOption.COMPLETE.value());
        assertEquals(IPcre2.JIT_PARTIAL_SOFT, Pcre2JitOption.PARTIAL_SOFT.value());
        assertEquals(IPcre2.JIT_PARTIAL_HARD, Pcre2JitOption.PARTIAL_HARD.value());
        assertEquals(IPcre2.JIT_INVALID_UTF, Pcre2JitOption.INVALID_UTF.value());
    }

    @Test
    void jitOptionValueOfValid() {
        for (var option : Pcre2JitOption.values()) {
            var result = Pcre2JitOption.valueOf(option.value());
            assertTrue(result.isPresent(), "valueOf should find " + option);
            assertEquals(option, result.get());
        }
    }

    @Test
    void jitOptionValueOfInvalid() {
        assertFalse(Pcre2JitOption.valueOf(-999).isPresent());
    }

    // === Pcre2CompileExtraOption ===

    @Test
    void compileExtraOptionValues() {
        assertEquals(IPcre2.EXTRA_ALLOW_SURROGATE_ESCAPES, Pcre2CompileExtraOption.ALLOW_SURROGATE_ESCAPES.value());
        assertEquals(IPcre2.EXTRA_BAD_ESCAPE_IS_LITERAL, Pcre2CompileExtraOption.BAD_ESCAPE_IS_LITERAL.value());
        assertEquals(IPcre2.EXTRA_MATCH_WORD, Pcre2CompileExtraOption.MATCH_WORD.value());
        assertEquals(IPcre2.EXTRA_MATCH_LINE, Pcre2CompileExtraOption.MATCH_LINE.value());
        assertEquals(IPcre2.EXTRA_ESCAPED_CR_IS_LF, Pcre2CompileExtraOption.ESCAPED_CR_IS_LF.value());
        assertEquals(IPcre2.EXTRA_ALT_BSUX, Pcre2CompileExtraOption.ALT_BSUX.value());
        assertEquals(IPcre2.EXTRA_ALLOW_LOOKAROUND_BSK, Pcre2CompileExtraOption.ALLOW_LOOKAROUND_BSK.value());
        assertEquals(IPcre2.EXTRA_CASELESS_RESTRICT, Pcre2CompileExtraOption.CASELESS_RESTRICT.value());
        assertEquals(IPcre2.EXTRA_ASCII_BSD, Pcre2CompileExtraOption.ASCII_BSD.value());
        assertEquals(IPcre2.EXTRA_ASCII_BSS, Pcre2CompileExtraOption.ASCII_BSS.value());
        assertEquals(IPcre2.EXTRA_ASCII_BSW, Pcre2CompileExtraOption.ASCII_BSW.value());
        assertEquals(IPcre2.EXTRA_ASCII_POSIX, Pcre2CompileExtraOption.ASCII_POSIX.value());
        assertEquals(IPcre2.EXTRA_ASCII_DIGIT, Pcre2CompileExtraOption.ASCII_DIGIT.value());
    }

    @Test
    void compileExtraOptionValueOfValid() {
        for (var option : Pcre2CompileExtraOption.values()) {
            var result = Pcre2CompileExtraOption.valueOf(option.value());
            assertTrue(result.isPresent(), "valueOf should find " + option);
            assertEquals(option, result.get());
        }
    }

    @Test
    void compileExtraOptionValueOfInvalid() {
        assertFalse(Pcre2CompileExtraOption.valueOf(-999).isPresent());
    }

    // === Pcre2PatternInfo ===

    @Test
    void patternInfoValues() {
        assertEquals(IPcre2.INFO_ALLOPTIONS, Pcre2PatternInfo.INFO_ALLOPTIONS.value());
        assertEquals(IPcre2.INFO_ARGOPTIONS, Pcre2PatternInfo.INFO_ARGOPTIONS.value());
        assertEquals(IPcre2.INFO_BACKREFMAX, Pcre2PatternInfo.INFO_BACKREFMAX.value());
        assertEquals(IPcre2.INFO_BSR, Pcre2PatternInfo.INFO_BSR.value());
        assertEquals(IPcre2.INFO_CAPTURECOUNT, Pcre2PatternInfo.INFO_CAPTURECOUNT.value());
        assertEquals(IPcre2.INFO_FIRSTCODEUNIT, Pcre2PatternInfo.INFO_FIRSTCODEUNIT.value());
        assertEquals(IPcre2.INFO_FIRSTCODETYPE, Pcre2PatternInfo.INFO_FIRSTCODETYPE.value());
        assertEquals(IPcre2.INFO_FIRSTBITMAP, Pcre2PatternInfo.INFO_FIRSTBITMAP.value());
        assertEquals(IPcre2.INFO_HASCRORLF, Pcre2PatternInfo.INFO_HASCRORLF.value());
        assertEquals(IPcre2.INFO_JCHANGED, Pcre2PatternInfo.INFO_JCHANGED.value());
        assertEquals(IPcre2.INFO_JITSIZE, Pcre2PatternInfo.INFO_JITSIZE.value());
        assertEquals(IPcre2.INFO_LASTCODEUNIT, Pcre2PatternInfo.INFO_LASTCODEUNIT.value());
        assertEquals(IPcre2.INFO_LASTCODETYPE, Pcre2PatternInfo.INFO_LASTCODETYPE.value());
        assertEquals(IPcre2.INFO_MATCHEMPTY, Pcre2PatternInfo.INFO_MATCHEMPTY.value());
        assertEquals(IPcre2.INFO_MATCHLIMIT, Pcre2PatternInfo.INFO_MATCHLIMIT.value());
        assertEquals(IPcre2.INFO_MAXLOOKBEHIND, Pcre2PatternInfo.INFO_MAXLOOKBEHIND.value());
        assertEquals(IPcre2.INFO_MINLENGTH, Pcre2PatternInfo.INFO_MINLENGTH.value());
        assertEquals(IPcre2.INFO_NAMECOUNT, Pcre2PatternInfo.INFO_NAMECOUNT.value());
        assertEquals(IPcre2.INFO_NAMEENTRYSIZE, Pcre2PatternInfo.INFO_NAMEENTRYSIZE.value());
        assertEquals(IPcre2.INFO_NAMETABLE, Pcre2PatternInfo.INFO_NAMETABLE.value());
        assertEquals(IPcre2.INFO_NEWLINE, Pcre2PatternInfo.INFO_NEWLINE.value());
        assertEquals(IPcre2.INFO_DEPTHLIMIT, Pcre2PatternInfo.INFO_DEPTHLIMIT.value());
        assertEquals(IPcre2.INFO_RECURSIONLIMIT, Pcre2PatternInfo.INFO_RECURSIONLIMIT.value());
        assertEquals(IPcre2.INFO_SIZE, Pcre2PatternInfo.INFO_SIZE.value());
        assertEquals(IPcre2.INFO_HASBACKSLASHC, Pcre2PatternInfo.INFO_HASBACKSLASHC.value());
        assertEquals(IPcre2.INFO_FRAMESIZE, Pcre2PatternInfo.INFO_FRAMESIZE.value());
        assertEquals(IPcre2.INFO_HEAPLIMIT, Pcre2PatternInfo.INFO_HEAPLIMIT.value());
        assertEquals(IPcre2.INFO_EXTRAOPTIONS, Pcre2PatternInfo.INFO_EXTRAOPTIONS.value());
    }

    @Test
    void patternInfoValueOfValid() {
        for (var info : Pcre2PatternInfo.values()) {
            var result = Pcre2PatternInfo.valueOf(info.value());
            assertTrue(result.isPresent(), "valueOf should find value for " + info);
        }
        // INFO_RECURSIONLIMIT is a deprecated synonym for INFO_DEPTHLIMIT (same value),
        // so valueOf returns INFO_DEPTHLIMIT for both
        assertEquals(
                Pcre2PatternInfo.INFO_DEPTHLIMIT,
                Pcre2PatternInfo.valueOf(IPcre2.INFO_RECURSIONLIMIT).get()
        );
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

    @Test
    void substituteErrorWithCause() {
        var cause = new RuntimeException("underlying");
        var error = new Pcre2SubstituteError("substitution error", cause);
        assertEquals("substitution error", error.getMessage());
        assertEquals(cause, error.getCause());
    }

    // === Pcre2NoSubstringError ===

    @Test
    void noSubstringErrorMessage() {
        var error = new Pcre2NoSubstringError("no substring");
        assertEquals("no substring", error.getMessage());
    }

    @Test
    void noSubstringErrorWithCause() {
        var cause = new RuntimeException("underlying");
        var error = new Pcre2NoSubstringError("no substring", cause);
        assertEquals("no substring", error.getMessage());
        assertEquals(cause, error.getCause());
    }

    // === Pcre2NoUniqueSubstringError ===

    @Test
    void noUniqueSubstringErrorMessage() {
        var error = new Pcre2NoUniqueSubstringError("not unique");
        assertEquals("not unique", error.getMessage());
    }

    @Test
    void noUniqueSubstringErrorWithCause() {
        var cause = new RuntimeException("underlying");
        var error = new Pcre2NoUniqueSubstringError("not unique", cause);
        assertEquals("not unique", error.getMessage());
        assertEquals(cause, error.getCause());
    }

    // === Pcre2PatternInfoSizeError ===

    @Test
    void patternInfoSizeErrorMessage() {
        var error = new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_SIZE, 42);
        assertNotNull(error.getMessage());
        assertTrue(error.getMessage().contains("42"));
        assertTrue(error.getMessage().contains("INFO_SIZE"));
    }

    @Test
    void patternInfoSizeErrorWithCause() {
        var cause = new RuntimeException("underlying");
        var error = new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_SIZE, 42, cause);
        assertNotNull(error.getMessage());
        assertEquals(cause, error.getCause());
    }

    // === Pcre2ConvertOption ===

    @Test
    void convertOptionValueOfValid() {
        assertTrue(Pcre2ConvertOption.valueOf(IPcre2.CONVERT_GLOB).isPresent());
        assertEquals(Pcre2ConvertOption.GLOB, Pcre2ConvertOption.valueOf(IPcre2.CONVERT_GLOB).get());

        assertTrue(Pcre2ConvertOption.valueOf(IPcre2.CONVERT_POSIX_BASIC).isPresent());
        assertEquals(Pcre2ConvertOption.POSIX_BASIC, Pcre2ConvertOption.valueOf(IPcre2.CONVERT_POSIX_BASIC).get());

        assertTrue(Pcre2ConvertOption.valueOf(IPcre2.CONVERT_POSIX_EXTENDED).isPresent());
        assertEquals(Pcre2ConvertOption.POSIX_EXTENDED,
                Pcre2ConvertOption.valueOf(IPcre2.CONVERT_POSIX_EXTENDED).get());
    }

    @Test
    void convertOptionValueOfInvalid() {
        assertFalse(Pcre2ConvertOption.valueOf(-999).isPresent());
    }

    @Test
    void convertOptionValue() {
        assertEquals(IPcre2.CONVERT_UTF, Pcre2ConvertOption.UTF.value());
        assertEquals(IPcre2.CONVERT_NO_UTF_CHECK, Pcre2ConvertOption.NO_UTF_CHECK.value());
        assertEquals(IPcre2.CONVERT_POSIX_BASIC, Pcre2ConvertOption.POSIX_BASIC.value());
        assertEquals(IPcre2.CONVERT_POSIX_EXTENDED, Pcre2ConvertOption.POSIX_EXTENDED.value());
        assertEquals(IPcre2.CONVERT_GLOB, Pcre2ConvertOption.GLOB.value());
        assertEquals(IPcre2.CONVERT_GLOB_NO_WILD_SEPARATOR, Pcre2ConvertOption.GLOB_NO_WILD_SEPARATOR.value());
        assertEquals(IPcre2.CONVERT_GLOB_NO_STARSTAR, Pcre2ConvertOption.GLOB_NO_STARSTAR.value());
    }

    // === Pcre2ConvertException ===

    @Test
    void convertExceptionMessage() {
        var ex = new Pcre2ConvertException("*.{bad", "invalid syntax", IPcre2.ERROR_CONVERT_SYNTAX);
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("*.{bad"));
        assertTrue(ex.getMessage().contains("invalid syntax"));
        assertEquals("*.{bad", ex.pattern());
        assertEquals(IPcre2.ERROR_CONVERT_SYNTAX, ex.errorCode());
    }

    @Test
    void convertExceptionWithCause() {
        var cause = new RuntimeException("underlying");
        var ex = new Pcre2ConvertException("pattern", "error", -64, cause);
        assertEquals(cause, ex.getCause());
    }
}
