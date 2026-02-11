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

import org.junit.jupiter.api.Test;
import org.pcre4j.api.Pcre2LibraryFinder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for JNA backend native library loading failure modes.
 *
 * <p>Verifies that the JNA {@link Pcre2} backend produces clear, actionable errors when the PCRE2 native library
 * cannot be loaded.</p>
 */
class Pcre2LoadingTests {

    @Test
    void loadNonExistentLibraryByName_throwsUnsatisfiedLinkError() {
        System.setProperty(Pcre2LibraryFinder.DISCOVERY_PROPERTY, "false");
        try {
            var error = assertThrows(
                    UnsatisfiedLinkError.class,
                    () -> new Pcre2("nonexistent-pcre2-library-xyz", "_8")
            );
            assertNotNull(error.getMessage(), "Error message must not be null");
            assertTrue(
                    error.getMessage().contains("nonexistent-pcre2-library-xyz"),
                    "Error message should mention the library name, got: " + error.getMessage()
            );
        } finally {
            System.clearProperty(Pcre2LibraryFinder.DISCOVERY_PROPERTY);
        }
    }

    @Test
    void loadLibraryFromNonExistentPath_throwsUnsatisfiedLinkError() {
        var error = assertThrows(
                UnsatisfiedLinkError.class,
                () -> new Pcre2("/nonexistent/path/to/libpcre2-8.so", "_8")
        );
        assertNotNull(error.getMessage(), "Error message must not be null");
    }

    @Test
    void loadNullLibraryName_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Pcre2(null, "_8")
        );
    }

    @Test
    void loadNullSuffix_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Pcre2("pcre2-8", null)
        );
    }
}
