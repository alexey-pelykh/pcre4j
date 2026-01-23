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
package org.pcre4j.jna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import org.junit.jupiter.api.Test;
import org.pcre4j.api.IPcre2;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Pcre2Tests extends org.pcre4j.test.Pcre2Tests {

    private final Pcre2 pcre2;

    public Pcre2Tests() {
        super(new Pcre2());
        this.pcre2 = (Pcre2) api;
    }

    /**
     * Callback interface for pcre2_callout_enumerate.
     */
    public interface CalloutEnumerateCallback extends Callback {
        int invoke(Pointer block, Pointer userData);
    }

    @Test
    public void calloutEnumerateWithNoCallouts() {
        // Compile a pattern without callouts
        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        long code = api.compile("abc", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern compilation should succeed");

        // Create a callback that counts invocations
        final var calloutCount = new AtomicInteger(0);
        CalloutEnumerateCallback callback = (block, userData) -> {
            calloutCount.incrementAndGet();
            return 0; // Continue enumeration
        };

        // Get the native function pointer for the callback
        long callbackPtr = Pointer.nativeValue(
                com.sun.jna.CallbackReference.getFunctionPointer(callback));

        // Enumerate callouts
        int result = api.calloutEnumerate(code, callbackPtr, 0);
        assertEquals(0, result, "calloutEnumerate should return 0 for successful enumeration");
        assertEquals(0, calloutCount.get(), "No callouts should be enumerated for pattern without callouts");

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

        // Create a callback that counts invocations
        final var calloutCount = new AtomicInteger(0);
        CalloutEnumerateCallback callback = (block, userData) -> {
            calloutCount.incrementAndGet();
            return 0; // Continue enumeration
        };

        // Get the native function pointer for the callback
        long callbackPtr = Pointer.nativeValue(
                com.sun.jna.CallbackReference.getFunctionPointer(callback));

        // Enumerate callouts
        int result = api.calloutEnumerate(code, callbackPtr, 0);
        assertEquals(0, result, "calloutEnumerate should return 0 for successful enumeration");
        assertEquals(1, calloutCount.get(), "One callout should be enumerated");

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

        // Create a callback that counts invocations
        final var calloutCount = new AtomicInteger(0);
        CalloutEnumerateCallback callback = (block, userData) -> {
            calloutCount.incrementAndGet();
            return 0; // Continue enumeration
        };

        // Get the native function pointer for the callback
        long callbackPtr = Pointer.nativeValue(
                com.sun.jna.CallbackReference.getFunctionPointer(callback));

        // Enumerate callouts
        int result = api.calloutEnumerate(code, callbackPtr, 0);
        assertEquals(0, result, "calloutEnumerate should return 0 for successful enumeration");
        assertTrue(calloutCount.get() > 0, "Auto callouts should be enumerated");

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

        // Create a callback that stops after first callout
        final var calloutCount = new AtomicInteger(0);
        CalloutEnumerateCallback callback = (block, userData) -> {
            calloutCount.incrementAndGet();
            return 1; // Stop enumeration
        };

        // Get the native function pointer for the callback
        long callbackPtr = Pointer.nativeValue(
                com.sun.jna.CallbackReference.getFunctionPointer(callback));

        // Enumerate callouts
        int result = api.calloutEnumerate(code, callbackPtr, 0);
        assertEquals(1, result, "calloutEnumerate should return callback's non-zero value");
        assertEquals(1, calloutCount.get(), "Only one callout should be enumerated before stopping");

        // Clean up
        api.codeFree(code);
    }
}
