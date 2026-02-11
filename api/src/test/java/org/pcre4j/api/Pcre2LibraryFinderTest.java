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
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link Pcre2LibraryFinder}.
 */
class Pcre2LibraryFinderTest {

    // --- inferWidthSuffix ---

    @Test
    void inferWidthSuffix_utf8() {
        assertEquals("8", Pcre2LibraryFinder.inferWidthSuffix("pcre2-8"));
    }

    @Test
    void inferWidthSuffix_utf16() {
        assertEquals("16", Pcre2LibraryFinder.inferWidthSuffix("pcre2-16"));
    }

    @Test
    void inferWidthSuffix_utf32() {
        assertEquals("32", Pcre2LibraryFinder.inferWidthSuffix("pcre2-32"));
    }

    @Test
    void inferWidthSuffix_unknownReturnsNull() {
        assertNull(Pcre2LibraryFinder.inferWidthSuffix("something-else"));
    }

    // --- parseLibDirFromFlags ---

    @Test
    void parseLibDirFromFlags_attachedLFlag() {
        assertEquals("/opt/homebrew/lib", Pcre2LibraryFinder.parseLibDirFromFlags("-L/opt/homebrew/lib -lpcre2-8"));
    }

    @Test
    void parseLibDirFromFlags_separatedLFlag() {
        assertEquals("/usr/local/lib", Pcre2LibraryFinder.parseLibDirFromFlags("-L /usr/local/lib -lpcre2-8"));
    }

    @Test
    void parseLibDirFromFlags_noLFlag() {
        assertNull(Pcre2LibraryFinder.parseLibDirFromFlags("-lpcre2-8"));
    }

    @Test
    void parseLibDirFromFlags_emptyInput() {
        assertNull(Pcre2LibraryFinder.parseLibDirFromFlags(""));
    }

    @Test
    void parseLibDirFromFlags_multipleLFlags() {
        assertEquals("/first/path",
                Pcre2LibraryFinder.parseLibDirFromFlags("-L/first/path -L/second/path -lpcre2-8"));
    }

    // --- runCommand ---

    @Test
    void runCommand_successfulCommand() {
        var result = Pcre2LibraryFinder.runCommand("echo", "hello");
        assertEquals("hello", result);
    }

    @Test
    void runCommand_nonExistentCommand() {
        var result = Pcre2LibraryFinder.runCommand("nonexistent-command-xyz-12345");
        assertNull(result);
    }

    @Test
    void runCommand_failingCommand() {
        var result = Pcre2LibraryFinder.runCommand("false");
        assertNull(result);
    }

    @Test
    void runCommand_multiLineOutput() {
        // Should return only the first line
        var result = Pcre2LibraryFinder.runCommand("printf", "line1\nline2\nline3");
        assertEquals("line1", result);
    }

    // --- checkLibrary ---

    @Test
    void checkLibrary_existingFile(@TempDir Path tempDir) throws IOException {
        var file = Files.createFile(tempDir.resolve("libpcre2-8.so"));
        var result = Pcre2LibraryFinder.checkLibrary(file, "test");
        assertTrue(result.isPresent());
        assertEquals(file, result.get());
    }

    @Test
    void checkLibrary_nonExistentFile(@TempDir Path tempDir) {
        var file = tempDir.resolve("nonexistent.so");
        var result = Pcre2LibraryFinder.checkLibrary(file, "test");
        assertTrue(result.isEmpty());
    }

    @Test
    void checkLibrary_directory(@TempDir Path tempDir) {
        // A directory is not a regular file
        var result = Pcre2LibraryFinder.checkLibrary(tempDir, "test");
        assertTrue(result.isEmpty());
    }

    // --- discover argument validation ---

    @Test
    void discover_nullWidthThrows() {
        assertThrows(IllegalArgumentException.class, () -> Pcre2LibraryFinder.discover((Pcre2UtfWidth) null));
    }

