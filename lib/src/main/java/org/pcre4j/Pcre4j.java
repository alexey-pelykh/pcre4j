/*
 * Copyright (C) 2024 Oleksii PELYKH
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


/**
 * Global singleton that holds the active {@link IPcre2} backend instance.
 *
 * <h2>Initialization</h2>
 *
 * <p>Applications must call {@link #setup(IPcre2)} once before using any PCRE4J convenience API
 * that relies on the global backend (e.g.&nbsp;{@link Pcre2Code#Pcre2Code(String)},
 * {@code org.pcre4j.regex.Pattern.compile(String)}). A static initializer in the application's
 * entry point is the recommended approach:</p>
 *
 * <pre>{@code
 * static {
 *     Pcre4j.setup(new org.pcre4j.jna.Pcre2());   // or org.pcre4j.ffm.Pcre2
 * }
 * }</pre>
 *
 * <p>Calling {@link #api()} before {@link #setup(IPcre2)} throws
 * {@link IllegalStateException}.</p>
 *
 * <h2>Thread Safety</h2>
 *
 * <p>Both {@link #setup(IPcre2)} and {@link #api()} are synchronized and safe to call from any
 * thread. The backend reference may be replaced by calling {@link #setup(IPcre2)} again; existing
 * {@link Pcre2Code} instances are unaffected because each instance captures the {@link IPcre2}
 * reference at construction time.</p>
 *
 * <h2>Multi-Classloader Environments</h2>
 *
 * <p>The singleton is held in a {@code static} field of this class. In environments where the same
 * JAR is loaded by multiple classloaders (application servers, OSGi, plugin frameworks), each
 * classloader creates its own copy of the {@code Pcre4j} class and therefore its own independent
 * singleton. This means:</p>
 *
 * <ul>
 *   <li>Each classloader must call {@link #setup(IPcre2)} independently.</li>
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
     * Return the global {@link IPcre2} backend previously installed via {@link #setup(IPcre2)}.
     *
     * @return the active backend instance
     * @throws IllegalStateException if {@link #setup(IPcre2)} has not been called yet
     */
    public static IPcre2 api() {
        synchronized (lock) {
            if (api == null) {
                throw new IllegalStateException("Call Pcre4j.setup() first.");
            }
            return api;
        }
    }

}
