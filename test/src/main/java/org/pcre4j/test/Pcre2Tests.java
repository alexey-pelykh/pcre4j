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

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

}
