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
import org.pcre4j.api.IPcre2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract tests for miscellaneous PCRE2 operations (code copy, tables, match data, etc.).
 *
 * @param <T> the PCRE2 API implementation type
 */
public interface Pcre2MiscContractTest<T extends IPcre2> {

    /**
     * Returns the PCRE2 API implementation to test.
     *
     * @return the PCRE2 API implementation
     */
    T getApi();

    @Test
    default void testGetMatchDataSize() {
        var api = getApi();

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
    default void testGetStartchar() {
        var api = getApi();
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
    default void testGetMark() {
        var api = getApi();
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
    default void testCodeCopy() {
        var api = getApi();
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
    default void testCodeCopyWithTables() {
        var api = getApi();
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
    default void setCalloutDisablesCallouts() {
        var api = getApi();

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
    default void maketablesAndFree() {
        var api = getApi();

        // Test 1: Create character tables with default general context (0)
        long tables = api.maketables(0);
        assertTrue(tables != 0, "maketables should return non-zero pointer for character tables");

        // Free the tables with default general context
        api.maketablesFree(0, tables);

        // Test 2: Create and free with null tables pointer (should be no-op)
        api.maketablesFree(0, 0);
    }

    @Test
    default void testPatternInfoWithLongOutput() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Compile a pattern to query info from
        final var code = api.compile("(a)(b)(c)", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Failed to compile pattern");

        // Test INFO_FRAMESIZE with long[] output
        final var frameSize = new long[1];
        final var frameSizeResult = api.patternInfo(code, IPcre2.INFO_FRAMESIZE, frameSize);
        assertEquals(0, frameSizeResult, "patternInfo(INFO_FRAMESIZE) should return 0 on success");
        assertTrue(frameSize[0] > 0, "Frame size should be positive");

        // Test INFO_SIZE with long[] output
        final var size = new long[1];
        final var sizeResult = api.patternInfo(code, IPcre2.INFO_SIZE, size);
        assertEquals(0, sizeResult, "patternInfo(INFO_SIZE) should return 0 on success");
        assertTrue(size[0] > 0, "Compiled pattern size should be positive");

        api.codeFree(code);
    }

    @Test
    default void testPatternInfoWithByteBufferOutput() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Compile a pattern with named capturing groups to produce a nametable
        final var code = api.compile("(?<first>a)(?<second>b)", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Failed to compile pattern with named groups");

        // Query the name count
        final var nameCount = new int[1];
        final var nameCountResult = api.patternInfo(code, IPcre2.INFO_NAMECOUNT, nameCount);
        assertEquals(0, nameCountResult, "patternInfo(INFO_NAMECOUNT) should return 0 on success");
        assertEquals(2, nameCount[0], "Pattern should have 2 named groups");

        // Query the name entry size (in code units)
        final var nameEntrySize = new int[1];
        final var nameEntrySizeResult = api.patternInfo(code, IPcre2.INFO_NAMEENTRYSIZE, nameEntrySize);
        assertEquals(0, nameEntrySizeResult, "patternInfo(INFO_NAMEENTRYSIZE) should return 0 on success");
        assertTrue(nameEntrySize[0] > 0, "Name entry size should be positive");

        // Calculate the nametable byte size: nameCount * nameEntrySize * codeUnitWidth
        // Use a generous buffer to accommodate any code unit width (1, 2, or 4 bytes)
        final var bufferSize = nameCount[0] * nameEntrySize[0] * 4;

        // Query the nametable with ByteBuffer output
        final var buffer = ByteBuffer.allocateDirect(bufferSize);
        buffer.order(ByteOrder.nativeOrder());
        final var nametableResult = api.patternInfo(code, IPcre2.INFO_NAMETABLE, buffer);
        assertEquals(0, nametableResult, "patternInfo(INFO_NAMETABLE) should return 0 on success");

        // The nametable contains binary data with embedded group names.
        // Verify we can find both group names by scanning the raw bytes for ASCII sequences.
        final var nameBytes = new byte[bufferSize];
        buffer.rewind();
        buffer.get(nameBytes);

        boolean foundFirst = false;
        boolean foundSecond = false;
        for (int i = 0; i < nameBytes.length; i++) {
            if (nameBytes[i] == 'f' && i + 4 < nameBytes.length
                    && nameBytes[i + 1] == 'i' && nameBytes[i + 2] == 'r'
                    && nameBytes[i + 3] == 's' && nameBytes[i + 4] == 't') {
                foundFirst = true;
            }
            if (nameBytes[i] == 's' && i + 5 < nameBytes.length
                    && nameBytes[i + 1] == 'e' && nameBytes[i + 2] == 'c'
                    && nameBytes[i + 3] == 'o' && nameBytes[i + 4] == 'n'
                    && nameBytes[i + 5] == 'd') {
                foundSecond = true;
            }
        }
        assertTrue(foundFirst, "Nametable should contain 'first'");
        assertTrue(foundSecond, "Nametable should contain 'second'");

        api.codeFree(code);
    }

    @Test
    default void testPatternInfoConsistencyAcrossOverloads() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        final var code = api.compile("(a)(b)(c)", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Failed to compile pattern");

        // Query capture count via int[] overload
        final var captureCountInt = new int[1];
        assertEquals(0, api.patternInfo(code, IPcre2.INFO_CAPTURECOUNT, captureCountInt),
                "patternInfo with int[] should succeed");
        assertEquals(3, captureCountInt[0], "Capture count should be 3");

        // Query INFO_SIZE via long[] overload (returns the compiled pattern size)
        final var sizeFromLong = new long[1];
        assertEquals(0, api.patternInfo(code, IPcre2.INFO_SIZE, sizeFromLong),
                "patternInfo with long[] should succeed");
        assertTrue(sizeFromLong[0] > 0, "Compiled pattern size from long[] should be positive");

        // Query INFO_SIZE via int[] overload (also returns the compiled pattern size, truncated to int)
        final var sizeFromInt = new int[1];
        assertEquals(0, api.patternInfo(code, IPcre2.INFO_SIZE, sizeFromInt),
                "patternInfo with int[] should succeed");
        assertTrue(sizeFromInt[0] > 0, "Compiled pattern size from int[] should be positive");

        // Both typed overloads should report the same value
        assertEquals(sizeFromInt[0], (int) sizeFromLong[0],
                "Compiled pattern size from int[] and long[] overloads should match");

        // Query INFO_FRAMESIZE via long[] overload
        final var frameSizeFromLong = new long[1];
        assertEquals(0, api.patternInfo(code, IPcre2.INFO_FRAMESIZE, frameSizeFromLong),
                "patternInfo(INFO_FRAMESIZE) with long[] should succeed");
        assertTrue(frameSizeFromLong[0] > 0, "Frame size from long[] should be positive");

        // Query INFO_FRAMESIZE via int[] overload
        final var frameSizeFromInt = new int[1];
        assertEquals(0, api.patternInfo(code, IPcre2.INFO_FRAMESIZE, frameSizeFromInt),
                "patternInfo(INFO_FRAMESIZE) with int[] should succeed");
        assertTrue(frameSizeFromInt[0] > 0, "Frame size from int[] should be positive");

        // Both overloads should agree
        assertEquals(frameSizeFromInt[0], (int) frameSizeFromLong[0],
                "Frame size from int[] and long[] overloads should match");

        api.codeFree(code);
    }

    @Test
    default void testGeneralContextCopy() {
        var api = getApi();

        // Create a general context
        final var gcontext = api.generalContextCreate(0, 0, 0);
        assertTrue(gcontext != 0, "General context creation should succeed");

        // Copy the general context
        final var gcontextCopy = api.generalContextCopy(gcontext);
        assertTrue(gcontextCopy != 0, "General context copy should succeed");
        assertNotEquals(gcontext, gcontextCopy, "Copy should be a different handle");

        // Verify the copy works by using it to create a compile context
        final var ccontext = api.compileContextCreate(gcontextCopy);
        assertTrue(ccontext != 0, "Should be able to create compile context from copied general context");

        api.compileContextFree(ccontext);
        api.generalContextFree(gcontextCopy);
        api.generalContextFree(gcontext);
    }

    @Test
    default void testCompileContextCopy() {
        var api = getApi();

        // Create a compile context and set newline to CRLF
        final var ccontext = api.compileContextCreate(0);
        assertTrue(ccontext != 0, "Compile context creation should succeed");

        assertEquals(0, api.setNewline(ccontext, IPcre2.NEWLINE_CRLF),
                "setNewline should return 0 on success");

        // Copy the compile context
        final var ccontextCopy = api.compileContextCopy(ccontext);
        assertTrue(ccontextCopy != 0, "Compile context copy should succeed");
        assertNotEquals(ccontext, ccontextCopy, "Copy should be a different handle");

        // Verify the copy has the same newline setting by compiling a pattern and checking INFO_NEWLINE
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        final var code = api.compile("a.b", 0, errorcode, erroroffset, ccontextCopy);
        assertTrue(code != 0, "Pattern compilation with copied compile context should succeed");

        final var newline = new int[1];
        assertEquals(0, api.patternInfo(code, IPcre2.INFO_NEWLINE, newline),
                "patternInfo(INFO_NEWLINE) should succeed");
        assertEquals(IPcre2.NEWLINE_CRLF, newline[0],
                "Copied compile context should preserve CRLF newline setting");

        api.codeFree(code);
        api.compileContextFree(ccontextCopy);
        api.compileContextFree(ccontext);
    }

    @Test
    default void testMatchContextCopy() {
        var api = getApi();

        // Create a match context and set a match limit
        final var mcontext = api.matchContextCreate(0);
        assertTrue(mcontext != 0, "Match context creation should succeed");

        assertEquals(0, api.setMatchLimit(mcontext, 5000),
                "setMatchLimit should return 0 on success");

        // Copy the match context
        final var mcontextCopy = api.matchContextCopy(mcontext);
        assertTrue(mcontextCopy != 0, "Match context copy should succeed");
        assertNotEquals(mcontext, mcontextCopy, "Copy should be a different handle");

        // Verify the copied context works for matching
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern compilation should succeed");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        final var result = api.match(code, "hello world", 0, 0, matchData, mcontextCopy);
        assertTrue(result > 0, "Match should succeed with copied match context");

        api.matchDataFree(matchData);
        api.codeFree(code);
        api.matchContextFree(mcontextCopy);
        api.matchContextFree(mcontext);
    }

    @Test
    default void testConvertContextCopy() {
        var api = getApi();

        // Create a convert context
        final var cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        // Set a glob escape character
        assertEquals(0, api.setGlobEscape(cvcontext, '\\'),
                "setGlobEscape should return 0 on success");

        // Copy the convert context
        final var cvcontextCopy = api.convertContextCopy(cvcontext);
        assertTrue(cvcontextCopy != 0, "Convert context copy should succeed");
        assertNotEquals(cvcontext, cvcontextCopy, "Copy should be a different handle");

        // Verify the copy works by using it for pattern conversion
        final var buffer = new long[]{0};
        final var blength = new long[]{0};

        final var convertResult = api.patternConvert(
                "*.txt",
                IPcre2.CONVERT_GLOB,
                buffer,
                blength,
                cvcontextCopy
        );
        assertEquals(0, convertResult, "Pattern conversion should succeed with copied convert context");
        assertTrue(buffer[0] != 0, "Buffer should contain a pointer");
        assertTrue(blength[0] > 0, "Pattern length should be positive");

        api.convertedPatternFree(buffer[0]);
        api.convertContextFree(cvcontextCopy);
        api.convertContextFree(cvcontext);
    }

    @Test
    default void maketablesWithGeneralContext() {
        var api = getApi();

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
    default void setCharacterTablesWithCustomTables() {
        var api = getApi();

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
    default void setCharacterTablesWithNullTables() {
        var api = getApi();

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
}
