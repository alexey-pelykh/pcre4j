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
package org.pcre4j.test;

import org.junit.jupiter.api.Test;
import org.pcre4j.Pcre2Code;
import org.pcre4j.Pcre2CompileContext;
import org.pcre4j.Pcre2MatchData;
import org.pcre4j.Pcre4jUtils;
import org.pcre4j.api.IPcre2;
import org.pcre4j.exception.Pcre2CompileException;
import org.pcre4j.option.Pcre2Bsr;
import org.pcre4j.option.Pcre2CompileExtraOption;
import org.pcre4j.option.Pcre2CompileOption;
import org.pcre4j.option.Pcre2MatchOption;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Contract tests for PCRE2 compile context operations.
 *
 * @param <T> the PCRE2 API implementation type
 */
public interface Pcre2CompileContextContractTest<T extends IPcre2> {

    /**
     * Returns the PCRE2 API implementation to test.
     *
     * @return the PCRE2 API implementation
     */
    T getApi();

    @Test
    default void setBsrNullThrows() {
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        assertThrows(IllegalArgumentException.class, () -> compileContext.setBsr(null));
    }

    @Test
    default void setBsrUnicodeAllowed() {
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        // Should not throw
        compileContext.setBsr(Pcre2Bsr.UNICODE);
    }

    @Test
    default void setBsrAnyCrLfAllowed() {
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        // Should not throw
        compileContext.setBsr(Pcre2Bsr.ANYCRLF);
    }

    @Test
    default void bsrUnicodeMatchesVerticalTab() {
        // With BSR_UNICODE, \R should match vertical tab (U+000B)
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        compileContext.setBsr(Pcre2Bsr.UNICODE);

        final var code = new Pcre2Code(
                getApi(),
                "\\R",
                EnumSet.noneOf(Pcre2CompileOption.class),
                compileContext
        );
        final var matchData = new Pcre2MatchData(code);

        // Vertical tab (U+000B) should match with BSR_UNICODE
        final var result = code.match(
                "\u000B",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );

        assertTrue(result > 0, "\\R with BSR_UNICODE should match vertical tab");
    }

    @Test
    default void bsrAnyCrLfDoesNotMatchVerticalTab() {
        // With BSR_ANYCRLF, \R should NOT match vertical tab (U+000B)
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        compileContext.setBsr(Pcre2Bsr.ANYCRLF);

        final var code = new Pcre2Code(
                getApi(),
                "\\R",
                EnumSet.noneOf(Pcre2CompileOption.class),
                compileContext
        );
        final var matchData = new Pcre2MatchData(code);

        // Vertical tab (U+000B) should NOT match with BSR_ANYCRLF
        final var result = code.match(
                "\u000B",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );

        assertEquals(IPcre2.ERROR_NOMATCH, result, "\\R with BSR_ANYCRLF should NOT match vertical tab");
    }

    @Test
    default void bsrAnyCrLfMatchesCrLf() {
        // With BSR_ANYCRLF, \R should match CR, LF, and CRLF
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        compileContext.setBsr(Pcre2Bsr.ANYCRLF);

        final var code = new Pcre2Code(
                getApi(),
                "\\R",
                EnumSet.noneOf(Pcre2CompileOption.class),
                compileContext
        );
        final var matchData = new Pcre2MatchData(code);

        // CR should match
        assertTrue(code.match("\r", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) > 0,
                "\\R with BSR_ANYCRLF should match CR");

        // LF should match
        assertTrue(code.match("\n", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) > 0,
                "\\R with BSR_ANYCRLF should match LF");

        // CRLF should match
        assertTrue(code.match("\r\n", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) > 0,
                "\\R with BSR_ANYCRLF should match CRLF");
    }

