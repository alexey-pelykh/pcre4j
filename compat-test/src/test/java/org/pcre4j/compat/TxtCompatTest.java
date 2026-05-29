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
package org.pcre4j.compat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

class TxtCompatTest {

    private static ComparisonRecorder RECORDER;

    /**
     * Seconds before a single compare() invocation is abandoned.
     * java.util.regex can catastrophically backtrack on certain patterns with supplementary
     * characters; this guard prevents the test suite from hanging indefinitely.
     * Timed-out cases are reported as JUnit assumptions-aborted (skipped), not failed.
     */
    private static final int TIMEOUT_SECONDS = 10;

    private static final ExecutorService EXEC = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "compat-test-worker");
        t.setDaemon(true);
        return t;
    });

    @BeforeAll
    static void open() throws IOException {
        var out = Path.of("build/reports/compat/raw.jsonl");
        RECORDER = new ComparisonRecorder(out);
    }

    @AfterAll
    static void close() throws IOException {
        RECORDER.close();
    }

    static Stream<Arguments> cases() throws IOException {
        String[] files = {
            "TestCases.txt",
            "BMPTestCases.txt",
            "SupplementaryTestCases.txt",
            "GraphemeTestCases.txt"
        };
        List<Arguments> args = new ArrayList<>();
        for (String name : files) {
            String body = new String(
                    TxtCompatTest.class.getResourceAsStream("/imported/" + name).readAllBytes());
            for (TxtCaseRunner.Case c : TxtCaseRunner.parse(body)) {
                args.add(Arguments.of(name, c));
            }
        }
        return args.stream();
    }

    @ParameterizedTest(name = "{0}#{1}")
    @MethodSource("cases")
    void compare(String source, TxtCaseRunner.Case c) {
        Future<?> future = EXEC.submit(() -> {
            MatchProbe oracle = Probes.oracle(c.pattern(), c.input(), c.patternFlags());
            MatchProbe sut = Probes.sut(c.pattern(), c.input(), c.patternFlags());
            RECORDER.record(source, c.index(), c.pattern(), c.input(), c.patternFlags(), oracle, sut);
        });
        try {
            future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            RECORDER.recordTimeout(source, c.index(), c.pattern(), c.input(), c.patternFlags(),
                    TIMEOUT_SECONDS * 1000L);
            Assumptions.abort("Timed out after " + TIMEOUT_SECONDS + "s: " + source + "#" + c.index()
                    + " pattern=" + c.pattern());
        } catch (java.util.concurrent.ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new RuntimeException("Probe execution failed: " + cause.getMessage(), cause);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

