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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link Pcre4j} bootstrap singleton.
 *
 * <p>Verifies success paths, error paths, and edge cases of the bootstrap singleton configuration.</p>
 */
public class Pcre4jTests {

    @BeforeEach
    void resetSingleton() throws Exception {
        Field apiField = Pcre4j.class.getDeclaredField("api");
        apiField.setAccessible(true);
        apiField.set(null, null);
    }

    @Test
    void api_withoutSetup_autoDiscoversBackend() {
        // Both JNA and FFM backends are on the test classpath, so auto-discovery should succeed
        var api = assertDoesNotThrow(() -> Pcre4j.api());
        assertNotNull(api, "Auto-discovered backend must not be null");
        assertInstanceOf(IPcre2.class, api);
    }

    @Test
    void api_withoutSetup_prefersFfmBackend() {
        // Both backends are on the test classpath; FFM should be preferred
        var api = Pcre4j.api();
        assertTrue(
                api.getClass().getName().equals("org.pcre4j.ffm.Pcre2"),
                "Expected FFM backend to be preferred, got: " + api.getClass().getName()
        );
    }

    @Test
    void setup_nullApi_throwsIllegalArgumentException() {
        var error = assertThrows(
                IllegalArgumentException.class,
                () -> Pcre4j.setup(null)
        );
        assertNotNull(error.getMessage(), "Error message must not be null");
        assertTrue(
                error.getMessage().contains("null"),
                "Error message should indicate the null argument, got: " + error.getMessage()
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void setup_withValidApi_succeeds(IPcre2 api) {
        assertDoesNotThrow(() -> Pcre4j.setup(api));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void api_afterSetup_returnsConfiguredApi(IPcre2 api) {
        Pcre4j.setup(api);
        assertSame(api, Pcre4j.api());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void setup_calledTwice_replacesApi(IPcre2 api) {
        Pcre4j.setup(api);
        assertSame(api, Pcre4j.api());

        // Second setup call should succeed and replace the API
        Pcre4j.setup(api);
        assertSame(api, Pcre4j.api());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void setup_overridesAutoDiscovery(IPcre2 api) {
        Pcre4j.setup(api);
        assertSame(api, Pcre4j.api(), "Explicit setup should take priority over auto-discovery");
    }

    @Test
    void api_autoDiscovery_cachesSingleton() {
        var first = Pcre4j.api();
        var second = Pcre4j.api();
        assertSame(first, second, "Auto-discovered backend should be cached as singleton");
    }

    @Test
    void setup_apiWithoutUtf8Support_throwsIllegalArgumentException() {
        var stubApi = new NoUtf8Api();
        var error = assertThrows(
                IllegalArgumentException.class,
                () -> Pcre4j.setup(stubApi)
        );
        assertNotNull(error.getMessage(), "Error message must not be null");
        assertTrue(
                error.getMessage().contains("UTF-8"),
                "Error message should mention UTF-8, got: " + error.getMessage()
        );
    }

    /**
     * Minimal {@link IPcre2} stub that reports no UTF-8 support via {@link IPcre2#CONFIG_COMPILED_WIDTHS}.
     *
     * <p>Only the {@code config} methods are implemented; all other methods throw
     * {@link UnsupportedOperationException}.</p>
     */
    private static class NoUtf8Api implements IPcre2 {

        @Override
        public int config(int what) {
            if (what == CONFIG_COMPILED_WIDTHS) {
                return 4; // sizeof(int)
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public int config(int what, int[] where) {
            if (what == CONFIG_COMPILED_WIDTHS) {
                where[0] = 0; // no widths supported — no UTF-8 bit set
                return 4;
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public int config(int what, ByteBuffer where) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long generalContextCreate(long privateMalloc, long privateFree, long memoryData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long generalContextCopy(long gcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void generalContextFree(long gcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long maketables(long gcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void maketablesFree(long gcontext, long tables) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long compileContextCreate(long gcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long compileContextCopy(long ccontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void compileContextFree(long ccontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long compile(String pattern, int options, int[] errorcode, long[] erroroffset, long ccontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long codeCopy(long code) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long codeCopyWithTables(long code) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void codeFree(long code) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int calloutEnumerate(long code, long callback, long calloutData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getErrorMessage(int errorcode, ByteBuffer buffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int patternInfo(long code, int what) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int patternInfo(long code, int what, int[] where) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int patternInfo(long code, int what, long[] where) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int patternInfo(long code, int what, ByteBuffer where) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int jitCompile(long code, int options) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int jitMatch(long code, String subject, int startoffset, int options, long matchData, long mcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long jitStackCreate(long startsize, long maxsize, long gcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void jitStackFree(long jitStack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void jitStackAssign(long mcontext, long callback, long data) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void jitFreeUnusedMemory(long gcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long matchDataCreate(int ovecsize, long gcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long matchDataCreateFromPattern(long code, long gcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void matchDataFree(long matchData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long matchContextCreate(long gcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long matchContextCopy(long mcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void matchContextFree(long mcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long convertContextCreate(long gcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long convertContextCopy(long cvcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void convertContextFree(long cvcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setGlobEscape(long cvcontext, int escapeChar) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setGlobSeparator(long cvcontext, int separatorChar) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int patternConvert(String pattern, int options, long[] buffer, long[] blength, long cvcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void convertedPatternFree(long convertedPattern) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int match(long code, String subject, int startoffset, int options, long matchData, long mcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int dfaMatch(
                long code,
                String subject,
                int startoffset,
                int options,
                long matchData,
                long mcontext,
                int[] workspace,
                int wscount
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getOvectorCount(long matchData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getMatchDataSize(long matchData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getOvector(long matchData, long[] ovector) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getStartchar(long matchData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getMark(long matchData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setNewline(long ccontext, int newline) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setBsr(long ccontext, int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setParensNestLimit(long ccontext, int limit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setMaxPatternLength(long ccontext, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setCompileExtraOptions(long ccontext, int extraOptions) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setCharacterTables(long ccontext, long tables) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setCompileRecursionGuard(long ccontext, long guardFunction, long userData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setMatchLimit(long mcontext, int limit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setDepthLimit(long mcontext, int limit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setHeapLimit(long mcontext, int limit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setOffsetLimit(long mcontext, long limit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setCallout(long mcontext, long callback, long calloutData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int substitute(
                long code,
                String subject,
                int startoffset,
                int options,
                long matchData,
                long mcontext,
                String replacement,
                ByteBuffer outputbuffer,
                long[] outputlength
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int substringGetByNumber(long matchData, int number, long[] bufferptr, long[] bufflen) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int substringCopyByNumber(long matchData, int number, ByteBuffer buffer, long[] bufflen) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int substringGetByName(long matchData, String name, long[] bufferptr, long[] bufflen) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int substringCopyByName(long matchData, String name, ByteBuffer buffer, long[] bufflen) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int substringLengthByName(long matchData, String name, long[] length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int substringLengthByNumber(long matchData, int number, long[] length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void substringFree(long buffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int substringListGet(long matchData, long[] listptr, long[] lengthsptr) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void substringListFree(long list) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int substringNumberFromName(long code, String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int substringNametableScan(long code, String name, long[] first, long[] last) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int serializeEncode(long[] codes, int numberOfCodes, long[] serializedBytes, long[] serializedSize,
                long gcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int serializeDecode(long[] codes, int numberOfCodes, byte[] bytes, long gcontext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void serializeFree(long bytes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int serializeGetNumberOfCodes(byte[] bytes) {
            throw new UnsupportedOperationException();
        }
    }
}
