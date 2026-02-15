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
package org.pcre4j.test;

import org.junit.jupiter.params.provider.Arguments;
import org.pcre4j.api.IPcre2;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Test fixture utility that provides {@link IPcre2} backend instances for parameterized tests.
 *
 * <p>This class centralizes backend discovery and instantiation logic shared across test classes in multiple modules.
 * Backends are loaded reflectively to avoid compile-time coupling with specific backend implementations.</p>
 *
 * <p>The set of backends can be controlled via the {@code pcre4j.test.backends} system property.
 * When set, only the listed backends are included. Valid values are {@code jna} and {@code ffm},
 * separated by commas. When not set, all backends are included.</p>
 *
 * <p>Usage in test classes:</p>
 * <pre>{@code
 * @ParameterizedTest
 * @MethodSource("org.pcre4j.test.BackendProvider#parameters")
 * void testSomething(IPcre2 api) {
 *     // test code using api
 * }
 * }</pre>
 */
public final class BackendProvider {

    private static final String JNA_BACKEND = "org.pcre4j.jna.Pcre2";
    private static final String FFM_BACKEND = "org.pcre4j.ffm.Pcre2";

    private BackendProvider() {
    }

    /**
     * Reflectively instantiates an {@link IPcre2} backend by class name.
     *
     * @param className the fully qualified class name of the backend
     * @return the backend instance
     * @throws RuntimeException if the backend class is not found or cannot be instantiated
     */
    public static IPcre2 loadBackend(String className) {
        try {
            return (IPcre2) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Backend " + className + " not found on classpath", e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException
                 | NoSuchMethodException e) {
            throw new RuntimeException("Failed to instantiate backend " + className, e);
        }
    }

    /**
     * Provides backend instances as JUnit 5 parameterized test arguments.
     *
     * <p>Use with {@code @MethodSource("org.pcre4j.test.BackendProvider#parameters")}.</p>
     *
     * <p>The set of backends is controlled by the {@code pcre4j.test.backends} system property.
     * When set to a comma-separated list (e.g., {@code "jna"} or {@code "jna,ffm"}), only the
     * listed backends are included. When not set, all backends are included.</p>
     *
     * @return a stream of arguments, each containing an {@link IPcre2} backend instance
     */
    public static Stream<Arguments> parameters() {
        var enabledBackends = enabledBackends();
        var args = new ArrayList<Arguments>(2);
        if (enabledBackends.contains("jna")) {
            args.add(Arguments.of(loadBackend(JNA_BACKEND)));
        }
        if (enabledBackends.contains("ffm")) {
            args.add(Arguments.of(loadBackend(FFM_BACKEND)));
        }
        return args.stream();
    }

    private static Set<String> enabledBackends() {
        var property = System.getProperty("pcre4j.test.backends");
        if (property == null || property.isBlank()) {
            return Set.of("jna", "ffm");
        }
        var backends = new java.util.HashSet<String>();
        for (var part : property.split(",")) {
            var trimmed = part.trim().toLowerCase();
            if (!trimmed.isEmpty()) {
                backends.add(trimmed);
            }
        }
        return backends;
    }
}
