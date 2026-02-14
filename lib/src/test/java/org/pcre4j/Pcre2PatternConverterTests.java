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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Pcre2PatternConverterTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void fromGlobSimple(IPcre2 api) {
        var result = Pcre2PatternConverter.fromGlob(api, "*.txt",
                EnumSet.noneOf(Pcre2ConvertOption.class), null);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Verify the converted pattern can be compiled and matches correctly
        var code = new Pcre2Code(api, result);
        var matchData = new Pcre2MatchData(code);

        assertTrue(code.match("file.txt", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "Glob *.txt should match file.txt");
        assertTrue(code.match("report.txt", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "Glob *.txt should match report.txt");
        assertTrue(code.match(".txt", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "Glob *.txt should match .txt");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void fromGlobNoMatch(IPcre2 api) {
        var result = Pcre2PatternConverter.fromGlob(api, "*.txt",
                EnumSet.noneOf(Pcre2ConvertOption.class), null);
        var code = new Pcre2Code(api, result);
        var matchData = new Pcre2MatchData(code);

        assertTrue(code.match("file.pdf", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) < 0,
                "Glob *.txt should not match file.pdf");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void fromGlobQuestionMark(IPcre2 api) {
        var result = Pcre2PatternConverter.fromGlob(api, "file?.log",
                EnumSet.noneOf(Pcre2ConvertOption.class), null);
        var code = new Pcre2Code(api, result);
        var matchData = new Pcre2MatchData(code);

        assertTrue(code.match("file1.log", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "Glob file?.log should match file1.log");
        assertTrue(code.match("fileX.log", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "Glob file?.log should match fileX.log");
        assertTrue(code.match("file.log", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) < 0,
                "Glob file?.log should not match file.log (? requires exactly one char)");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void fromGlobWithConvertContext(IPcre2 api) {
        var convertContext = new Pcre2ConvertContext(api, null);
        convertContext.setGlobSeparator('/');

        var result = Pcre2PatternConverter.fromGlob(api, "*.txt",
                EnumSet.noneOf(Pcre2ConvertOption.class), convertContext);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        var code = new Pcre2Code(api, result);
        var matchData = new Pcre2MatchData(code);

        assertTrue(code.match("file.txt", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "Glob *.txt with context should match file.txt");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void fromPosixBre(IPcre2 api) {
        // POSIX BRE: brackets require escaping for grouping
        var result = Pcre2PatternConverter.fromPosixBre(api, "^[0-9]\\{3\\}$");
        assertNotNull(result);
        assertFalse(result.isEmpty());

        var code = new Pcre2Code(api, result);
        var matchData = new Pcre2MatchData(code);

        assertTrue(code.match("123", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "POSIX BRE ^[0-9]\\{3\\}$ should match 123");
        assertTrue(code.match("1234", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) < 0,
                "POSIX BRE ^[0-9]\\{3\\}$ should not match 1234");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void fromPosixEre(IPcre2 api) {
        var result = Pcre2PatternConverter.fromPosixEre(api, "^[a-z]+$");
        assertNotNull(result);
        assertFalse(result.isEmpty());

        var code = new Pcre2Code(api, result);
        var matchData = new Pcre2MatchData(code);

        assertTrue(code.match("hello", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "POSIX ERE ^[a-z]+$ should match hello");
        assertTrue(code.match("Hello", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) < 0,
                "POSIX ERE ^[a-z]+$ should not match Hello");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void fromPosixEreWithCharacterClass(IPcre2 api) {
        var result = Pcre2PatternConverter.fromPosixEre(api, "^[[:alpha:]]+$");
        assertNotNull(result);
        assertFalse(result.isEmpty());

        var code = new Pcre2Code(api, result);
        var matchData = new Pcre2MatchData(code);

        assertTrue(code.match("Hello", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "POSIX ERE ^[[:alpha:]]+$ should match Hello");
        assertTrue(code.match("123", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) < 0,
                "POSIX ERE ^[[:alpha:]]+$ should not match 123");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void convertWithEnumSetOptions(IPcre2 api) {
        var result = Pcre2PatternConverter.convert(
                api,
                "*.java",
                EnumSet.of(Pcre2ConvertOption.GLOB),
                null
        );
        assertNotNull(result);
        assertFalse(result.isEmpty());

        var code = new Pcre2Code(api, result);
        var matchData = new Pcre2MatchData(code);

        assertTrue(code.match("Main.java", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null) >= 0,
                "Converted glob *.java should match Main.java");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void fromGlobNullPatternThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () ->
                Pcre2PatternConverter.fromGlob(api, null, EnumSet.noneOf(Pcre2ConvertOption.class), null)
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void fromPosixBreNullPatternThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () ->
                Pcre2PatternConverter.fromPosixBre(api, null)
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void fromPosixEreNullPatternThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () ->
                Pcre2PatternConverter.fromPosixEre(api, null)
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void convertNullApiThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () ->
                Pcre2PatternConverter.fromGlob(null, "*.txt", EnumSet.noneOf(Pcre2ConvertOption.class), null)
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void convertEmptyOptionsThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () ->
                Pcre2PatternConverter.convert(api, "*.txt", EnumSet.noneOf(Pcre2ConvertOption.class), null)
        );
    }
}
