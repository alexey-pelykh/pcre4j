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

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.stream.Stream;

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

    private static IPcre2 loadBackend(String className) {
        try {
            return (IPcre2) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Backend " + className + " not found on classpath", e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException
                 | NoSuchMethodException e) {
            throw new RuntimeException("Failed to instantiate backend " + className, e);
        }
    }

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(loadBackend("org.pcre4j.jna.Pcre2")),
                Arguments.of(loadBackend("org.pcre4j.ffm.Pcre2"))
        );
    }

    // --- backRefMax ---

    @ParameterizedTest
    @MethodSource("parameters")
    void backRefMaxNoBackrefs(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        assertEquals(0, code.backRefMax());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void backRefMaxWithBackrefs(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)\\2");
        assertEquals(2, code.backRefMax());
    }

    // --- argOptions ---

    @ParameterizedTest
    @MethodSource("parameters")
    void argOptionsDefault(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var options = code.argOptions();
        assertNotNull(options);
        // Default compile with no options should return empty or just UTF
        assertFalse(options.contains(Pcre2CompileOption.CASELESS));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void argOptionsWithCaseless(IPcre2 api) {
        var code = new Pcre2Code(api, "test", EnumSet.of(Pcre2CompileOption.CASELESS));
        var options = code.argOptions();
        assertTrue(options.contains(Pcre2CompileOption.CASELESS));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void argOptionsWithMultipleOptions(IPcre2 api) {
        var code = new Pcre2Code(api, "test",
                EnumSet.of(Pcre2CompileOption.CASELESS, Pcre2CompileOption.DOTALL));
        var options = code.argOptions();
        assertTrue(options.contains(Pcre2CompileOption.CASELESS));
        assertTrue(options.contains(Pcre2CompileOption.DOTALL));
    }

    // --- captureCount ---

    @ParameterizedTest
    @MethodSource("parameters")
    void captureCountNoGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        assertEquals(0, code.captureCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void captureCountWithGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)(c)");
        assertEquals(3, code.captureCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void captureCountNonCapturing(IPcre2 api) {
        var code = new Pcre2Code(api, "(?:a)(b)");
        assertEquals(1, code.captureCount());
    }

    // --- bsr ---

    @ParameterizedTest
    @MethodSource("parameters")
    void bsrReturnsValidValue(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var bsr = code.bsr();
        assertNotNull(bsr);
        assertTrue(bsr == Pcre2Bsr.UNICODE || bsr == Pcre2Bsr.ANYCRLF);
    }

    // --- frameSize ---

    @ParameterizedTest
    @MethodSource("parameters")
    void frameSizePositive(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)");
        assertTrue(code.frameSize() > 0);
    }

    // --- firstCodeType ---

    @ParameterizedTest
    @MethodSource("parameters")
    void firstCodeTypeAnchored(IPcre2 api) {
        // Pattern anchored with ^ and starting with a literal should return 1 (first code unit set)
        var code = new Pcre2Code(api, "^test");
        assertEquals(1, code.firstCodeType());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void firstCodeTypeNoFixedStart(IPcre2 api) {
        // Pattern starting with .* is implicitly anchored, returns 2
        var code = new Pcre2Code(api, ".*test");
        assertEquals(2, code.firstCodeType());
    }

    // --- hasBackslashC ---

    @ParameterizedTest
    @MethodSource("parameters")
    void hasBackslashCFalse(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertFalse(code.hasBackslashC());
    }

    // --- hasCrOrLf ---

    @ParameterizedTest
    @MethodSource("parameters")
    void hasCrOrLfFalse(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertFalse(code.hasCrOrLf());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void hasCrOrLfTrue(IPcre2 api) {
        var code = new Pcre2Code(api, "test\\r");
        // The pattern has an explicit CR
        assertTrue(code.hasCrOrLf());
    }

    // --- jChanged ---

    @ParameterizedTest
    @MethodSource("parameters")
    void jChangedFalse(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertFalse(code.jChanged());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void jChangedTrue(IPcre2 api) {
        var code = new Pcre2Code(api, "(?J)(?<name>a)(?<name>b)");
        assertTrue(code.jChanged());
    }

    // --- jitSize ---

    @ParameterizedTest
    @MethodSource("parameters")
    void jitSizeZeroForNonJit(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertEquals(0, code.jitSize());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void jitSizePositiveForJit(IPcre2 api) {
        var code = new Pcre2JitCode(api, "test", null, null, null);
        assertTrue(code.jitSize() > 0);
    }

    // --- matchEmpty ---

    @ParameterizedTest
    @MethodSource("parameters")
    void matchEmptyFalse(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertFalse(code.matchEmpty());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchEmptyTrue(IPcre2 api) {
        var code = new Pcre2Code(api, "a*");
        assertTrue(code.matchEmpty());
    }

    // --- maxLookBehind ---

    @ParameterizedTest
    @MethodSource("parameters")
    void maxLookBehindZero(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertEquals(0, code.maxLookBehind());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void maxLookBehindPositive(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<=abc)test");
        assertEquals(3, code.maxLookBehind());
    }

    // --- minLength ---

    @ParameterizedTest
    @MethodSource("parameters")
    void minLengthSimple(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertEquals(4, code.minLength());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void minLengthWithOptional(IPcre2 api) {
        // "te?st" matches "tst" (3 chars) or "test" (4 chars), minimum is 3
        var code = new Pcre2Code(api, "te?st");
        assertEquals(3, code.minLength());
    }

    // --- nameCount ---

    @ParameterizedTest
    @MethodSource("parameters")
    void nameCountZero(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)");
        assertEquals(0, code.nameCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void nameCountWithNamedGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>a)(?<second>b)");
        assertEquals(2, code.nameCount());
    }

    // --- newline ---

    @ParameterizedTest
    @MethodSource("parameters")
    void newlineReturnsValidValue(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var newline = code.newline();
        assertNotNull(newline);
    }

    // --- nameEntrySize ---

    @ParameterizedTest
    @MethodSource("parameters")
    void nameEntrySizeWithNamedGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>a)");
        assertTrue(code.nameEntrySize() > 0);
    }

    // --- nameTable ---

    @ParameterizedTest
    @MethodSource("parameters")
    void nameTableEmpty(IPcre2 api) {
        var code = new Pcre2Code(api, "(a)(b)");
        assertEquals(0, code.nameTable().length);
    }

    @ParameterizedTest
    @MethodSource("parameters")
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

    // --- size ---

    @ParameterizedTest
    @MethodSource("parameters")
    void sizePositive(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertTrue(code.size() > 0);
    }

    // --- api() and handle() ---

    @ParameterizedTest
    @MethodSource("parameters")
    void apiReturnsNonNull(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertNotNull(code.api());
        assertEquals(api, code.api());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void handleReturnsNonZero(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        assertTrue(code.handle() != 0);
    }

    // --- groupNumberFromName ---

    @ParameterizedTest
    @MethodSource("parameters")
    void groupNumberFromNameValid(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>a)(?<second>b)");
        assertEquals(1, code.groupNumberFromName("first"));
        assertEquals(2, code.groupNumberFromName("second"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void groupNumberFromNameNullThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>a)");
        assertThrows(IllegalArgumentException.class, () -> code.groupNumberFromName(null));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void groupNumberFromNameNonexistentThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>a)");
        assertThrows(Pcre2NoSubstringError.class, () -> code.groupNumberFromName("nonexistent"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void groupNumberFromNameDuplicateThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>a)|(?<name>b)",
                EnumSet.of(Pcre2CompileOption.DUPNAMES));
        assertThrows(Pcre2NoUniqueSubstringError.class, () -> code.groupNumberFromName("name"));
    }

    // --- scanNametable ---

    @ParameterizedTest
    @MethodSource("parameters")
    void scanNametableValid(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>a)(?<second>b)");
        var groups = code.scanNametable("first");
        assertArrayEquals(new int[]{1}, groups);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void scanNametableDuplicateNames(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>a)|(?<name>b)",
                EnumSet.of(Pcre2CompileOption.DUPNAMES));
        var groups = code.scanNametable("name");
        assertEquals(2, groups.length);
        assertEquals(1, groups[0]);
        assertEquals(2, groups[1]);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void scanNametableNullThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>a)");
        assertThrows(IllegalArgumentException.class, () -> code.scanNametable(null));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void scanNametableNonexistentThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<name>a)");
        assertThrows(Pcre2NoSubstringError.class, () -> code.scanNametable("nonexistent"));
    }
}
