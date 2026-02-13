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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for error handling paths in {@link Pcre2Code} and related classes.
 */
public class Pcre2CodeErrorHandlingTests {

    // --- Pcre2Code constructor null checks ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void constructorNullApiThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> new Pcre2Code(null, "test"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void constructorNullPatternThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> new Pcre2Code(api, null));
    }

    // --- Various invalid patterns ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unmatchedBracketThrows(IPcre2 api) {
        var error = assertThrows(Pcre2CompileException.class, () -> new Pcre2Code(api, "[abc"));
        assertNotNull(error.pattern());
        assertTrue(error.offset() >= 0);
        assertNotNull(error.message());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unmatchedParenthesisThrows(IPcre2 api) {
        assertThrows(Pcre2CompileException.class, () -> new Pcre2Code(api, "(abc"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void invalidQuantifierThrows(IPcre2 api) {
        assertThrows(Pcre2CompileException.class, () -> new Pcre2Code(api, "?"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void invalidEscapeThrows(IPcre2 api) {
        assertThrows(Pcre2CompileException.class, () -> new Pcre2Code(api, "\\"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void invalidRepetitionRangeThrows(IPcre2 api) {
        assertThrows(Pcre2CompileException.class, () -> new Pcre2Code(api, "a{5,3}"));
    }

    // --- Pcre2CompileException fields ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileErrorContainsPatternInfo(IPcre2 api) {
        var error = assertThrows(Pcre2CompileException.class, () -> new Pcre2Code(api, "(?P<>)"));
        assertNotNull(error.getMessage());
        assertNotNull(error.pattern());
        assertNotNull(error.message());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileErrorLongPatternRegion(IPcre2 api) {
        // Pattern with error far from the start to exercise getPatternRegion truncation
        // Use a long valid prefix followed by an invalid construct
        var longPattern = "abcdefghijklmnopqrstuvwxyz(((";
        var error = assertThrows(Pcre2CompileException.class, () -> new Pcre2Code(api, longPattern));
        assertNotNull(error.getMessage());
        assertTrue(error.getMessage().length() > 0);
    }

    // --- match() null/invalid parameter checks ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchNullSubjectThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var matchData = new Pcre2MatchData(code);
        assertThrows(IllegalArgumentException.class, () ->
                code.match(null, 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchNegativeStartOffsetThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var matchData = new Pcre2MatchData(code);
        assertThrows(IllegalArgumentException.class, () ->
                code.match("test", -1, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchNullMatchDataThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalArgumentException.class, () ->
                code.match("test", 0, EnumSet.noneOf(Pcre2MatchOption.class), null, null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchNoMatchReturnsNegative(IPcre2 api) {
        var code = new Pcre2Code(api, "xyz");
        var matchData = new Pcre2MatchData(code);
        var result = code.match("abc", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result < 0, "Non-matching should return negative (ERROR_NOMATCH)");
    }

    // --- substitute() null/invalid parameter checks ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void substituteNullSubjectThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalArgumentException.class, () ->
                code.substitute(null, 0, null, null, null, "replacement"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void substituteNegativeStartOffsetThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalArgumentException.class, () ->
                code.substitute("test", -1, null, null, null, "replacement"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void substituteStartOffsetPastEndThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalArgumentException.class, () ->
                code.substitute("abc", 4, null, null, null, "replacement"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void substituteNullReplacementThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalArgumentException.class, () ->
                code.substitute("test", 0, null, null, null, null));
    }

    // --- Pcre2MatchData constructor null checks ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchDataNullApiThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> new Pcre2MatchData(null, 10));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchDataNullCodeThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> new Pcre2MatchData(null));
    }

    // --- Context null checks ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextNullApiThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> new Pcre2CompileContext(null, null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextNullApiThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> new Pcre2MatchContext(null, null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void generalContextNullApiThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> new Pcre2GeneralContext(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitStackNullApiThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () ->
                new Pcre2JitStack(null, 32 * 1024, 512 * 1024, null));
    }

    // --- JitCode error handling ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitCodeBadPatternThrows(IPcre2 api) {
        assertThrows(Pcre2CompileException.class, () ->
                new Pcre2JitCode(api, "?", null, null, null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitCodeMatchNullSubjectThrows(IPcre2 api) {
        var code = new Pcre2JitCode(api, "test", null, null, null);
        var matchData = new Pcre2MatchData(code);
        assertThrows(IllegalArgumentException.class, () ->
                code.match(null, 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitCodeMatchNegativeOffsetThrows(IPcre2 api) {
        var code = new Pcre2JitCode(api, "test", null, null, null);
        var matchData = new Pcre2MatchData(code);
        assertThrows(IllegalArgumentException.class, () ->
                code.match("test", -1, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitCodeMatchOffsetPastEndThrows(IPcre2 api) {
        var code = new Pcre2JitCode(api, "test", null, null, null);
        var matchData = new Pcre2MatchData(code);
        assertThrows(IllegalArgumentException.class, () ->
                code.match("ab", 3, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitCodeMatchNullMatchDataThrows(IPcre2 api) {
        var code = new Pcre2JitCode(api, "test", null, null, null);
        assertThrows(IllegalArgumentException.class, () ->
                code.match("test", 0, EnumSet.noneOf(Pcre2MatchOption.class), null, null));
    }
}
