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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Benchmarks simple pattern matching: literal strings, character classes, and alternation.
 *
 * <p>Compares {@link java.util.regex} against PCRE4J (JNA/FFM, interpreted/JIT) for basic
 * matching operations that are representative of common use cases.</p>
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(value = 2, jvmArgsAppend = "--enable-preview")
public class SimpleMatchBenchmark extends BenchmarkBase {

    @Param({"literal", "character-class", "alternation"})
    private String patternType;

    private Pattern javaPattern;
    private org.pcre4j.regex.Pattern pcre4jPattern;
    private String input;

    @Setup(Level.Trial)
    public void setupPatterns() {
        String regex;
        switch (patternType) {
            case "literal":
                regex = "hello";
                input = "The quick brown fox says hello to the world and hello again to everyone nearby";
                break;
            case "character-class":
                regex = "[a-zA-Z]+";
                input = "abc123 DEF456 ghi789 JKL012 mno345 PQR678 stu901 VWX234 yz0";
                break;
            case "alternation":
                regex = "cat|dog|bird|fish";
                input = "I have a cat and a dog, my neighbor has a bird, and the pond has fish";
                break;
            default:
                throw new IllegalArgumentException("Unknown pattern type: " + patternType);
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

    @Benchmark
    public boolean matches() {
        if (isPcre4j()) {
            return pcre4jPattern.matcher(input).find();
        } else {
            return javaPattern.matcher(input).find();
        }
    }
}