    @Test
    default void setParensNestLimitAllowsValidNesting() {
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        compileContext.setParensNestLimit(10);

        // A pattern with nesting depth of 3 should compile successfully
        final var code = new Pcre2Code(
                getApi(),
                "((a)(b))",
                EnumSet.noneOf(Pcre2CompileOption.class),
                compileContext
        );

        final var matchData = new Pcre2MatchData(code);
        final var result = code.match(
                "ab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );

        assertTrue(result > 0, "Pattern with acceptable nesting should match");
    }

    @Test
    default void setParensNestLimitRejectsExcessiveNesting() {
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        compileContext.setParensNestLimit(2);

        // A pattern with nesting depth of 3 should fail to compile with limit of 2
        final var exception = assertThrows(Pcre2CompileException.class, () -> new Pcre2Code(
                getApi(),
                "(((a)))",
                EnumSet.noneOf(Pcre2CompileOption.class),
                compileContext
        ));

        assertTrue(exception.message().contains("parentheses") || exception.message().contains("nest"),
                "Should fail with parentheses nesting error, got: " + exception.message());
    }

    @Test
    default void setParensNestLimitWithHighValue() {
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        // Setting a high limit should not throw
        compileContext.setParensNestLimit(1000);

        // Should compile complex nested pattern
        final var code = new Pcre2Code(
                getApi(),
                "((((((a))))))",
                EnumSet.noneOf(Pcre2CompileOption.class),
                compileContext
        );

        final var matchData = new Pcre2MatchData(code);
        final var result = code.match(
                "a",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );

        assertTrue(result > 0, "Pattern should match with high nesting limit");
    }

    @Test
    default void setMaxPatternLengthAllowsValidPattern() {
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        compileContext.setMaxPatternLength(100);

        // A short pattern should compile successfully
        final var code = new Pcre2Code(
                getApi(),
                "abc",
                EnumSet.noneOf(Pcre2CompileOption.class),
                compileContext
        );

        final var matchData = new Pcre2MatchData(code);
        final var result = code.match(
                "abc",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );

        assertTrue(result > 0, "Pattern within length limit should compile and match");
    }

    @Test
    default void setMaxPatternLengthRejectsLongPattern() {
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        compileContext.setMaxPatternLength(5);

        // A pattern longer than the limit should fail to compile
        final var exception = assertThrows(Pcre2CompileException.class, () -> new Pcre2Code(
                getApi(),
                "abcdefghij",
                EnumSet.noneOf(Pcre2CompileOption.class),
                compileContext
        ));

        assertTrue(exception.message().contains("pattern") || exception.message().contains("long"),
                "Should fail with pattern length error, got: " + exception.message());
    }

    @Test
    default void setMaxPatternLengthWithHighValue() {
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        // Setting a high limit should not throw
        compileContext.setMaxPatternLength(1000000);

        // Should compile a reasonably long pattern
        final var longPattern = "a{1,100}";
        final var code = new Pcre2Code(
                getApi(),
                longPattern,
                EnumSet.noneOf(Pcre2CompileOption.class),
                compileContext
        );

        final var matchData = new Pcre2MatchData(code);
        final var result = code.match(
                "aaaaaaaaaa",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );

        assertTrue(result > 0, "Pattern should match with high length limit");
    }

    @Test
    default void setCompileExtraOptionsNullThrows() {
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        assertThrows(IllegalArgumentException.class,
                () -> compileContext.setCompileExtraOptions(null));
    }

    @Test
    default void setCompileExtraOptionsEmpty() {
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        // Should not throw with empty EnumSet
        compileContext.setCompileExtraOptions(EnumSet.noneOf(Pcre2CompileExtraOption.class));

        // Pattern should still compile and match
        final var code = new Pcre2Code(
                getApi(),
                "\\d+",
                EnumSet.of(Pcre2CompileOption.UCP, Pcre2CompileOption.UTF),
                compileContext
        );

        final var matchData = new Pcre2MatchData(code);
        // Unicode digit (Arabic-Indic digit 5) should match with UCP+UTF and no ASCII restriction
        final var result = code.match(
                "\u0665",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );

        assertTrue(result > 0, "Unicode digit should match \\d with UCP+UTF enabled");
    }

    @Test
    default void setCompileExtraOptionsAsciiDigitRestrictsToAscii() {
        var api = getApi();
        // ASCII_BSD was added in PCRE2 10.43
        assumeTrue(Pcre4jUtils.isVersionAtLeast(api, 10, 43),
                "Skipping test: ASCII_BSD requires PCRE2 10.43+");

        final var compileContext = new Pcre2CompileContext(api, null);
        compileContext.setCompileExtraOptions(EnumSet.of(Pcre2CompileExtraOption.ASCII_BSD));

        // With ASCII_BSD, \d should only match ASCII digits even with UCP+UTF
        final var code = new Pcre2Code(
                api,
                "\\d+",
                EnumSet.of(Pcre2CompileOption.UCP, Pcre2CompileOption.UTF),
                compileContext
        );

        final var matchData = new Pcre2MatchData(code);

        // ASCII digit should still match
        final var asciiResult = code.match(
                "5",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertTrue(asciiResult > 0, "ASCII digit should match \\d with ASCII_BSD");

        // Unicode digit (Arabic-Indic digit 5) should NOT match
        final var unicodeResult = code.match(
                "\u0665",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertTrue(unicodeResult < 0, "Unicode digit should NOT match \\d with ASCII_BSD");
    }

    @Test
    default void setCompileExtraOptionsMatchWord() {
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        compileContext.setCompileExtraOptions(EnumSet.of(Pcre2CompileExtraOption.MATCH_WORD));

        // With MATCH_WORD, pattern should only match whole words
        final var code = new Pcre2Code(
                getApi(),
                "test",
                EnumSet.noneOf(Pcre2CompileOption.class),
                compileContext
        );

        final var matchData = new Pcre2MatchData(code);

        // Should match "test" as a whole word
        final var wholeWordResult = code.match(
                "a test here",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertTrue(wholeWordResult > 0, "Should match 'test' as whole word");

        // Should NOT match "test" within "testing"
        final var partialResult = code.match(
                "testing",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertTrue(partialResult < 0, "Should NOT match 'test' within 'testing' with MATCH_WORD");
    }

    @Test
    default void setCompileExtraOptionsMatchLine() {
        final var compileContext = new Pcre2CompileContext(getApi(), null);
        compileContext.setCompileExtraOptions(EnumSet.of(Pcre2CompileExtraOption.MATCH_LINE));

        // With MATCH_LINE, pattern should only match whole lines
        final var code = new Pcre2Code(
                getApi(),
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                compileContext
        );

        final var matchData = new Pcre2MatchData(code);

        // Should match "hello" as a whole line
        final var wholeLineResult = code.match(
                "hello",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertTrue(wholeLineResult > 0, "Should match 'hello' as whole line");

        // Should NOT match "hello" within a longer line
        final var partialResult = code.match(
                "say hello world",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertTrue(partialResult < 0, "Should NOT match 'hello' within a longer line with MATCH_LINE");
    }

    @Test
    default void setCompileExtraOptionsMultipleOptions() {
        var api = getApi();
        // ASCII_BSD, ASCII_BSS, ASCII_BSW were added in PCRE2 10.43
        assumeTrue(Pcre4jUtils.isVersionAtLeast(api, 10, 43),
                "Skipping test: ASCII_BSD/BSS/BSW requires PCRE2 10.43+");

        final var compileContext = new Pcre2CompileContext(api, null);
        // Set multiple extra options at once
        compileContext.setCompileExtraOptions(EnumSet.of(
                Pcre2CompileExtraOption.ASCII_BSD,
                Pcre2CompileExtraOption.ASCII_BSS,
                Pcre2CompileExtraOption.ASCII_BSW
        ));

        // With all ASCII options, \w should only match ASCII word characters even with UCP+UTF
        final var code = new Pcre2Code(
                api,
                "\\w+",
                EnumSet.of(Pcre2CompileOption.UCP, Pcre2CompileOption.UTF),
                compileContext
        );

        final var matchData = new Pcre2MatchData(code);

        // ASCII letters should match
        final var asciiResult = code.match(
                "hello",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertTrue(asciiResult > 0, "ASCII letters should match \\w with ASCII_BSW");

        // Non-ASCII letter (Greek alpha) should NOT match
        final var unicodeResult = code.match(
                "\u03B1",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertTrue(unicodeResult < 0, "Non-ASCII letter should NOT match \\w with ASCII_BSW");
    }
}
