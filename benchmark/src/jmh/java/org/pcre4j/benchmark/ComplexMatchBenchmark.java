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
 * Benchmarks complex real-world pattern matching: email addresses, URLs, dates, and log parsing.
 *
 * <p>These patterns exercise quantifiers, groups, lookahead, and character class nesting,
 * representing realistic workloads in production applications.</p>
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(value = 2, jvmArgsAppend = "--enable-preview")
public class ComplexMatchBenchmark extends BenchmarkBase {

    @Param({"email", "url", "log-line"})
    private String patternType;

    private Pattern javaPattern;
    private org.pcre4j.regex.Pattern pcre4jPattern;
    private String input;

    @Setup(Level.Trial)
    public void setupPatterns() {
        String regex;
        switch (patternType) {
            case "email":
                regex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
                input = "Contact us at support@example.com or sales@company.org. "
                        + "Invalid emails: @missing.com, user@, plain text. "
                        + "More: admin@test.io, info@sub.domain.co.uk";
                break;
            case "url":
                regex = "https?://[\\w.-]+(?:/[\\w./?%&=+-]*)?";
                input = "Visit https://example.com/path?q=1&r=2 or http://test.org/page. "
                        + "See also https://sub.domain.io/a/b/c and http://localhost:8080/api";
                break;
            case "log-line":
                regex = "(\\d{4}-\\d{2}-\\d{2})T(\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+(\\w+)\\s+\\[([^]]+)]\\s+(.+)";
                input = "2024-01-15T10:30:45.123 INFO  [main] Application started successfully\n"
                        + "2024-01-15T10:30:46.456 WARN  [http-nio-8080-exec-1] Slow query detected: 2340ms\n"
                        + "2024-01-15T10:30:47.789 ERROR [scheduler-1] Failed to connect to database";
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
    public void findAllWithGroups(Blackhole bh) {
        if (isPcre4j()) {
            var matcher = pcre4jPattern.matcher(input);
            while (matcher.find()) {
                for (int i = 0; i <= matcher.groupCount(); i++) {
                    bh.consume(matcher.group(i));
                }
            }
        } else {
            Matcher matcher = javaPattern.matcher(input);
            while (matcher.find()) {
                for (int i = 0; i <= matcher.groupCount(); i++) {
                    bh.consume(matcher.group(i));
                }
            }
        }
    }
}
