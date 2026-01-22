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
import org.pcre4j.*;
import org.pcre4j.api.IPcre2;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * {@link IPcre2} implementation tests.
 */
public abstract class Pcre2Tests {

    protected final IPcre2 api;

    protected Pcre2Tests(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        this.api = api;
    }

    @Test
    public void config() {
        Pcre4jUtils.getVersion(api);
        Pcre4jUtils.getUnicodeVersion(api);
        Pcre4jUtils.isUnicodeSupported(api);
        Pcre4jUtils.getDefaultParenthesesNestingLimit(api);
        Pcre4jUtils.getDefaultNewline(api);
        Pcre4jUtils.isBackslashCDisabled(api);
        Pcre4jUtils.getDefaultMatchLimit(api);
        Pcre4jUtils.getInternalLinkSize(api);
        Pcre4jUtils.getJitTarget(api);
        Pcre4jUtils.isJitSupported(api);
        Pcre4jUtils.getDefaultHeapLimit(api);
        Pcre4jUtils.getDefaultDepthLimit(api);
        Pcre4jUtils.getCompiledWidths(api);
        Pcre4jUtils.getDefaultBsr(api);
    }

    @Test
    public void plainStringMatch() {
        final var code = new Pcre2Code(
                api,
                "42",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "42",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(1, result);

        final var ovector = matchData.ovector();
        assertArrayEquals(new long[]{0, 2}, ovector);
    }

    @Test
    public void plainStringMatchNoCapture() {
        final var code = new Pcre2Code(
                api,
                "(?:42)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "42",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(1, result);

        final var ovector = matchData.ovector();
        assertArrayEquals(new long[]{0, 2}, ovector);
    }

    @Test
    public void plainStringMatchCapture() {
        final var code = new Pcre2Code(
                api,
                "(42)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "42",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(2, result);

        final var ovector = matchData.ovector();
        assertArrayEquals(new long[]{0, 2, 0, 2}, ovector);
    }

    @Test
    public void plainStringMatchNamedCapture() {
        final var code = new Pcre2Code(
                api,
                "(?P<group>42)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "42",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(2, result);

        final var ovector = matchData.ovector();
        assertArrayEquals(new long[]{0, 2, 0, 2}, ovector);
    }

    @Test
    public void unicodeStringMatch() {
        final var code = new Pcre2Code(
                api,
                "🌐",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "🌐",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(1, result);

        final var ovector = matchData.ovector();
        assertArrayEquals(new long[]{0, 4}, ovector);
    }

    @Test
    public void unicodeStringMatchNoCapture() {
        final var code = new Pcre2Code(
                api,
                "(?:🌐)",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "🌐",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(1, result);

        final var ovector = matchData.ovector();
        assertArrayEquals(new long[]{0, 4}, ovector);
    }

    @Test
    public void unicodeStringMatchCapture() {
        final var code = new Pcre2Code(
                api,
                "(🌐)",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "🌐",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(2, result);

        final var ovector = matchData.ovector();
        assertArrayEquals(new long[]{0, 4, 0, 4}, ovector);
    }

    @Test
    public void unicodeStringMatchNamedCapture() {
        final var code = new Pcre2Code(
                api,
                "(?P<group>🌐)",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "🌐",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(2, result);

        final var ovector = matchData.ovector();
        assertArrayEquals(new long[]{0, 4, 0, 4}, ovector);
    }

    @Test
    public void nameTable() {
        final var code = new Pcre2Code(
                api,
                "(?<number>42)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var nameTable = code.nameTable();
        assertEquals(1, nameTable.length);
        assertEquals(new Pcre2Code.NameTableEntry(1, "number"), nameTable[0]);
    }

    @Test
    public void substituteBasic() {
        final var code = new Pcre2Code(
                api,
                "world",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.noneOf(Pcre2SubstituteOption.class),
                null,
                null,
                "universe"
        );
        assertEquals("hello universe", result);
    }

    @Test
    public void substituteGlobal() {
        final var code = new Pcre2Code(
                api,
                "o",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.of(Pcre2SubstituteOption.GLOBAL),
                null,
                null,
                "0"
        );
        assertEquals("hell0 w0rld", result);
    }

    @Test
    public void substituteWithCapture() {
        final var code = new Pcre2Code(
                api,
                "(\\w+) (\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.of(Pcre2SubstituteOption.EXTENDED),
                null,
                null,
                "$2 $1"
        );
        assertEquals("world hello", result);
    }

    @Test
    public void substituteWithNamedCapture() {
        final var code = new Pcre2Code(
                api,
                "(?<first>\\w+) (?<second>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.of(Pcre2SubstituteOption.EXTENDED),
                null,
                null,
                "${second} ${first}"
        );
        assertEquals("world hello", result);
    }

    @Test
    public void substituteUnicode() {
        final var code = new Pcre2Code(
                api,
                "🌐",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );

        final var result = code.substitute(
                "hello 🌐 world",
                0,
                EnumSet.noneOf(Pcre2SubstituteOption.class),
                null,
                null,
                "🌍"
        );
        assertEquals("hello 🌍 world", result);
    }

    @Test
    public void substituteUnicodeGlobal() {
        final var code = new Pcre2Code(
                api,
                "🌐",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );

        final var result = code.substitute(
                "🌐 hello 🌐 world 🌐",
                0,
                EnumSet.of(Pcre2SubstituteOption.GLOBAL),
                null,
                null,
                "🌍"
        );
        assertEquals("🌍 hello 🌍 world 🌍", result);
    }

    @Test
    public void substituteReplacementOnly() {
        final var code = new Pcre2Code(
                api,
                "world",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.of(Pcre2SubstituteOption.REPLACEMENT_ONLY),
                null,
                null,
                "universe"
        );
        assertEquals("universe", result);
    }

    @Test
    public void substituteLiteral() {
        final var code = new Pcre2Code(
                api,
                "(\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello",
                0,
                EnumSet.of(Pcre2SubstituteOption.LITERAL),
                null,
                null,
                "$1"
        );
        assertEquals("$1", result);
    }

    @Test
    public void substituteNoMatch() {
        final var code = new Pcre2Code(
                api,
                "xyz",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.noneOf(Pcre2SubstituteOption.class),
                null,
                null,
                "replacement"
        );
        assertEquals("hello world", result);
    }

    @Test
    public void substituteEmptySubject() {
        final var code = new Pcre2Code(
                api,
                "world",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "",
                0,
                EnumSet.noneOf(Pcre2SubstituteOption.class),
                null,
                null,
                "universe"
        );
        assertEquals("", result);
    }

    @Test
    public void substituteEmptyReplacement() {
        final var code = new Pcre2Code(
                api,
                "world",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.noneOf(Pcre2SubstituteOption.class),
                null,
                null,
                ""
        );
        assertEquals("hello ", result);
    }

    @Test
    public void substituteWithStartOffset() {
        final var code = new Pcre2Code(
                api,
                "o",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                5,
                EnumSet.noneOf(Pcre2SubstituteOption.class),
                null,
                null,
                "0"
        );
        assertEquals("hello w0rld", result);
    }

    @Test
    public void substituteGlobalWithStartOffset() {
        final var code = new Pcre2Code(
                api,
                "o",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                5,
                EnumSet.of(Pcre2SubstituteOption.GLOBAL),
                null,
                null,
                "0"
        );
        assertEquals("hello w0rld", result);
    }

    @Test
    public void getSubstringEntireMatch() {
        final var code = new Pcre2Code(
                api,
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
    public void getSubstringCapturingGroup() {
        final var code = new Pcre2Code(
                api,
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
    public void getSubstringUnicode() {
        final var code = new Pcre2Code(
                api,
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
    public void getSubstringNegativeNumber() {
        final var code = new Pcre2Code(
                api,
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
    public void getSubstringInvalidGroupNumber() {
        final var code = new Pcre2Code(
                api,
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
    public void getSubstringUnsetGroup() {
        final var code = new Pcre2Code(
                api,
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
    public void getSubstringByNameSimple() {
        final var code = new Pcre2Code(
                api,
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
    public void getSubstringByNameMultipleGroups() {
        final var code = new Pcre2Code(
                api,
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
    public void getSubstringByNameUnicode() {
        final var code = new Pcre2Code(
                api,
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
    public void getSubstringByNameNull() {
        final var code = new Pcre2Code(
                api,
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
    public void getSubstringByNameInvalid() {
        final var code = new Pcre2Code(
                api,
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
    public void getSubstringByNameUnsetGroup() {
        final var code = new Pcre2Code(
                api,
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
    public void getSubstringByNameDuplicateNames() {
        // DUPNAMES option allows duplicate named groups
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringEntireMatch() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringCapturingGroups() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringUnicode() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringNegativeNumber() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringNullBuffer() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringNonDirectBuffer() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringInvalidGroupNumber() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringUnsetGroup() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringBufferTooSmall() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringByNameSimple() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringByNameMultipleGroups() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringByNameUnicode() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringByNameNullName() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringByNameNullBuffer() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringByNameNonDirectBuffer() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringByNameInvalid() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringByNameUnsetGroup() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringByNameBufferTooSmall() {
        final var code = new Pcre2Code(
                api,
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
    public void copySubstringByNameDuplicateNames() {
        // DUPNAMES option allows duplicate named groups
        final var code = new Pcre2Code(
                api,
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
    public void groupNumberFromNameSingle() {
        final var code = new Pcre2Code(
                api,
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var groupNumber = code.groupNumberFromName("word");
        assertEquals(1, groupNumber);
    }

    @Test
    public void groupNumberFromNameMultiple() {
        final var code = new Pcre2Code(
                api,
                "(?<first>\\w+) (?<second>\\w+) (?<third>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        assertEquals(1, code.groupNumberFromName("first"));
        assertEquals(2, code.groupNumberFromName("second"));
        assertEquals(3, code.groupNumberFromName("third"));
    }

    @Test
    public void groupNumberFromNameNonexistent() {
        final var code = new Pcre2Code(
                api,
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        assertThrows(Pcre2NoSubstringError.class, () -> code.groupNumberFromName("nonexistent"));
    }

    @Test
    public void groupNumberFromNameNull() {
        final var code = new Pcre2Code(
                api,
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        assertThrows(IllegalArgumentException.class, () -> code.groupNumberFromName(null));
    }

    @Test
    public void groupNumberFromNameDuplicateNames() {
        // DUPNAMES option allows duplicate named groups, but querying returns non-unique error
        final var code = new Pcre2Code(
                api,
                "(?<num>\\d+)|(?<num>\\w+)",
                EnumSet.of(Pcre2CompileOption.DUPNAMES),
                null
        );

        assertThrows(Pcre2NoUniqueSubstringError.class, () -> code.groupNumberFromName("num"));
    }

    @Test
    public void groupNumberFromNameWithUnderscore() {
        final var code = new Pcre2Code(
                api,
                "(?<my_group_name>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var groupNumber = code.groupNumberFromName("my_group_name");
        assertEquals(1, groupNumber);
    }

    @Test
    public void groupNumberFromNameWithDigits() {
        final var code = new Pcre2Code(
                api,
                "(?<group123>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var groupNumber = code.groupNumberFromName("group123");
        assertEquals(1, groupNumber);
    }

    @Test
    public void scanNametableSingle() {
        final var code = new Pcre2Code(
                api,
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var groupNumbers = code.scanNametable("word");
        assertArrayEquals(new int[]{1}, groupNumbers);
    }

    @Test
    public void scanNametableMultipleGroups() {
        final var code = new Pcre2Code(
                api,
                "(?<first>\\w+) (?<second>\\w+) (?<third>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        assertArrayEquals(new int[]{1}, code.scanNametable("first"));
        assertArrayEquals(new int[]{2}, code.scanNametable("second"));
        assertArrayEquals(new int[]{3}, code.scanNametable("third"));
    }

    @Test
    public void scanNametableDuplicateNames() {
        // DUPNAMES option allows duplicate named groups
        final var code = new Pcre2Code(
                api,
                "(?<num>\\d+)|(?<num>\\w+)",
                EnumSet.of(Pcre2CompileOption.DUPNAMES),
                null
        );

        final var groupNumbers = code.scanNametable("num");
        assertArrayEquals(new int[]{1, 2}, groupNumbers);
    }

    @Test
    public void scanNametableMultipleDuplicateNames() {
        // Multiple duplicate named groups
        final var code = new Pcre2Code(
                api,
                "(?<a>a)|(?<b>b)|(?<a>aa)|(?<b>bb)|(?<a>aaa)",
                EnumSet.of(Pcre2CompileOption.DUPNAMES),
                null
        );

        assertArrayEquals(new int[]{1, 3, 5}, code.scanNametable("a"));
        assertArrayEquals(new int[]{2, 4}, code.scanNametable("b"));
    }

    @Test
    public void scanNametableNonexistent() {
        final var code = new Pcre2Code(
                api,
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        assertThrows(Pcre2NoSubstringError.class, () -> code.scanNametable("nonexistent"));
    }

    @Test
    public void scanNametableNull() {
        final var code = new Pcre2Code(
                api,
                "(?<word>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        assertThrows(IllegalArgumentException.class, () -> code.scanNametable(null));
    }

    @Test
    public void setMatchLimitNegativeThrows() {
        final var matchContext = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> matchContext.setMatchLimit(-1));
    }

    @Test
    public void setMatchLimitZeroAllowed() {
        final var matchContext = new Pcre2MatchContext(api, null);
        // Should not throw
        matchContext.setMatchLimit(0);
    }

    @Test
    public void setMatchLimitPositiveAllowed() {
        final var matchContext = new Pcre2MatchContext(api, null);
        // Should not throw
        matchContext.setMatchLimit(1000);
    }

    @Test
    public void matchWithEmbeddedLimitCausesMatchLimitError() {
        // Use a pattern with embedded match limit and disable PCRE2 optimizations
        // that would otherwise prevent catastrophic backtracking.
        // (*NO_AUTO_POSSESS) and (*NO_START_OPT) disable optimizations that
        // would normally make the match efficient.
        final var code = new Pcre2Code(
                api,
                "(*LIMIT_MATCH=100)(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // Match against a string that cannot match (ends with 'b' not matching '$' after 'a's)
        // This forces exponential backtracking as PCRE2 tries all possible groupings
        final var result = code.match(
                "aaaaaaaaaaaaaaaaaaaaaaaab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );

        // The match should fail with MATCHLIMIT error due to excessive backtracking
        assertEquals(IPcre2.ERROR_MATCHLIMIT, result);
    }

    @Test
    public void matchWithContextLimitCausesMatchLimitError() {
        // A pattern that requires extensive backtracking - disable PCRE2 optimizations
        // that would otherwise prevent catastrophic backtracking.
        final var code = new Pcre2Code(
                api,
                "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(api, null);

        // Set a match limit via the match context
        matchContext.setMatchLimit(100);

        // Match against a string that cannot match (ends with 'b' not matching '$' after 'a's)
        // This forces exponential backtracking as PCRE2 tries all possible groupings
        final var result = code.match(
                "aaaaaaaaaaaaaaaaaaaaaaaab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        // The match should fail with MATCHLIMIT error due to excessive backtracking
        assertEquals(IPcre2.ERROR_MATCHLIMIT, result);
    }

    @Test
    public void matchLimitFromPattern() {
        // A pattern with embedded match limit
        final var code = new Pcre2Code(
                api,
                "(*LIMIT_MATCH=5000)test",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        // The match limit should be readable from the compiled pattern
        assertEquals(5000, code.matchLimit());
    }

    @Test
    public void setDepthLimitNegativeThrows() {
        final var matchContext = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> matchContext.setDepthLimit(-1));
    }

    @Test
    public void setDepthLimitZeroAllowed() {
        final var matchContext = new Pcre2MatchContext(api, null);
        // Should not throw
        matchContext.setDepthLimit(0);
    }

    @Test
    public void setDepthLimitPositiveAllowed() {
        final var matchContext = new Pcre2MatchContext(api, null);
        // Should not throw
        matchContext.setDepthLimit(1000);
    }

    @Test
    public void matchWithEmbeddedDepthLimitCausesDepthLimitError() {
        // Use a pattern with embedded depth limit and disable PCRE2 optimizations
        // that would otherwise prevent catastrophic backtracking.
        // (*NO_AUTO_POSSESS) and (*NO_START_OPT) disable optimizations that
        // would normally make the match efficient.
        final var code = new Pcre2Code(
                api,
                "(*LIMIT_DEPTH=10)(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // Match against a string that cannot match (ends with 'b' not matching '$' after 'a's)
        // This forces recursive backtracking as PCRE2 tries all possible groupings
        final var result = code.match(
                "aaaaaaaaaaaaaaaaaaaaaaaab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );

        // The match should fail with DEPTHLIMIT error due to deep recursion
        assertEquals(IPcre2.ERROR_DEPTHLIMIT, result);
    }

    @Test
    public void matchWithContextDepthLimitCausesDepthLimitError() {
        // A pattern that requires extensive backtracking - disable PCRE2 optimizations
        // that would otherwise prevent catastrophic backtracking.
        final var code = new Pcre2Code(
                api,
                "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(api, null);

        // Set a depth limit via the match context
        matchContext.setDepthLimit(10);

        // Match against a string that cannot match (ends with 'b' not matching '$' after 'a's)
        // This forces recursive backtracking as PCRE2 tries all possible groupings
        final var result = code.match(
                "aaaaaaaaaaaaaaaaaaaaaaaab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        // The match should fail with DEPTHLIMIT error due to deep recursion
        assertEquals(IPcre2.ERROR_DEPTHLIMIT, result);
    }

    @Test
    public void depthLimitFromPattern() {
        // A pattern with embedded depth limit
        final var code = new Pcre2Code(
                api,
                "(*LIMIT_DEPTH=5000)test",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        // The depth limit should be readable from the compiled pattern
        assertEquals(5000, code.depthLimit());
    }

    @Test
    public void setHeapLimitNegativeThrows() {
        final var matchContext = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> matchContext.setHeapLimit(-1));
    }

    @Test
    public void setHeapLimitZeroAllowed() {
        final var matchContext = new Pcre2MatchContext(api, null);
        // Should not throw
        matchContext.setHeapLimit(0);
    }

    @Test
    public void setHeapLimitPositiveAllowed() {
        final var matchContext = new Pcre2MatchContext(api, null);
        // Should not throw
        matchContext.setHeapLimit(1000);
    }

    @Test
    public void heapLimitFromPattern() {
        // A pattern with embedded heap limit
        final var code = new Pcre2Code(
                api,
                "(*LIMIT_HEAP=5000)test",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        // The heap limit should be readable from the compiled pattern
        assertEquals(5000, code.heapLimit());
    }

    @Test
    public void matchWithEmbeddedHeapLimitCausesHeapLimitError() {
        // Use a pattern with embedded heap limit and disable PCRE2 optimizations
        // that would otherwise prevent catastrophic backtracking.
        // (*NO_AUTO_POSSESS) and (*NO_START_OPT) disable optimizations that
        // would normally make the match efficient.
        final var code = new Pcre2Code(
                api,
                "(*LIMIT_HEAP=1)(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // Match against a string that cannot match (ends with 'b' not matching '$' after 'a's)
        // This forces extensive backtracking that requires heap memory
        final var result = code.match(
                "aaaaaaaaaaaaaaaaaaaaaaaab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );

        // The match should fail with HEAPLIMIT error due to heap memory exhaustion
        assertEquals(IPcre2.ERROR_HEAPLIMIT, result);
    }

    @Test
    public void matchWithContextHeapLimitCausesHeapLimitError() {
        // A pattern that requires extensive backtracking - disable PCRE2 optimizations
        // that would otherwise prevent catastrophic backtracking.
        final var code = new Pcre2Code(
                api,
                "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // Create a match context with a very low heap limit
        final var matchContext = new Pcre2MatchContext(api, null);
        matchContext.setHeapLimit(1); // 1 KiB - very low limit

        // Match against a string that cannot match (ends with 'b' not matching '$' after 'a's)
        // This forces extensive backtracking that requires heap memory
        final var result = code.match(
                "aaaaaaaaaaaaaaaaaaaaaaaab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        // The match should fail with HEAPLIMIT error due to heap memory exhaustion
        assertEquals(IPcre2.ERROR_HEAPLIMIT, result);
    }

    @Test
    public void setOffsetLimitNegativeThrows() {
        final var matchContext = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> matchContext.setOffsetLimit(-1));
    }

    @Test
    public void setOffsetLimitZeroAllowed() {
        final var matchContext = new Pcre2MatchContext(api, null);
        // Should not throw
        matchContext.setOffsetLimit(0);
    }

    @Test
    public void setOffsetLimitPositiveAllowed() {
        final var matchContext = new Pcre2MatchContext(api, null);
        // Should not throw
        matchContext.setOffsetLimit(1000);
    }

    @Test
    public void offsetLimitEnforcedWhenPatternCompiledWithOption() {
        // Compile pattern with USE_OFFSET_LIMIT option
        final var code = new Pcre2Code(
                api,
                "test",
                EnumSet.of(Pcre2CompileOption.USE_OFFSET_LIMIT),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // Offset limit semantics: match can start at position <= limit (inclusive)
        // To prevent a match at position P, set limit to P-1

        // Test 1: "test" starts at position 0
        // With limit 0, position 0 IS allowed (0 <= 0), so match succeeds
        final var matchContext1 = new Pcre2MatchContext(api, null);
        matchContext1.setOffsetLimit(0);

        final var result1 = code.match(
                "test",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext1
        );
        assertTrue(result1 > 0, "Limit 0, pos 0: should match (0<=0), result=" + result1);

        // Test 2: In "XXXXtest", "test" starts at position 4
        // With limit 3, positions 0-3 are allowed, position 4 is NOT (4 > 3)
        final var matchContext2 = new Pcre2MatchContext(api, null);
        matchContext2.setOffsetLimit(3);

        final var result2 = code.match(
                "XXXXtest",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext2
        );
        assertEquals(IPcre2.ERROR_NOMATCH, result2, "Limit 3, pos 4: should fail (4>3), result=" + result2);

        // Test 3: With limit 4, position 4 IS allowed (4 <= 4)
        final var matchContext3 = new Pcre2MatchContext(api, null);
        matchContext3.setOffsetLimit(4);

        final var result3 = code.match(
                "XXXXtest",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext3
        );
        assertTrue(result3 > 0, "Match should succeed: test at pos 4, limit 4 (4 <= 4) (result=" + result3 + ")");

        // Test 4: With limit 5, position 4 is also allowed (4 <= 5)
        final var matchContext4 = new Pcre2MatchContext(api, null);
        matchContext4.setOffsetLimit(5);

        final var result4 = code.match(
                "XXXXtest",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext4
        );
        assertTrue(result4 > 0, "Match should succeed: test at pos 4, limit 5 (4 <= 5) (result=" + result4 + ")");
    }

    @Test
    public void offsetLimitCausesErrorWithoutCompileOption() {
        // Compile pattern WITHOUT USE_OFFSET_LIMIT option
        final var code = new Pcre2Code(
                api,
                "test",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // First verify the pattern matches without any match context
        final var resultNoContext = code.match(
                "01234test",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertTrue(resultNoContext > 0, "Match should succeed without match context");

        // Create a match context with offset limit of 5
        final var matchContext = new Pcre2MatchContext(api, null);
        matchContext.setOffsetLimit(5);

        // According to PCRE2, using offset limit on a pattern not compiled with USE_OFFSET_LIMIT
        // causes PCRE2_ERROR_BADOFFSETLIMIT error
        final var result = code.match(
                "01234test",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );
        assertEquals(
                IPcre2.ERROR_BADOFFSETLIMIT,
                result,
                "Offset limit without USE_OFFSET_LIMIT should cause BADOFFSETLIMIT"
        );
    }

    @Test
    public void offsetLimitRawApiTest() {
        // Test using raw API to verify low-level behavior
        // Test with "Xtest" where "test" starts at position 1
        String pattern = "test";
        int options = IPcre2.USE_OFFSET_LIMIT;
        int[] errorcode = new int[1];
        long[] erroroffset = new long[1];

        // Compile with USE_OFFSET_LIMIT
        long code = api.compile(pattern, options, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed (code=" + code + ", error=" + errorcode[0] + ")");

        // Create match data
        long matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        // Test 1: Match without offset limit (should succeed)
        int result = api.match(code, "Xtest", 0, 0, matchData, 0);
        assertTrue(result > 0, "Match without offset limit should succeed (result=" + result + ")");

        // Create match context
        long matchCtx = api.matchContextCreate(0);
        assertTrue(matchCtx != 0, "Match context creation should succeed");

        // Offset limit semantics: match can start at position <= limit (inclusive)

        // Test 2: Set offset limit to 0 - "test" starts at position 1, which is > 0, so should fail
        api.setOffsetLimit(matchCtx, 0);
        result = api.match(code, "Xtest", 0, 0, matchData, matchCtx);
        assertEquals(IPcre2.ERROR_NOMATCH, result, "Limit 0, pos 1: should fail (1>0), result=" + result);

        // Test 3: Set offset limit to 1 - "test" starts at position 1, which IS <= 1, so should succeed
        api.setOffsetLimit(matchCtx, 1);
        result = api.match(code, "Xtest", 0, 0, matchData, matchCtx);
        assertTrue(result > 0, "Limit 1, pos 1: should match (1<=1), result=" + result);

        // Test 4: Set offset limit to 2 - "test" starts at position 1, which IS <= 2, so should succeed
        api.setOffsetLimit(matchCtx, 2);
        result = api.match(code, "Xtest", 0, 0, matchData, matchCtx);
        assertTrue(result > 0, "Limit 2, pos 1: should match (1<=2), result=" + result);

        // Clean up
        api.matchContextFree(matchCtx);
        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void getSubstringLengthEntireMatch() {
        final var code = new Pcre2Code(
                api,
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(1, result);

        final var length = matchData.getSubstringLength(0);
        assertEquals(5, length);
    }

    @Test
    public void getSubstringLengthCapturingGroups() {
        final var code = new Pcre2Code(
                api,
                "(\\w+) (\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(3, result);

        assertEquals(11, matchData.getSubstringLength(0)); // "hello world"
        assertEquals(5, matchData.getSubstringLength(1));  // "hello"
        assertEquals(5, matchData.getSubstringLength(2));  // "world"
    }

    @Test
    public void getSubstringLengthUnicode() {
        final var code = new Pcre2Code(
                api,
                "(🌐+)",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello 🌐🌐🌐 world",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(2, result);

        // Each emoji is 4 bytes in UTF-8, 3 emojis = 12 bytes
        final var length = matchData.getSubstringLength(0);
        assertEquals(12, length);
    }

    @Test
    public void getSubstringLengthNegativeNumber() {
        final var code = new Pcre2Code(
                api,
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(1, result);

        assertThrows(IllegalArgumentException.class, () -> matchData.getSubstringLength(-1));
    }

    @Test
    public void getSubstringLengthNoGroup() {
        final var code = new Pcre2Code(
                api,
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(1, result);

        assertThrows(IndexOutOfBoundsException.class, () -> matchData.getSubstringLength(1));
    }

    @Test
    public void getSubstringLengthUnsetGroup() {
        final var code = new Pcre2Code(
                api,
                "(a)|(b)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "a",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(2, result);

        // Group 1 matched "a"
        assertEquals(1, matchData.getSubstringLength(1));

        // Group 2 did not participate in the match
        assertThrows(IllegalStateException.class, () -> matchData.getSubstringLength(2));
    }

    @Test
    public void substringLengthByNumberApiDirect() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0);

        final var matchData = api.matchDataCreateFromPattern(code, 0);

        final var result = api.match(code, "hello world", 0, 0, matchData, 0);
        assertTrue(result > 0);

        final var length = new long[1];
        final var lengthResult = api.substringLengthByNumber(matchData, 0, length);
        assertEquals(0, lengthResult);
        assertEquals(5, length[0]);

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void substringLengthByNumberApiNullLength() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0);

        final var matchData = api.matchDataCreateFromPattern(code, 0);

        final var result = api.match(code, "hello world", 0, 0, matchData, 0);
        assertTrue(result > 0);

        // Calling with null length should still succeed (just checking existence)
        final var lengthResult = api.substringLengthByNumber(matchData, 0, null);
        assertEquals(0, lengthResult);

        // Invalid group should fail even with null length
        final var invalidResult = api.substringLengthByNumber(matchData, 99, null);
        assertEquals(IPcre2.ERROR_NOSUBSTRING, invalidResult);

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void substringListGetApiDirect() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("(\\w+) (\\w+)", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0);

        final var matchData = api.matchDataCreateFromPattern(code, 0);

        final var result = api.match(code, "hello world", 0, 0, matchData, 0);
        assertEquals(3, result); // Full match + 2 groups

        final var listptr = new long[1];
        final var lengthsptr = new long[1];
        final var listResult = api.substringListGet(matchData, listptr, lengthsptr);
        assertEquals(0, listResult);
        assertTrue(listptr[0] != 0);
        assertTrue(lengthsptr[0] != 0);

        // Read and verify the lengths array (3 entries: full match + 2 groups)
        // Each PCRE2_SIZE is 8 bytes (long) in native byte order
        final var lengthsBytes = api.readBytes(lengthsptr[0], 3 * 8);
        final var lengthsBuffer = java.nio.ByteBuffer.wrap(lengthsBytes)
                .order(java.nio.ByteOrder.nativeOrder());
        assertEquals(11, lengthsBuffer.getLong(0));  // "hello world" = 11 chars
        assertEquals(5, lengthsBuffer.getLong(8));   // "hello" = 5 chars
        assertEquals(5, lengthsBuffer.getLong(16));  // "world" = 5 chars

        // Free the list
        api.substringListFree(listptr[0]);

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void substringListGetApiNullLengthsPtr() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("(\\w+) (\\w+)", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0);

        final var matchData = api.matchDataCreateFromPattern(code, 0);

        final var result = api.match(code, "hello world", 0, 0, matchData, 0);
        assertTrue(result > 0);

        final var listptr = new long[1];
        // Pass null for lengthsptr - should work
        final var listResult = api.substringListGet(matchData, listptr, null);
        assertEquals(0, listResult);
        assertTrue(listptr[0] != 0);

        // Free the list
        api.substringListFree(listptr[0]);

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void substringListFreeApiNullPointer() {
        // Free with null pointer should not throw
        api.substringListFree(0);
    }

    @Test
    public void getSubstringLengthByName() {
        final var code = new Pcre2Code(
                api,
                "(?<greeting>\\w+) (?<target>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(3, result);

        assertEquals(5, matchData.getSubstringLength("greeting")); // "hello"
        assertEquals(5, matchData.getSubstringLength("target"));   // "world"
    }

    @Test
    public void getSubstringLengthByNameUnicode() {
        final var code = new Pcre2Code(
                api,
                "(?<emojis>🌐+)",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello 🌐🌐🌐 world",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(2, result);

        // Each emoji is 4 bytes in UTF-8, 3 emojis = 12 bytes
        assertEquals(12, matchData.getSubstringLength("emojis"));
    }

    @Test
    public void getSubstringLengthByNameNullName() {
        final var code = new Pcre2Code(
                api,
                "(?<greeting>hello)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(2, result);

        assertThrows(IllegalArgumentException.class, () -> matchData.getSubstringLength((String) null));
    }

    @Test
    public void getSubstringLengthByNameNoGroup() {
        final var code = new Pcre2Code(
                api,
                "hello",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "hello world",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(1, result);

        assertThrows(IndexOutOfBoundsException.class, () -> matchData.getSubstringLength("nonexistent"));
    }

    @Test
    public void getSubstringLengthByNameUnsetGroup() {
        final var code = new Pcre2Code(
                api,
                "(?<a>a)|(?<b>b)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "a",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(2, result);

        // Group "a" matched
        assertEquals(1, matchData.getSubstringLength("a"));

        // Group "b" did not participate in the match
        assertThrows(IllegalStateException.class, () -> matchData.getSubstringLength("b"));
    }

    @Test
    public void substringLengthByNameApiDirect() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("(?<greeting>hello)", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0);

        final var matchData = api.matchDataCreateFromPattern(code, 0);

        final var result = api.match(code, "hello world", 0, 0, matchData, 0);
        assertTrue(result > 0);

        final var length = new long[1];
        final var lengthResult = api.substringLengthByName(matchData, "greeting", length);
        assertEquals(0, lengthResult);
        assertEquals(5, length[0]);

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void substringLengthByNameApiNullLength() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("(?<greeting>hello)", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0);

        final var matchData = api.matchDataCreateFromPattern(code, 0);

        final var result = api.match(code, "hello world", 0, 0, matchData, 0);
        assertTrue(result > 0);

        // Calling with null length should still succeed (just checking existence)
        final var lengthResult = api.substringLengthByName(matchData, "greeting", null);
        assertEquals(0, lengthResult);

        // Invalid group name should fail even with null length
        final var invalidResult = api.substringLengthByName(matchData, "nonexistent", null);
        assertEquals(IPcre2.ERROR_NOSUBSTRING, invalidResult);

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void dfaMatchBasic() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        // DFA matching requires a workspace array
        final var workspace = new int[100];
        final var result = api.dfaMatch(code, "hello world", 0, 0, matchData, 0, workspace, workspace.length);

        // DFA match should succeed and return 1 for a simple match
        assertTrue(result > 0, "DFA match should succeed (result=" + result + ")");

        // Verify match position using ovector
        final var ovector = new long[2];
        api.getOvector(matchData, ovector);
        assertEquals(0, ovector[0], "Match should start at position 0");
        assertEquals(5, ovector[1], "Match should end at position 5");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void dfaMatchNoMatch() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("xyz", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];
        final var result = api.dfaMatch(code, "hello world", 0, 0, matchData, 0, workspace, workspace.length);

        assertEquals(IPcre2.ERROR_NOMATCH, result, "DFA match should return NOMATCH");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void dfaMatchWithStartOffset() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("world", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];
        // Start matching from offset 6
        final var result = api.dfaMatch(code, "hello world", 6, 0, matchData, 0, workspace, workspace.length);

        assertTrue(result > 0, "DFA match should succeed (result=" + result + ")");

        // Verify match position
        final var ovector = new long[2];
        api.getOvector(matchData, ovector);
        assertEquals(6, ovector[0], "Match should start at position 6");
        assertEquals(11, ovector[1], "Match should end at position 11");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void dfaMatchAlternatives() {
        // DFA matching can find all alternative matches - use a pattern with alternation
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        // Pattern matches "ab", "abc", or "abcd"
        final var code = api.compile("ab(c(d)?)?", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        // Create match data with enough space for multiple matches
        // DFA can return multiple matches, so we need space in ovector
        final var matchData = api.matchDataCreate(10, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];
        final var result = api.dfaMatch(code, "abcd", 0, 0, matchData, 0, workspace, workspace.length);

        // DFA should find multiple matches (ab, abc, abcd)
        assertTrue(result >= 1, "DFA match should find at least one match (result=" + result + ")");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void dfaMatchWorkspaceTooSmall() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("(a+)+", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        // Use a very small workspace
        final var workspace = new int[1];
        final var result = api.dfaMatch(code, "aaaaaaaaaa", 0, 0, matchData, 0, workspace, workspace.length);

        // Should return DFA workspace too small error
        assertEquals(IPcre2.ERROR_DFA_WSSIZE, result, "Should return DFA_WSSIZE error");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void dfaMatchNullSubject() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];

        assertThrows(IllegalArgumentException.class,
                () -> api.dfaMatch(code, null, 0, 0, matchData, 0, workspace, workspace.length));

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void dfaMatchNullWorkspace() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        assertThrows(IllegalArgumentException.class,
                () -> api.dfaMatch(code, "hello world", 0, 0, matchData, 0, null, 100));

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void dfaMatchNegativeWscount() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];

        assertThrows(IllegalArgumentException.class,
                () -> api.dfaMatch(code, "hello world", 0, 0, matchData, 0, workspace, -1));

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void dfaMatchWscountTooLarge() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];

        // wscount larger than workspace array
        assertThrows(IllegalArgumentException.class,
                () -> api.dfaMatch(code, "hello world", 0, 0, matchData, 0, workspace, 200));

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void dfaMatchUnicode() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = api.compile("🌐+", IPcre2.UTF, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];
        final var result = api.dfaMatch(code, "hello 🌐🌐🌐 world", 0, 0, matchData, 0, workspace, workspace.length);

        assertTrue(result > 0, "DFA match should succeed (result=" + result + ")");

        // Verify match position (UTF-8 encoding: each emoji is 4 bytes)
        final var ovector = new long[2];
        api.getOvector(matchData, ovector);
        assertEquals(6, ovector[0], "Match should start at byte offset 6");
        assertEquals(18, ovector[1], "Match should end at byte offset 18 (6 + 3*4)");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void dfaMatchShortest() {
        // Test DFA_SHORTEST option which returns shortest match
        // DFA_SHORTEST only has effect with patterns that have alternatives of different lengths
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        // Pattern with explicit alternatives of different lengths
        final var code = api.compile("a|aa|aaa|aaaa", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed");

        final var matchData = api.matchDataCreate(10, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var workspace = new int[100];

        // Without DFA_SHORTEST, DFA returns all matches with longest first (rc=count of matches)
        final var resultLongest = api.dfaMatch(code, "aaaa", 0, 0, matchData, 0, workspace, workspace.length);
        assertEquals(4, resultLongest, "DFA without DFA_SHORTEST should return all 4 alternative matches");

        final var ovectorLongest = new long[2];
        api.getOvector(matchData, ovectorLongest);
        assertEquals(4, ovectorLongest[1], "Without DFA_SHORTEST, first match should be longest (4 a's)");

        // With DFA_SHORTEST, DFA returns only the shortest match
        final var resultShortest = api.dfaMatch(
                code, "aaaa", 0, IPcre2.DFA_SHORTEST, matchData, 0, workspace, workspace.length);
        assertEquals(1, resultShortest, "DFA with DFA_SHORTEST should return only 1 match");

        final var ovectorShortest = new long[2];
        api.getOvector(matchData, ovectorShortest);
        assertEquals(1, ovectorShortest[1], "With DFA_SHORTEST, should match only 1 'a'");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    public void setBsrNullThrows() {
        final var compileContext = new Pcre2CompileContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> compileContext.setBsr(null));
    }

    @Test
    public void setBsrUnicodeAllowed() {
        final var compileContext = new Pcre2CompileContext(api, null);
        // Should not throw
        compileContext.setBsr(Pcre2Bsr.UNICODE);
    }

    @Test
    public void setBsrAnyCrLfAllowed() {
        final var compileContext = new Pcre2CompileContext(api, null);
        // Should not throw
        compileContext.setBsr(Pcre2Bsr.ANYCRLF);
    }

    @Test
    public void bsrUnicodeMatchesVerticalTab() {
        // With BSR_UNICODE, \R should match vertical tab (U+000B)
        final var compileContext = new Pcre2CompileContext(api, null);
        compileContext.setBsr(Pcre2Bsr.UNICODE);

        final var code = new Pcre2Code(
                api,
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
    public void bsrAnyCrLfDoesNotMatchVerticalTab() {
        // With BSR_ANYCRLF, \R should NOT match vertical tab (U+000B)
        final var compileContext = new Pcre2CompileContext(api, null);
        compileContext.setBsr(Pcre2Bsr.ANYCRLF);

        final var code = new Pcre2Code(
                api,
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
    public void bsrAnyCrLfMatchesCrLf() {
        // With BSR_ANYCRLF, \R should match CR, LF, and CRLF
        final var compileContext = new Pcre2CompileContext(api, null);
        compileContext.setBsr(Pcre2Bsr.ANYCRLF);

        final var code = new Pcre2Code(
                api,
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
    public void setParensNestLimitAllowsValidNesting() {
        final var compileContext = new Pcre2CompileContext(api, null);
        compileContext.setParensNestLimit(10);

        // A pattern with nesting depth of 3 should compile successfully
        final var code = new Pcre2Code(
                api,
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
    public void setParensNestLimitRejectsExcessiveNesting() {
        final var compileContext = new Pcre2CompileContext(api, null);
        compileContext.setParensNestLimit(2);

        // A pattern with nesting depth of 3 should fail to compile with limit of 2
        final var exception = assertThrows(Pcre2CompileError.class, () -> new Pcre2Code(
                api,
                "(((a)))",
                EnumSet.noneOf(Pcre2CompileOption.class),
                compileContext
        ));

        assertTrue(exception.message().contains("parentheses") || exception.message().contains("nest"),
                "Should fail with parentheses nesting error, got: " + exception.message());
    }

    @Test
    public void setParensNestLimitWithHighValue() {
        final var compileContext = new Pcre2CompileContext(api, null);
        // Setting a high limit should not throw
        compileContext.setParensNestLimit(1000);

        // Should compile complex nested pattern
        final var code = new Pcre2Code(
                api,
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
    public void setMaxPatternLengthAllowsValidPattern() {
        final var compileContext = new Pcre2CompileContext(api, null);
        compileContext.setMaxPatternLength(100);

        // A short pattern should compile successfully
        final var code = new Pcre2Code(
                api,
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
    public void setMaxPatternLengthRejectsLongPattern() {
        final var compileContext = new Pcre2CompileContext(api, null);
        compileContext.setMaxPatternLength(5);

        // A pattern longer than the limit should fail to compile
        final var exception = assertThrows(Pcre2CompileError.class, () -> new Pcre2Code(
                api,
                "abcdefghij",
                EnumSet.noneOf(Pcre2CompileOption.class),
                compileContext
        ));

        assertTrue(exception.message().contains("pattern") || exception.message().contains("long"),
                "Should fail with pattern length error, got: " + exception.message());
    }

    @Test
    public void setMaxPatternLengthWithHighValue() {
        final var compileContext = new Pcre2CompileContext(api, null);
        // Setting a high limit should not throw
        compileContext.setMaxPatternLength(1000000);

        // Should compile a reasonably long pattern
        final var longPattern = "a{1,100}";
        final var code = new Pcre2Code(
                api,
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
    public void setCompileExtraOptionsNullThrows() {
        final var compileContext = new Pcre2CompileContext(api, null);
        assertThrows(IllegalArgumentException.class,
                () -> compileContext.setCompileExtraOptions((Pcre2CompileExtraOption[]) null));
    }

    @Test
    public void setCompileExtraOptionsNullElementThrows() {
        final var compileContext = new Pcre2CompileContext(api, null);
        assertThrows(IllegalArgumentException.class,
                () -> compileContext.setCompileExtraOptions(Pcre2CompileExtraOption.MATCH_WORD, null));
    }

    @Test
    public void setCompileExtraOptionsEmpty() {
        final var compileContext = new Pcre2CompileContext(api, null);
        // Should not throw with empty varargs
        compileContext.setCompileExtraOptions();

        // Pattern should still compile and match
        final var code = new Pcre2Code(
                api,
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
    public void setCompileExtraOptionsAsciiDigitRestrictsToAscii() {
        // ASCII_BSD was added in PCRE2 10.43
        assumeTrue(Pcre4jUtils.isVersionAtLeast(api, 10, 43),
                "Skipping test: ASCII_BSD requires PCRE2 10.43+");

        final var compileContext = new Pcre2CompileContext(api, null);
        compileContext.setCompileExtraOptions(Pcre2CompileExtraOption.ASCII_BSD);

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
    public void setCompileExtraOptionsMatchWord() {
        final var compileContext = new Pcre2CompileContext(api, null);
        compileContext.setCompileExtraOptions(Pcre2CompileExtraOption.MATCH_WORD);

        // With MATCH_WORD, pattern should only match whole words
        final var code = new Pcre2Code(
                api,
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
    public void setCompileExtraOptionsMatchLine() {
        final var compileContext = new Pcre2CompileContext(api, null);
        compileContext.setCompileExtraOptions(Pcre2CompileExtraOption.MATCH_LINE);

        // With MATCH_LINE, pattern should only match whole lines
        final var code = new Pcre2Code(
                api,
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
    public void setCompileExtraOptionsMultipleOptions() {
        // ASCII_BSD, ASCII_BSS, ASCII_BSW were added in PCRE2 10.43
        assumeTrue(Pcre4jUtils.isVersionAtLeast(api, 10, 43),
                "Skipping test: ASCII_BSD/BSS/BSW requires PCRE2 10.43+");

        final var compileContext = new Pcre2CompileContext(api, null);
        // Set multiple extra options at once
        compileContext.setCompileExtraOptions(
                Pcre2CompileExtraOption.ASCII_BSD,
                Pcre2CompileExtraOption.ASCII_BSS,
                Pcre2CompileExtraOption.ASCII_BSW
        );

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

    @Test
    public void testGetStartchar() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Test 1: Normal case - getStartchar should equal ovector[0]
        final var code1 = api.compile("bar", 0, errorcode, erroroffset, 0);
        assertTrue(code1 != 0, "Failed to compile pattern");

        final var matchData1 = api.matchDataCreateFromPattern(code1, 0);
        assertTrue(matchData1 != 0, "Failed to create match data");

        final var result1 = api.match(code1, "foobar", 0, 0, matchData1, 0);
        assertTrue(result1 > 0, "Match should succeed");

        final var ovector1 = new long[2];
        api.getOvector(matchData1, ovector1);
        final var startchar1 = api.getStartchar(matchData1);
        assertEquals(ovector1[0], startchar1, "For normal patterns, getStartchar should equal ovector[0]");
        assertEquals(3, startchar1, "Match should start at position 3");

        api.matchDataFree(matchData1);
        api.codeFree(code1);

        // Test 2: Pattern with \K - getStartchar should differ from ovector[0]
        // \K resets the start of the matched string, so ovector[0] will be after \K,
        // but getStartchar returns where the match actually started
        final var code2 = api.compile("foo\\Kbar", 0, errorcode, erroroffset, 0);
        assertTrue(code2 != 0, "Failed to compile pattern with \\K");

        final var matchData2 = api.matchDataCreateFromPattern(code2, 0);
        assertTrue(matchData2 != 0, "Failed to create match data");

        final var result2 = api.match(code2, "foobar", 0, 0, matchData2, 0);
        assertTrue(result2 > 0, "Match with \\K should succeed");

        final var ovector2 = new long[2];
        api.getOvector(matchData2, ovector2);
        final var startchar2 = api.getStartchar(matchData2);

        // ovector[0] should be 3 (start of "bar" after \K reset)
        assertEquals(3, ovector2[0], "ovector[0] should be 3 (after \\K reset)");
        // getStartchar should be 0 (where the actual match started, at "foo")
        assertEquals(0, startchar2, "getStartchar should be 0 (where match actually started)");
        // They should differ due to \K
        assertTrue(ovector2[0] != startchar2, "With \\K, getStartchar should differ from ovector[0]");

        api.matchDataFree(matchData2);
        api.codeFree(code2);
    }

}
