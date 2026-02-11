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
import org.pcre4j.Pcre2CompileOption;
import org.pcre4j.Pcre2MatchData;
import org.pcre4j.Pcre2MatchOption;
import org.pcre4j.Pcre2NoSubstringError;
import org.pcre4j.Pcre2NoUniqueSubstringError;
import org.pcre4j.api.IPcre2;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Contract tests for PCRE2 substring and capture group operations.
 *
 * @param <T> the PCRE2 API implementation type
 */
public interface Pcre2SubstringContractTest<T extends IPcre2> {

    /**
     * Returns the PCRE2 API implementation to test.
     *
     * @return the PCRE2 API implementation
     */
    T getApi();

    @Test
    default void getSubstringEntireMatch() {
        final var code = new Pcre2Code(
                getApi(),
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(1, result);

        final var substring = matchData.getSubstring(0);
        assertEquals("hello", new String(substring, StandardCharsets.UTF_8));
    }

    @Test
    default void getSubstringCapturingGroup() {
        final var code = new Pcre2Code(
                getApi(),
                "(\\w+) (\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(3, result);

        final var group0 = matchData.getSubstring(0);
        assertEquals("hello world", new String(group0, StandardCharsets.UTF_8));

        final var group1 = matchData.getSubstring(1);
        assertEquals("hello", new String(group1, StandardCharsets.UTF_8));

        final var group2 = matchData.getSubstring(2);
        assertEquals("world", new String(group2, StandardCharsets.UTF_8));
    }

    @Test
    default void getSubstringUnicode() {
        final var code = new Pcre2Code(
                getApi(),
                "(🌐+)",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello 🌐🌐🌐 world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        final var group0 = matchData.getSubstring(0);
        assertEquals("🌐🌐🌐", new String(group0, StandardCharsets.UTF_8));

        final var group1 = matchData.getSubstring(1);
        assertEquals("🌐🌐🌐", new String(group1, StandardCharsets.UTF_8));
    }

    @Test
    default void getSubstringNegativeNumber() {
        final var code = new Pcre2Code(
                getApi(),
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(1, result);

        assertThrows(IllegalArgumentException.class, () -> matchData.getSubstring(-1));
    }

    @Test
    default void getSubstringInvalidGroupNumber() {
        final var code = new Pcre2Code(
                getApi(),
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(1, result);

        assertThrows(IndexOutOfBoundsException.class, () -> matchData.getSubstring(99));
    }

    @Test
    default void getSubstringUnsetGroup() {
        final var code = new Pcre2Code(
                getApi(),
                "(a)|(b)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "a",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        final var group1 = matchData.getSubstring(1);
        assertEquals("a", new String(group1, StandardCharsets.UTF_8));

        // Group 2 did not participate in the match
        assertThrows(IllegalStateException.class, () -> matchData.getSubstring(2));
    }

    @Test
    default void getSubstringByNameSimple() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        final var substring = matchData.getSubstring("word");
        assertEquals("hello", new String(substring, StandardCharsets.UTF_8));
    }

    @Test
    default void getSubstringByNameMultipleGroups() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<first>\\w+) (?<second>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(3, result);

        final var first = matchData.getSubstring("first");
        assertEquals("hello", new String(first, StandardCharsets.UTF_8));

        final var second = matchData.getSubstring("second");
        assertEquals("world", new String(second, StandardCharsets.UTF_8));
    }

    @Test
    default void getSubstringByNameUnicode() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<emoji>🌐+)",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello 🌐🌐🌐 world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        final var emoji = matchData.getSubstring("emoji");
        assertEquals("🌐🌐🌐", new String(emoji, StandardCharsets.UTF_8));
    }

    @Test
    default void getSubstringByNameNull() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        assertThrows(IllegalArgumentException.class, () -> matchData.getSubstring((String) null));
    }

    @Test
    default void getSubstringByNameInvalid() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        assertThrows(IndexOutOfBoundsException.class, () -> matchData.getSubstring("nonexistent"));
    }

