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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract tests for PCRE2 pattern conversion operations (glob, POSIX).
 *
 * @param <T> the PCRE2 API implementation type
 */
public interface Pcre2PatternConvertContractTest<T extends IPcre2> {

    /**
     * Returns the PCRE2 API implementation to test.
     *
     * @return the PCRE2 API implementation
     */
    T getApi();

    @Test
    default void patternConvertGlob() {
        var api = getApi();

        // Test converting a glob pattern to PCRE2
        // The glob pattern "*.txt" should be converted to a PCRE2 pattern

        // First, get the required buffer size by passing buffer[0] = 0
        long[] buffer = new long[]{0};
        long[] blength = new long[]{0};

        int result = api.patternConvert(
                "*.txt",
                IPcre2.CONVERT_GLOB,
                buffer,
                blength,
                0
        );
        assertEquals(0, result, "patternConvert should return 0 on success");
        assertTrue(buffer[0] != 0, "Buffer should contain a pointer after conversion");
        assertTrue(blength[0] > 0, "blength should contain the pattern length");

        // The buffer was allocated by PCRE2, so we need to free it
        api.convertedPatternFree(buffer[0]);
    }

    @Test
    default void patternConvertPosixExtended() {
        var api = getApi();

        // Test converting a POSIX Extended Regular Expression to PCRE2
        // The POSIX ERE "^[a-z]+$" should be converted

        long[] buffer = new long[]{0};
        long[] blength = new long[]{0};

        int result = api.patternConvert(
                "^[a-z]+$",
                IPcre2.CONVERT_POSIX_EXTENDED,
                buffer,
                blength,
                0
        );
        assertEquals(0, result, "patternConvert should return 0 on success");
        assertTrue(buffer[0] != 0, "Buffer should contain a pointer after conversion");
        assertTrue(blength[0] > 0, "blength should contain the pattern length");

        api.convertedPatternFree(buffer[0]);
    }

    @Test
    default void patternConvertWithContext() {
        var api = getApi();

        // Test using a convert context
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        long[] buffer = new long[]{0};
        long[] blength = new long[]{0};

        int result = api.patternConvert(
                "file?.log",
                IPcre2.CONVERT_GLOB,
                buffer,
                blength,
                cvcontext
        );
        assertEquals(0, result, "patternConvert should return 0 on success");
        assertTrue(buffer[0] != 0, "Buffer should contain a pointer after conversion");

        api.convertedPatternFree(buffer[0]);
        api.convertContextFree(cvcontext);
    }

    @Test
    default void convertedPatternFreeNull() {
        var api = getApi();

        // Test that convertedPatternFree handles null pointer gracefully
        api.convertedPatternFree(0);
    }

    @Test
    default void patternConvertNullPatternThrows() {
        var api = getApi();
        long[] buffer = new long[]{0};
        long[] blength = new long[]{0};

        assertThrows(IllegalArgumentException.class, () ->
                api.patternConvert(null, IPcre2.CONVERT_GLOB, buffer, blength, 0)
        );
    }

    @Test
    default void patternConvertNullBufferThrows() {
        var api = getApi();
        long[] blength = new long[]{0};

        assertThrows(IllegalArgumentException.class, () ->
                api.patternConvert("*.txt", IPcre2.CONVERT_GLOB, null, blength, 0)
        );
    }

    @Test
    default void patternConvertNullBlengthThrows() {
        var api = getApi();
        long[] buffer = new long[]{0};

        assertThrows(IllegalArgumentException.class, () ->
                api.patternConvert("*.txt", IPcre2.CONVERT_GLOB, buffer, null, 0)
        );
    }

    @Test
    default void setGlobEscapeValid() {
        var api = getApi();

        // Test setting a valid escape character (backslash)
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        int result = api.setGlobEscape(cvcontext, '\\');
        assertEquals(0, result, "setGlobEscape should return 0 for valid punctuation character");

        api.convertContextFree(cvcontext);
    }

    @Test
    default void setGlobEscapeDisable() {
        var api = getApi();

        // Test disabling escape processing by setting to 0
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        int result = api.setGlobEscape(cvcontext, 0);
        assertEquals(0, result, "setGlobEscape should return 0 when disabling escapes");

        api.convertContextFree(cvcontext);
    }

    @Test
    default void setGlobEscapeInvalid() {
        var api = getApi();

        // Test setting an invalid escape character (non-punctuation)
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        // 'a' is not a punctuation character, should return ERROR_BADDATA
        int result = api.setGlobEscape(cvcontext, 'a');
        assertEquals(IPcre2.ERROR_BADDATA, result,
                "setGlobEscape should return ERROR_BADDATA for invalid character");

        api.convertContextFree(cvcontext);
    }

    @Test
    default void setGlobSeparatorForwardSlash() {
        var api = getApi();

        // Test setting forward slash as separator (valid)
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        int result = api.setGlobSeparator(cvcontext, '/');
        assertEquals(0, result, "setGlobSeparator should return 0 for forward slash");

        api.convertContextFree(cvcontext);
    }

    @Test
    default void setGlobSeparatorBackslash() {
        var api = getApi();

        // Test setting backslash as separator (valid)
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        int result = api.setGlobSeparator(cvcontext, '\\');
        assertEquals(0, result, "setGlobSeparator should return 0 for backslash");

        api.convertContextFree(cvcontext);
    }

    @Test
    default void setGlobSeparatorDot() {
        var api = getApi();

        // Test setting dot as separator (valid)
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        int result = api.setGlobSeparator(cvcontext, '.');
        assertEquals(0, result, "setGlobSeparator should return 0 for dot");

        api.convertContextFree(cvcontext);
    }

    @Test
    default void setGlobSeparatorInvalid() {
        var api = getApi();

        // Test setting an invalid separator character
        long cvcontext = api.convertContextCreate(0);
        assertTrue(cvcontext != 0, "Convert context creation should succeed");

        // 'a' is not a valid separator, should return ERROR_BADDATA
        int result = api.setGlobSeparator(cvcontext, 'a');
        assertEquals(IPcre2.ERROR_BADDATA, result,
                "setGlobSeparator should return ERROR_BADDATA for invalid char");

        api.convertContextFree(cvcontext);
    }
}
