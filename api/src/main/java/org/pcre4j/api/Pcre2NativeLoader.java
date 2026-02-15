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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extracts bundled PCRE2 native libraries from the classpath.
 * <p>
 * When a {@code pcre4j-native-{os}-{arch}} dependency is on the classpath, this loader finds the bundled
 * PCRE2 shared library under {@code META-INF/native/{os}-{arch}/}, extracts it to a temporary directory,
 * and returns the path so that backends (JNA, FFM) can load it.
 * <p>
 * <strong>Loading priority</strong> (as integrated into backends):
 * <ol>
 *   <li>Bundled native (this class) &mdash; if the native dependency is on the classpath</li>
 *   <li>{@code pcre2.library.path} system property &mdash; explicit user override</li>
 *   <li>{@code System.loadLibrary()} / JNA {@code Native.load()} &mdash; standard system search</li>
 *   <li>{@link Pcre2LibraryFinder#discover(String)} &mdash; pcre2-config, pkg-config, well-known paths</li>
 * </ol>
 * <p>
 * The bundled native can be skipped by setting the system property {@code pcre2.native.ignore} to {@code "true"}.
 * The temporary directory for extraction can be overridden via the {@code pcre2.native.tmpdir} system property.
 */
public final class Pcre2NativeLoader {

    private static final Logger LOG = Logger.getLogger(Pcre2NativeLoader.class.getName());

    /**
     * System property to skip bundled native loading. Set to {@code "true"} to skip.
     */
    public static final String IGNORE_PROPERTY = "pcre2.native.ignore";

    /**
     * System property to override the temporary directory for native library extraction.
     */
    public static final String TMPDIR_PROPERTY = "pcre2.native.tmpdir";

    /**
     * The resource path prefix for bundled native libraries.
     */
    static final String RESOURCE_PREFIX = "META-INF/native/";

    /**
     * Lock object for thread-safe extraction.
     */
    private static final Object LOCK = new Object();

    /**
     * Cached path to the directory containing the extracted native library, or {@code null} if not yet extracted.
     */
    private static volatile Path extractedDir;

    private Pcre2NativeLoader() {
        // Utility class
    }

    /**
     * Load the bundled PCRE2 native library for the current platform.
     * <p>
     * If a bundled native library is found on the classpath, it is extracted to a temporary directory and the
     * directory path is returned. The extracted library is registered for deletion on JVM shutdown.
     * <p>
     * If the system property {@code pcre2.native.ignore} is set to {@code "true"}, this method returns
     * {@link Optional#empty()} immediately.
     *
     * @param libraryName the PCRE2 library name (e.g. {@code "pcre2-8"})
     * @return the path to the directory containing the extracted native library, or empty if not available
     */
    public static Optional<Path> load(String libraryName) {
        if (libraryName == null) {
            throw new IllegalArgumentException("libraryName must not be null");
        }

        if ("true".equalsIgnoreCase(System.getProperty(IGNORE_PROPERTY))) {
            LOG.log(Level.FINE, "Bundled native loading disabled via {0} system property", IGNORE_PROPERTY);
            return Optional.empty();
        }

        // Use cached result if available
        var cached = extractedDir;
        if (cached != null) {
            var mappedName = System.mapLibraryName(libraryName);
            var libFile = cached.resolve(mappedName);
            if (Files.isRegularFile(libFile)) {
                LOG.log(Level.FINE, "Using previously extracted native library: {0}", libFile);
                return Optional.of(cached);
            }
        }

        var platform = detectPlatform();
        if (platform == null) {
            LOG.log(Level.FINE, "Unsupported platform for bundled native loading");
            return Optional.empty();
        }

        var mappedName = System.mapLibraryName(libraryName);
        var resourcePath = RESOURCE_PREFIX + platform + "/" + mappedName;

        return extractResource(resourcePath, mappedName);
    }

    /**
     * Detect the current platform in the format used by the resource path convention.
     *
     * @return the platform identifier (e.g. {@code "linux-x86_64"}), or {@code null} if unsupported
     */
    static String detectPlatform() {
        var os = detectOS();
        var arch = detectArch();
        if (os == null || arch == null) {
            return null;
        }
        return os + "-" + arch;
    }

    /**
     * Detect the operating system name for the resource path.
     *
     * @return the OS identifier ({@code "linux"}, {@code "macos"}, {@code "windows"}), or {@code null}
     */
    static String detectOS() {
        var osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (osName.contains("linux")) {
            return "linux";
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            return "macos";
        } else if (osName.contains("windows")) {
            return "windows";
        }
        return null;
    }

    /**
     * Detect the CPU architecture for the resource path.
     *
     * @return the architecture identifier ({@code "x86_64"}, {@code "aarch64"}), or {@code null}
     */
    static String detectArch() {
        var osArch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
        if ("amd64".equals(osArch) || "x86_64".equals(osArch)) {
            return "x86_64";
        } else if ("aarch64".equals(osArch) || "arm64".equals(osArch)) {
            return "aarch64";
        }
        return null;
    }

    /**
     * Extract the resource to a temporary directory and return the directory path.
     *
     * @param resourcePath the classpath resource path
     * @param fileName     the file name for the extracted library
     * @return the directory containing the extracted library, or empty
     */
    private static Optional<Path> extractResource(String resourcePath, String fileName) {
        synchronized (LOCK) {
            // Double-check after acquiring lock
            var cached = extractedDir;
            if (cached != null) {
                var libFile = cached.resolve(fileName);
                if (Files.isRegularFile(libFile)) {
                    return Optional.of(cached);
                }
            }

            InputStream in = Pcre2NativeLoader.class.getClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                LOG.log(Level.FINE, "No bundled native library found at: {0}", resourcePath);
                return Optional.empty();
            }

            try {
                var tmpDir = createTempDirectory();
                var libFile = tmpDir.resolve(fileName);
                Files.copy(in, libFile, StandardCopyOption.REPLACE_EXISTING);
                in.close();

                // Register cleanup
                libFile.toFile().deleteOnExit();
                tmpDir.toFile().deleteOnExit();

                LOG.log(Level.INFO, "Extracted bundled PCRE2 native library to: {0}", libFile);

                extractedDir = tmpDir;
                return Optional.of(tmpDir);
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Failed to extract bundled native library", e);
                try {
                    in.close();
                } catch (IOException ignored) {
                    // Best effort
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Create a temporary directory for native library extraction.
     * <p>
     * Respects the {@code pcre2.native.tmpdir} system property if set.
     *
     * @return the temporary directory path
     * @throws IOException if the directory cannot be created
     */
    private static Path createTempDirectory() throws IOException {
        var customTmpDir = System.getProperty(TMPDIR_PROPERTY);
        if (customTmpDir != null && !customTmpDir.isBlank()) {
            var parent = Path.of(customTmpDir);
            Files.createDirectories(parent);
            return Files.createTempDirectory(parent, "pcre4j-native");
        }
        return Files.createTempDirectory("pcre4j-native");
    }
}
