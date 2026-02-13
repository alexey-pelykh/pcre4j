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
package org.pcre4j;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Stress tests for native memory management under load.
 *
 * <p>These tests verify that Cleaner-based resource management handles rapid creation and disposal of native
 * objects without memory leaks or double-free errors.</p>
 */
@Tag("stress")
public class NativeMemoryStressTests {

    private static final int RAPID_CREATION_COUNT = 100_000;
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2;

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void rapidPatternCreationAndGc(IPcre2 api) {
        for (int i = 0; i < RAPID_CREATION_COUNT; i++) {
            new Pcre2Code(api, "pattern" + (i % 1000));
            if (i % 10_000 == 0) {
                System.gc();
            }
        }

        assertDoesNotThrow(() -> {
            var code = new Pcre2Code(api, "final-pattern");
            var matchData = new Pcre2MatchData(api, 1);
            code.match("final-pattern", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        }, "Pattern creation should work after rapid creation/GC cycles");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void rapidMatchDataCreationAndGc(IPcre2 api) {
        var code = new Pcre2Code(api, "(\\w+)");

        for (int i = 0; i < RAPID_CREATION_COUNT; i++) {
            var matchData = new Pcre2MatchData(code);
            code.match("hello", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
            if (i % 10_000 == 0) {
                System.gc();
            }
        }

        assertDoesNotThrow(() -> {
            var matchData = new Pcre2MatchData(code);
            code.match("hello", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        }, "MatchData creation should work after rapid creation/GC cycles");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void rapidContextCreationAndGc(IPcre2 api) {
        for (int i = 0; i < RAPID_CREATION_COUNT / 10; i++) {
            var generalCtx = new Pcre2GeneralContext(api);
            new Pcre2CompileContext(api, generalCtx);
            new Pcre2MatchContext(api, generalCtx);
            if (i % 1_000 == 0) {
                System.gc();
            }
        }

        assertDoesNotThrow(() -> {
            var generalCtx = new Pcre2GeneralContext(api);
            new Pcre2CompileContext(api, generalCtx);
            new Pcre2MatchContext(api, generalCtx);
        }, "Context creation should work after rapid creation/GC cycles");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentPatternCreationAndDisposal(IPcre2 api) throws Exception {
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT; t++) {
                final int threadIndex = t;
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < 10_000; i++) {
                            var code = new Pcre2Code(
                                    api, "thread" + threadIndex + "_pattern" + (i % 100)
                            );
                            var matchData = new Pcre2MatchData(api, 1);
                            code.match(
                                    "thread" + threadIndex + "_pattern42",
                                    0,
                                    EnumSet.noneOf(Pcre2MatchOption.class),
                                    matchData,
                                    null
                            );
                            Reference.reachabilityFence(matchData);
                            Reference.reachabilityFence(code);
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

        assertTrue(errors.isEmpty(), () -> "Concurrent pattern creation errors: " + errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentMatchDataCreationAndDisposal(IPcre2 api) throws Exception {
        var code = new Pcre2Code(api, "(\\w+)-(\\d+)");
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT; t++) {
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < 10_000; i++) {
                            var matchData = new Pcre2MatchData(code);
                            code.match(
                                    "hello-42",
                                    0,
                                    EnumSet.noneOf(Pcre2MatchOption.class),
                                    matchData,
                                    null
                            );
                            Reference.reachabilityFence(matchData);
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

        assertTrue(errors.isEmpty(), () -> "Concurrent match data creation errors: " + errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchWhileGcRunsConcurrently(IPcre2 api) throws Exception {
        var code = new Pcre2Code(api, "\\d+");
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);
        var done = new java.util.concurrent.atomic.AtomicBoolean(false);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();

            // GC pressure thread: rapidly creates and discards patterns to trigger cleanup
            futures.add(executor.submit(() -> {
                try {
                    latch.await();
                    while (!done.get()) {
                        for (int i = 0; i < 100; i++) {
                            new Pcre2Code(api, "disposable" + i);
                        }
                        System.gc();
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Throwable e) {
                    errors.add(e);
                }
            }));

            // Matching threads: use the shared code while GC runs
            for (int t = 0; t < THREAD_COUNT; t++) {
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < 10_000; i++) {
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

            // Wait for matching threads (skip the GC thread at index 0)
            for (int i = 1; i < futures.size(); i++) {
                futures.get(i).get(120, TimeUnit.SECONDS);
            }
            done.set(true);
            futures.getFirst().get(10, TimeUnit.SECONDS);
        }

        assertTrue(errors.isEmpty(), () -> "Match-during-GC errors: " + errors.getFirst());
    }
}
