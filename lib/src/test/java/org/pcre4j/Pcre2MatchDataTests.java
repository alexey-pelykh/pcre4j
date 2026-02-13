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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link Pcre2MatchData} operations.
 */
public class Pcre2MatchDataTests {

    // --- api() and handle() ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void apiReturnsNonNull(IPcre2 api) {
        var matchData = new Pcre2MatchData(api, 10);
        assertNotNull(matchData.api());
        assertEquals(api, matchData.api());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void handleReturnsNonZero(IPcre2 api) {
        var matchData = new Pcre2MatchData(api, 10);
        assertTrue(matchData.handle() != 0);
    }

    // --- ovectorCount ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void ovectorCountAfterMatch(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)(c)");
        var matchData = new Pcre2MatchData(code);
        code.match("abc", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertEquals(4, matchData.ovectorCount()); // full match + 3 groups
    }

    // --- size ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void sizePositive(IPcre2 api) {
        var matchData = new Pcre2MatchData(api, 10);
        assertTrue(matchData.size() > 0);
    }

    // --- ovector ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void ovectorReturnsByteOffsets(IPcre2 api) {
        var code = new Pcre2Code(api, "(ab)");
        var matchData = new Pcre2MatchData(code);
        code.match("xaby", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        var ovector = matchData.ovector();
        assertEquals(4, ovector.length); // 2 pairs: full match + group 1
        assertEquals(1, ovector[0]); // full match start byte
        assertEquals(3, ovector[1]); // full match end byte
        assertEquals(1, ovector[2]); // group 1 start byte
        assertEquals(3, ovector[3]); // group 1 end byte
    }

    // --- getSubstring(int) ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringByNumber(IPcre2 api) {
        var code = new Pcre2Code(api, "(hello) (world)");
        var matchData = new Pcre2MatchData(code);
        code.match("hello world", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);

        var fullMatch = new String(matchData.getSubstring(0), StandardCharsets.UTF_8);
        assertEquals("hello world", fullMatch);

        var group1 = new String(matchData.getSubstring(1), StandardCharsets.UTF_8);
        assertEquals("hello", group1);

        var group2 = new String(matchData.getSubstring(2), StandardCharsets.UTF_8);
        assertEquals("world", group2);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringNegativeNumberThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalArgumentException.class, () -> matchData.getSubstring(-1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringOutOfBoundsThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IndexOutOfBoundsException.class, () -> matchData.getSubstring(5));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringUnsetGroupThrows(IPcre2 api) {
        // (a)|(b) - when "a" matches, group 2 is unset
        var code = new Pcre2Code(api, "(a)|(b)");
        var matchData = new Pcre2MatchData(code);
        code.match("a", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalStateException.class, () -> matchData.getSubstring(2));
    }

    // --- getSubstring(String) ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringByName(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<greeting>hello) (?<target>world)");
        var matchData = new Pcre2MatchData(code);
        code.match("hello world", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);

        var greeting = new String(matchData.getSubstring("greeting"), StandardCharsets.UTF_8);
        assertEquals("hello", greeting);

        var target = new String(matchData.getSubstring("target"), StandardCharsets.UTF_8);
        assertEquals("world", target);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringByNameNullThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalArgumentException.class, () -> matchData.getSubstring((String) null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringByNameNonexistentThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IndexOutOfBoundsException.class, () -> matchData.getSubstring("nonexistent"));
    }

    // --- copySubstring(int, ByteBuffer) ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringByNumber(IPcre2 api) {
        var code = new Pcre2Code(api, "(hello)");
        var matchData = new Pcre2MatchData(code);
        code.match("hello", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);

        var buffer = ByteBuffer.allocateDirect(256);
        var written = matchData.copySubstring(1, buffer);
        assertEquals(5, written);

        var result = new byte[written];
        buffer.get(result);
        assertEquals("hello", new String(result, StandardCharsets.UTF_8));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringNegativeNumberThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalArgumentException.class, () ->
                matchData.copySubstring(-1, ByteBuffer.allocateDirect(10)));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringNullBufferThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalArgumentException.class, () -> matchData.copySubstring(0, null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringNonDirectBufferThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalArgumentException.class, () ->
                matchData.copySubstring(0, ByteBuffer.allocate(10)));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringOutOfBoundsThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IndexOutOfBoundsException.class, () ->
                matchData.copySubstring(5, ByteBuffer.allocateDirect(10)));
    }

    // --- copySubstring(String, ByteBuffer) ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringByName(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<word>hello)");
        var matchData = new Pcre2MatchData(code);
        code.match("hello", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);

        var buffer = ByteBuffer.allocateDirect(256);
        var written = matchData.copySubstring("word", buffer);
        assertEquals(5, written);

        var result = new byte[written];
        buffer.get(result);
        assertEquals("hello", new String(result, StandardCharsets.UTF_8));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringByNameNullNameThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalArgumentException.class, () ->
                matchData.copySubstring((String) null, ByteBuffer.allocateDirect(10)));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringByNameNullBufferThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalArgumentException.class, () -> matchData.copySubstring("name", null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringByNameNonDirectBufferThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalArgumentException.class, () ->
                matchData.copySubstring("name", ByteBuffer.allocate(10)));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringByNameNonexistentThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IndexOutOfBoundsException.class, () ->
                matchData.copySubstring("nonexistent", ByteBuffer.allocateDirect(10)));
    }

    // --- getSubstringLength(int) ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringLengthByNumber(IPcre2 api) {
        var code = new Pcre2Code(api, "(hello)");
        var matchData = new Pcre2MatchData(code);
        code.match("hello", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertEquals(5, matchData.getSubstringLength(0));
        assertEquals(5, matchData.getSubstringLength(1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringLengthNegativeThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalArgumentException.class, () -> matchData.getSubstringLength(-1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringLengthOutOfBoundsThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IndexOutOfBoundsException.class, () -> matchData.getSubstringLength(5));
    }

    // --- getSubstringLength(String) ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringLengthByName(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<word>hello)");
        var matchData = new Pcre2MatchData(code);
        code.match("hello", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertEquals(5, matchData.getSubstringLength("word"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringLengthByNameNullThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalArgumentException.class, () -> matchData.getSubstringLength((String) null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringLengthByNameNonexistentThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>test)");
        var matchData = new Pcre2MatchData(code);
        code.match("test", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IndexOutOfBoundsException.class, () -> matchData.getSubstringLength("nonexistent"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringByNameUnsetGroupThrows(IPcre2 api) {
        // (?<first>a)|(?<second>b) - when "a" matches, named group "second" is unset
        var code = new Pcre2Code(api, "(?<first>a)|(?<second>b)");
        var matchData = new Pcre2MatchData(code);
        code.match("a", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalStateException.class, () -> matchData.getSubstring("second"));
    }

    // --- copySubstring(int, ByteBuffer) error paths ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringByNumberUnsetGroupThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)|(b)");
        var matchData = new Pcre2MatchData(code);
        code.match("a", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalStateException.class, () ->
                matchData.copySubstring(2, ByteBuffer.allocateDirect(10)));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringByNumberUndersizedBufferThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(hello)");
        var matchData = new Pcre2MatchData(code);
        code.match("hello", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        // "hello" is 5 bytes + null terminator = 6 bytes needed, provide only 2
        assertThrows(IllegalStateException.class, () ->
                matchData.copySubstring(1, ByteBuffer.allocateDirect(2)));
    }

    // --- copySubstring(String, ByteBuffer) error paths ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringByNameUnsetGroupThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>a)|(?<second>b)");
        var matchData = new Pcre2MatchData(code);
        code.match("a", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalStateException.class, () ->
                matchData.copySubstring("second", ByteBuffer.allocateDirect(10)));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void copySubstringByNameUndersizedBufferThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<word>hello)");
        var matchData = new Pcre2MatchData(code);
        code.match("hello", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        // "hello" is 5 bytes + null terminator = 6 bytes needed, provide only 2
        assertThrows(IllegalStateException.class, () ->
                matchData.copySubstring("word", ByteBuffer.allocateDirect(2)));
    }

    // --- getSubstringLength error paths ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringLengthByNumberUnsetGroupThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)|(b)");
        var matchData = new Pcre2MatchData(code);
        code.match("a", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalStateException.class, () -> matchData.getSubstringLength(2));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void getSubstringLengthByNameUnsetGroupThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>a)|(?<second>b)");
        var matchData = new Pcre2MatchData(code);
        code.match("a", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertThrows(IllegalStateException.class, () -> matchData.getSubstringLength("second"));
    }

    // --- Constructor variants ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void constructorWithExplicitOvecsize(IPcre2 api) {
        var matchData = new Pcre2MatchData(api, 5);
        assertNotNull(matchData);
        assertTrue(matchData.handle() != 0);
        assertTrue(matchData.size() > 0);
        assertEquals(api, matchData.api());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void constructorNullApiThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> new Pcre2MatchData(null, 10));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void constructorNullCodeThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> new Pcre2MatchData((Pcre2Code) null));
    }

    // --- MatchData from pattern ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchDataFromPatternWorksCorrectly(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)(c)");
        var matchData = new Pcre2MatchData(code);
        assertNotNull(matchData);
        assertTrue(matchData.handle() != 0);

        // Should be usable for matching
        var result = code.match("abc", 0, EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT), matchData, null);
        assertTrue(result > 0);
    }
}
