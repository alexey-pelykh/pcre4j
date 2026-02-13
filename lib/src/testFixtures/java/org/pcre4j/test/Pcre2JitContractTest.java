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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Contract tests for PCRE2 JIT (Just-In-Time) compilation operations.
 *
 * @param <T> the PCRE2 API implementation type
 */
public interface Pcre2JitContractTest<T extends IPcre2> {

    /**
     * Returns the PCRE2 API implementation to test.
     *
     * @return the PCRE2 API implementation
     */
    T getApi();

    @Test
    default void testJitFreeUnusedMemory() {
        var api = getApi();

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
    default void testJitMatchProducesSameResultAsMatch() {
        var api = getApi();

        // Check if JIT is available
        int[] jitAvailable = new int[1];
        api.config(IPcre2.CONFIG_JIT, jitAvailable);
        assumeTrue(jitAvailable[0] != 0, "JIT support is not available on this platform");

        // Compile a pattern with a capture group
        int[] errorcode = new int[1];
        long[] erroroffset = new long[1];
        long code = api.compile("(\\d+)", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern compilation should succeed");

        // JIT compile
        int jitResult = api.jitCompile(code, IPcre2.JIT_COMPLETE);
        assumeTrue(jitResult == 0, "JIT compilation failed with error code " + jitResult);

        String subject = "abc123def";

        // Match using interpreted match()
        long matchData1 = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData1 != 0, "Match data creation should succeed");
        int result1 = api.match(code, subject, 0, 0, matchData1, 0);

        // Match using jitMatch()
        long matchData2 = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData2 != 0, "Match data creation should succeed");
        int result2 = api.jitMatch(code, subject, 0, 0, matchData2, 0);

        // Both should report the same number of captures
        assertEquals(result1, result2, "jitMatch should produce same result count as match");
        assertTrue(result2 > 0, "jitMatch should find a match");

        // Both should produce the same ovector
        int ovecCount1 = api.getOvectorCount(matchData1);
        long[] ovector1 = new long[ovecCount1 * 2];
        api.getOvector(matchData1, ovector1);

        int ovecCount2 = api.getOvectorCount(matchData2);
        long[] ovector2 = new long[ovecCount2 * 2];
        api.getOvector(matchData2, ovector2);

        assertArrayEquals(ovector1, ovector2, "jitMatch should produce same ovector as match");

        api.matchDataFree(matchData1);
        api.matchDataFree(matchData2);
        api.codeFree(code);
    }

