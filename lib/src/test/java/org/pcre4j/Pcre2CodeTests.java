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

import java.util.stream.Stream;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Pcre2CodeTests {

    private static final IPcre2 JNA_PCRE2 = new org.pcre4j.jna.Pcre2();
    private static final IPcre2 FFM_PCRE2 = new org.pcre4j.ffm.Pcre2();

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(JNA_PCRE2),
                Arguments.of(FFM_PCRE2)
        );
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void badPattern(IPcre2 api) {
        assertThrows(Pcre2CompileError.class, () -> {
            new Pcre2Code(api, "?");
        });
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void emptyStringMatch(IPcre2 api) {
        var code = new Pcre2Code(api, "^$");
        var matchData = new Pcre2MatchData(code);
        var result = code.match("", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result >= 0, "Empty string should match pattern ^$");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void emptyStringMatchJit(IPcre2 api) {
        var code = new Pcre2JitCode(api, "^$", null, null, null);
        var matchData = new Pcre2MatchData(code);
        var result = code.match("", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result >= 0, "Empty string should match pattern ^$ with JIT");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchAtEndOfString(IPcre2 api) {
        // Pattern $ matches at end of string, startOffset at length should work
        var code = new Pcre2Code(api, "$");
        var matchData = new Pcre2MatchData(code);
        var subject = "abc";
        var result = code.match(subject, subject.length(), EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result >= 0, "Pattern $ should match at end of string");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchAtEndOfStringJit(IPcre2 api) {
        // Pattern $ matches at end of string, startOffset at length should work with JIT
        var code = new Pcre2JitCode(api, "$", null, null, null);
        var matchData = new Pcre2MatchData(code);
        var subject = "abc";
        var result = code.match(subject, subject.length(), EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result >= 0, "Pattern $ should match at end of string with JIT");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void startOffsetPastEndThrows(IPcre2 api) {
        var code = new Pcre2Code(api, ".");
        var matchData = new Pcre2MatchData(code);
        assertThrows(IllegalArgumentException.class, () -> {
            code.match("abc", 4, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        });
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void startOffsetPastEndThrowsJit(IPcre2 api) {
        var code = new Pcre2JitCode(api, ".", null, null, null);
        var matchData = new Pcre2MatchData(code);
        assertThrows(IllegalArgumentException.class, () -> {
            code.match("abc", 4, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        });
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchLimitThrowsWhenUnset(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalStateException.class, code::matchLimit);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void depthLimitThrowsWhenUnset(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalStateException.class, code::depthLimit);
    }

}
