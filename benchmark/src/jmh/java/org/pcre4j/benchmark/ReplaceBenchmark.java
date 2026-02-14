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
import java.util.regex.Pattern;

/**
 * Benchmarks string replacement operations ({@code replaceAll} and {@code replaceFirst}).
 *
 * <p>Measures the combined cost of pattern matching and string construction during
 * replacement, with varying numbers of matches in the input.</p>
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(value = 2, jvmArgsAppend = "--enable-preview")
public class ReplaceBenchmark extends BenchmarkBase {

    @Param({"few-matches", "many-matches"})
    private String matchDensity;

    private Pattern javaPattern;
    private org.pcre4j.regex.Pattern pcre4jPattern;
    private String input;
    private String replacement;

    @Setup(Level.Trial)
    public void setupPatterns() {
        String regex = "\\b\\d+\\b";
        replacement = "NUM";

        switch (matchDensity) {
            case "few-matches":
                input = "There are 3 cats and 5 dogs in the park near the old 42 oak street";
                break;
            case "many-matches":
                input = "Values: 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20";
                break;
            default:
                throw new IllegalArgumentException("Unknown match density: " + matchDensity);
        }

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
    public void replaceAll(Blackhole bh) {
        if (isPcre4j()) {
            bh.consume(pcre4jPattern.matcher(input).replaceAll(replacement));
        } else {
            bh.consume(javaPattern.matcher(input).replaceAll(replacement));
        }
    }

    @Benchmark
    public void replaceFirst(Blackhole bh) {
        if (isPcre4j()) {
            bh.consume(pcre4jPattern.matcher(input).replaceFirst(replacement));
        } else {
            bh.consume(javaPattern.matcher(input).replaceFirst(replacement));
        }
    }
}
