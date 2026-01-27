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
import org.pcre4j.api.Pcre2UtfWidth;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Contract tests for PCRE2 UTF width support.
 *
 * <p>These tests verify that PCRE2 API implementations correctly handle different UTF widths
 * (UTF-8, UTF-16, UTF-32) when encoding patterns and subjects for matching operations.</p>
 *
 * @param <T> the PCRE2 API implementation type
 */
public interface Pcre2UtfWidthContractTest<T extends IPcre2> {

    /**
     * Returns the PCRE2 API implementation to test.
     *
     * @return the PCRE2 API implementation
     */
    T getApi();

    /**
     * Creates a new PCRE2 API instance with the specified UTF width.
     *
     * @param width the UTF width
     * @return a new PCRE2 API instance configured for the specified width
     */
    T createApi(Pcre2UtfWidth width);

    /**
     * Checks if the specified UTF width is available on the current system.
     *
     * @param width the UTF width to check
     * @return true if the width is available, false otherwise
     */
    default boolean isWidthAvailable(Pcre2UtfWidth width) {
        try {
            final var api = createApi(width);
            // Try to compile a simple pattern to verify the library works
            final var errorcode = new int[1];
            final var erroroffset = new long[1];
            final var code = api.compile("test", 0, errorcode, erroroffset, 0);
            if (code != 0) {
                api.codeFree(code);
                return true;
            }
            return false;
        } catch (UnsatisfiedLinkError | Exception e) {
            return false;
        }
    }

    @Test
    default void utf8BasicMatch() {
        assumeTrue(isWidthAvailable(Pcre2UtfWidth.UTF8), "UTF-8 library not available");

        final var api = createApi(Pcre2UtfWidth.UTF8);
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern should compile successfully");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data should be created");