    @Test
    void discover_nullLibraryNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> Pcre2LibraryFinder.discover((String) null));
    }

    // --- discover disable property ---

    @Test
    void discover_disabledViaSystemProperty() {
        System.setProperty(Pcre2LibraryFinder.DISCOVERY_PROPERTY, "false");
        try {
            assertTrue(Pcre2LibraryFinder.discover("pcre2-8").isEmpty());
        } finally {
            System.clearProperty(Pcre2LibraryFinder.DISCOVERY_PROPERTY);
        }
    }

    @Test
    void discover_disabledCaseInsensitive() {
        System.setProperty(Pcre2LibraryFinder.DISCOVERY_PROPERTY, "FALSE");
        try {
            assertTrue(Pcre2LibraryFinder.discover("pcre2-8").isEmpty());
        } finally {
            System.clearProperty(Pcre2LibraryFinder.DISCOVERY_PROPERTY);
        }
    }

    // --- discover integration ---

    @Test
    void discover_pcre2LibraryByName() {
        // On systems where pcre2 is installed, this should find the library
        var result = Pcre2LibraryFinder.discover("pcre2-8");
        if (result.isPresent()) {
            assertTrue(Files.isRegularFile(result.get()));
            assertTrue(result.get().getFileName().toString().contains("pcre2-8"));
        }
        // If not found, the test still passes — it's environment-dependent
    }

    @Test
    void discover_pcre2LibraryByWidth() {
        var result = Pcre2LibraryFinder.discover(Pcre2UtfWidth.UTF8);
        if (result.isPresent()) {
            assertTrue(Files.isRegularFile(result.get()));
        }
    }

    @Test
    void discover_unknownLibraryReturnsEmpty() {
        // An unrecognized library name skips pcre2-config/pkg-config but still tries well-known paths.
        // A completely fake name should not be found anywhere.
        var result = Pcre2LibraryFinder.discover("nonexistent-library-xyz-12345");
        assertTrue(result.isEmpty());
    }

    // --- tryPcre2Config ---

    @Test
    void tryPcre2Config_withInstalledPcre2() {
        var mappedName = System.mapLibraryName("pcre2-8");
        var result = Pcre2LibraryFinder.tryPcre2Config("8", mappedName);
        // pcre2-config may or may not be available; either outcome is valid
        if (result.isPresent()) {
            assertTrue(Files.isRegularFile(result.get()));
        }
    }

    @Test
    void tryPcre2Config_nonExistentWidth() {
        var result = Pcre2LibraryFinder.tryPcre2Config("99", "libpcre2-99.so");
        assertTrue(result.isEmpty());
    }

    // --- tryPkgConfig ---

    @Test
    void tryPkgConfig_withInstalledPcre2() {
        var mappedName = System.mapLibraryName("pcre2-8");
        var result = Pcre2LibraryFinder.tryPkgConfig("pcre2-8", mappedName);
        // pkg-config may or may not be available; either outcome is valid
        if (result.isPresent()) {
            assertTrue(Files.isRegularFile(result.get()));
        }
    }

    @Test
    void tryPkgConfig_nonExistentLibrary() {
        var result = Pcre2LibraryFinder.tryPkgConfig("nonexistent-xyz", "libnonexistent-xyz.so");
        assertTrue(result.isEmpty());
    }

    // --- tryWellKnownPaths ---

    @Test
    void tryWellKnownPaths_withInstalledPcre2() {
        var mappedName = System.mapLibraryName("pcre2-8");
        var result = Pcre2LibraryFinder.tryWellKnownPaths(mappedName);
        // Depends on OS and pcre2 installation; either outcome is valid
        if (result.isPresent()) {
            assertTrue(Files.isRegularFile(result.get()));
        }
    }

    @Test
    void tryWellKnownPaths_nonExistentLibrary() {
        var result = Pcre2LibraryFinder.tryWellKnownPaths("libnonexistent-xyz-12345.so");
        assertTrue(result.isEmpty());
    }

    // --- validateDiscoveryPath ---

    @Test
    void validateDiscoveryPath_validAbsoluteDirectory(@TempDir Path tempDir) {
        var result = Pcre2LibraryFinder.validateDiscoveryPath(tempDir.toString(), "test");
        assertNotNull(result);
        assertEquals(tempDir, result);
    }

    @Test
    void validateDiscoveryPath_nullInput() {
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath(null, "test"));
    }

    @Test
    void validateDiscoveryPath_emptyInput() {
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath("", "test"));
    }

    @Test
    void validateDiscoveryPath_blankInput() {
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath("   ", "test"));
    }

    @Test
    void validateDiscoveryPath_nullByte() {
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath("/usr/lib\0/evil", "test"));
    }

    @Test
    void validateDiscoveryPath_shellMetacharSemicolon() {
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath("/usr/lib;rm -rf /", "test"));
    }

    @Test
    void validateDiscoveryPath_shellMetacharPipe() {
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath("/usr/lib|cat /etc/passwd", "test"));
    }

    @Test
    void validateDiscoveryPath_shellMetacharAmpersand() {
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath("/usr/lib&echo pwned", "test"));
    }

    @Test
    void validateDiscoveryPath_shellMetacharDollar() {
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath("/usr/lib/$HOME", "test"));
    }

    @Test
    void validateDiscoveryPath_shellMetacharBacktick() {
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath("/usr/lib/`id`", "test"));
    }

    @Test
    void validateDiscoveryPath_shellMetacharBackslash() {
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath("/usr/lib\\evil", "test"));
    }

    @Test
    void validateDiscoveryPath_relativePath() {
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath("relative/path", "test"));
    }

    @Test
    void validateDiscoveryPath_pathTraversal() {
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath("/usr/lib/../etc", "test"));
    }

    @Test
    void validateDiscoveryPath_nonExistentDirectory() {
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath("/nonexistent/directory/xyz/12345", "test"));
    }

    @Test
    void validateDiscoveryPath_fileNotDirectory(@TempDir Path tempDir) throws IOException {
        var file = Files.createFile(tempDir.resolve("not-a-directory"));
        assertNull(Pcre2LibraryFinder.validateDiscoveryPath(file.toString(), "test"));
    }

    // --- DISCOVERY_PROPERTY constant ---

    @Test
    void discoveryPropertyConstant() {
        assertNotNull(Pcre2LibraryFinder.DISCOVERY_PROPERTY);
        assertFalse(Pcre2LibraryFinder.DISCOVERY_PROPERTY.isEmpty());
    }
}
