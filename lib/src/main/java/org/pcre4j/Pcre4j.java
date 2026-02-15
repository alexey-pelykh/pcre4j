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

import org.pcre4j.api.IPcre2;
import org.pcre4j.api.Pcre2UtfWidth;

import java.util.ServiceLoader;


/**
 * Backend holder that provides three tiers of backend resolution for the PCRE4J library.
 *
 * <p>When {@link #api()} is called, the backend is resolved in the following order:</p>
 *
 * <ol>
 *   <li><strong>Thread-scoped backend</strong> — set via {@link #withBackend(IPcre2)}, active only
 *       for the current thread within a try-with-resources block. Scopes can be nested; closing a
 *       scope restores the previous one.</li>
 *   <li><strong>Global backend</strong> — set via {@link #setup(IPcre2)}, or auto-discovered on the
 *       first call to {@link #api()} using {@link ServiceLoader}. When both the FFM and JNA
 *       backends are present, the FFM backend is preferred for its better performance.</li>
 * </ol>
 *
 * <h2>Initialization</h2>
 *
 * <p>The global backend can be initialized in two ways:</p>
 *
 * <ol>
 *   <li><strong>Automatic discovery (recommended)</strong> — simply add a backend artifact
 *       ({@code pcre4j-jna} or {@code pcre4j-ffm}) to your classpath. The first call to
 *       {@link #api()} will use {@link ServiceLoader} to discover and initialize a backend
 *       automatically.</li>
 *   <li><strong>Explicit setup</strong> — call {@link #setup(IPcre2)} with a backend instance
 *       before any other PCRE4J usage. This takes priority over auto-discovery.</li>
 * </ol>
 *
 * <p>Example with explicit setup:</p>
 *
 * <pre>{@code
 * static {
 *     Pcre4j.setup(new org.pcre4j.jna.Pcre2());   // or org.pcre4j.ffm.Pcre2
 * }
 * }</pre>
 *
 * <h2>Scoped Backend</h2>
 *
 * <p>Use {@link #withBackend(IPcre2)} to temporarily override the backend for the current thread.
 * All PCRE4J operations within the scope will use the scoped backend instead of the global one.
 * Scopes can be nested and are restored when closed:</p>
 *
 * <pre>{@code
 * Pcre4j.setup(new org.pcre4j.jna.Pcre2());           // global default: JNA
 *
 * try (var scope = Pcre4j.withBackend(ffmBackend)) {
 *     Pattern p = Pattern.compile("\\d+");             // uses FFM
 *     try (var inner = Pcre4j.withBackend(jnaBackend)) {
 *         Pattern q = Pattern.compile("\\w+");         // uses JNA
 *     }
 *     // back to FFM
 * }
 * // back to global JNA
 * }</pre>
 *
 * <h2>Thread Safety</h2>
 *
 * <p>{@link #setup(IPcre2)} and {@link #api()} are synchronized and safe to call from any thread.
 * The global backend reference may be replaced by calling {@link #setup(IPcre2)} again; existing
 * {@link Pcre2Code} instances are unaffected because each instance captures the {@link IPcre2}
 * reference at construction time.</p>
 *
 * <p>Thread-scoped backends are inherently thread-safe because each thread has its own scope stack.
 * Virtual threads (Java 21+) each have their own {@link ThreadLocal} state, so scoped backends work
 * correctly with virtual threads. For structured concurrency scenarios where a backend must be
 * inherited by child tasks, use the explicit-API overloads instead.</p>
 *
 * <h2>Multi-Classloader Environments</h2>
 *
 * <p>The singleton is held in a {@code static} field of this class. In environments where the same
 * JAR is loaded by multiple classloaders (application servers, OSGi, plugin frameworks), each
 * classloader creates its own copy of the {@code Pcre4j} class and therefore its own independent
 * singleton. This means:</p>
 *
 * <ul>
 *   <li>Each classloader must call {@link #setup(IPcre2)} independently (or rely on
 *       auto-discovery independently).</li>
 *   <li>{@link Pcre2Code} and other objects created under one classloader cannot be mixed with a
 *       backend set up under a different classloader.</li>
 * </ul>
 *
 * <p>To avoid classloader isolation issues, place the PCRE4J JARs in a shared classloader (e.g.
 * the server-level classpath) so that all applications share a single {@code Pcre4j} class
 * definition and a single {@link #setup(IPcre2)} call.</p>
 *
 * <p>Alternatively, use the explicit-API overloads (e.g.
 * {@link Pcre2Code#Pcre2Code(IPcre2, String)},
 * {@code org.pcre4j.regex.Pattern.compile(IPcre2, String)}) to bypass the global singleton
 * entirely.</p>
 */
public final class Pcre4j {

    private static final Object lock = new Object();
    private static IPcre2 api = null;
    private static final ThreadLocal<IPcre2> scopedApi = new ThreadLocal<>();

