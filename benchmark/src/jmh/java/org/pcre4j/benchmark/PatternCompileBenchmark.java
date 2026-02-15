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

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Benchmarks pattern compilation speed across engines.
 *
 * <p>Measures the cost of compiling patterns of varying complexity, from simple literals
 * to complex patterns with groups and quantifiers. This is important for applications that
 * compile patterns dynamically rather than reusing pre-compiled patterns.</p>
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(value = 2, jvmArgsAppend = "--enable-preview")
public class PatternCompileBenchmark extends BenchmarkBase {

    @Param({"simple", "moderate", "complex"})
    private String complexity;

    private String regex;

    @Setup(Level.Trial)
    public void setupRegex() {
        switch (complexity) {
            case "simple":
                regex = "hello";
                break;
            case "moderate":
                regex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
                break;
            case "complex":
                regex = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
                        + "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
                break;
            default:
                throw new IllegalArgumentException("Unknown complexity: " + complexity);
        }
    }

    @Benchmark
    public void compile(Blackhole bh) {
        if (isPcre4j()) {
            if (jit) {
                bh.consume(org.pcre4j.regex.Pattern.compile(api, regex));
            } else {
                System.setProperty("pcre2.regex.jit", "false");
                bh.consume(org.pcre4j.regex.Pattern.compile(api, regex));
                System.clearProperty("pcre2.regex.jit");
            }
        } else {
            bh.consume(Pattern.compile(regex));
        }
    }
}
