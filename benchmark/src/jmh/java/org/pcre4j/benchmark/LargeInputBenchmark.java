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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Benchmarks pattern matching on large input strings (1KB to 1MB).
 *
 * <p>Measures how matching performance scales with input size, simulating log parsing
 * and text processing workloads. This highlights the throughput advantage of PCRE2 JIT
 * compilation on large volumes of text.</p>
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(value = 2, jvmArgsAppend = "--enable-preview")
public class LargeInputBenchmark extends BenchmarkBase {

    @Param({"1024", "65536", "1048576"})
    private int inputSize;

    private Pattern javaPattern;
    private org.pcre4j.regex.Pattern pcre4jPattern;
    private String input;

    @Setup(Level.Trial)
    public void setupPatterns() {
        String regex = "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b";

        // Build a realistic log-like input with occasional IP addresses
        var sb = new StringBuilder(inputSize + 256);
        String logLine = "2024-01-15 10:30:45 INFO  Request from 192.168.1.100 processed in 42ms\n";
        String textLine = "The quick brown fox jumps over the lazy dog and runs around the park\n";
        while (sb.length() < inputSize) {
            if (sb.length() % 5 == 0) {
                sb.append(logLine);
            } else {
                sb.append(textLine);
            }
        }
        input = sb.substring(0, inputSize);

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
    public void findAll(Blackhole bh) {
        if (isPcre4j()) {
            var matcher = pcre4jPattern.matcher(input);
            while (matcher.find()) {
                bh.consume(matcher.group());
            }
        } else {
            Matcher matcher = javaPattern.matcher(input);
            while (matcher.find()) {
                bh.consume(matcher.group());
            }
        }
    }
}
