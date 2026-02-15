/*
 * Copyright (C) 2026 Oleksii PELYKH
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link Pcre2NativeLoader}.
 */
class Pcre2NativeLoaderTest {

    // --- detectOS ---

    @Test
    void detectOS_returnsNonNullOnSupportedPlatform() {
        var os = Pcre2NativeLoader.detectOS();
        // The test environment must be one of the supported platforms
        assertNotNull(os, "Test is running on an unsupported platform");
        assertTrue(
                os.equals("linux") || os.equals("macos") || os.equals("windows"),
                "Unexpected OS: " + os
        );
    }

    // --- detectArch ---

    @Test
    void detectArch_returnsNonNullOnSupportedArchitecture() {
        var arch = Pcre2NativeLoader.detectArch();
        // The test environment must be one of the supported architectures
        assertNotNull(arch, "Test is running on an unsupported architecture");
        assertTrue(
                arch.equals("x86_64") || arch.equals("aarch64"),
                "Unexpected arch: " + arch
        );
    }

    // --- detectPlatform ---

    @Test
    void detectPlatform_returnsExpectedFormat() {
        var platform = Pcre2NativeLoader.detectPlatform();
        assertNotNull(platform);
        assertTrue(platform.contains("-"), "Platform should be in os-arch format: " + platform);
    }

    // --- load argument validation ---

    @Test
    void load_nullLibraryNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> Pcre2NativeLoader.load(null));
    }

    // --- load with ignore property ---

    @Test
    void load_disabledViaSystemProperty() {
        System.setProperty(Pcre2NativeLoader.IGNORE_PROPERTY, "true");
        try {
            assertTrue(Pcre2NativeLoader.load("pcre2-8").isEmpty());
        } finally {
            System.clearProperty(Pcre2NativeLoader.IGNORE_PROPERTY);
        }
    }

    @Test
    void load_disabledCaseInsensitive() {
        System.setProperty(Pcre2NativeLoader.IGNORE_PROPERTY, "TRUE");
        try {
            assertTrue(Pcre2NativeLoader.load("pcre2-8").isEmpty());
        } finally {
            System.clearProperty(Pcre2NativeLoader.IGNORE_PROPERTY);
        }
    }

    // --- load when no bundled native on classpath ---

    @Test
    void load_returnsEmptyWhenNoBundledNative() {
        // In the api module tests, no native bundles are on the classpath
        var result = Pcre2NativeLoader.load("pcre2-8");
        assertTrue(result.isEmpty());
    }

    // --- RESOURCE_PREFIX constant ---

    @Test
    void resourcePrefixConstant() {
        assertEquals("META-INF/native/", Pcre2NativeLoader.RESOURCE_PREFIX);
    }

    // --- IGNORE_PROPERTY constant ---

    @Test
    void ignorePropertyConstant() {
        assertNotNull(Pcre2NativeLoader.IGNORE_PROPERTY);
        assertEquals("pcre2.native.ignore", Pcre2NativeLoader.IGNORE_PROPERTY);
    }

    // --- TMPDIR_PROPERTY constant ---

    @Test
    void tmpdirPropertyConstant() {
        assertNotNull(Pcre2NativeLoader.TMPDIR_PROPERTY);
        assertEquals("pcre2.native.tmpdir", Pcre2NativeLoader.TMPDIR_PROPERTY);
    }
}
