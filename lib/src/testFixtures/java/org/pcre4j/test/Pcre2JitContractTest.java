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

import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
