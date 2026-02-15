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
import org.pcre4j.exception.Pcre2MatchException;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Benchmarks pathological backtracking patterns (ReDoS-prone).
 *
 * <p>Demonstrates PCRE2's match limit protection against catastrophic backtracking.
 * The pattern {@code (a+)+b} with an input of repeated {@code a}s causes exponential
 * backtracking in {@link java.util.regex}, while PCRE4J terminates predictably via
 * its match limit mechanism.</p>
 *
 * <p>This benchmark highlights a key safety differentiator: PCRE4J provides bounded
 * execution time for ReDoS-prone patterns, preventing denial-of-service scenarios.</p>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(value = 1, jvmArgsAppend = "--enable-preview")
public class BacktrackingBenchmark extends BenchmarkBase {

    @Param({"15", "20", "25"})
    private int inputLength;

    private Pattern javaPattern;
    private org.pcre4j.regex.Pattern pcre4jPattern;
    private String input;

    @Setup(Level.Trial)
    public void setupPatterns() {
        String regex = "(a+)+b";
        input = "a".repeat(inputLength);

        if (isPcre4j()) {
            if (jit) {
                pcre4jPattern = org.pcre4j.regex.Pattern.compile(api, regex);
            } else {
                System.setProperty("pcre2.regex.jit", "false");
                pcre4jPattern = org.pcre4j.regex.Pattern.compile(api, regex);
                System.clearProperty("pcre2.regex.jit");
            }
        } else {
            javaPattern = Pattern.compile(regex);
        }
    }

    @Benchmark
    public boolean matchAttempt(Blackhole bh) {
        if (isPcre4j()) {
            try {
                return pcre4jPattern.matcher(input).matches();
            } catch (Pcre2MatchException e) {
                bh.consume(e);
                return false;
            }
        } else {
            return javaPattern.matcher(input).matches();
        }
    }
}