    @Test
    default void testJitMatchNoMatch() {
        var api = getApi();

        int[] jitAvailable = new int[1];
        api.config(IPcre2.CONFIG_JIT, jitAvailable);
        assumeTrue(jitAvailable[0] != 0, "JIT support is not available on this platform");

        int[] errorcode = new int[1];
        long[] erroroffset = new long[1];
        long code = api.compile("xyz", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern compilation should succeed");

        int jitResult = api.jitCompile(code, IPcre2.JIT_COMPLETE);
        assumeTrue(jitResult == 0, "JIT compilation failed with error code " + jitResult);

        long matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        int result = api.jitMatch(code, "abc", 0, 0, matchData, 0);
        assertEquals(IPcre2.ERROR_NOMATCH, result, "jitMatch should return ERROR_NOMATCH for non-matching subject");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void testJitMatchWithStartOffset() {
        var api = getApi();

        int[] jitAvailable = new int[1];
        api.config(IPcre2.CONFIG_JIT, jitAvailable);
        assumeTrue(jitAvailable[0] != 0, "JIT support is not available on this platform");

        int[] errorcode = new int[1];
        long[] erroroffset = new long[1];
        long code = api.compile("test", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern compilation should succeed");

        int jitResult = api.jitCompile(code, IPcre2.JIT_COMPLETE);
        assumeTrue(jitResult == 0, "JIT compilation failed with error code " + jitResult);

        long matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        // Match from offset 0 should succeed
        int result1 = api.jitMatch(code, "test", 0, 0, matchData, 0);
        assertTrue(result1 > 0, "jitMatch from offset 0 should find a match");

        // Match from offset 1 should not find the pattern
        int result2 = api.jitMatch(code, "test", 1, 0, matchData, 0);
        assertEquals(IPcre2.ERROR_NOMATCH, result2,
                "jitMatch from offset past the pattern start should not match");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void testJitMatchWithMatchContext() {
        var api = getApi();

        int[] jitAvailable = new int[1];
        api.config(IPcre2.CONFIG_JIT, jitAvailable);
        assumeTrue(jitAvailable[0] != 0, "JIT support is not available on this platform");

        int[] errorcode = new int[1];
        long[] erroroffset = new long[1];
        long code = api.compile("(\\w+)", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern compilation should succeed");

        int jitResult = api.jitCompile(code, IPcre2.JIT_COMPLETE);
        assumeTrue(jitResult == 0, "JIT compilation failed with error code " + jitResult);

        long matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        // Create a match context
        long mcontext = api.matchContextCreate(0);
        assertTrue(mcontext != 0, "Match context creation should succeed");

        // jitMatch with a match context should work
        int result = api.jitMatch(code, "hello", 0, 0, matchData, mcontext);
        assertTrue(result > 0, "jitMatch with match context should find a match");

        api.matchContextFree(mcontext);
        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void testJitStackCreateAndFree() {
        var api = getApi();

        int[] jitAvailable = new int[1];
        api.config(IPcre2.CONFIG_JIT, jitAvailable);
        assumeTrue(jitAvailable[0] != 0, "JIT support is not available on this platform");

        // Create a JIT stack with 32KB start, 1MB max
        long jitStack = api.jitStackCreate(32 * 1024, 1024 * 1024, 0);
        assertTrue(jitStack != 0, "JIT stack creation should succeed");

        // Free the JIT stack
        api.jitStackFree(jitStack);
    }

    @Test
    default void testJitStackCreateWithGeneralContext() {
        var api = getApi();

        int[] jitAvailable = new int[1];
        api.config(IPcre2.CONFIG_JIT, jitAvailable);
        assumeTrue(jitAvailable[0] != 0, "JIT support is not available on this platform");

        // Create a general context
        long gcontext = api.generalContextCreate(0, 0, 0);
        assertTrue(gcontext != 0, "General context creation should succeed");

        // Create a JIT stack with a general context
        long jitStack = api.jitStackCreate(32 * 1024, 1024 * 1024, gcontext);
        assertTrue(jitStack != 0, "JIT stack creation with general context should succeed");

        api.jitStackFree(jitStack);
        api.generalContextFree(gcontext);
    }

    @Test
    default void testJitStackAssignAndUseInMatch() {
        var api = getApi();

        int[] jitAvailable = new int[1];
        api.config(IPcre2.CONFIG_JIT, jitAvailable);
        assumeTrue(jitAvailable[0] != 0, "JIT support is not available on this platform");

        // Compile and JIT compile a pattern
        int[] errorcode = new int[1];
        long[] erroroffset = new long[1];
        long code = api.compile("(\\d+)", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern compilation should succeed");

        int jitResult = api.jitCompile(code, IPcre2.JIT_COMPLETE);
        assumeTrue(jitResult == 0, "JIT compilation failed with error code " + jitResult);

        // Create JIT stack
        long jitStack = api.jitStackCreate(32 * 1024, 1024 * 1024, 0);
        assertTrue(jitStack != 0, "JIT stack creation should succeed");

        // Create match context and assign JIT stack
        long mcontext = api.matchContextCreate(0);
        assertTrue(mcontext != 0, "Match context creation should succeed");
        api.jitStackAssign(mcontext, 0, jitStack);

        // Use jitMatch with the assigned stack
        long matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        int result = api.jitMatch(code, "abc123", 0, 0, matchData, mcontext);
        assertTrue(result > 0, "jitMatch with assigned JIT stack should find a match");

        // Verify the match result
        int ovecCount = api.getOvectorCount(matchData);
        long[] ovector = new long[ovecCount * 2];
        api.getOvector(matchData, ovector);
        assertEquals(3, ovector[0], "Match should start at offset 3");
        assertEquals(6, ovector[1], "Match should end at offset 6");

        api.matchDataFree(matchData);
        api.jitStackFree(jitStack);
        api.matchContextFree(mcontext);
        api.codeFree(code);
    }

    @Test
    default void testJitStackWithDifferentSizes() {
        var api = getApi();

        int[] jitAvailable = new int[1];
        api.config(IPcre2.CONFIG_JIT, jitAvailable);
        assumeTrue(jitAvailable[0] != 0, "JIT support is not available on this platform");

        // Test with small stack: 1KB start, 64KB max
        long smallStack = api.jitStackCreate(1024, 64 * 1024, 0);
        assertTrue(smallStack != 0, "Small JIT stack creation should succeed");
        api.jitStackFree(smallStack);

        // Test with large stack: 64KB start, 8MB max
        long largeStack = api.jitStackCreate(64 * 1024, 8 * 1024 * 1024, 0);
        assertTrue(largeStack != 0, "Large JIT stack creation should succeed");
        api.jitStackFree(largeStack);
    }

    @Test
    default void testJitMatchWithAssignedStackProducesSameResult() {
        var api = getApi();

        int[] jitAvailable = new int[1];
        api.config(IPcre2.CONFIG_JIT, jitAvailable);
        assumeTrue(jitAvailable[0] != 0, "JIT support is not available on this platform");

        // Compile and JIT compile a pattern with multiple capture groups
        int[] errorcode = new int[1];
        long[] erroroffset = new long[1];
        long code = api.compile("(\\w+)@(\\w+)\\.(\\w+)", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern compilation should succeed");

        int jitResult = api.jitCompile(code, IPcre2.JIT_COMPLETE);
        assumeTrue(jitResult == 0, "JIT compilation failed with error code " + jitResult);

        String subject = "user@example.com";

        // Match without JIT stack (default stack)
        long matchData1 = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData1 != 0, "Match data creation should succeed");
        int result1 = api.jitMatch(code, subject, 0, 0, matchData1, 0);

        // Match with explicit JIT stack
        long jitStack = api.jitStackCreate(32 * 1024, 1024 * 1024, 0);
        assertTrue(jitStack != 0, "JIT stack creation should succeed");

        long mcontext = api.matchContextCreate(0);
        assertTrue(mcontext != 0, "Match context creation should succeed");
        api.jitStackAssign(mcontext, 0, jitStack);

        long matchData2 = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData2 != 0, "Match data creation should succeed");
        int result2 = api.jitMatch(code, subject, 0, 0, matchData2, mcontext);

        // Results should be identical
        assertEquals(result1, result2, "Results with and without explicit JIT stack should match");
        assertTrue(result2 > 0, "jitMatch should find a match");

        int ovecCount1 = api.getOvectorCount(matchData1);
        long[] ovector1 = new long[ovecCount1 * 2];
        api.getOvector(matchData1, ovector1);

        int ovecCount2 = api.getOvectorCount(matchData2);
        long[] ovector2 = new long[ovecCount2 * 2];
        api.getOvector(matchData2, ovector2);

        assertArrayEquals(ovector1, ovector2,
                "Ovectors with and without explicit JIT stack should be identical");

        api.matchDataFree(matchData1);
        api.matchDataFree(matchData2);
        api.jitStackFree(jitStack);
        api.matchContextFree(mcontext);
        api.codeFree(code);
    }
}
