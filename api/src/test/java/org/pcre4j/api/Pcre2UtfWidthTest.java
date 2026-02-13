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
package org.pcre4j.api;

import org.junit.jupiter.api.Test;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link Pcre2UtfWidth}.
 */
class Pcre2UtfWidthTest {

    // --- value() ---

    @Test
    void value_utf8() {
        assertEquals(0b1, Pcre2UtfWidth.UTF8.value());
    }

    @Test
    void value_utf16() {
        assertEquals(0b10, Pcre2UtfWidth.UTF16.value());
    }

    @Test
    void value_utf32() {
        assertEquals(0b1000, Pcre2UtfWidth.UTF32.value());
    }

    // --- fromValue() with valid values ---

    @Test
    void fromValue_utf8() {
        var result = Pcre2UtfWidth.fromValue(0b1);
        assertTrue(result.isPresent());
        assertEquals(Pcre2UtfWidth.UTF8, result.get());
    }

    @Test
    void fromValue_utf16() {
        var result = Pcre2UtfWidth.fromValue(0b10);
        assertTrue(result.isPresent());
        assertEquals(Pcre2UtfWidth.UTF16, result.get());
    }

    @Test
    void fromValue_utf32() {
        var result = Pcre2UtfWidth.fromValue(0b1000);
        assertTrue(result.isPresent());
        assertEquals(Pcre2UtfWidth.UTF32, result.get());
    }

    // --- fromValue() with invalid values ---

    @Test
    void fromValue_zero() {
        assertFalse(Pcre2UtfWidth.fromValue(0).isPresent());
    }

    @Test
    void fromValue_negative() {
        assertFalse(Pcre2UtfWidth.fromValue(-1).isPresent());
    }

    @Test
    void fromValue_unusedBitPattern() {
        assertFalse(Pcre2UtfWidth.fromValue(0b100).isPresent());
    }

    @Test
    void fromValue_largValue() {
        assertFalse(Pcre2UtfWidth.fromValue(999).isPresent());
    }

    // --- fromValue() roundtrip ---

    @Test
    void fromValue_roundtripAllConstants() {
        for (var width : Pcre2UtfWidth.values()) {
            var result = Pcre2UtfWidth.fromValue(width.value());
            assertTrue(result.isPresent(), "fromValue should find " + width.name());
            assertEquals(width, result.get());
        }
    }

    // --- libraryName() ---

    @Test
    void libraryName_utf8() {
        assertEquals("pcre2-8", Pcre2UtfWidth.UTF8.libraryName());
    }

    @Test
    void libraryName_utf16() {
        assertEquals("pcre2-16", Pcre2UtfWidth.UTF16.libraryName());
    }

    @Test
    void libraryName_utf32() {
        assertEquals("pcre2-32", Pcre2UtfWidth.UTF32.libraryName());
    }

    // --- functionSuffix() ---

    @Test
    void functionSuffix_utf8() {
        assertEquals("_8", Pcre2UtfWidth.UTF8.functionSuffix());
    }

    @Test
    void functionSuffix_utf16() {
        assertEquals("_16", Pcre2UtfWidth.UTF16.functionSuffix());
    }

    @Test
    void functionSuffix_utf32() {
        assertEquals("_32", Pcre2UtfWidth.UTF32.functionSuffix());
    }

    // --- charset() ---

    @Test
    void charset_utf8() {
        assertEquals(StandardCharsets.UTF_8, Pcre2UtfWidth.UTF8.charset());
    }

    @Test
    void charset_utf16_matchesNativeOrder() {
        var expected = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
                ? StandardCharsets.UTF_16LE
                : StandardCharsets.UTF_16BE;
        assertEquals(expected, Pcre2UtfWidth.UTF16.charset());
    }

    @Test
    void charset_utf32_matchesNativeOrder() {
        var expected = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
                ? Charset.forName("UTF-32LE")
                : Charset.forName("UTF-32BE");
        assertEquals(expected, Pcre2UtfWidth.UTF32.charset());
    }

    @Test
    void charset_allNonNull() {
        for (var width : Pcre2UtfWidth.values()) {
            assertNotNull(width.charset(), "charset() should not be null for " + width.name());
        }
    }

    // --- codeUnitSize() ---

    @Test
    void codeUnitSize_utf8() {
        assertEquals(1, Pcre2UtfWidth.UTF8.codeUnitSize());
    }

    @Test
    void codeUnitSize_utf16() {
        assertEquals(2, Pcre2UtfWidth.UTF16.codeUnitSize());
    }

    @Test
    void codeUnitSize_utf32() {
        assertEquals(4, Pcre2UtfWidth.UTF32.codeUnitSize());
    }

    // --- enum completeness ---

    @Test
    void valuesContainsAllThreeWidths() {
        assertEquals(3, Pcre2UtfWidth.values().length);
    }

    @Test
    void allValuesAreDistinct() {
        var values = Pcre2UtfWidth.values();
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertFalse(values[i].value() == values[j].value(),
                        values[i].name() + " and " + values[j].name() + " should have distinct values");
            }
        }
    }
}
