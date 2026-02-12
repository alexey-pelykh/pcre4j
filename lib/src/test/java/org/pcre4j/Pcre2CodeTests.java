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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Pcre2CodeTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void badPattern(IPcre2 api) {
        assertThrows(Pcre2CompileError.class, () -> {
            new Pcre2Code(api, "?");
        });
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyStringMatch(IPcre2 api) {
        var code = new Pcre2Code(api, "^$");
        var matchData = new Pcre2MatchData(code);
        var result = code.match("", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result >= 0, "Empty string should match pattern ^$");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyStringMatchJit(IPcre2 api) {
        var code = new Pcre2JitCode(api, "^$", null, null, null);
        var matchData = new Pcre2MatchData(code);
        var result = code.match("", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result >= 0, "Empty string should match pattern ^$ with JIT");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchAtEndOfString(IPcre2 api) {
        // Pattern $ matches at end of string, startOffset at length should work
        var code = new Pcre2Code(api, "$");
        var matchData = new Pcre2MatchData(code);
        var subject = "abc";
        var result = code.match(subject, subject.length(), EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result >= 0, "Pattern $ should match at end of string");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchAtEndOfStringJit(IPcre2 api) {
        // Pattern $ matches at end of string, startOffset at length should work with JIT
        var code = new Pcre2JitCode(api, "$", null, null, null);
        var matchData = new Pcre2MatchData(code);
        var subject = "abc";
        var result = code.match(subject, subject.length(), EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result >= 0, "Pattern $ should match at end of string with JIT");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void startOffsetPastEndThrows(IPcre2 api) {
        var code = new Pcre2Code(api, ".");
        var matchData = new Pcre2MatchData(code);
        assertThrows(IllegalArgumentException.class, () -> {
            code.match("abc", 4, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        });
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void startOffsetPastEndThrowsJit(IPcre2 api) {
        var code = new Pcre2JitCode(api, ".", null, null, null);
        var matchData = new Pcre2MatchData(code);
        assertThrows(IllegalArgumentException.class, () -> {
            code.match("abc", 4, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        });
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchLimitThrowsWhenUnset(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalStateException.class, code::matchLimit);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void depthLimitThrowsWhenUnset(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalStateException.class, code::depthLimit);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void heapLimitThrowsWhenUnset(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertThrows(IllegalStateException.class, code::heapLimit);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void firstCodeTypeReturnsValidValue(IPcre2 api) {
        // Test that firstCodeType returns a valid value (0, 1, or 2)
        var code = new Pcre2Code(api, "test");
        var result = code.firstCodeType();
        assertTrue(result >= 0 && result <= 2,
                "firstCodeType should return 0, 1, or 2, but got " + result);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void firstCodeTypeForLiteralPattern(IPcre2 api) {
        // Pattern starting with a literal character should return 1 (first code unit is set)
        var code = new Pcre2Code(api, "test");
        assertEquals(1, code.firstCodeType(),
                "Pattern starting with literal should return 1 (first code unit set)");
    }

}
