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
package org.pcre4j;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Thread-safety tests for {@link Pcre2Code}.
 *
 * <p>Compiled PCRE2 patterns are immutable and safe to share across threads for matching. These tests verify that
 * concurrent matching on a shared {@link Pcre2Code} instance produces correct results without corruption.</p>
 *
 * <p>Note: {@link Reference#reachabilityFence(Object)} calls are used to prevent the JVM from collecting
 * {@link Pcre2MatchData} instances (and triggering their Cleaner to free native memory) before the native
 * match call has returned.</p>
 */
@Tag("stress")
public class Pcre2CodeThreadSafetyTests {

    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2;
    private static final int ITERATIONS_PER_THREAD = 10_000;

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentMatchingOnSharedPattern(IPcre2 api) throws Exception {
        var code = new Pcre2Code(api, "\\d+");
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT; t++) {
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                            var matchData = new Pcre2MatchData(api, 1);
                            var result = code.match(
                                    "abc123def",
                                    0,
                                    EnumSet.noneOf(Pcre2MatchOption.class),
                                    matchData,
                                    null
                            );
                            Reference.reachabilityFence(matchData);
                            if (result < 1) {
                                errors.add(new AssertionError(
                                        "Expected match but got result=" + result
                                ));
                            }
                        }
                    } catch (Throwable e) {
                        errors.add(e);
                    }
                }));
            }

            latch.countDown();
            for (var future : futures) {
                future.get(60, TimeUnit.SECONDS);
            }
        }

        assertTrue(errors.isEmpty(), () -> "Concurrent matching errors: " + errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentMatchingWithCaptures(IPcre2 api) throws Exception {
        var code = new Pcre2Code(api, "(\\w+)@(\\w+\\.\\w+)");
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);
        var subjects = List.of(
                "user@example.com",
                "admin@test.org",
                "info@domain.net",
                "hello@world.io"
        );

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT; t++) {
                final int threadIndex = t;
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        var subject = subjects.get(threadIndex % subjects.size());
                        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                            var matchData = new Pcre2MatchData(api, 3);
                            var result = code.match(
                                    subject,
                                    0,
                                    EnumSet.noneOf(Pcre2MatchOption.class),
                                    matchData,
                                    null
                            );
                            Reference.reachabilityFence(matchData);
                            if (result < 1) {
                                errors.add(new AssertionError(
                                        "Expected match on '" + subject + "' but got result=" + result
                                ));
                            }
                        }
                    } catch (Throwable e) {
                        errors.add(e);
                    }
                }));
            }

            latch.countDown();
            for (var future : futures) {
                future.get(60, TimeUnit.SECONDS);
            }
        }

        assertTrue(errors.isEmpty(), () -> "Concurrent capture matching errors: " + errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentSubstituteOnSharedPattern(IPcre2 api) throws Exception {
        var code = new Pcre2Code(api, "\\d+");
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT; t++) {
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                            var matchData = new Pcre2MatchData(api, 1);
                            var result = code.substitute(
                                    "abc123def",
                                    0,
                                    EnumSet.noneOf(Pcre2SubstituteOption.class),
                                    matchData,
                                    null,
                                    "NUM"
                            );
                            Reference.reachabilityFence(matchData);
                            if (!"abcNUMdef".equals(result)) {
                                errors.add(new AssertionError(
                                        "Expected 'abcNUMdef' but got '" + result + "'"
                                ));
                            }
                        }
                    } catch (Throwable e) {
                        errors.add(e);
                    }
                }));
            }

            latch.countDown();
            for (var future : futures) {
                future.get(60, TimeUnit.SECONDS);
            }
        }

        assertTrue(errors.isEmpty(), () -> "Concurrent substitute errors: " + errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentPatternInfoAccess(IPcre2 api) throws Exception {
        var code = new Pcre2Code(api, "(\\w+)(\\d+)(\\s+)");
        var expectedCaptureCount = code.captureCount();
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT; t++) {
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                            var captureCount = code.captureCount();
                            if (captureCount != expectedCaptureCount) {
                                errors.add(new AssertionError(
                                        "Expected captureCount=" + expectedCaptureCount
                                                + " but got " + captureCount
                                ));
                            }
                            code.size();
                        }
                    } catch (Throwable e) {
                        errors.add(e);
                    }
                }));
            }

            latch.countDown();
            for (var future : futures) {
                future.get(60, TimeUnit.SECONDS);
            }
        }

        assertTrue(errors.isEmpty(), () -> "Concurrent pattern info errors: " + errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentMatchAndSubstituteMixed(IPcre2 api) throws Exception {
        var code = new Pcre2Code(api, "(\\w+)");
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT; t++) {
                final boolean doSubstitute = (t % 2 == 0);
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                            var matchData = new Pcre2MatchData(api, 2);
                            if (doSubstitute) {
                                var result = code.substitute(
                                        "hello",
                                        0,
                                        EnumSet.noneOf(Pcre2SubstituteOption.class),
                                        matchData,
                                        null,
                                        "world"
                                );
                                if (!"world".equals(result)) {
                                    errors.add(new AssertionError(
                                            "Expected 'world' but got '" + result + "'"
                                    ));
                                }
                            } else {
                                var result = code.match(
                                        "hello",
                                        0,
                                        EnumSet.noneOf(Pcre2MatchOption.class),
                                        matchData,
                                        null
                                );
                                if (result < 1) {
                                    errors.add(new AssertionError(
                                            "Expected match but got result=" + result
                                    ));
                                }
                            }
                            Reference.reachabilityFence(matchData);
                        }
                    } catch (Throwable e) {
                        errors.add(e);
                    }
                }));
            }

            latch.countDown();
            for (var future : futures) {
                future.get(60, TimeUnit.SECONDS);
            }
        }

        assertTrue(errors.isEmpty(), () -> "Concurrent mixed operations errors: " + errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void highConcurrencyMatchCorrectness(IPcre2 api) throws Exception {
        var code = new Pcre2Code(api, "^(\\d{3})-(\\d{3})-(\\d{4})$");
        var subject = "555-123-4567";
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);
        var matchCount = new java.util.concurrent.atomic.AtomicInteger(0);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT * 4; t++) {
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < ITERATIONS_PER_THREAD / 2; i++) {
                            var matchData = new Pcre2MatchData(api, 4);
                            var result = code.match(
                                    subject,
                                    0,
                                    EnumSet.noneOf(Pcre2MatchOption.class),
                                    matchData,
                                    null
                            );
                            Reference.reachabilityFence(matchData);
                            if (result >= 1) {
                                matchCount.incrementAndGet();
                            } else {
                                errors.add(new AssertionError(
                                        "Expected match but got result=" + result
                                ));
                            }
                        }
                    } catch (Throwable e) {
                        errors.add(e);
                    }
                }));
            }

            latch.countDown();
            for (var future : futures) {
                future.get(120, TimeUnit.SECONDS);
            }
        }

        assertTrue(errors.isEmpty(), () -> "High concurrency match errors: " + errors.getFirst());
        assertEquals(
                THREAD_COUNT * 4 * (ITERATIONS_PER_THREAD / 2),
                matchCount.get(),
                "All matches should have succeeded"
        );
    }
}