    private Pcre4j() {
    }

    /**
     * Set up the PCRE4J library by installing the given backend as the global default.
     *
     * <p>May be called more than once; subsequent calls replace the active backend. Existing
     * {@link Pcre2Code} instances retain the backend they were compiled with.</p>
     *
     * @param api the PCRE2 backend to use; must not be {@code null} and must support UTF-8
     * @throws IllegalArgumentException if {@code api} is {@code null} or does not support UTF-8
     */
    public static void setup(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var compiledWidths = Pcre4jUtils.getCompiledWidths(api);
        if (!compiledWidths.contains(Pcre2UtfWidth.UTF8)) {
            throw new IllegalArgumentException("api must support UTF-8, yet it only supports " + compiledWidths);
        }

        synchronized (lock) {
            Pcre4j.api = api;
        }
    }

    /**
     * Set a thread-scoped backend override that takes precedence over the global backend.
     *
     * <p>The returned {@link AutoCloseable} must be used in a try-with-resources block. When the
     * scope is closed, the previous thread-scoped backend (or no thread-scoped backend) is restored.
     * Scopes can be nested; each close restores exactly the state that existed before the
     * corresponding {@code withBackend} call.</p>
     *
     * <p>The scoped backend is not inherited by child threads. For scenarios where a backend must be
     * shared across threads (e.g. structured concurrency with virtual threads), use the explicit-API
     * overloads such as {@link Pcre2Code#Pcre2Code(IPcre2, String)} instead.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     * try (var scope = Pcre4j.withBackend(ffmBackend)) {
     *     Pattern p = Pattern.compile("\\d+");  // uses ffmBackend
     * }
     * // previous backend restored
     * }</pre>
     *
     * @param api the PCRE2 backend to use within the scope; must not be {@code null} and must
     *            support UTF-8
     * @return an {@link AutoCloseable} that restores the previous backend when closed
     * @throws IllegalArgumentException if {@code api} is {@code null} or does not support UTF-8
     */
    public static AutoCloseable withBackend(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var compiledWidths = Pcre4jUtils.getCompiledWidths(api);
        if (!compiledWidths.contains(Pcre2UtfWidth.UTF8)) {
            throw new IllegalArgumentException("api must support UTF-8, yet it only supports " + compiledWidths);
        }

        final var previous = scopedApi.get();
        scopedApi.set(api);
        return () -> {
            if (previous == null) {
                scopedApi.remove();
            } else {
                scopedApi.set(previous);
            }
        };
    }

    /**
     * Return the active {@link IPcre2} backend for the current context.
     *
     * <p>Resolution order:</p>
     * <ol>
     *   <li>Thread-scoped backend set by {@link #withBackend(IPcre2)}, if any.</li>
     *   <li>Global backend set by {@link #setup(IPcre2)}, or auto-discovered via
     *       {@link ServiceLoader}. When both the FFM and JNA backends are present on the classpath,
     *       the FFM backend is preferred for its better performance. A discovered backend must
     *       support UTF-8; backends that fail to load or lack UTF-8 support are skipped.</li>
     * </ol>
     *
     * @return the active backend instance
     * @throws IllegalStateException if no backend has been set up and none could be discovered
     */
    public static IPcre2 api() {
        final var scoped = scopedApi.get();
        if (scoped != null) {
            return scoped;
        }

        synchronized (lock) {
            if (api == null) {
                api = discoverBackend();
                if (api == null) {
                    throw new IllegalStateException(
                            "No PCRE2 backend found. Add pcre4j-jna or pcre4j-ffm to your classpath, "
                                    + "or call Pcre4j.setup() explicitly."
                    );
                }
            }
            return api;
        }
    }

    /**
     * Attempt to discover a usable {@link IPcre2} backend via {@link ServiceLoader}.
     *
     * <p>Backends are loaded lazily and checked for UTF-8 support. The FFM backend
     * ({@code org.pcre4j.ffm.Pcre2}) is preferred over the JNA backend
     * ({@code org.pcre4j.jna.Pcre2}) when both are available.</p>
     *
     * @return a usable backend, or {@code null} if none could be loaded
     */
    private static IPcre2 discoverBackend() {
        final var loader = ServiceLoader.load(IPcre2.class);

        IPcre2 fallback = null;
        for (var provider : loader) {
            try {
                final var candidate = provider;
                final var compiledWidths = Pcre4jUtils.getCompiledWidths(candidate);
                if (!compiledWidths.contains(Pcre2UtfWidth.UTF8)) {
                    continue;
                }
                if (candidate.getClass().getName().equals("org.pcre4j.ffm.Pcre2")) {
                    return candidate;
                }
                if (fallback == null) {
                    fallback = candidate;
                }
            } catch (Exception ignored) {
                // Backend failed to load (e.g. native library not found, FFM preview not
                // enabled on Java 21); skip and try the next one.
            }
        }
        return fallback;
    }

}
