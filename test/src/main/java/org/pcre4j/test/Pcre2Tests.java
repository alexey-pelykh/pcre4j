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
    public void testGetMatchDataSize() {
        // Test 1: Match data created with explicit ovector size
        final var matchData1 = api.matchDataCreate(10, 0);
        assertTrue(matchData1 != 0, "Failed to create match data with size 10");

        final var size1 = api.getMatchDataSize(matchData1);
        assertTrue(size1 > 0, "Match data size should be greater than 0");

        api.matchDataFree(matchData1);

        // Test 2: Match data created from pattern
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Pattern with 3 capture groups - should allocate space for 4 ovector pairs (full match + 3 groups)
        final var code2 = api.compile("(a)(b)(c)", 0, errorcode, erroroffset, 0);
        assertTrue(code2 != 0, "Failed to compile pattern");

        final var matchData2 = api.matchDataCreateFromPattern(code2, 0);
        assertTrue(matchData2 != 0, "Failed to create match data from pattern");

        final var size2 = api.getMatchDataSize(matchData2);
        assertTrue(size2 > 0, "Match data size from pattern should be greater than 0");

        api.matchDataFree(matchData2);
        api.codeFree(code2);

        // Test 3: Different ovector sizes should result in different match data sizes
        final var smallMatchData = api.matchDataCreate(1, 0);
        final var largeMatchData = api.matchDataCreate(100, 0);

        assertTrue(smallMatchData != 0, "Failed to create small match data");
        assertTrue(largeMatchData != 0, "Failed to create large match data");

        final var smallSize = api.getMatchDataSize(smallMatchData);
        final var largeSize = api.getMatchDataSize(largeMatchData);

        assertTrue(smallSize > 0, "Small match data size should be greater than 0");
        assertTrue(largeSize > 0, "Large match data size should be greater than 0");
        assertTrue(largeSize > smallSize, "Larger ovector should result in larger match data block");

        api.matchDataFree(smallMatchData);
        api.matchDataFree(largeMatchData);
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

    @Test
    public void testGetMark() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Test 1: Pattern with (*MARK:name) - getMark should return non-null pointer
        final var code1 = api.compile("a(*MARK:foo)b", 0, errorcode, erroroffset, 0);
        assertTrue(code1 != 0, "Failed to compile pattern with (*MARK:foo)");

        final var matchData1 = api.matchDataCreateFromPattern(code1, 0);
        assertTrue(matchData1 != 0, "Failed to create match data");

        final var result1 = api.match(code1, "ab", 0, 0, matchData1, 0);
        assertTrue(result1 > 0, "Match should succeed");

        final var mark1 = api.getMark(matchData1);
        assertTrue(mark1 != 0, "getMark should return non-null after successful match with (*MARK)");

        api.matchDataFree(matchData1);
        api.codeFree(code1);

        // Test 2: Pattern without mark - getMark should return NULL (0)
        final var code2 = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code2 != 0, "Failed to compile pattern without mark");

        final var matchData2 = api.matchDataCreateFromPattern(code2, 0);
        assertTrue(matchData2 != 0, "Failed to create match data");

        final var result2 = api.match(code2, "hello", 0, 0, matchData2, 0);
        assertTrue(result2 > 0, "Match should succeed");

        assertEquals(0, api.getMark(matchData2), "getMark should return 0 (NULL) when no mark is set");

        api.matchDataFree(matchData2);
        api.codeFree(code2);

        // Test 3: Pattern with multiple marks - should return the last encountered mark
        final var code3 = api.compile("(*MARK:first)a(*MARK:second)b", 0, errorcode, erroroffset, 0);
        assertTrue(code3 != 0, "Failed to compile pattern with multiple marks");

        final var matchData3 = api.matchDataCreateFromPattern(code3, 0);
        assertTrue(matchData3 != 0, "Failed to create match data");

        final var result3 = api.match(code3, "ab", 0, 0, matchData3, 0);
        assertTrue(result3 > 0, "Match should succeed");

        final var mark3 = api.getMark(matchData3);
        assertTrue(mark3 != 0, "getMark should return non-null for pattern with marks");

        api.matchDataFree(matchData3);
        api.codeFree(code3);

        // Test 4: getMark after failed match - should return mark passed before failure
        // Pattern: (*MARK:passed) sets the mark, 'a' matches, then (*FAIL) forces failure
        final var code4 = api.compile("(*MARK:passed)a(*FAIL)", 0, errorcode, erroroffset, 0);
        assertTrue(code4 != 0, "Failed to compile pattern for failed match test");

        final var matchData4 = api.matchDataCreateFromPattern(code4, 0);
        assertTrue(matchData4 != 0, "Failed to create match data");

        final var result4 = api.match(code4, "a", 0, 0, matchData4, 0);
        assertEquals(IPcre2.ERROR_NOMATCH, result4, "Match should fail with NOMATCH due to (*FAIL)");

        final var mark4 = api.getMark(matchData4);
        assertTrue(mark4 != 0, "getMark should return non-null for mark passed before match failure");

        api.matchDataFree(matchData4);
        api.codeFree(code4);

        // Test 5: getMark after partial match
        final var code5 = api.compile("abc", 0, errorcode, erroroffset, 0);
        assertTrue(code5 != 0, "Failed to compile pattern for partial match test");

        final var matchData5 = api.matchDataCreateFromPattern(code5, 0);
        assertTrue(matchData5 != 0, "Failed to create match data");

        // Match "ab" against pattern "abc" with PARTIAL_SOFT - should return partial match
        final var result5 = api.match(code5, "ab", 0, IPcre2.PARTIAL_SOFT, matchData5, 0);
        assertEquals(IPcre2.ERROR_PARTIAL, result5, "Match should return PARTIAL");

        // No mark in this pattern, so getMark should return 0 even after partial match
        assertEquals(0, api.getMark(matchData5), "getMark should return 0 when pattern has no marks");

        api.matchDataFree(matchData5);
        api.codeFree(code5);

        // Test 6: getMark with (*PRUNE:name) - getMark also returns PRUNE names
        final var code6 = api.compile("a(*PRUNE:pruned)b", 0, errorcode, erroroffset, 0);
        assertTrue(code6 != 0, "Failed to compile pattern with (*PRUNE:name)");

        final var matchData6 = api.matchDataCreateFromPattern(code6, 0);
        assertTrue(matchData6 != 0, "Failed to create match data");

        final var result6 = api.match(code6, "ab", 0, 0, matchData6, 0);
        assertTrue(result6 > 0, "Match should succeed");

        final var mark6 = api.getMark(matchData6);
        assertTrue(mark6 != 0, "getMark should return non-null for (*PRUNE:name)");

        api.matchDataFree(matchData6);
        api.codeFree(code6);

        // Test 7: getMark with (*THEN:name) - getMark also returns THEN names
        final var code7 = api.compile("a(*THEN:thenname)b", 0, errorcode, erroroffset, 0);
        assertTrue(code7 != 0, "Failed to compile pattern with (*THEN:name)");

        final var matchData7 = api.matchDataCreateFromPattern(code7, 0);
        assertTrue(matchData7 != 0, "Failed to create match data");

        final var result7 = api.match(code7, "ab", 0, 0, matchData7, 0);
        assertTrue(result7 > 0, "Match should succeed");

        final var mark7 = api.getMark(matchData7);
        assertTrue(mark7 != 0, "getMark should return non-null for (*THEN:name)");

        api.matchDataFree(matchData7);
        api.codeFree(code7);
    }

    @Test
    public void testCodeCopy() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Test 1: Basic code copy functionality
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Failed to compile pattern");

        final var codeCopy = api.codeCopy(code);
        assertTrue(codeCopy != 0, "codeCopy should return non-zero for valid pattern");
        assertTrue(codeCopy != code, "codeCopy should return a different handle");

        // Verify the copy works for matching
        final var matchData = api.matchDataCreateFromPattern(codeCopy, 0);
        assertTrue(matchData != 0, "Failed to create match data from copied code");

        final var result = api.match(codeCopy, "hello world", 0, 0, matchData, 0);
        assertTrue(result > 0, "Match should succeed with copied code");

        api.matchDataFree(matchData);
        api.codeFree(codeCopy);
        api.codeFree(code);

        // Test 2: Copy with capturing groups
        final var code2 = api.compile("(\\w+)@(\\w+)", 0, errorcode, erroroffset, 0);
        assertTrue(code2 != 0, "Failed to compile pattern with capturing groups");

        final var code2Copy = api.codeCopy(code2);
        assertTrue(code2Copy != 0, "codeCopy should return non-zero for pattern with capturing groups");

        final var matchData2 = api.matchDataCreateFromPattern(code2Copy, 0);
        assertTrue(matchData2 != 0, "Failed to create match data from copied code");

        final var result2 = api.match(code2Copy, "user@domain", 0, 0, matchData2, 0);
        assertEquals(3, result2, "Match should return 3 (full match + 2 capturing groups)");

        api.matchDataFree(matchData2);
        api.codeFree(code2Copy);
        api.codeFree(code2);

        // Test 3: codeCopy returns 0 for null/zero input
        final var nullCopy = api.codeCopy(0);
        assertEquals(0, nullCopy, "codeCopy should return 0 for null/zero input");
    }

    @Test
    public void testCodeCopyWithTables() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Test 1: Basic code copy with tables functionality
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Failed to compile pattern");

        final var codeCopy = api.codeCopyWithTables(code);
        assertTrue(codeCopy != 0, "codeCopyWithTables should return non-zero for valid pattern");
        assertTrue(codeCopy != code, "codeCopyWithTables should return a different handle");

        // Verify the copy works for matching
        final var matchData = api.matchDataCreateFromPattern(codeCopy, 0);
        assertTrue(matchData != 0, "Failed to create match data from copied code");

        final var result = api.match(codeCopy, "hello world", 0, 0, matchData, 0);
        assertTrue(result > 0, "Match should succeed with copied code");

        api.matchDataFree(matchData);
        api.codeFree(codeCopy);
        api.codeFree(code);

        // Test 2: Copy with capturing groups
        final var code2 = api.compile("(\\w+)@(\\w+)", 0, errorcode, erroroffset, 0);
        assertTrue(code2 != 0, "Failed to compile pattern with capturing groups");

        final var code2Copy = api.codeCopyWithTables(code2);
        assertTrue(code2Copy != 0, "codeCopyWithTables should return non-zero for pattern with capturing groups");

        final var matchData2 = api.matchDataCreateFromPattern(code2Copy, 0);
        assertTrue(matchData2 != 0, "Failed to create match data from copied code");

        final var result2 = api.match(code2Copy, "user@domain", 0, 0, matchData2, 0);
        assertEquals(3, result2, "Match should return 3 (full match + 2 capturing groups)");

        api.matchDataFree(matchData2);
        api.codeFree(code2Copy);
        api.codeFree(code2);

        // Test 3: codeCopyWithTables returns 0 for null/zero input
        final var nullCopy = api.codeCopyWithTables(0);
        assertEquals(0, nullCopy, "codeCopyWithTables should return 0 for null/zero input");

        // Test 4: Copy with custom character tables
        final var tables = api.maketables(0);
        assertTrue(tables != 0, "Failed to create character tables");

        final var ccontext = api.compileContextCreate(0);
        assertTrue(ccontext != 0, "Failed to create compile context");

        api.setCharacterTables(ccontext, tables);

        final var code3 = api.compile("HELLO", IPcre2.CASELESS, errorcode, erroroffset, ccontext);
        assertTrue(code3 != 0, "Failed to compile pattern with custom tables");

        final var code3Copy = api.codeCopyWithTables(code3);
        assertTrue(code3Copy != 0, "codeCopyWithTables should return non-zero for pattern with custom tables");

        // Verify the copy with tables works for matching
        final var matchData3 = api.matchDataCreateFromPattern(code3Copy, 0);
        assertTrue(matchData3 != 0, "Failed to create match data from copied code with tables");

        final var result3 = api.match(code3Copy, "hello world", 0, 0, matchData3, 0);
        assertTrue(result3 > 0, "Match should succeed with copied code with tables");

        api.matchDataFree(matchData3);
        api.codeFree(code3Copy);
        api.codeFree(code3);
        api.compileContextFree(ccontext);
        api.maketablesFree(0, tables);
    }

    @Test
    public void testSerializeEncode() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Test 1: Basic serialization of a single pattern
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Failed to compile pattern");

        final var serializedBytes = new long[1];
        final var serializedSize = new long[1];

        final var result = api.serializeEncode(new long[]{code}, 1, serializedBytes, serializedSize, 0);
        assertEquals(1, result, "serializeEncode should return 1 for single pattern");
        assertTrue(serializedBytes[0] != 0, "serializedBytes should be non-null");
        assertTrue(serializedSize[0] > 0, "serializedSize should be positive");

        // Read the serialized data to verify it's accessible
        final var bytes = api.readBytes(serializedBytes[0], (int) serializedSize[0]);
        assertEquals(serializedSize[0], bytes.length, "Read bytes should match serialized size");

        api.serializeFree(serializedBytes[0]);
        api.codeFree(code);

        // Test 2: Serialization of multiple patterns
        final var code1 = api.compile("pattern1", 0, errorcode, erroroffset, 0);
        assertTrue(code1 != 0, "Failed to compile pattern1");

        final var code2 = api.compile("pattern2", 0, errorcode, erroroffset, 0);
        assertTrue(code2 != 0, "Failed to compile pattern2");

        final var serializedBytes2 = new long[1];
        final var serializedSize2 = new long[1];

        final var result2 = api.serializeEncode(new long[]{code1, code2}, 2, serializedBytes2, serializedSize2, 0);
        assertEquals(2, result2, "serializeEncode should return 2 for two patterns");
        assertTrue(serializedBytes2[0] != 0, "serializedBytes should be non-null for multiple patterns");
        assertTrue(serializedSize2[0] > serializedSize[0], "Multiple patterns should have larger serialized size");

        api.serializeFree(serializedBytes2[0]);
        api.codeFree(code1);
        api.codeFree(code2);

        // Test 3: Pattern with capturing groups
        final var code3 = api.compile("(\\w+)@(\\w+\\.\\w+)", 0, errorcode, erroroffset, 0);
        assertTrue(code3 != 0, "Failed to compile pattern with capturing groups");

        final var serializedBytes3 = new long[1];
        final var serializedSize3 = new long[1];

        final var result3 = api.serializeEncode(new long[]{code3}, 1, serializedBytes3, serializedSize3, 0);
        assertEquals(1, result3, "serializeEncode should return 1 for pattern with capturing groups");
        assertTrue(serializedBytes3[0] != 0, "serializedBytes should be non-null");
        assertTrue(serializedSize3[0] > 0, "serializedSize should be positive");

        api.serializeFree(serializedBytes3[0]);
        api.codeFree(code3);

        // Test 4: Invalid input - null codes array
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeEncode(null, 1, new long[1], new long[1], 0);
        }, "Should throw IllegalArgumentException for null codes array");

        // Test 5: Invalid input - zero numberOfCodes
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeEncode(new long[]{0}, 0, new long[1], new long[1], 0);
        }, "Should throw IllegalArgumentException for zero numberOfCodes");

        // Test 6: Invalid input - negative numberOfCodes
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeEncode(new long[]{0}, -1, new long[1], new long[1], 0);
        }, "Should throw IllegalArgumentException for negative numberOfCodes");

        // Test 7: Invalid input - null serializedBytes
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeEncode(new long[]{0}, 1, null, new long[1], 0);
        }, "Should throw IllegalArgumentException for null serializedBytes");

        // Test 8: Invalid input - null serializedSize
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeEncode(new long[]{0}, 1, new long[1], null, 0);
        }, "Should throw IllegalArgumentException for null serializedSize");
    }

    @Test
    public void testSerializeDecode() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Test 1: Serialize and decode a single pattern
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Failed to compile pattern");

        final var serializedBytes = new long[1];
        final var serializedSize = new long[1];

        final var encodeResult = api.serializeEncode(new long[]{code}, 1, serializedBytes, serializedSize, 0);
        assertEquals(1, encodeResult, "serializeEncode should return 1 for single pattern");

        // Read the serialized data
        final var bytes = api.readBytes(serializedBytes[0], (int) serializedSize[0]);
        assertEquals(serializedSize[0], bytes.length, "Read bytes should match serialized size");

        // Decode the pattern
        final var decodedCodes = new long[1];
        final var decodeResult = api.serializeDecode(decodedCodes, 1, bytes, 0);
        assertEquals(1, decodeResult, "serializeDecode should return 1 for single pattern");
        assertTrue(decodedCodes[0] != 0, "Decoded code should be non-null");

        // Verify the decoded pattern works by matching
        final var matchData = api.matchDataCreateFromPattern(decodedCodes[0], 0);
        assertTrue(matchData != 0, "Failed to create match data from decoded pattern");

        final var matchResult = api.match(decodedCodes[0], "hello world", 0, 0, matchData, 0);
        assertTrue(matchResult > 0, "Decoded pattern should match");

        api.matchDataFree(matchData);
        api.codeFree(decodedCodes[0]);
        api.serializeFree(serializedBytes[0]);
        api.codeFree(code);

        // Test 2: Serialize and decode multiple patterns
        final var code1 = api.compile("pattern1", 0, errorcode, erroroffset, 0);
        assertTrue(code1 != 0, "Failed to compile pattern1");

        final var code2 = api.compile("pattern2", 0, errorcode, erroroffset, 0);
        assertTrue(code2 != 0, "Failed to compile pattern2");

        final var serializedBytes2 = new long[1];
        final var serializedSize2 = new long[1];

        final var codes12 = new long[]{code1, code2};
        final var encodeResult2 = api.serializeEncode(codes12, 2, serializedBytes2, serializedSize2, 0);
        assertEquals(2, encodeResult2, "serializeEncode should return 2 for two patterns");

        final var bytes2 = api.readBytes(serializedBytes2[0], (int) serializedSize2[0]);

        final var decodedCodes2 = new long[2];
        final var decodeResult2 = api.serializeDecode(decodedCodes2, 2, bytes2, 0);
        assertEquals(2, decodeResult2, "serializeDecode should return 2 for two patterns");
        assertTrue(decodedCodes2[0] != 0, "First decoded code should be non-null");
        assertTrue(decodedCodes2[1] != 0, "Second decoded code should be non-null");

        // Verify both decoded patterns work
        final var matchData1 = api.matchDataCreateFromPattern(decodedCodes2[0], 0);
        final var match1 = api.match(decodedCodes2[0], "pattern1 test", 0, 0, matchData1, 0);
        assertTrue(match1 > 0, "First decoded pattern should match");
        api.matchDataFree(matchData1);

        final var matchData2 = api.matchDataCreateFromPattern(decodedCodes2[1], 0);
        final var match2 = api.match(decodedCodes2[1], "pattern2 test", 0, 0, matchData2, 0);
        assertTrue(match2 > 0, "Second decoded pattern should match");
        api.matchDataFree(matchData2);

        api.codeFree(decodedCodes2[0]);
        api.codeFree(decodedCodes2[1]);
        api.serializeFree(serializedBytes2[0]);
        api.codeFree(code1);
        api.codeFree(code2);

        // Test 3: Decode with capturing groups preserved
        final var code3 = api.compile("(\\w+)@(\\w+\\.\\w+)", 0, errorcode, erroroffset, 0);
        assertTrue(code3 != 0, "Failed to compile pattern with capturing groups");

        final var serializedBytes3 = new long[1];
        final var serializedSize3 = new long[1];

        final var encodeResult3 = api.serializeEncode(new long[]{code3}, 1, serializedBytes3, serializedSize3, 0);
        assertEquals(1, encodeResult3, "serializeEncode should return 1");

        final var bytes3 = api.readBytes(serializedBytes3[0], (int) serializedSize3[0]);

        final var decodedCodes3 = new long[1];
        final var decodeResult3 = api.serializeDecode(decodedCodes3, 1, bytes3, 0);
        assertEquals(1, decodeResult3, "serializeDecode should return 1");

        // Verify capturing groups work in decoded pattern
        final var matchData3 = api.matchDataCreateFromPattern(decodedCodes3[0], 0);
        final var match3 = api.match(decodedCodes3[0], "user@example.com", 0, 0, matchData3, 0);
        assertTrue(match3 > 0, "Decoded pattern with groups should match");

        // Check that we got the expected number of groups
        final var ovectorCount = api.getOvectorCount(matchData3);
        assertEquals(3, ovectorCount, "Should have 3 groups (full match + 2 capturing groups)");

        api.matchDataFree(matchData3);
        api.codeFree(decodedCodes3[0]);
        api.serializeFree(serializedBytes3[0]);
        api.codeFree(code3);

        // Test 4: Invalid input - null codes array
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeDecode(null, 1, new byte[10], 0);
        }, "Should throw IllegalArgumentException for null codes array");

        // Test 5: Invalid input - zero numberOfCodes
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeDecode(new long[1], 0, new byte[10], 0);
        }, "Should throw IllegalArgumentException for zero numberOfCodes");

        // Test 6: Invalid input - negative numberOfCodes
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeDecode(new long[1], -1, new byte[10], 0);
        }, "Should throw IllegalArgumentException for negative numberOfCodes");

        // Test 7: Invalid input - null bytes array
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeDecode(new long[1], 1, null, 0);
        }, "Should throw IllegalArgumentException for null bytes array");

        // Test 8: Invalid input - codes array too small
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeDecode(new long[1], 2, new byte[10], 0);
        }, "Should throw IllegalArgumentException when codes array is smaller than numberOfCodes");
    }

    @Test
    public void testSerializeFree() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Test 1: Free serialized data from a single pattern
        final var code = api.compile("test", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Failed to compile pattern");

        final var serializedBytes = new long[1];
        final var serializedSize = new long[1];

        final var result = api.serializeEncode(new long[]{code}, 1, serializedBytes, serializedSize, 0);
        assertEquals(1, result, "serializeEncode should return 1 for single pattern");
        assertTrue(serializedBytes[0] != 0, "serializedBytes should be non-null");

        // Free the serialized data - should not throw
        api.serializeFree(serializedBytes[0]);

        api.codeFree(code);

        // Test 2: Free with null pointer (0) - should not throw
        api.serializeFree(0);

        // Test 3: Free serialized data from multiple patterns
        final var code1 = api.compile("pattern1", 0, errorcode, erroroffset, 0);
        final var code2 = api.compile("pattern2", 0, errorcode, erroroffset, 0);
        assertTrue(code1 != 0, "Failed to compile pattern1");
        assertTrue(code2 != 0, "Failed to compile pattern2");

        final var serializedBytes2 = new long[1];
        final var serializedSize2 = new long[1];

        final var result2 = api.serializeEncode(new long[]{code1, code2}, 2, serializedBytes2, serializedSize2, 0);
        assertEquals(2, result2, "serializeEncode should return 2 for two patterns");

        // Free the serialized data - should not throw
        api.serializeFree(serializedBytes2[0]);

        api.codeFree(code1);
        api.codeFree(code2);
    }

    @Test
    public void testSerializeGetNumberOfCodes() {
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Test 1: Get count from serialized single pattern
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Failed to compile pattern");

        final var serializedBytes = new long[1];
        final var serializedSize = new long[1];

        final var encodeResult = api.serializeEncode(new long[]{code}, 1, serializedBytes, serializedSize, 0);
        assertEquals(1, encodeResult, "serializeEncode should return 1 for single pattern");

        final var bytes = api.readBytes(serializedBytes[0], (int) serializedSize[0]);
        final var count = api.serializeGetNumberOfCodes(bytes);
        assertEquals(1, count, "serializeGetNumberOfCodes should return 1 for single pattern");

        api.serializeFree(serializedBytes[0]);
        api.codeFree(code);

        // Test 2: Get count from serialized multiple patterns
        final var code1 = api.compile("pattern1", 0, errorcode, erroroffset, 0);
        assertTrue(code1 != 0, "Failed to compile pattern1");

        final var code2 = api.compile("pattern2", 0, errorcode, erroroffset, 0);
        assertTrue(code2 != 0, "Failed to compile pattern2");

        final var code3 = api.compile("pattern3", 0, errorcode, erroroffset, 0);
        assertTrue(code3 != 0, "Failed to compile pattern3");

        final var serializedBytes2 = new long[1];
        final var serializedSize2 = new long[1];

        final var encodeResult2 = api.serializeEncode(
                new long[]{code1, code2, code3}, 3, serializedBytes2, serializedSize2, 0);
        assertEquals(3, encodeResult2, "serializeEncode should return 3 for three patterns");

        final var bytes2 = api.readBytes(serializedBytes2[0], (int) serializedSize2[0]);
        final var count2 = api.serializeGetNumberOfCodes(bytes2);
        assertEquals(3, count2, "serializeGetNumberOfCodes should return 3 for three patterns");

        api.serializeFree(serializedBytes2[0]);
        api.codeFree(code1);
        api.codeFree(code2);
        api.codeFree(code3);

        // Test 3: Invalid input - null bytes array
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeGetNumberOfCodes(null);
        }, "Should throw IllegalArgumentException for null bytes array");

        // Test 4: Invalid input - corrupted/invalid data (should return ERROR_BADMAGIC)
        final var invalidBytes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        final var invalidResult = api.serializeGetNumberOfCodes(invalidBytes);
        assertEquals(IPcre2.ERROR_BADMAGIC, invalidResult,
                "serializeGetNumberOfCodes should return ERROR_BADMAGIC for invalid data");
    }

    @Test
    public void setCalloutDisablesCallouts() {
        // Create match context
        long matchCtx = api.matchContextCreate(0);
        assertTrue(matchCtx != 0, "Match context creation should succeed");

        // Set callout to 0 (disable callouts) - should always return 0
        int result = api.setCallout(matchCtx, 0, 0);
        assertEquals(0, result, "setCallout should return 0");

        // Clean up
        api.matchContextFree(matchCtx);
    }

    @Test
    public void maketablesAndFree() {
        // Test 1: Create character tables with default general context (0)
        long tables = api.maketables(0);
        assertTrue(tables != 0, "maketables should return non-zero pointer for character tables");

        // Free the tables with default general context
        api.maketablesFree(0, tables);

        // Test 2: Create and free with null tables pointer (should be no-op)
        api.maketablesFree(0, 0);
    }

    @Test
    public void maketablesWithGeneralContext() {
        // Create a general context
        long gcontext = api.generalContextCreate(0, 0, 0);
        assertTrue(gcontext != 0, "General context creation should succeed");

        // Create character tables with the general context
        long tables = api.maketables(gcontext);
        assertTrue(tables != 0, "maketables should return non-zero pointer for character tables");

        // Free the tables using the same general context
        api.maketablesFree(gcontext, tables);

        // Free the general context
        api.generalContextFree(gcontext);
    }

    @Test
    public void setCharacterTablesWithCustomTables() {
        // Create compile context
        long ccontext = api.compileContextCreate(0);
        assertTrue(ccontext != 0, "Compile context creation should succeed");

        // Create custom character tables
        long tables = api.maketables(0);
        assertTrue(tables != 0, "maketables should return non-zero pointer for character tables");

        // Set custom character tables - should always return 0
        int result = api.setCharacterTables(ccontext, tables);
        assertEquals(0, result, "setCharacterTables should return 0");

        // Compile a pattern using the compile context with custom tables
        int[] errorcode = new int[1];
        long[] erroroffset = new long[1];
        long code = api.compile("[a-z]+", 0, errorcode, erroroffset, ccontext);
        assertTrue(code != 0, "Pattern compilation with custom tables should succeed");

        // Clean up
        api.codeFree(code);
        api.compileContextFree(ccontext);
        api.maketablesFree(0, tables);
    }

    @Test
    public void setCharacterTablesWithNullTables() {
        // Create compile context
        long ccontext = api.compileContextCreate(0);
        assertTrue(ccontext != 0, "Compile context creation should succeed");

        // Set tables to 0 (use default tables) - should always return 0
        int result = api.setCharacterTables(ccontext, 0);
        assertEquals(0, result, "setCharacterTables with 0 should return 0");

        // Compile a pattern using the compile context with default tables
        int[] errorcode = new int[1];
        long[] erroroffset = new long[1];
        long code = api.compile("[a-z]+", 0, errorcode, erroroffset, ccontext);
        assertTrue(code != 0, "Pattern compilation with default tables should succeed");

        // Clean up
        api.codeFree(code);
        api.compileContextFree(ccontext);
    }

    @Test
    public void testJitFreeUnusedMemory() {
        // Test 1: Call with null context (0) - should not throw
        api.jitFreeUnusedMemory(0);

        // Test 2: Call with valid general context - should not throw
        long gcontext = api.generalContextCreate(0, 0, 0);
        assertTrue(gcontext != 0, "General context creation should succeed");
        api.jitFreeUnusedMemory(gcontext);
        api.generalContextFree(gcontext);

        // Test 3: Call after JIT compilation (if JIT is available)
        int[] errorcode = new int[1];
        long[] erroroffset = new long[1];
        long code = api.compile("test\\d+", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern compilation should succeed");

        // Attempt JIT compilation - may fail if JIT not available, which is OK
        int jitResult = api.jitCompile(code, IPcre2.JIT_COMPLETE);
        // jitResult == 0 means success, negative means JIT not available or error

        // Free unused JIT memory - should not throw regardless of JIT availability
        api.jitFreeUnusedMemory(0);

        api.codeFree(code);

        // Test 4: Multiple calls should be safe (idempotent)
        api.jitFreeUnusedMemory(0);
        api.jitFreeUnusedMemory(0);
    }

    @Test
    public void patternConvertGlob() {
        // Test converting a glob pattern to PCRE2
        // The glob pattern "*.txt" should be converted to a PCRE2 pattern

        // First, get the required buffer size by passing buffer[0] = 0
        long[] buffer = new long[]{0};
        long[] blength = new long[]{0};

        int result = api.patternConvert(
                "*.txt",
                IPcre2.CONVERT_GLOB,
                buffer,
                blength,
                0
        );
        assertEquals(0, result, "patternConvert should return 0 on success");
        assertTrue(buffer[0] != 0, "Buffer should contain a pointer after conversion");
        assertTrue(blength[0] > 0, "blength should contain the pattern length");

        // The buffer was allocated by PCRE2, so we need to free it
        api.convertedPatternFree(buffer[0]);
    }

    @Test
    public void patternConvertPosixExtended() {
        // Test converting a POSIX Extended Regular Expression to PCRE2
        // The POSIX ERE "^[a-z]+$" should be converted

        long[] buffer = new long[]{0};
        long[] blength = new long[]{0};

        int result = api.patternConvert(
                "^[a-z]+$",
                IPcre2.CONVERT_POSIX_EXTENDED,
                buffer,
                blength,
                0
        );
        assertEquals(0, result, "patternConvert should return 0 on success");
        assertTrue(buffer[0] != 0, "Buffer should contain a pointer after conversion");
        assertTrue(blength[0] > 0, "blength should contain the pattern length");

        api.convertedPatternFree(buffer[0]);
    }

    @Test
    public void patternConvertWithContext() {
        // Test using a convert context
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        long[] buffer = new long[]{0};
        long[] blength = new long[]{0};

        int result = api.patternConvert(
                "file?.log",
                IPcre2.CONVERT_GLOB,
                buffer,
                blength,
                cvcontext
        );
        assertEquals(0, result, "patternConvert should return 0 on success");
        assertTrue(buffer[0] != 0, "Buffer should contain a pointer after conversion");

        api.convertedPatternFree(buffer[0]);
        api.convertContextFree(cvcontext);
    }

    @Test
    public void convertedPatternFreeNull() {
        // Test that convertedPatternFree handles null pointer gracefully
        api.convertedPatternFree(0);
    }

    @Test
    public void patternConvertNullPatternThrows() {
        long[] buffer = new long[]{0};
        long[] blength = new long[]{0};

        assertThrows(IllegalArgumentException.class, () ->
                api.patternConvert(null, IPcre2.CONVERT_GLOB, buffer, blength, 0)
        );
    }

    @Test
    public void patternConvertNullBufferThrows() {
        long[] blength = new long[]{0};

        assertThrows(IllegalArgumentException.class, () ->
                api.patternConvert("*.txt", IPcre2.CONVERT_GLOB, null, blength, 0)
        );
    }

    @Test
    public void patternConvertNullBlengthThrows() {
        long[] buffer = new long[]{0};

        assertThrows(IllegalArgumentException.class, () ->
                api.patternConvert("*.txt", IPcre2.CONVERT_GLOB, buffer, null, 0)
        );
    }

    @Test
    public void setGlobEscapeValid() {
        // Test setting a valid escape character (backslash)
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        int result = api.setGlobEscape(cvcontext, '\\');
        assertEquals(0, result, "setGlobEscape should return 0 for valid punctuation character");

        api.convertContextFree(cvcontext);
    }

    @Test
    public void setGlobEscapeDisable() {
        // Test disabling escape processing by setting to 0
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        int result = api.setGlobEscape(cvcontext, 0);
        assertEquals(0, result, "setGlobEscape should return 0 when disabling escapes");

        api.convertContextFree(cvcontext);
    }

    @Test
    public void setGlobEscapeInvalid() {
        // Test setting an invalid escape character (non-punctuation)
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        // 'a' is not a punctuation character, should return ERROR_BADDATA
        int result = api.setGlobEscape(cvcontext, 'a');
        assertEquals(IPcre2.ERROR_BADDATA, result, "setGlobEscape should return ERROR_BADDATA for invalid character");

        api.convertContextFree(cvcontext);
    }

    @Test
    public void setGlobSeparatorForwardSlash() {
        // Test setting forward slash as separator (valid)
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        int result = api.setGlobSeparator(cvcontext, '/');
        assertEquals(0, result, "setGlobSeparator should return 0 for forward slash");

        api.convertContextFree(cvcontext);
    }

    @Test
    public void setGlobSeparatorBackslash() {
        // Test setting backslash as separator (valid)
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        int result = api.setGlobSeparator(cvcontext, '\\');
        assertEquals(0, result, "setGlobSeparator should return 0 for backslash");

        api.convertContextFree(cvcontext);
    }

    @Test
    public void setGlobSeparatorDot() {
        // Test setting dot as separator (valid)
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        int result = api.setGlobSeparator(cvcontext, '.');
        assertEquals(0, result, "setGlobSeparator should return 0 for dot");

        api.convertContextFree(cvcontext);
    }

    @Test
    public void setGlobSeparatorInvalid() {
        // Test setting an invalid separator character
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        // 'a' is not a valid separator, should return ERROR_BADDATA
        int result = api.setGlobSeparator(cvcontext, 'a');
        assertEquals(IPcre2.ERROR_BADDATA, result, "setGlobSeparator should return ERROR_BADDATA for invalid char");

        api.convertContextFree(cvcontext);
    }

}
