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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link Pcre2JitCode}.
 */
public class Pcre2JitCodeTests {

    private static IPcre2 loadBackend(String className) {
        try {
            return (IPcre2) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Backend " + className + " not found on classpath", e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException
                 | NoSuchMethodException e) {
            throw new RuntimeException("Failed to instantiate backend " + className, e);
        }
    }

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(loadBackend("org.pcre4j.jna.Pcre2")),
                Arguments.of(loadBackend("org.pcre4j.ffm.Pcre2"))
        );
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void getSupportedMatchOptions(IPcre2 api) {
        var options = Pcre2JitCode.getSupportedMatchOptions();
        assertNotNull(options);
        assertEquals(6, options.size());
        assertTrue(options.contains(Pcre2MatchOption.NOTBOL));
        assertTrue(options.contains(Pcre2MatchOption.NOTEOL));
        assertTrue(options.contains(Pcre2MatchOption.NOTEMPTY));
        assertTrue(options.contains(Pcre2MatchOption.NOTEMPTY_ATSTART));
        assertTrue(options.contains(Pcre2MatchOption.PARTIAL_HARD));
        assertTrue(options.contains(Pcre2MatchOption.PARTIAL_SOFT));
        // COPY_MATCHED_SUBJECT should NOT be in supported options
        assertFalse(options.contains(Pcre2MatchOption.COPY_MATCHED_SUBJECT));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void jitMatchSuccess(IPcre2 api) {
        var code = new Pcre2JitCode(api, "(hello) (world)", null, null, null);
        var matchData = new Pcre2MatchData(code);
        var result = code.match("hello world", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result > 0, "JIT match should succeed");
        assertEquals(3, matchData.ovectorCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void jitMatchNoMatch(IPcre2 api) {
        var code = new Pcre2JitCode(api, "xyz", null, null, null);
        var matchData = new Pcre2MatchData(code);
        var result = code.match("abc", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result < 0, "JIT match should return negative for no match");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void jitMatchWithStartOffset(IPcre2 api) {
        var code = new Pcre2JitCode(api, "o", null, null, null);
        var matchData = new Pcre2MatchData(code);

        // Match at offset 0 should find "o" at position 1
        var result = code.match("foo", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result > 0);

        // Match starting at offset 2 should find "o" at position 2
        result = code.match("foo", 2, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result > 0);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void jitMatchWithMatchContext(IPcre2 api) {
        var matchCtx = new Pcre2MatchContext(api, null);
        var jitStack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, null);
        matchCtx.assignJitStack(jitStack);

        var code = new Pcre2JitCode(api, "(test)", null, null, null);
        var matchData = new Pcre2MatchData(code);
        var result = code.match("test", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, matchCtx);
        assertTrue(result > 0, "JIT match with match context should succeed");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void jitCodeWithSpecificJitOptions(IPcre2 api) {
        // Compile with only COMPLETE (not PARTIAL_SOFT and PARTIAL_HARD)
        var code = new Pcre2JitCode(api, "test", null,
                EnumSet.of(Pcre2JitOption.COMPLETE), null);
        var matchData = new Pcre2MatchData(code);
        var result = code.match("test", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result > 0, "JIT match with specific JIT options should succeed");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void jitCodeWithCompileOptions(IPcre2 api) {
        var code = new Pcre2JitCode(api, "test",
                EnumSet.of(Pcre2CompileOption.CASELESS), null, null);
        var matchData = new Pcre2MatchData(code);
        var result = code.match("TEST", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result > 0, "JIT match with CASELESS option should match TEST");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void jitCodeWithCompileContext(IPcre2 api) {
        var compileCtx = new Pcre2CompileContext(api, null);
        compileCtx.setNewline(Pcre2Newline.LF);

        var code = new Pcre2JitCode(api, "^test$", null, null, compileCtx);
        var matchData = new Pcre2MatchData(code);
        var result = code.match("test", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result > 0, "JIT match with compile context should succeed");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void jitCodeWithMatchOptions(IPcre2 api) {
        var code = new Pcre2JitCode(api, "^test", null, null, null);
        var matchData = new Pcre2MatchData(code);

        // NOTBOL should prevent ^ from matching at start
        var result = code.match("test", 0,
                EnumSet.of(Pcre2MatchOption.NOTBOL), matchData, null);
        assertTrue(result < 0, "NOTBOL should prevent ^ from matching");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void jitCodePartialMatch(IPcre2 api) {
        var code = new Pcre2JitCode(api, "testing", null, null, null);
        var matchData = new Pcre2MatchData(code);

        var result = code.match("test", 0,
                EnumSet.of(Pcre2MatchOption.PARTIAL_SOFT), matchData, null);
        // Partial match returns ERROR_PARTIAL (negative)
        assertEquals(IPcre2.ERROR_PARTIAL, result, "Should get partial match");
    }
}
