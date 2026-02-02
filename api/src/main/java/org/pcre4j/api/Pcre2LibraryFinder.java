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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility for discovering the PCRE2 native library on the system.
 * <p>
 * This finder tries the following discovery chain in order, returning the first successful result:
 * <ol>
 *   <li>{@code pcre2-config --libs8} (or {@code --libs16}/{@code --libs32}) &mdash; parses {@code -L&lt;path&gt;}
 *       from output</li>
 *   <li>{@code pkg-config --variable=libdir libpcre2-8} (or {@code -16}/{@code -32}) &mdash; uses output as
 *       library directory</li>
 *   <li>Well-known platform paths &mdash; probes common library directories for macOS and Linux</li>
 * </ol>
 * <p>
 * Uses {@link System#mapLibraryName(String)} for platform-correct filenames. All failures are graceful
 * (returns {@link Optional#empty()}).
 * <p>
 * Discovery can be disabled by setting the system property {@code pcre2.library.discovery} to {@code "false"}.
 */
public final class Pcre2LibraryFinder {

    private static final Logger LOG = Logger.getLogger(Pcre2LibraryFinder.class.getName());

    /**
     * System property to disable automatic library discovery. Set to {@code "false"} to disable.
     */
    public static final String DISCOVERY_PROPERTY = "pcre2.library.discovery";

    private static final long SUBPROCESS_TIMEOUT_SECONDS = 5;

    private static final List<String> MACOS_WELL_KNOWN_PATHS = List.of(
            "/opt/homebrew/lib",
            "/usr/local/lib",
            "/opt/local/lib"
    );

    private static final List<String> LINUX_WELL_KNOWN_PATHS = List.of(
            "/usr/lib/x86_64-linux-gnu",
            "/usr/lib/aarch64-linux-gnu",
            "/usr/lib64",
            "/usr/lib",
            "/usr/local/lib"
    );

    private Pcre2LibraryFinder() {
        // Utility class
    }

    /**
     * Discover the PCRE2 native library for the given UTF width.
     *
     * @param width the UTF width to discover the library for
     * @return the path to the library file, or empty if not found
     */
    public static Optional<Path> discover(Pcre2UtfWidth width) {
        if (width == null) {
            throw new IllegalArgumentException("width must not be null");
        }
        return discover(width.libraryName());
    }

    /**
     * Discover the PCRE2 native library by its library name.
     * <p>
     * The library name is used to infer the UTF width for {@code pcre2-config} and {@code pkg-config} lookups
     * (e.g. {@code "pcre2-8"} maps to {@code --libs8} and {@code libpcre2-8}). If the name is not a recognized
     * PCRE2 library name, only well-known platform paths are probed.
     *
     * @param libraryName the library name (e.g. {@code "pcre2-8"}, {@code "pcre2-16"}, {@code "pcre2-32"})
     * @return the path to the library file, or empty if not found
     */
    public static Optional<Path> discover(String libraryName) {
        if (libraryName == null) {
            throw new IllegalArgumentException("libraryName must not be null");
        }

        if ("false".equalsIgnoreCase(System.getProperty(DISCOVERY_PROPERTY))) {
            LOG.log(Level.FINE, "Library discovery disabled via {0} system property", DISCOVERY_PROPERTY);
            return Optional.empty();
        }

        var mappedName = System.mapLibraryName(libraryName);
        var widthSuffix = inferWidthSuffix(libraryName);

        if (widthSuffix != null) {
            // Try pcre2-config
            var result = tryPcre2Config(widthSuffix, mappedName);
            if (result.isPresent()) {
                return result;
            }

            // Try pkg-config
            result = tryPkgConfig(libraryName, mappedName);
            if (result.isPresent()) {
                return result;
            }
        }

        // Try well-known platform paths
        return tryWellKnownPaths(mappedName);
    }

    /**
     * Infer the PCRE2 width suffix from the library name.
     *
     * @param libraryName the library name
     * @return the width suffix (e.g. {@code "8"}, {@code "16"}, {@code "32"}), or {@code null} if unrecognized
     */
    static String inferWidthSuffix(String libraryName) {
        if (libraryName.equals("pcre2-8")) {
            return "8";
        } else if (libraryName.equals("pcre2-16")) {
            return "16";
        } else if (libraryName.equals("pcre2-32")) {
            return "32";
        }
        return null;
    }

    /**
     * Try discovering the library using {@code pcre2-config}.
     *
     * @param widthSuffix the width suffix (e.g. {@code "8"})
     * @param mappedName  the platform-mapped library filename
     * @return the path to the library file, or empty if not found
     */
    static Optional<Path> tryPcre2Config(String widthSuffix, String mappedName) {
        LOG.log(Level.FINE, "Trying pcre2-config --libs{0}", widthSuffix);
        var output = runCommand("pcre2-config", "--libs" + widthSuffix);
        if (output == null) {
            return Optional.empty();
        }

        var libDir = parseLibDirFromFlags(output);
        if (libDir == null) {
            LOG.log(Level.FINE, "No -L flag found in pcre2-config output: {0}", output);
            return Optional.empty();
        }

        return checkLibrary(Path.of(libDir, mappedName), "pcre2-config");
    }

    /**
     * Parse the library directory from linker flags output.
     * <p>
     * Looks for a {@code -L&lt;path&gt;} or {@code -L &lt;path&gt;} token in the output.
     *
     * @param flags the linker flags output
     * @return the library directory path, or {@code null} if no {@code -L} flag found
     */
    static String parseLibDirFromFlags(String flags) {
        var parts = flags.trim().split("\\s+");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].startsWith("-L") && parts[i].length() > 2) {
                return parts[i].substring(2);
            }
            if (parts[i].equals("-L") && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return null;
    }

    /**
     * Try discovering the library using {@code pkg-config}.
     *
     * @param libraryName the PCRE2 library name (e.g. {@code "pcre2-8"})
     * @param mappedName  the platform-mapped library filename
     * @return the path to the library file, or empty if not found
     */
    static Optional<Path> tryPkgConfig(String libraryName, String mappedName) {
        var pkgName = "lib" + libraryName;
        LOG.log(Level.FINE, "Trying pkg-config --variable=libdir {0}", pkgName);
        var output = runCommand("pkg-config", "--variable=libdir", pkgName);
        if (output == null || output.isBlank()) {
            return Optional.empty();
        }

        return checkLibrary(Path.of(output.trim(), mappedName), "pkg-config");
    }

    /**
     * Try discovering the library in well-known platform paths.
     *
     * @param mappedName the platform-mapped library filename
     * @return the path to the library file, or empty if not found
     */
    static Optional<Path> tryWellKnownPaths(String mappedName) {
        var osName = System.getProperty("os.name", "").toLowerCase();
        List<String> paths;
        if (osName.contains("mac") || osName.contains("darwin")) {
            paths = MACOS_WELL_KNOWN_PATHS;
        } else if (osName.contains("linux")) {
            paths = LINUX_WELL_KNOWN_PATHS;
        } else {
            LOG.log(Level.FINE, "No well-known paths for OS: {0}", osName);
            return Optional.empty();
        }

        for (var dir : paths) {
            var candidate = Path.of(dir, mappedName);
            LOG.log(Level.FINE, "Probing well-known path: {0}", candidate);
            if (Files.isRegularFile(candidate)) {
                LOG.log(Level.INFO, "Discovered PCRE2 library via well-known path: {0}", candidate);
                return Optional.of(candidate);
            }
        }

        return Optional.empty();
    }

    /**
     * Check if a candidate library path exists and is a regular file.
     *
     * @param candidate the candidate path
     * @param source    the discovery source name for logging
     * @return the path if it exists, or empty
     */
    static Optional<Path> checkLibrary(Path candidate, String source) {
        LOG.log(Level.FINE, "Checking candidate from {0}: {1}", new Object[]{source, candidate});
        if (Files.isRegularFile(candidate)) {
            LOG.log(Level.INFO, "Discovered PCRE2 library via {0}: {1}", new Object[]{source, candidate});
            return Optional.of(candidate);
        }
        LOG.log(Level.FINE, "Candidate does not exist: {0}", candidate);
        return Optional.empty();
    }

    /**
     * Run a command and return its trimmed stdout, or {@code null} on failure.
     *
     * @param command the command and arguments
     * @return the trimmed stdout output, or {@code null} if the command failed
     */
    static String runCommand(String... command) {
        try {
            var process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
            String output;
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                output = reader.readLine();
                // Drain remaining output to prevent broken pipe
                while (reader.readLine() != null) {
                    // discard
                }
            }
            if (!process.waitFor(SUBPROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                LOG.log(Level.FINE, "Command timed out: {0}", String.join(" ", command));
                process.destroyForcibly();
                return null;
            }
            if (process.exitValue() != 0) {
                LOG.log(Level.FINE, "Command exited with code {0}: {1}",
                        new Object[]{process.exitValue(), String.join(" ", command)});
                return null;
            }
            return output;
        } catch (InterruptedException e) {
            LOG.log(Level.FINE, "Command interrupted: " + String.join(" ", command), e);
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            LOG.log(Level.FINE, "Command failed: " + String.join(" ", command), e);
            return null;
        }
    }
}