        final var result = api.match(code, "hello world", 0, 0, matchData, 0);
        assertTrue(result > 0, "Match should succeed");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void utf8UnicodeMatch() {
        assumeTrue(isWidthAvailable(Pcre2UtfWidth.UTF8), "UTF-8 library not available");

        final var api = createApi(Pcre2UtfWidth.UTF8);
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Pattern with emoji (🌐 = 4 bytes in UTF-8)
        final var code = api.compile("🌐", IPcre2.UTF, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern should compile successfully");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data should be created");

        final var result = api.match(code, "Hello 🌐 World", 0, 0, matchData, 0);
        assertTrue(result > 0, "Match should succeed");

        // Verify the match was captured correctly
        final var ovectorCount = api.getOvectorCount(matchData);
        assertEquals(1, ovectorCount, "Should have one capture group");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void utf16BasicMatch() {
        assumeTrue(isWidthAvailable(Pcre2UtfWidth.UTF16), "UTF-16 library not available");

        final var api = createApi(Pcre2UtfWidth.UTF16);
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern should compile successfully for UTF-16");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data should be created");

        final var result = api.match(code, "hello world", 0, 0, matchData, 0);
        assertTrue(result > 0, "Match should succeed with UTF-16 backend");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void utf16UnicodeMatch() {
        assumeTrue(isWidthAvailable(Pcre2UtfWidth.UTF16), "UTF-16 library not available");

        final var api = createApi(Pcre2UtfWidth.UTF16);
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Pattern with emoji (🌐 = 2 code units in UTF-16 as surrogate pair)
        final var code = api.compile("🌐", IPcre2.UTF, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern should compile successfully for UTF-16");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data should be created");

        final var result = api.match(code, "Hello 🌐 World", 0, 0, matchData, 0);
        assertTrue(result > 0, "Match should succeed with UTF-16 backend");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void utf16SubstitutionMatch() {
        assumeTrue(isWidthAvailable(Pcre2UtfWidth.UTF16), "UTF-16 library not available");

        final var api = createApi(Pcre2UtfWidth.UTF16);
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        final var code = api.compile("world", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern should compile successfully");

        final var matchData = api.matchDataCreateFromPattern(code, 0);

        // Allocate a direct ByteBuffer for the output
        final var outputBuffer = java.nio.ByteBuffer.allocateDirect(256);
        final var outputLength = new long[]{128}; // Output length in code units (not bytes for UTF-16)

        final var result = api.substitute(
                code,
                "hello world",
                0,
                IPcre2.SUBSTITUTE_GLOBAL,
                matchData,
                0,
                "universe",
                outputBuffer,
                outputLength
        );

        assertTrue(result >= 0, "Substitution should succeed, result: " + result);

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void utf32BasicMatch() {
        assumeTrue(isWidthAvailable(Pcre2UtfWidth.UTF32), "UTF-32 library not available");

        final var api = createApi(Pcre2UtfWidth.UTF32);
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern should compile successfully for UTF-32");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data should be created");

        final var result = api.match(code, "hello world", 0, 0, matchData, 0);
        assertTrue(result > 0, "Match should succeed with UTF-32 backend");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void utf32UnicodeMatch() {
        assumeTrue(isWidthAvailable(Pcre2UtfWidth.UTF32), "UTF-32 library not available");

        final var api = createApi(Pcre2UtfWidth.UTF32);
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Pattern with emoji (🌐 = 1 code unit in UTF-32)
        final var code = api.compile("🌐", IPcre2.UTF, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern should compile successfully for UTF-32");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data should be created");

        final var result = api.match(code, "Hello 🌐 World", 0, 0, matchData, 0);
        assertTrue(result > 0, "Match should succeed with UTF-32 backend");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void utf16NamedCaptureGroup() {
        assumeTrue(isWidthAvailable(Pcre2UtfWidth.UTF16), "UTF-16 library not available");

        final var api = createApi(Pcre2UtfWidth.UTF16);
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Pattern with named capture group
        final var code = api.compile("(?<greeting>hello) (?<target>world)", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern should compile successfully for UTF-16");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data should be created");

        final var result = api.match(code, "hello world", 0, 0, matchData, 0);
        assertTrue(result > 0, "Match should succeed with UTF-16 backend");

        // Test substringNumberFromName - this exercises the null-terminated name handling
        final var greetingNumber = api.substringNumberFromName(code, "greeting");
        assertEquals(1, greetingNumber, "Named group 'greeting' should be capture group 1");

        final var targetNumber = api.substringNumberFromName(code, "target");
        assertEquals(2, targetNumber, "Named group 'target' should be capture group 2");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void utf32NamedCaptureGroup() {
        assumeTrue(isWidthAvailable(Pcre2UtfWidth.UTF32), "UTF-32 library not available");

        final var api = createApi(Pcre2UtfWidth.UTF32);
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Pattern with named capture group
        final var code = api.compile("(?<greeting>hello) (?<target>world)", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Pattern should compile successfully for UTF-32");

        final var matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data should be created");

        final var result = api.match(code, "hello world", 0, 0, matchData, 0);
        assertTrue(result > 0, "Match should succeed with UTF-32 backend");

        // Test substringNumberFromName - this exercises the null-terminated name handling
        final var greetingNumber = api.substringNumberFromName(code, "greeting");
        assertEquals(1, greetingNumber, "Named group 'greeting' should be capture group 1");

        final var targetNumber = api.substringNumberFromName(code, "target");
        assertEquals(2, targetNumber, "Named group 'target' should be capture group 2");

        api.matchDataFree(matchData);
        api.codeFree(code);
    }

    @Test
    default void widthMetadataIsCorrect() {
        assertEquals("pcre2-8", Pcre2UtfWidth.UTF8.libraryName());
        assertEquals("_8", Pcre2UtfWidth.UTF8.functionSuffix());
        assertEquals(1, Pcre2UtfWidth.UTF8.codeUnitSize());

        assertEquals("pcre2-16", Pcre2UtfWidth.UTF16.libraryName());
        assertEquals("_16", Pcre2UtfWidth.UTF16.functionSuffix());
        assertEquals(2, Pcre2UtfWidth.UTF16.codeUnitSize());

        assertEquals("pcre2-32", Pcre2UtfWidth.UTF32.libraryName());
        assertEquals("_32", Pcre2UtfWidth.UTF32.functionSuffix());
        assertEquals(4, Pcre2UtfWidth.UTF32.codeUnitSize());
    }
}
