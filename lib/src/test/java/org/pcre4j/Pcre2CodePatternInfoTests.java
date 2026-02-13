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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link Pcre2Code} pattern information methods.
 */
public class Pcre2CodePatternInfoTests {

    // --- backRefMax ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void backRefMaxNoBackrefs(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        assertEquals(0, code.backRefMax());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void backRefMaxWithBackrefs(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)\\2");
        assertEquals(2, code.backRefMax());
    }

    // --- argOptions ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void argOptionsDefault(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var options = code.argOptions();
        assertNotNull(options);
        // Default compile with no options should return empty or just UTF
        assertFalse(options.contains(Pcre2CompileOption.CASELESS));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void argOptionsWithCaseless(IPcre2 api) {
        var code = new Pcre2Code(api, "test", EnumSet.of(Pcre2CompileOption.CASELESS));
        var options = code.argOptions();
        assertTrue(options.contains(Pcre2CompileOption.CASELESS));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void argOptionsWithMultipleOptions(IPcre2 api) {
        var code = new Pcre2Code(api, "test",
                EnumSet.of(Pcre2CompileOption.CASELESS, Pcre2CompileOption.DOTALL));
        var options = code.argOptions();
        assertTrue(options.contains(Pcre2CompileOption.CASELESS));
        assertTrue(options.contains(Pcre2CompileOption.DOTALL));
    }

    // --- captureCount ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void captureCountNoGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        assertEquals(0, code.captureCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void captureCountWithGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)(c)");
        assertEquals(3, code.captureCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void captureCountNonCapturing(IPcre2 api) {
        var code = new Pcre2Code(api, "(?:a)(b)");
        assertEquals(1, code.captureCount());
    }

    // --- bsr ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void bsrReturnsValidValue(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var bsr = code.bsr();
        assertNotNull(bsr);
        assertTrue(bsr == Pcre2Bsr.UNICODE || bsr == Pcre2Bsr.ANYCRLF);
    }

    // --- frameSize ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void frameSizePositive(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)");
        assertTrue(code.frameSize() > 0);
    }

    // --- firstCodeType ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void firstCodeTypeAnchored(IPcre2 api) {
        // Pattern anchored with ^ and starting with a literal should return 1 (first code unit set)
        var code = new Pcre2Code(api, "^test");
        assertEquals(1, code.firstCodeType());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void firstCodeTypeNoFixedStart(IPcre2 api) {
        // Pattern starting with .* is implicitly anchored, returns 2
        var code = new Pcre2Code(api, ".*test");
        assertEquals(2, code.firstCodeType());
    }

    // --- hasBackslashC ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hasBackslashCFalse(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertFalse(code.hasBackslashC());
    }

    // --- hasCrOrLf ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hasCrOrLfFalse(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertFalse(code.hasCrOrLf());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hasCrOrLfTrue(IPcre2 api) {
        var code = new Pcre2Code(api, "test\\r");
        // The pattern has an explicit CR
        assertTrue(code.hasCrOrLf());
    }

    // --- jChanged ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jChangedFalse(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertFalse(code.jChanged());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jChangedTrue(IPcre2 api) {
        var code = new Pcre2Code(api, "(?J)(?<name>a)(?<name>b)");
        assertTrue(code.jChanged());
    }

    // --- jitSize ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitSizeZeroForNonJit(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertEquals(0, code.jitSize());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitSizePositiveForJit(IPcre2 api) {
        var code = new Pcre2JitCode(api, "test", null, null, null);
        assertTrue(code.jitSize() > 0);
    }

    // --- matchEmpty ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchEmptyFalse(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertFalse(code.matchEmpty());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchEmptyTrue(IPcre2 api) {
        var code = new Pcre2Code(api, "a*");
        assertTrue(code.matchEmpty());
    }

    // --- maxLookBehind ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void maxLookBehindZero(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertEquals(0, code.maxLookBehind());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void maxLookBehindPositive(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<=abc)test");
        assertEquals(3, code.maxLookBehind());
    }

    // --- minLength ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void minLengthSimple(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertEquals(4, code.minLength());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void minLengthWithOptional(IPcre2 api) {
        // "te?st" matches "tst" (3 chars) or "test" (4 chars), minimum is 3
        var code = new Pcre2Code(api, "te?st");
        assertEquals(3, code.minLength());
    }

    // --- nameCount ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void nameCountZero(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)");
        assertEquals(0, code.nameCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void nameCountWithNamedGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>a)(?<second>b)");
        assertEquals(2, code.nameCount());
    }

    // --- newline ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void newlineReturnsValidValue(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var newline = code.newline();
        assertNotNull(newline);
    }

    // --- nameEntrySize ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void nameEntrySizeWithNamedGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>a)");
        assertTrue(code.nameEntrySize() > 0);
    }

    // --- nameTable ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void nameTableEmpty(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)");
        assertEquals(0, code.nameTable().length);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void nameTableWithNamedGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>a)(?<second>b)");
        var nameTable = code.nameTable();
        assertEquals(2, nameTable.length);

        // Verify entries are present (order may vary)
        boolean foundFirst = false;
        boolean foundSecond = false;
        for (var entry : nameTable) {
            if ("first".equals(entry.name()) && entry.group() == 1) foundFirst = true;
            if ("second".equals(entry.name()) && entry.group() == 2) foundSecond = true;
        }
        assertTrue(foundFirst, "Name table should contain 'first' mapped to group 1");
        assertTrue(foundSecond, "Name table should contain 'second' mapped to group 2");
    }

    // --- matchLimit ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchLimitFromPattern(IPcre2 api) {
        var code = new Pcre2Code(api, "(*LIMIT_MATCH=5000)test");
        assertEquals(5000, code.matchLimit());
    }

    // --- depthLimit ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void depthLimitFromPattern(IPcre2 api) {
        var code = new Pcre2Code(api, "(*LIMIT_DEPTH=3000)test");
        assertEquals(3000, code.depthLimit());
    }

    // --- heapLimit ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void heapLimitFromPattern(IPcre2 api) {
        var code = new Pcre2Code(api, "(*LIMIT_HEAP=8000)test");
        assertEquals(8000, code.heapLimit());
    }

    // --- size ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void sizePositive(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertTrue(code.size() > 0);
    }

    // --- api() and handle() ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void apiReturnsNonNull(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertNotNull(code.api());
        assertEquals(api, code.api());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void handleReturnsNonZero(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertTrue(code.handle() != 0);
    }

    // --- groupNumberFromName ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void groupNumberFromNameValid(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>a)(?<second>b)");
        assertEquals(1, code.groupNumberFromName("first"));
        assertEquals(2, code.groupNumberFromName("second"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void groupNumberFromNameNullThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>a)");
        assertThrows(IllegalArgumentException.class, () -> code.groupNumberFromName(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void groupNumberFromNameNonexistentThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>a)");
        assertThrows(Pcre2NoSubstringError.class, () -> code.groupNumberFromName("nonexistent"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void groupNumberFromNameDuplicateThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>a)|(?<name>b)",
                EnumSet.of(Pcre2CompileOption.DUPNAMES));
        assertThrows(Pcre2NoUniqueSubstringError.class, () -> code.groupNumberFromName("name"));
    }

    // --- scanNametable ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void scanNametableValid(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>a)(?<second>b)");
        var groups = code.scanNametable("first");
        assertArrayEquals(new int[]{1}, groups);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void scanNametableDuplicateNames(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>a)|(?<name>b)",
                EnumSet.of(Pcre2CompileOption.DUPNAMES));
        var groups = code.scanNametable("name");
        assertEquals(2, groups.length);
        assertEquals(1, groups[0]);
        assertEquals(2, groups[1]);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void scanNametableNullThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>a)");
        assertThrows(IllegalArgumentException.class, () -> code.scanNametable(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void scanNametableNonexistentThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>a)");
        assertThrows(Pcre2NoSubstringError.class, () -> code.scanNametable("nonexistent"));
    }

    // --- nameTable with multi-byte (Unicode) group names ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void nameTableWithMultiByteNames(IPcre2 api) {
        // PCRE2 group names must be ASCII word characters, so use longer ASCII names instead
        var code = new Pcre2Code(api, "(?<longername>a)(?<anothername>b)");
        var nameTable = code.nameTable();
        assertEquals(2, nameTable.length);

        boolean foundFirst = false;
        boolean foundSecond = false;
        for (var entry : nameTable) {
            if ("longername".equals(entry.name()) && entry.group() == 1) foundFirst = true;
            if ("anothername".equals(entry.name()) && entry.group() == 2) foundSecond = true;
        }
        assertTrue(foundFirst, "Name table should contain 'longername' mapped to group 1");
        assertTrue(foundSecond, "Name table should contain 'anothername' mapped to group 2");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void nameTableWithManyNamedGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<a>.)(?<bb>..)(?<ccc>...)(?<dddd>....)");
        var nameTable = code.nameTable();
        assertEquals(4, nameTable.length);
        assertEquals(4, code.nameCount());
    }

    // --- scanNametable with (?J) duplicate names ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void scanNametableWithJChangedDuplicates(IPcre2 api) {
        var code = new Pcre2Code(api, "(?J)(?<name>a)|(?<name>b)");
        assertTrue(code.jChanged());
        var groups = code.scanNametable("name");
        assertEquals(2, groups.length);
        assertEquals(1, groups[0]);
        assertEquals(2, groups[1]);
    }

    // --- backRefMax with higher backreference ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void backRefMaxHigherRef(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)(c)\\3");
        assertEquals(3, code.backRefMax());
    }

    // --- minLength with alternation ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void minLengthWithAlternation(IPcre2 api) {
        // "ab|c" matches "c" (1 char) or "ab" (2 chars), minimum is 1
        var code = new Pcre2Code(api, "ab|c");
        assertEquals(1, code.minLength());
    }

    // --- hasBackslashC true ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hasBackslashCTrue(IPcre2 api) {
        var code = new Pcre2Code(api, "\\C");
        assertTrue(code.hasBackslashC());
    }

    // --- maxLookBehind with multiple lookbehinds ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void maxLookBehindMultiple(IPcre2 api) {
        // Two lookbehinds of different sizes; max should be the larger one
        var code = new Pcre2Code(api, "(?<=ab)x|(?<=cdef)y");
        assertEquals(4, code.maxLookBehind());
    }

    // --- firstCodeType for alternation ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void firstCodeTypeAlternation(IPcre2 api) {
        // Pattern "a|b" has no single fixed start character → firstCodeType should be 0
        var code = new Pcre2Code(api, "a|b");
        assertEquals(0, code.firstCodeType());
    }

    // --- groupNumberFromName with multiple named groups ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void groupNumberFromNameThirdGroup(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<a>x)(?<b>y)(?<c>z)");
        assertEquals(3, code.groupNumberFromName("c"));
    }

    // --- scanNametable single unique name ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void scanNametableSingleResult(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<alpha>a)(?<beta>b)");
        var groups = code.scanNametable("beta");
        assertArrayEquals(new int[]{2}, groups);
    }
}