    @Test
    default void getSubstringByNameUnsetGroup() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<first>a)|(?<second>b)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "a",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        final var first = matchData.getSubstring("first");
        assertEquals("a", new String(first, StandardCharsets.UTF_8));

        // Group "second" did not participate in the match
        assertThrows(IllegalStateException.class, () -> matchData.getSubstring("second"));
    }

    @Test
    default void getSubstringByNameDuplicateNames() {
        // DUPNAMES option allows duplicate named groups
        final var code = new Pcre2Code(
                getApi(),
                "(?<num>\\d+)|(?<num>\\w+)",
                EnumSet.of(Pcre2CompileOption.DUPNAMES),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // Match letters (second alternative)
        // Pattern has group 0 (entire match), group 1 (first num), group 2 (second num)
        final var result = code.match(
                "hello",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(3, result);

        final var num = matchData.getSubstring("num");
        assertEquals("hello", new String(num, StandardCharsets.UTF_8));
    }

    @Test
    default void copySubstringEntireMatch() {
        final var code = new Pcre2Code(
                getApi(),
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(1, result);

        final var buffer = ByteBuffer.allocateDirect(100);
        final var length = matchData.copySubstring(0, buffer);
        assertEquals(5, length);

        final var bytes = new byte[length];
        buffer.get(bytes);
        assertEquals("hello", new String(bytes, StandardCharsets.UTF_8));
    }

    @Test
    default void copySubstringCapturingGroups() {
        final var code = new Pcre2Code(
                getApi(),
                "(\\w+) (\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(3, result);

        // Group 0 - entire match
        final var buffer0 = ByteBuffer.allocateDirect(100);
        final var length0 = matchData.copySubstring(0, buffer0);
        assertEquals(11, length0);
        final var bytes0 = new byte[length0];
        buffer0.get(bytes0);
        assertEquals("hello world", new String(bytes0, StandardCharsets.UTF_8));

        // Group 1
        final var buffer1 = ByteBuffer.allocateDirect(100);
        final var length1 = matchData.copySubstring(1, buffer1);
        assertEquals(5, length1);
        final var bytes1 = new byte[length1];
        buffer1.get(bytes1);
        assertEquals("hello", new String(bytes1, StandardCharsets.UTF_8));

        // Group 2
        final var buffer2 = ByteBuffer.allocateDirect(100);
        final var length2 = matchData.copySubstring(2, buffer2);
        assertEquals(5, length2);
        final var bytes2 = new byte[length2];
        buffer2.get(bytes2);
        assertEquals("world", new String(bytes2, StandardCharsets.UTF_8));
    }

    @Test
    default void copySubstringUnicode() {
        final var code = new Pcre2Code(
                getApi(),
                "(🌐+)",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello 🌐🌐🌐 world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        final var buffer = ByteBuffer.allocateDirect(100);
        final var length = matchData.copySubstring(1, buffer);
        // Each 🌐 is 4 bytes in UTF-8
        assertEquals(12, length);

        final var bytes = new byte[length];
        buffer.get(bytes);
        assertEquals("🌐🌐🌐", new String(bytes, StandardCharsets.UTF_8));
    }

    @Test
    default void copySubstringNegativeNumber() {
        final var code = new Pcre2Code(
                getApi(),
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(1, result);

        final var buffer = ByteBuffer.allocateDirect(100);
        assertThrows(IllegalArgumentException.class, () -> matchData.copySubstring(-1, buffer));
    }

    @Test
    default void copySubstringNullBuffer() {
        final var code = new Pcre2Code(
                getApi(),
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(1, result);

        assertThrows(IllegalArgumentException.class, () -> matchData.copySubstring(0, null));
    }

    @Test
    default void copySubstringNonDirectBuffer() {
        final var code = new Pcre2Code(
                getApi(),
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(1, result);

        final var buffer = ByteBuffer.allocate(100); // Not direct
        assertThrows(IllegalArgumentException.class, () -> matchData.copySubstring(0, buffer));
    }

    @Test
    default void copySubstringInvalidGroupNumber() {
        final var code = new Pcre2Code(
                getApi(),
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(1, result);

        final var buffer = ByteBuffer.allocateDirect(100);
        assertThrows(IndexOutOfBoundsException.class, () -> matchData.copySubstring(99, buffer));
    }

    @Test
    default void copySubstringUnsetGroup() {
        final var code = new Pcre2Code(
                getApi(),
                "(a)|(b)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "a",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        // Group 1 matched
        final var buffer1 = ByteBuffer.allocateDirect(100);
        final var length1 = matchData.copySubstring(1, buffer1);
        assertEquals(1, length1);

        // Group 2 did not participate in the match
        final var buffer2 = ByteBuffer.allocateDirect(100);
        assertThrows(IllegalStateException.class, () -> matchData.copySubstring(2, buffer2));
    }

    @Test
    default void copySubstringBufferTooSmall() {
        final var code = new Pcre2Code(
                getApi(),
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(1, result);

        // Buffer too small - needs at least 6 bytes (5 for "hello" + 1 for null terminator)
        final var buffer = ByteBuffer.allocateDirect(3);
        assertThrows(IllegalStateException.class, () -> matchData.copySubstring(0, buffer));
    }

    @Test
    default void copySubstringByNameSimple() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        final var buffer = ByteBuffer.allocateDirect(100);
        final var length = matchData.copySubstring("word", buffer);
        assertEquals(5, length);

        final var bytes = new byte[length];
        buffer.get(bytes);
        assertEquals("hello", new String(bytes, StandardCharsets.UTF_8));
    }

    @Test
    default void copySubstringByNameMultipleGroups() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<first>\\w+) (?<second>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(3, result);

        // Group "first"
        final var buffer1 = ByteBuffer.allocateDirect(100);
        final var length1 = matchData.copySubstring("first", buffer1);
        assertEquals(5, length1);
        final var bytes1 = new byte[length1];
        buffer1.get(bytes1);
        assertEquals("hello", new String(bytes1, StandardCharsets.UTF_8));

        // Group "second"
        final var buffer2 = ByteBuffer.allocateDirect(100);
        final var length2 = matchData.copySubstring("second", buffer2);
        assertEquals(5, length2);
        final var bytes2 = new byte[length2];
        buffer2.get(bytes2);
        assertEquals("world", new String(bytes2, StandardCharsets.UTF_8));
    }

    @Test
    default void copySubstringByNameUnicode() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<emoji>🌐+)",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello 🌐🌐🌐 world",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        final var buffer = ByteBuffer.allocateDirect(100);
        final var length = matchData.copySubstring("emoji", buffer);
        // Each 🌐 is 4 bytes in UTF-8
        assertEquals(12, length);

        final var bytes = new byte[length];
        buffer.get(bytes);
        assertEquals("🌐🌐🌐", new String(bytes, StandardCharsets.UTF_8));
    }

    @Test
    default void copySubstringByNameNullName() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        final var buffer = ByteBuffer.allocateDirect(100);
        assertThrows(IllegalArgumentException.class, () -> matchData.copySubstring((String) null, buffer));
    }

    @Test
    default void copySubstringByNameNullBuffer() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        assertThrows(IllegalArgumentException.class, () -> matchData.copySubstring("word", null));
    }

    @Test
    default void copySubstringByNameNonDirectBuffer() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        final var buffer = ByteBuffer.allocate(100); // Not direct
        assertThrows(IllegalArgumentException.class, () -> matchData.copySubstring("word", buffer));
    }

    @Test
    default void copySubstringByNameInvalid() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        final var buffer = ByteBuffer.allocateDirect(100);
        assertThrows(IndexOutOfBoundsException.class, () -> matchData.copySubstring("nonexistent", buffer));
    }

    @Test
    default void copySubstringByNameUnsetGroup() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<first>a)|(?<second>b)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "a",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        // Group "first" matched
        final var buffer1 = ByteBuffer.allocateDirect(100);
        final var length1 = matchData.copySubstring("first", buffer1);
        assertEquals(1, length1);
        final var bytes1 = new byte[length1];
        buffer1.get(bytes1);
        assertEquals("a", new String(bytes1, StandardCharsets.UTF_8));

        // Group "second" did not participate in the match
        final var buffer2 = ByteBuffer.allocateDirect(100);
        assertThrows(IllegalStateException.class, () -> matchData.copySubstring("second", buffer2));
    }

    @Test
    default void copySubstringByNameBufferTooSmall() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(2, result);

        // Buffer too small - needs at least 6 bytes (5 for "hello" + 1 for null terminator)
        final var buffer = ByteBuffer.allocateDirect(3);
        assertThrows(IllegalStateException.class, () -> matchData.copySubstring("word", buffer));
    }

    @Test
    default void copySubstringByNameDuplicateNames() {
        // DUPNAMES option allows duplicate named groups
        final var code = new Pcre2Code(
                getApi(),
                "(?<num>\\d+)|(?<num>\\w+)",
                EnumSet.of(Pcre2CompileOption.DUPNAMES),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // Match letters (second alternative)
        final var result = code.match(
                "hello",
                0,
                EnumSet.of(Pcre2MatchOption.COPY_MATCHED_SUBJECT),
                matchData,
                null
        );
        assertEquals(3, result);

        final var buffer = ByteBuffer.allocateDirect(100);
        final var length = matchData.copySubstring("num", buffer);
        assertEquals(5, length);

        final var bytes = new byte[length];
        buffer.get(bytes);
        assertEquals("hello", new String(bytes, StandardCharsets.UTF_8));
    }

    @Test
    default void groupNumberFromNameSingle() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var groupNumber = code.groupNumberFromName("word");
        assertEquals(1, groupNumber);
    }

    @Test
    default void groupNumberFromNameMultiple() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<first>\\w+) (?<second>\\w+) (?<third>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        assertEquals(1, code.groupNumberFromName("first"));
        assertEquals(2, code.groupNumberFromName("second"));
        assertEquals(3, code.groupNumberFromName("third"));
    }

    @Test
    default void groupNumberFromNameNonexistent() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        assertThrows(Pcre2NoSubstringError.class, () -> code.groupNumberFromName("nonexistent"));
    }

    @Test
    default void groupNumberFromNameNull() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        assertThrows(IllegalArgumentException.class, () -> code.groupNumberFromName(null));
    }

    @Test
    default void groupNumberFromNameDuplicateNames() {
        // DUPNAMES option allows duplicate named groups, but querying returns non-unique error
        final var code = new Pcre2Code(
                getApi(),
                "(?<num>\\d+)|(?<num>\\w+)",
                EnumSet.of(Pcre2CompileOption.DUPNAMES),
                null
        );

        assertThrows(Pcre2NoUniqueSubstringError.class, () -> code.groupNumberFromName("num"));
    }

    @Test
    default void groupNumberFromNameWithUnderscore() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<my_group_name>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var groupNumber = code.groupNumberFromName("my_group_name");
        assertEquals(1, groupNumber);
    }

    @Test
    default void groupNumberFromNameWithDigits() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<group123>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var groupNumber = code.groupNumberFromName("group123");
        assertEquals(1, groupNumber);
    }

    @Test
    default void scanNametableSingle() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var groupNumbers = code.scanNametable("word");
        assertArrayEquals(new int[]{1}, groupNumbers);
    }

    @Test
    default void scanNametableMultipleGroups() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<first>\\w+) (?<second>\\w+) (?<third>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        assertArrayEquals(new int[]{1}, code.scanNametable("first"));
        assertArrayEquals(new int[]{2}, code.scanNametable("second"));
        assertArrayEquals(new int[]{3}, code.scanNametable("third"));
    }

    @Test
    default void scanNametableDuplicateNames() {
        // DUPNAMES option allows duplicate named groups
        final var code = new Pcre2Code(
                getApi(),
                "(?<num>\\d+)|(?<num>\\w+)",
                EnumSet.of(Pcre2CompileOption.DUPNAMES),
                null
        );

        final var groupNumbers = code.scanNametable("num");
        assertArrayEquals(new int[]{1, 2}, groupNumbers);
    }

    @Test
    default void scanNametableMultipleDuplicateNames() {
        // Multiple duplicate named groups
        final var code = new Pcre2Code(
                getApi(),
                "(?<a>a)|(?<b>b)|(?<a>aa)|(?<b>bb)|(?<a>aaa)",
                EnumSet.of(Pcre2CompileOption.DUPNAMES),
                null
        );

        assertArrayEquals(new int[]{1, 3, 5}, code.scanNametable("a"));
        assertArrayEquals(new int[]{2, 4}, code.scanNametable("b"));
    }

    @Test
    default void scanNametableNonexistent() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        assertThrows(Pcre2NoSubstringError.class, () -> code.scanNametable("nonexistent"));
    }

    @Test
    default void scanNametableNull() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        assertThrows(IllegalArgumentException.class, () -> code.scanNametable(null));
    }
}
