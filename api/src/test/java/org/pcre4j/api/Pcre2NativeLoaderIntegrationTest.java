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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link Pcre2NativeLoader} exercising the end-to-end extraction path with
 * fixture resources on the test classpath. The fixtures live under
 * {@code api/src/test/resources/META-INF/native/test-platform/} and {@code .../test-empty/}.
 * <p>
 * These tests complement the unit coverage in {@link Pcre2NativeLoaderTest} by verifying behavior
 * that depends on real classpath resources — the gap that allowed issue #556 (empty native JARs)
 * to ship undetected in 1.0.0.
 */
class Pcre2NativeLoaderIntegrationTest {
    /**
     * Synthetic platform identifier for the happy-path fixture. A matching directory with
     * {@code libtest.so}, {@code libtest.dylib}, and {@code test.dll} of known content lives in
     * the test resources.
     */
    private static final String TEST_PLATFORM = "test-platform";

    /**
     * Synthetic platform identifier for the empty-placeholder fixture. A matching directory with
     * only {@code .gitkeep} (no library file) lives in the test resources — mirroring the empty
     * bundle JAR regression from issue #556.
     */
    private static final String TEST_EMPTY_PLATFORM = "test-empty";

    /**
     * Known fixture bytes. Must stay in sync with the contents of the {@code test-platform}
     * fixture files.
     */
    private static final byte[] FIXTURE_BYTES =
            "PCRE4J_NATIVE_TEST_FIXTURE_v1\n".getBytes(StandardCharsets.US_ASCII);

    private Logger loaderLogger;
    private CollectingHandler logHandler;
    private Level originalLevel;

    @BeforeEach
    void setUp() {
        loaderLogger = Logger.getLogger(Pcre2NativeLoader.class.getName());
        originalLevel = loaderLogger.getLevel();
        loaderLogger.setLevel(Level.ALL);
        logHandler = new CollectingHandler();
        loaderLogger.addHandler(logHandler);

        Pcre2NativeLoader.resetCacheForTesting();
    }

    @AfterEach
    void tearDown() {
        Pcre2NativeLoader.setPlatformForTesting(null);
        Pcre2NativeLoader.resetCacheForTesting();
        System.clearProperty(Pcre2NativeLoader.TMPDIR_PROPERTY);

        loaderLogger.removeHandler(logHandler);
        loaderLogger.setLevel(originalLevel);
    }

    // --- Scenario: Loader extracts a fixture from the classpath ---

    @Test
    void load_extractsFixtureFromClasspath_andExtractedBytesMatchFixture(@TempDir Path tmpDir) throws IOException {
        System.setProperty(Pcre2NativeLoader.TMPDIR_PROPERTY, tmpDir.toString());
        Pcre2NativeLoader.setPlatformForTesting(TEST_PLATFORM);

        var result = Pcre2NativeLoader.load("test");

        assertTrue(result.isPresent(), "Expected load() to return a non-empty Optional");
        var dir = result.get();
        var extractedFile = dir.resolve(System.mapLibraryName("test"));
        assertTrue(Files.isRegularFile(extractedFile),
                "Expected extracted library at " + extractedFile);
        assertArrayEquals(FIXTURE_BYTES, Files.readAllBytes(extractedFile),
                "Extracted bytes must be identical to fixture bytes");
    }

    // --- Scenario: Loader returns empty when no fixture is present ---

    @Test
    void load_returnsEmpty_andLeavesNoFilesInTmp_whenNoFixturePresent(@TempDir Path tmpDir) throws IOException {
        System.setProperty(Pcre2NativeLoader.TMPDIR_PROPERTY, tmpDir.toString());
        Pcre2NativeLoader.setPlatformForTesting("nonexistent-platform-" + System.nanoTime());

        var result = Pcre2NativeLoader.load("pcre2-8");

        assertTrue(result.isEmpty(), "Expected load() to return Optional.empty()");
        try (Stream<Path> entries = Files.list(tmpDir)) {
            assertEquals(0L, entries.count(),
                    "Expected no files left in tmp directory when no fixture was found");
        }
    }

    // --- Scenario: Loader handles empty-placeholder gracefully (the #556 signal) ---

    @Test
    void load_emitsWarning_whenOnlyPlaceholderIsPresent(@TempDir Path tmpDir) throws IOException {
        System.setProperty(Pcre2NativeLoader.TMPDIR_PROPERTY, tmpDir.toString());
        Pcre2NativeLoader.setPlatformForTesting(TEST_EMPTY_PLATFORM);

        var result = Pcre2NativeLoader.load("test");

        assertTrue(result.isEmpty(), "Expected load() to return Optional.empty()");
        try (Stream<Path> entries = Files.list(tmpDir)) {
            assertEquals(0L, entries.count(),
                    "Expected no files left in tmp directory when only a placeholder was found");
        }

        var warnings = logHandler.recordsAtLevel(Level.WARNING);
        assertTrue(
                warnings.stream().anyMatch(r -> {
                    var msg = formatRecord(r);
                    return msg.contains("placeholder") && msg.contains(TEST_EMPTY_PLATFORM);
                }),
                "Expected a WARNING mentioning 'placeholder' and '" + TEST_EMPTY_PLATFORM
                        + "' when only a .gitkeep is present; got: "
                        + warnings.stream().map(Pcre2NativeLoaderIntegrationTest::formatRecord).toList()
        );
    }

    /**
     * Format a {@link LogRecord} into its resolved message for assertions.
     */
    private static String formatRecord(LogRecord record) {
        var message = record.getMessage();
        var params = record.getParameters();
        if (params == null || params.length == 0 || message == null) {
            return message == null ? "" : message;
        }
        return MessageFormat.format(message, params);
    }

    /**
     * A {@link Handler} that collects every {@link LogRecord} it receives, so tests can make
     * assertions on emitted log output without depending on the JDK's default console handler.
     */
    private static final class CollectingHandler extends Handler {
        private final List<LogRecord> records = new CopyOnWriteArrayList<>();

        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {
            // No buffering — nothing to flush.
        }

        @Override
        public void close() {
            records.clear();
        }

        List<LogRecord> recordsAtLevel(Level level) {
            return records.stream()
                    .filter(r -> r.getLevel().intValue() >= level.intValue())
                    .toList();
        }
    }
}
