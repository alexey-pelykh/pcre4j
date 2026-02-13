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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link Pcre2PatternInfoSizeError}.
 */
public class Pcre2PatternInfoSizeErrorTests {

    @Test
    void isRuntimeException() {
        var error = new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_SIZE, 42);
        assertInstanceOf(RuntimeException.class, error);
    }

    @Test
    void messageContainsSizeAndInfoName() {
        var error = new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_SIZE, 42);
        assertTrue(error.getMessage().contains("42"));
        assertTrue(error.getMessage().contains("INFO_SIZE"));
    }

    @Test
    void messageFormatMatchesExpected() {
        var error = new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_CAPTURECOUNT, 99);
        assertEquals("Unexpected size of 99 bytes for INFO_CAPTURECOUNT", error.getMessage());
    }

    @Test
    void causeIsNullWhenNotProvided() {
        var error = new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_SIZE, 42);
        assertNull(error.getCause());
    }

    @Test
    void causeIsSetWhenProvided() {
        var cause = new RuntimeException("underlying");
        var error = new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_SIZE, 42, cause);
        assertEquals(cause, error.getCause());
    }

    @Test
    void messageIsSameWithAndWithoutCause() {
        var withoutCause = new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_ALLOPTIONS, 16);
        var withCause = new Pcre2PatternInfoSizeError(
                Pcre2PatternInfo.INFO_ALLOPTIONS, 16, new RuntimeException("cause")
        );
        assertEquals(withoutCause.getMessage(), withCause.getMessage());
    }

    @Test
    void withNullCauseMatchesSingleArgConstructor() {
        var singleArg = new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_SIZE, 42);
        var nullCause = new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_SIZE, 42, null);
        assertEquals(singleArg.getMessage(), nullCause.getMessage());
        assertNull(nullCause.getCause());
    }

    @Test
    void zeroSize() {
        var error = new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_SIZE, 0);
        assertTrue(error.getMessage().contains("0 bytes"));
    }

    @Test
    void negativeSize() {
        var error = new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_SIZE, -1);
        assertTrue(error.getMessage().contains("-1"));
    }

    @Test
    void largeSizeValue() {
        var error = new Pcre2PatternInfoSizeError(Pcre2PatternInfo.INFO_JITSIZE, Long.MAX_VALUE);
        assertTrue(error.getMessage().contains(String.valueOf(Long.MAX_VALUE)));
        assertTrue(error.getMessage().contains("INFO_JITSIZE"));
    }

    @Test
    void differentPatternInfoValues() {
        for (var info : Pcre2PatternInfo.values()) {
            var error = new Pcre2PatternInfoSizeError(info, 8);
            assertTrue(error.getMessage().contains(info.name()));
            assertTrue(error.getMessage().contains("8"));
        }
    }
}
