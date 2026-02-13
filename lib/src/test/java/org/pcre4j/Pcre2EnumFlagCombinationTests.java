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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Behavioral tests for enum flag combinations.
 * <p>
 * These tests verify that combining multiple flags via {@link EnumSet} produces the correct
 * behavioral effects when passed through the PCRE2 API, unlike {@link Pcre2EnumTests} which
 * only validates value mapping and round-trip conversions.
 */
public class Pcre2EnumFlagCombinationTests {

    // === Compile Option Combinations ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void caselessMatchesBothCases(IPcre2 api) {
        var code = new Pcre2Code(api, "hello", EnumSet.of(Pcre2CompileOption.CASELESS));
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("HELLO", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "CASELESS should match uppercase subject"
        );
        assertTrue(
                code.match("Hello", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "CASELESS should match mixed case subject"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void multilineCaretMatchesLineStarts(IPcre2 api) {
        var code = new Pcre2Code(api, "^line", EnumSet.of(Pcre2CompileOption.MULTILINE));
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("first\nline two", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "MULTILINE ^ should match at start of second line"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dotallDotMatchesNewline(IPcre2 api) {
        var code = new Pcre2Code(api, "a.b", EnumSet.of(Pcre2CompileOption.DOTALL));
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("a\nb", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "DOTALL should make . match newline"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dotallWithoutFlagDoesNotMatchNewline(IPcre2 api) {
        var code = new Pcre2Code(api, "a.b");
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("a\nb", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) < 0,
                "Without DOTALL, . should not match newline"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void caselessPlusDotall(IPcre2 api) {
        var code = new Pcre2Code(
                api, "hello.world",
                EnumSet.of(Pcre2CompileOption.CASELESS, Pcre2CompileOption.DOTALL)
        );
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("HELLO\nWORLD", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "CASELESS + DOTALL should match case-insensitively across newlines"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void caselessPlusMultiline(IPcre2 api) {
        var code = new Pcre2Code(
                api, "^hello$",
                EnumSet.of(Pcre2CompileOption.CASELESS, Pcre2CompileOption.MULTILINE)
        );
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("world\nHELLO\nfoo", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "CASELESS + MULTILINE should match case-insensitively at line boundaries"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void caselessPlusMultilinePlusDotall(IPcre2 api) {
        var code = new Pcre2Code(
                api, "^a.b$",
                EnumSet.of(
                        Pcre2CompileOption.CASELESS,
                        Pcre2CompileOption.MULTILINE,
                        Pcre2CompileOption.DOTALL
                )
        );
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("X\nA\nB\nY", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "CASELESS + MULTILINE + DOTALL should combine all three effects"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void extendedIgnoresWhitespaceAndComments(IPcre2 api) {
        var code = new Pcre2Code(
                api, "hello   world  # a comment",
                EnumSet.of(Pcre2CompileOption.EXTENDED)
        );
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("helloworld", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "EXTENDED should ignore whitespace and comments in the pattern"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void ungreedyInvertsQuantifiers(IPcre2 api) {
        var code = new Pcre2Code(api, "a+", EnumSet.of(Pcre2CompileOption.UNGREEDY));
        var matchData = new Pcre2MatchData(code);

        var result = code.match("aaa", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result >= 0, "UNGREEDY pattern should match");

        var ovector = matchData.ovector();
        // With UNGREEDY, a+ should match just one 'a' (minimal match)
        assertEquals(0, ovector[0], "Match should start at 0");
        assertEquals(1, ovector[1], "UNGREEDY a+ should match only one byte");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void literalTreatsPatternAsLiteral(IPcre2 api) {
        var code = new Pcre2Code(api, "a.b+c", EnumSet.of(Pcre2CompileOption.LITERAL));
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("a.b+c", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "LITERAL should treat regex metacharacters as literal characters"
        );
        assertTrue(
                code.match("aXbbc", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) < 0,
                "LITERAL should not interpret . and + as regex metacharacters"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void noAutoCaptureDisablesNumberedGroups(IPcre2 api) {
        var code = new Pcre2Code(
                api, "(a)(b)",
                EnumSet.of(Pcre2CompileOption.NO_AUTO_CAPTURE)
        );

        assertEquals(0, code.captureCount(),
                "NO_AUTO_CAPTURE should result in zero capturing groups");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dupnamesAllowsDuplicateGroupNames(IPcre2 api) {
        var code = new Pcre2Code(
                api, "(?<val>a)|(?<val>b)",
                EnumSet.of(Pcre2CompileOption.DUPNAMES)
        );
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("b", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "DUPNAMES should allow patterns with duplicate group names to compile and match"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoredOnlyMatchesAtStart(IPcre2 api) {
        var code = new Pcre2Code(api, "test", EnumSet.of(Pcre2CompileOption.ANCHORED));
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("test123", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "ANCHORED should match at the start"
        );
        assertTrue(
                code.match("123test", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) < 0,
                "ANCHORED should not match when pattern is not at the start"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void dollarEndOnlyDollarDoesNotMatchBeforeNewline(IPcre2 api) {
        var code = new Pcre2Code(
                api, "test$",
                EnumSet.of(Pcre2CompileOption.DOLLAR_ENDONLY)
        );
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("test", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "DOLLAR_ENDONLY should match at end of string"
        );
        assertTrue(
                code.match("test\n", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) < 0,
                "DOLLAR_ENDONLY should not match before trailing newline"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void firstlineOnlyMatchesInFirstLine(IPcre2 api) {
        var code = new Pcre2Code(api, "test", EnumSet.of(Pcre2CompileOption.FIRSTLINE));
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("test\nother", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "FIRSTLINE should match in first line"
        );
        assertTrue(
                code.match("other\ntest", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) < 0,
                "FIRSTLINE should not match in second line"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void argOptionsReflectsCombinedFlags(IPcre2 api) {
        var options = EnumSet.of(
                Pcre2CompileOption.CASELESS,
                Pcre2CompileOption.MULTILINE,
                Pcre2CompileOption.DOTALL
        );
        var code = new Pcre2Code(api, "test", options);
        var argOpts = code.argOptions();

        assertTrue(argOpts.contains(Pcre2CompileOption.CASELESS), "argOptions should contain CASELESS");
        assertTrue(argOpts.contains(Pcre2CompileOption.MULTILINE), "argOptions should contain MULTILINE");
        assertTrue(argOpts.contains(Pcre2CompileOption.DOTALL), "argOptions should contain DOTALL");
    }

    // === Match Option Combinations ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void notbolPreventsCaretFromMatchingStart(IPcre2 api) {
        var code = new Pcre2Code(api, "^test");
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("test", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "Without NOTBOL, ^ should match at start"
        );
        assertTrue(
                code.match("test", 0, EnumSet.of(Pcre2MatchOption.NOTBOL), matchData, null) < 0,
                "NOTBOL should prevent ^ from matching at start of subject"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void noteolPreventsDollarFromMatchingEnd(IPcre2 api) {
        var code = new Pcre2Code(api, "test$");
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("test", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "Without NOTEOL, $ should match at end"
        );
        assertTrue(
                code.match("test", 0, EnumSet.of(Pcre2MatchOption.NOTEOL), matchData, null) < 0,
                "NOTEOL should prevent $ from matching at end of subject"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void notbolPlusNoteolCombined(IPcre2 api) {
        var code = new Pcre2Code(api, "^test$");
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("test", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "Without NOTBOL/NOTEOL, ^test$ should match"
        );
        assertTrue(
                code.match("test", 0,
                        EnumSet.of(Pcre2MatchOption.NOTBOL, Pcre2MatchOption.NOTEOL),
                        matchData, null) < 0,
                "NOTBOL + NOTEOL should prevent both ^ and $ from matching"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void notemptyPreventsEmptyMatch(IPcre2 api) {
        var code = new Pcre2Code(api, "a*");
        var matchData = new Pcre2MatchData(code);

        // Without NOTEMPTY, a* matches empty string at position 0 of "bbb"
        assertTrue(
                code.match("bbb", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "Without NOTEMPTY, a* should match empty string"
        );

        // With NOTEMPTY, empty match is not valid
        assertTrue(
                code.match("bbb", 0, EnumSet.of(Pcre2MatchOption.NOTEMPTY), matchData, null) < 0,
                "NOTEMPTY should prevent empty string match"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoredMatchOptionOnlyMatchesAtStart(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("123test", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "Without ANCHORED match option, should find 'test' anywhere"
        );
        assertTrue(
                code.match("123test", 0, EnumSet.of(Pcre2MatchOption.ANCHORED), matchData, null) < 0,
                "ANCHORED match option should only match at the start position"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void partialSoftReturnsPartialMatch(IPcre2 api) {
        var code = new Pcre2Code(api, "abcdef");
        var matchData = new Pcre2MatchData(code);

        var result = code.match(
                "abc", 0,
                EnumSet.of(Pcre2MatchOption.PARTIAL_SOFT),
                matchData, null
        );

        assertEquals(IPcre2.ERROR_PARTIAL, result,
                "PARTIAL_SOFT should return ERROR_PARTIAL for a partial match");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void partialHardReturnsPartialEvenWithFullMatch(IPcre2 api) {
        // Pattern where "ab" is both a full match and a prefix of longer potential match
        var code = new Pcre2Code(api, "ab(cd)?");
        var matchData = new Pcre2MatchData(code);

        var result = code.match(
                "ab", 0,
                EnumSet.of(Pcre2MatchOption.PARTIAL_HARD),
                matchData, null
        );

        assertEquals(IPcre2.ERROR_PARTIAL, result,
                "PARTIAL_HARD should return ERROR_PARTIAL even when a full match exists");
    }

    // === Substitute Option Combinations ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void substituteGlobalReplacesAll(IPcre2 api) {
        var code = new Pcre2Code(api, "a");
        var result = code.substitute("aXaXa", 0,
                EnumSet.of(Pcre2SubstituteOption.GLOBAL), null, null, "b");
        assertEquals("bXbXb", result, "GLOBAL should replace all occurrences");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void substituteWithoutGlobalReplacesFirst(IPcre2 api) {
        var code = new Pcre2Code(api, "a");
        var result = code.substitute("aXaXa", 0, null, null, null, "b");
        assertEquals("bXaXa", result, "Without GLOBAL, only the first occurrence should be replaced");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void substituteLiteralTreatsReplacementAsLiteral(IPcre2 api) {
        var code = new Pcre2Code(api, "(test)");
        var result = code.substitute("test", 0,
                EnumSet.of(Pcre2SubstituteOption.LITERAL), null, null, "$1");
        assertEquals("$1", result,
                "LITERAL should treat $1 in replacement as literal text, not a backreference");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void substituteGlobalPlusLiteral(IPcre2 api) {
        var code = new Pcre2Code(api, "(x)");
        var result = code.substitute("xyx", 0,
                EnumSet.of(Pcre2SubstituteOption.GLOBAL, Pcre2SubstituteOption.LITERAL),
                null, null, "$1");
        assertEquals("$1y$1", result,
                "GLOBAL + LITERAL should replace all occurrences with literal replacement text");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void substituteReplacementOnlyReturnsOnlyReplacement(IPcre2 api) {
        var code = new Pcre2Code(api, "world");
        var result = code.substitute("hello world!", 0,
                EnumSet.of(Pcre2SubstituteOption.REPLACEMENT_ONLY), null, null, "earth");
        assertEquals("earth", result,
                "REPLACEMENT_ONLY should return only the replacement string, not the full subject");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void substituteUnsetEmptyInsertsEmptyForUnsetGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(?:(b)|(c))");
        var result = code.substitute("ac", 0,
                EnumSet.of(Pcre2SubstituteOption.UNSET_EMPTY), null, null, "[$1][$2][$3]");
        assertEquals("[a][][c]", result,
                "UNSET_EMPTY should insert empty string for unset group $2");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void substituteUnknownUnsetPlusUnsetEmpty(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)");
        var result = code.substitute("a", 0,
                EnumSet.of(
                        Pcre2SubstituteOption.UNKNOWN_UNSET,
                        Pcre2SubstituteOption.UNSET_EMPTY
                ),
                null, null, "[$1][$2]");
        assertEquals("[a][]", result,
                "UNKNOWN_UNSET + UNSET_EMPTY should treat unknown group references as unset (empty)");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void substituteGlobalPlusReplacementOnly(IPcre2 api) {
        var code = new Pcre2Code(api, "\\d+");
        var result = code.substitute("a1b2c3", 0,
                EnumSet.of(
                        Pcre2SubstituteOption.GLOBAL,
                        Pcre2SubstituteOption.REPLACEMENT_ONLY
                ),
                null, null, "N");
        assertEquals("NNN", result,
                "GLOBAL + REPLACEMENT_ONLY should return only the replacements for all matches");
    }

    // === Compile Extra Option Combinations (via CompileContext) ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void extraBadEscapeIsLiteralTreatsUnknownEscapesAsLiteral(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setCompileExtraOptions(EnumSet.of(Pcre2CompileExtraOption.BAD_ESCAPE_IS_LITERAL));

        // \j is not a recognized escape; with BAD_ESCAPE_IS_LITERAL it becomes literal 'j'
        var code = new Pcre2Code(api, "\\j", null, ctx);
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("j", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "BAD_ESCAPE_IS_LITERAL should treat \\j as literal 'j'"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void extraMatchWordMatchesWholeWords(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setCompileExtraOptions(EnumSet.of(Pcre2CompileExtraOption.MATCH_WORD));

        var code = new Pcre2Code(api, "test", null, ctx);
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("this is a test here", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "MATCH_WORD should match 'test' as a whole word"
        );
        assertTrue(
                code.match("testing", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) < 0,
                "MATCH_WORD should not match 'test' within 'testing'"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void extraMatchLineMatchesWholeLine(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setCompileExtraOptions(EnumSet.of(Pcre2CompileExtraOption.MATCH_LINE));

        var code = new Pcre2Code(api, "test", null, ctx);
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("test", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "MATCH_LINE should match when subject is exactly 'test'"
        );
        assertTrue(
                code.match("test line", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) < 0,
                "MATCH_LINE should not match when 'test' is not the whole line"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void extraMatchLineMatchesInMultilineSubject(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setCompileExtraOptions(EnumSet.of(Pcre2CompileExtraOption.MATCH_LINE));

        var code = new Pcre2Code(
                api, "test",
                EnumSet.of(Pcre2CompileOption.MULTILINE),
                ctx
        );
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("first\ntest\nlast", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "MATCH_LINE + MULTILINE should match 'test' as a standalone line"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void extraMatchWordPlusCaselessCompileOption(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setCompileExtraOptions(EnumSet.of(Pcre2CompileExtraOption.MATCH_WORD));

        var code = new Pcre2Code(
                api, "test",
                EnumSet.of(Pcre2CompileOption.CASELESS),
                ctx
        );
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("a TEST here", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "MATCH_WORD (extra) + CASELESS (compile) should match whole word case-insensitively"
        );
        assertTrue(
                code.match("TESTING", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) < 0,
                "MATCH_WORD (extra) + CASELESS (compile) should not match within a word"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void extraMultipleAsciiRestrictions(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setCompileExtraOptions(EnumSet.of(
                Pcre2CompileExtraOption.ASCII_BSD,
                Pcre2CompileExtraOption.ASCII_BSW
        ));

        var code = new Pcre2Code(
                api, "\\d\\w",
                EnumSet.of(Pcre2CompileOption.UCP),
                ctx
        );
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("1a", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "ASCII_BSD + ASCII_BSW with UCP should match ASCII digits and word characters"
        );
    }

    // === Cross-Layer Combinations (Compile + Match options together) ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void caselessCompileWithNotbolMatch(IPcre2 api) {
        var code = new Pcre2Code(api, "^hello", EnumSet.of(Pcre2CompileOption.CASELESS));
        var matchData = new Pcre2MatchData(code);

        assertTrue(
                code.match("HELLO", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "CASELESS should match at start without NOTBOL"
        );
        assertTrue(
                code.match("HELLO", 0, EnumSet.of(Pcre2MatchOption.NOTBOL), matchData, null) < 0,
                "CASELESS compile + NOTBOL match should still prevent ^ from matching"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void multilineCompileWithPartialSoftMatch(IPcre2 api) {
        var code = new Pcre2Code(
                api, "^hello world$",
                EnumSet.of(Pcre2CompileOption.MULTILINE)
        );
        var matchData = new Pcre2MatchData(code);

        var result = code.match(
                "hello wo", 0,
                EnumSet.of(Pcre2MatchOption.PARTIAL_SOFT),
                matchData, null
        );

        assertEquals(IPcre2.ERROR_PARTIAL, result,
                "MULTILINE compile + PARTIAL_SOFT match should return partial match for incomplete input");
    }

    // === Compile Option Reflection via argOptions() ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyOptionsReflected(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var argOpts = code.argOptions();

        assertFalse(argOpts.contains(Pcre2CompileOption.CASELESS), "Default should not have CASELESS");
        assertFalse(argOpts.contains(Pcre2CompileOption.MULTILINE), "Default should not have MULTILINE");
        assertFalse(argOpts.contains(Pcre2CompileOption.DOTALL), "Default should not have DOTALL");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void singleOptionReflected(IPcre2 api) {
        var code = new Pcre2Code(api, "test", EnumSet.of(Pcre2CompileOption.CASELESS));
        var argOpts = code.argOptions();

        assertTrue(argOpts.contains(Pcre2CompileOption.CASELESS), "argOptions should contain CASELESS");
        assertFalse(argOpts.contains(Pcre2CompileOption.MULTILINE), "argOptions should not contain MULTILINE");
    }
}
