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
package org.pcre4j.regex;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Thread-safety tests for the {@code java.util.regex}-compatible API layer.
 *
 * <p>{@link Pattern} is immutable and safe to share across threads. Each thread should create its own
 * {@link Matcher} instance via {@link Pattern#matcher(CharSequence)}. These tests verify these
 * thread-safety guarantees under concurrent load.</p>
 */
@Tag("stress")
public class PatternThreadSafetyTests {

    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2;
    private static final int ITERATIONS_PER_THREAD = 10_000;

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentMatcherCreationFromSharedPattern(IPcre2 api) throws Exception {
        var pattern = Pattern.compile(api, "(\\w+)@(\\w+\\.\\w+)");
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT; t++) {
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                            var matcher = pattern.matcher("user@example.com");
                            if (!matcher.find()) {
                                errors.add(new AssertionError("Expected match on 'user@example.com'"));
                            }
                            if (!"user".equals(matcher.group(1))) {
                                errors.add(new AssertionError(
                                        "Expected group(1)='user' but got '" + matcher.group(1) + "'"
                                ));
                            }
                            if (!"example.com".equals(matcher.group(2))) {
                                errors.add(new AssertionError(
                                        "Expected group(2)='example.com' but got '" + matcher.group(2) + "'"
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

        assertTrue(errors.isEmpty(), () -> "Concurrent matcher creation errors: " + errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentFindOnDifferentInputs(IPcre2 api) throws Exception {
        var pattern = Pattern.compile(api, "\\d+");
        var inputs = List.of("abc123", "def456", "ghi789", "jkl012");
        var expected = List.of("123", "456", "789", "012");
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT; t++) {
                final int threadIndex = t;
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        int idx = threadIndex % inputs.size();
                        var input = inputs.get(idx);
                        var expectedMatch = expected.get(idx);
                        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                            var matcher = pattern.matcher(input);
                            if (!matcher.find()) {
                                errors.add(new AssertionError(
                                        "Expected match on '" + input + "'"
                                ));
                                return;
                            }
                            var group = matcher.group();
                            if (!expectedMatch.equals(group)) {
                                errors.add(new AssertionError(
                                        "Expected '" + expectedMatch + "' but got '" + group + "'"
                                ));
                                return;
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

        assertTrue(errors.isEmpty(), () -> "Concurrent find errors: " + errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentMatches(IPcre2 api) throws Exception {
        var pattern = Pattern.compile(api, "^\\d{3}-\\d{3}-\\d{4}$");
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT; t++) {
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                            var matcher = pattern.matcher("555-123-4567");
                            if (!matcher.matches()) {
                                errors.add(new AssertionError(
                                        "Expected matches() to return true"
                                ));
                                return;
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

        assertTrue(errors.isEmpty(), () -> "Concurrent matches errors: " + errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentSplit(IPcre2 api) throws Exception {
        var pattern = Pattern.compile(api, "[,;]\\s*");
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT; t++) {
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                            var parts = pattern.split("apple, banana; cherry, date");
                            if (parts.length != 4) {
                                errors.add(new AssertionError(
                                        "Expected 4 parts but got " + parts.length
                                ));
                                return;
                            }
                            if (!"apple".equals(parts[0])) {
                                errors.add(new AssertionError(
                                        "Expected 'apple' but got '" + parts[0] + "'"
                                ));
                                return;
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

        assertTrue(errors.isEmpty(), () -> "Concurrent split errors: " + errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentReplaceAll(IPcre2 api) throws Exception {
        var pattern = Pattern.compile(api, "\\d+");
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT; t++) {
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                            var matcher = pattern.matcher("abc123def456ghi");
                            var result = matcher.replaceAll("NUM");
                            if (!"abcNUMdefNUMghi".equals(result)) {
                                errors.add(new AssertionError(
                                        "Expected 'abcNUMdefNUMghi' but got '" + result + "'"
                                ));
                                return;
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

        assertTrue(errors.isEmpty(), () -> "Concurrent replaceAll errors: " + errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentLazyInitialization(IPcre2 api) throws Exception {
        // Pattern with CASE_INSENSITIVE triggers lazy compilation of matchingCode/lookingAtCode
        var pattern = Pattern.compile(api, "hello", Pattern.CASE_INSENSITIVE);
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            // Use a high thread count to stress the double-checked locking
            for (int t = 0; t < THREAD_COUNT * 4; t++) {
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < 1_000; i++) {
                            var matcher = pattern.matcher("HELLO world");
                            if (!matcher.find()) {
                                errors.add(new AssertionError("Expected find() to succeed"));
                                return;
                            }
                            if (!"HELLO".equals(matcher.group())) {
                                errors.add(new AssertionError(
                                        "Expected 'HELLO' but got '" + matcher.group() + "'"
                                ));
                                return;
                            }

                            matcher.reset("HeLLo");
                            if (!matcher.matches()) {
                                errors.add(new AssertionError("Expected matches() to succeed"));
                                return;
                            }

                            matcher.reset("HELLO world");
                            if (!matcher.lookingAt()) {
                                errors.add(new AssertionError(
                                        "Expected lookingAt() to succeed for prefix match"
                                ));
                                return;
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

        assertTrue(errors.isEmpty(), () -> "Concurrent lazy init errors: " + errors.getFirst());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void concurrentPatternCreationAndMatching(IPcre2 api) throws Exception {
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT; t++) {
                final int threadIndex = t;
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < 5_000; i++) {
                            var pattern = Pattern.compile(
                                    api, "thread" + threadIndex + "_" + (i % 100)
                            );
                            var matcher = pattern.matcher(
                                    "thread" + threadIndex + "_42 some text"
                            );
                            matcher.find();
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

        assertTrue(
                errors.isEmpty(),
                () -> "Concurrent pattern creation/matching errors: " + errors.getFirst()
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void highConcurrencyCorrectness(IPcre2 api) throws Exception {
        var pattern = Pattern.compile(api, "(\\w+)\\s+(\\w+)");
        var input = "hello world";
        var errors = Collections.synchronizedList(new ArrayList<Throwable>());
        var latch = new CountDownLatch(1);
        var matchCount = new java.util.concurrent.atomic.AtomicInteger(0);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();
            for (int t = 0; t < THREAD_COUNT * 4; t++) {
                futures.add(executor.submit(() -> {
                    try {
                        latch.await();
                        for (int i = 0; i < ITERATIONS_PER_THREAD / 2; i++) {
                            var matcher = pattern.matcher(input);
                            if (!matcher.find()) {
                                errors.add(new AssertionError("Expected match on '" + input + "'"));
                                return;
                            }
                            if (!"hello".equals(matcher.group(1))) {
                                errors.add(new AssertionError(
                                        "Expected group(1)='hello' but got '" + matcher.group(1) + "'"
                                ));
                                return;
                            }
                            if (!"world".equals(matcher.group(2))) {
                                errors.add(new AssertionError(
                                        "Expected group(2)='world' but got '" + matcher.group(2) + "'"
                                ));
                                return;
                            }
                            matchCount.incrementAndGet();
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

        assertTrue(errors.isEmpty(), () -> "High concurrency correctness errors: " + errors.getFirst());
        assertEquals(
                THREAD_COUNT * 4 * (ITERATIONS_PER_THREAD / 2),
                matchCount.get(),
                "All matches should have succeeded"
        );
    }
}
