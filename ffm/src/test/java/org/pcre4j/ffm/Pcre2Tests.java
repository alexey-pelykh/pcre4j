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
package org.pcre4j.ffm;

import org.junit.jupiter.api.Test;
import org.pcre4j.api.IPcre2;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Pcre2Tests extends org.pcre4j.test.Pcre2Tests {

    private static final Linker LINKER = Linker.nativeLinker();

    public Pcre2Tests() {
        super(new Pcre2());
    }

    /**
     * Creates an upcall stub for the callout enumerate callback.
     *
     * @param counter the counter to increment for each callout
     * @param returnValue the value to return from the callback
     * @param arena the arena to allocate the stub in
     * @return the memory segment representing the callback function pointer
     */
    private MemorySegment createCalloutEnumerateCallback(AtomicInteger counter, int returnValue, Arena arena) {
        try {
            MethodHandle callbackHandle = MethodHandles.lookup().findStatic(
                    Pcre2Tests.class,
                    "calloutEnumerateCallbackImpl",
                    MethodType.methodType(int.class, AtomicInteger.class, int.class, MemorySegment.class,
                            MemorySegment.class)
            );
            // Bind the counter and returnValue parameters
            callbackHandle = MethodHandles.insertArguments(callbackHandle, 0, counter, returnValue);

            return LINKER.upcallStub(
                    callbackHandle,
                    FunctionDescriptor.of(
                            ValueLayout.JAVA_INT,    // return type: int
                            ValueLayout.ADDRESS,     // pcre2_callout_enumerate_block*
                            ValueLayout.ADDRESS      // void* user_data
                    ),
                    arena
            );
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Static callback implementation for callout enumerate.
     */
    public static int calloutEnumerateCallbackImpl(AtomicInteger counter, int returnValue, MemorySegment block,
                                                   MemorySegment userData) {
        counter.incrementAndGet();
        return returnValue;
    }

    @Test
    public void calloutEnumerateWithNoCallouts() {
        // Compile a pattern without callouts
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        long code = api.compile("abc", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern compilation should succeed");

        try (var arena = Arena.ofConfined()) {
            // Create a callback that counts invocations
            final var calloutCount = new AtomicInteger(0);
            MemorySegment callback = createCalloutEnumerateCallback(calloutCount, 0, arena);

            // Enumerate callouts
            int result = api.calloutEnumerate(code, callback.address(), 0);
            assertEquals(0, result, "calloutEnumerate should return 0 for successful enumeration");
            assertEquals(0, calloutCount.get(), "No callouts should be enumerated for pattern without callouts");
        }

        // Clean up
        api.codeFree(code);
    }

    @Test
    public void calloutEnumerateWithExplicitCallout() {
        // Compile a pattern with an explicit callout (?C1)
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        long code = api.compile("a(?C1)b", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern compilation should succeed");

        try (var arena = Arena.ofConfined()) {
            // Create a callback that counts invocations
            final var calloutCount = new AtomicInteger(0);
            MemorySegment callback = createCalloutEnumerateCallback(calloutCount, 0, arena);

            // Enumerate callouts
            int result = api.calloutEnumerate(code, callback.address(), 0);
            assertEquals(0, result, "calloutEnumerate should return 0 for successful enumeration");
            assertEquals(1, calloutCount.get(), "One callout should be enumerated");
        }

        // Clean up
        api.codeFree(code);
    }

    @Test
    public void calloutEnumerateWithAutoCallout() {
        // Compile a pattern with auto callout enabled
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        long code = api.compile("abc", IPcre2.AUTO_CALLOUT, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern compilation should succeed");

        try (var arena = Arena.ofConfined()) {
            // Create a callback that counts invocations
            final var calloutCount = new AtomicInteger(0);
            MemorySegment callback = createCalloutEnumerateCallback(calloutCount, 0, arena);

            // Enumerate callouts
            int result = api.calloutEnumerate(code, callback.address(), 0);
            assertEquals(0, result, "calloutEnumerate should return 0 for successful enumeration");
            assertTrue(calloutCount.get() > 0, "Auto callouts should be enumerated");
        }

        // Clean up
        api.codeFree(code);
    }

    @Test
    public void calloutEnumerateCallbackCanStopEnumeration() {
        // Compile a pattern with multiple explicit callouts
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        long code = api.compile("a(?C1)b(?C2)c(?C3)d", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern compilation should succeed");

        try (var arena = Arena.ofConfined()) {
            // Create a callback that stops after first callout
            final var calloutCount = new AtomicInteger(0);
            MemorySegment callback = createCalloutEnumerateCallback(calloutCount, 1, arena);

            // Enumerate callouts
            int result = api.calloutEnumerate(code, callback.address(), 0);
            assertEquals(1, result, "calloutEnumerate should return callback's non-zero value");
            assertEquals(1, calloutCount.get(), "Only one callout should be enumerated before stopping");
        }

        // Clean up
        api.codeFree(code);
    }

    @Test
    public void patternConvertEndToEnd() {
        // End-to-end test: convert a glob pattern, compile the result, and use it to match

        // Convert glob pattern "*.txt" to PCRE2
        long[] buffer = new long[]{0};
        long[] blength = new long[]{0};

        int convertResult = api.patternConvert(
                "*.txt",
                IPcre2.CONVERT_GLOB,
                buffer,
                blength,
                0
        );
        assertEquals(0, convertResult, "patternConvert should succeed");
        assertTrue(buffer[0] != 0, "Buffer should contain a pointer");
        assertTrue(blength[0] > 0, "Pattern length should be positive");

        // Read the converted pattern from native memory
        MemorySegment pConvertedPattern = MemorySegment.ofAddress(buffer[0])
                .reinterpret(blength[0] + 1); // +1 for null terminator
        String convertedPattern = pConvertedPattern.getUtf8String(0);

        // Compile the converted pattern
        int[] errorcode = new int[1];
        long[] erroroffset = new long[1];
        long code = api.compile(convertedPattern, 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Converted pattern should compile successfully: " + convertedPattern);

        // Create match data
        long matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        // Test that the pattern matches "file.txt"
        int matchResult = api.match(code, "file.txt", 0, 0, matchData, 0);
        assertTrue(matchResult > 0, "Pattern should match 'file.txt', result=" + matchResult);

        // Test that the pattern does NOT match "file.log"
        matchResult = api.match(code, "file.log", 0, 0, matchData, 0);
        assertEquals(IPcre2.ERROR_NOMATCH, matchResult, "Pattern should not match 'file.log'");

        // Clean up
        api.matchDataFree(matchData);
        api.codeFree(code);
        api.convertedPatternFree(buffer[0]);
    }
}
