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

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
