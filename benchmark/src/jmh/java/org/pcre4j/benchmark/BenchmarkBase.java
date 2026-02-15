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
package org.pcre4j.benchmark;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.pcre4j.Pcre4jUtils;
import org.pcre4j.api.IPcre2;

/**
 * Base state for PCRE4J benchmarks providing engine selection via {@code @Param}.
 *
 * <p>Engines:</p>
 * <ul>
 *   <li>{@code java-util-regex} &mdash; standard {@link java.util.regex} API</li>
 *   <li>{@code pcre4j-jna} &mdash; PCRE4J with JNA backend (interpreted PCRE2)</li>
 *   <li>{@code pcre4j-ffm} &mdash; PCRE4J with FFM backend (interpreted PCRE2)</li>
 *   <li>{@code pcre4j-jna-jit} &mdash; PCRE4J with JNA backend (JIT-compiled PCRE2)</li>
 *   <li>{@code pcre4j-ffm-jit} &mdash; PCRE4J with FFM backend (JIT-compiled PCRE2)</li>
 * </ul>
 */
@State(Scope.Benchmark)
public abstract class BenchmarkBase {

    private static final String JNA_BACKEND = "org.pcre4j.jna.Pcre2";
    private static final String FFM_BACKEND = "org.pcre4j.ffm.Pcre2";

    @Param({"java-util-regex", "pcre4j-jna", "pcre4j-ffm", "pcre4j-jna-jit", "pcre4j-ffm-jit"})
    protected String engine;

    protected IPcre2 api;
    protected boolean jit;

    @Setup(Level.Trial)
    public void setupEngine() {
        switch (engine) {
            case "java-util-regex":
                api = null;
                jit = false;
                break;
            case "pcre4j-jna":
                api = loadBackend(JNA_BACKEND);
                jit = false;
                break;
            case "pcre4j-ffm":
                api = loadBackend(FFM_BACKEND);
                jit = false;
                break;
            case "pcre4j-jna-jit":
                api = loadBackend(JNA_BACKEND);
                jit = true;
                if (!Pcre4jUtils.isJitSupported(api)) {
                    throw new UnsupportedOperationException("JIT not supported on this platform (JNA)");
                }
                break;
            case "pcre4j-ffm-jit":
                api = loadBackend(FFM_BACKEND);
                jit = true;
                if (!Pcre4jUtils.isJitSupported(api)) {
                    throw new UnsupportedOperationException("JIT not supported on this platform (FFM)");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown engine: " + engine);
        }
    }

    protected boolean isPcre4j() {
        return api != null;
    }

    private static IPcre2 loadBackend(String className) {
        try {
            return (IPcre2) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load backend: " + className, e);
        }
    }
}
