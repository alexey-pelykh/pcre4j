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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import java.lang.ref.WeakReference;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ResourceCleanupTests {

    /**
     * The maximum number of GC attempts before giving up.
     */
    private static final int MAX_GC_ATTEMPTS = 20;

    /**
     * Triggers garbage collection and waits for a weak reference to be cleared.
     * <p>
     * Allocates temporary objects between GC calls to create memory pressure,
     * which encourages the garbage collector to reclaim weakly-reachable objects
     * more reliably.
     *
     * @param ref the weak reference to wait for
     * @param maxAttempts the maximum number of GC attempts
     * @return true if the reference was cleared
     */
    @SuppressWarnings("unused")
    private static boolean awaitGc(WeakReference<?> ref, int maxAttempts) {
        for (int i = 0; i < maxAttempts && ref.get() != null; i++) {
            // Allocate temporary objects to create memory pressure
            byte[] pressure = new byte[1024 * 1024];
            System.gc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return ref.get() == null;
    }

    // --- Pcre2Code cleanup tests ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2CodeIsCollectedAfterRelease(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var ref = new WeakReference<>(code);
        code = null;
        assertNull(awaitGcAndReturn(ref), "Pcre2Code should be eligible for GC after releasing reference");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2CodeCreationWorksAfterPriorCleanup(IPcre2 api) {
        var code = new Pcre2Code(api, "first");
        var ref = new WeakReference<>(code);
        code = null;
        awaitGc(ref, MAX_GC_ATTEMPTS);

        assertDoesNotThrow(() -> {
            var newCode = new Pcre2Code(api, "second");
            var matchData = new Pcre2MatchData(api, 1);
            newCode.match("second", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        }, "Creating and using Pcre2Code should work after prior instance was cleaned up");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2CodeBulkCreationAndCleanup(IPcre2 api) {
        var lastRef = new WeakReference<>(new Object());
        for (int i = 0; i < 100; i++) {
            var code = new Pcre2Code(api, "pattern" + i);
            lastRef = new WeakReference<>(code);
        }
        awaitGc(lastRef, MAX_GC_ATTEMPTS);

        assertDoesNotThrow(() -> {
            var code = new Pcre2Code(api, "after-bulk");
            var matchData = new Pcre2MatchData(api, 1);
            code.match("after-bulk", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        }, "Creating Pcre2Code should work after bulk creation and cleanup");
    }

    // --- Pcre2MatchData cleanup tests ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2MatchDataIsCollectedAfterRelease(IPcre2 api) {
        var matchData = new Pcre2MatchData(api, 10);
        var ref = new WeakReference<>(matchData);
        matchData = null;
        assertNull(awaitGcAndReturn(ref), "Pcre2MatchData should be eligible for GC after releasing reference");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2MatchDataFromPatternIsCollectedAfterRelease(IPcre2 api) {
        var code = new Pcre2Code(api, "(test)");
        var matchData = new Pcre2MatchData(code);
        var ref = new WeakReference<>(matchData);
        matchData = null;
        assertNull(awaitGcAndReturn(ref), "Pcre2MatchData (from pattern) should be eligible for GC");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2MatchDataCreationWorksAfterPriorCleanup(IPcre2 api) {
        var matchData = new Pcre2MatchData(api, 10);
        var ref = new WeakReference<>(matchData);
        matchData = null;
        awaitGc(ref, MAX_GC_ATTEMPTS);

        assertDoesNotThrow(() -> {
            var code = new Pcre2Code(api, "(test)");
            var newMatchData = new Pcre2MatchData(code);
            code.match("test", 0, EnumSet.noneOf(Pcre2MatchOption.class), newMatchData, null);
        }, "Creating and using Pcre2MatchData should work after prior instance was cleaned up");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2MatchDataBulkCreationAndCleanup(IPcre2 api) {
        var lastRef = new WeakReference<>(new Object());
        for (int i = 0; i < 100; i++) {
            var matchData = new Pcre2MatchData(api, 10);
            lastRef = new WeakReference<>(matchData);
        }
        awaitGc(lastRef, MAX_GC_ATTEMPTS);

        assertDoesNotThrow(() -> {
            var code = new Pcre2Code(api, "test");
            var matchData = new Pcre2MatchData(code);
            code.match("test", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        }, "Creating Pcre2MatchData should work after bulk creation and cleanup");
    }

    // --- Pcre2GeneralContext cleanup tests ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2GeneralContextIsCollectedAfterRelease(IPcre2 api) {
        var ctx = new Pcre2GeneralContext(api);
        var ref = new WeakReference<>(ctx);
        ctx = null;
        assertNull(awaitGcAndReturn(ref), "Pcre2GeneralContext should be eligible for GC after releasing reference");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2GeneralContextCreationWorksAfterPriorCleanup(IPcre2 api) {
        var ctx = new Pcre2GeneralContext(api);
        var ref = new WeakReference<>(ctx);
        ctx = null;
        awaitGc(ref, MAX_GC_ATTEMPTS);

        assertDoesNotThrow(() -> {
            var newCtx = new Pcre2GeneralContext(api);
            new Pcre2CompileContext(api, newCtx);
        }, "Creating Pcre2GeneralContext should work after prior instance was cleaned up");
    }

    // --- Pcre2CompileContext cleanup tests ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2CompileContextIsCollectedAfterRelease(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        var ref = new WeakReference<>(ctx);
        ctx = null;
        assertNull(awaitGcAndReturn(ref), "Pcre2CompileContext should be eligible for GC after releasing reference");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2CompileContextWithGeneralContextIsCollectedAfterRelease(IPcre2 api) {
        var generalCtx = new Pcre2GeneralContext(api);
        var compileCtx = new Pcre2CompileContext(api, generalCtx);
        var ref = new WeakReference<>(compileCtx);
        compileCtx = null;
        assertNull(awaitGcAndReturn(ref), "Pcre2CompileContext (with general context) should be eligible for GC");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2CompileContextCreationWorksAfterPriorCleanup(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setNewline(Pcre2Newline.LF);
        var ref = new WeakReference<>(ctx);
        ctx = null;
        awaitGc(ref, MAX_GC_ATTEMPTS);

        assertDoesNotThrow(() -> {
            var newCtx = new Pcre2CompileContext(api, null);
            newCtx.setNewline(Pcre2Newline.CR);
        }, "Creating and configuring Pcre2CompileContext should work after prior instance was cleaned up");
    }

    // --- Pcre2MatchContext cleanup tests ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2MatchContextIsCollectedAfterRelease(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        var ref = new WeakReference<>(ctx);
        ctx = null;
        assertNull(awaitGcAndReturn(ref), "Pcre2MatchContext should be eligible for GC after releasing reference");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2MatchContextWithGeneralContextIsCollectedAfterRelease(IPcre2 api) {
        var generalCtx = new Pcre2GeneralContext(api);
        var matchCtx = new Pcre2MatchContext(api, generalCtx);
        var ref = new WeakReference<>(matchCtx);
        matchCtx = null;
        assertNull(awaitGcAndReturn(ref), "Pcre2MatchContext (with general context) should be eligible for GC");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2MatchContextCreationWorksAfterPriorCleanup(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        ctx.setMatchLimit(1000);
        var ref = new WeakReference<>(ctx);
        ctx = null;
        awaitGc(ref, MAX_GC_ATTEMPTS);

        assertDoesNotThrow(() -> {
            var newCtx = new Pcre2MatchContext(api, null);
            newCtx.setMatchLimit(2000);
        }, "Creating and configuring Pcre2MatchContext should work after prior instance was cleaned up");
    }

    // --- Pcre2JitStack cleanup tests ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2JitStackIsCollectedAfterRelease(IPcre2 api) {
        var stack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, null);
        var ref = new WeakReference<>(stack);
        stack = null;
        assertNull(awaitGcAndReturn(ref), "Pcre2JitStack should be eligible for GC after releasing reference");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void pcre2JitStackCreationWorksAfterPriorCleanup(IPcre2 api) {
        var stack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, null);
        var ref = new WeakReference<>(stack);
        stack = null;
        awaitGc(ref, MAX_GC_ATTEMPTS);

        assertDoesNotThrow(
                () -> new Pcre2JitStack(api, 32 * 1024, 512 * 1024, null),
                "Creating Pcre2JitStack should work after prior instance was cleaned up"
        );
    }

    // --- Cross-resource cleanup tests ---

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void allResourceTypesCleanedUpIndependently(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var matchData = new Pcre2MatchData(code);
        var generalCtx = new Pcre2GeneralContext(api);
        var compileCtx = new Pcre2CompileContext(api, generalCtx);
        var matchCtx = new Pcre2MatchContext(api, generalCtx);
        var jitStack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, null);

        var codeRef = new WeakReference<>(code);
        var matchDataRef = new WeakReference<>(matchData);
        var compileCtxRef = new WeakReference<>(compileCtx);
        var matchCtxRef = new WeakReference<>(matchCtx);
        var jitStackRef = new WeakReference<>(jitStack);
        var generalCtxRef = new WeakReference<>(generalCtx);

        code = null;
        matchData = null;
        compileCtx = null;
        matchCtx = null;
        jitStack = null;
        generalCtx = null;

        awaitGc(generalCtxRef, MAX_GC_ATTEMPTS * 2);

        assertDoesNotThrow(() -> {
            var newCode = new Pcre2Code(api, "test");
            var newMatchData = new Pcre2MatchData(newCode);
            newCode.match("test", 0, EnumSet.noneOf(Pcre2MatchOption.class), newMatchData, null);
            var newGeneralCtx = new Pcre2GeneralContext(api);
            new Pcre2CompileContext(api, newGeneralCtx);
            new Pcre2MatchContext(api, newGeneralCtx);
            new Pcre2JitStack(api, 32 * 1024, 512 * 1024, null);
        }, "All resource types should be creatable and usable after prior instances were cleaned up");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchDataCleanupDoesNotAffectCode(IPcre2 api) {
        var code = new Pcre2Code(api, "(hello)");

        var matchData = new Pcre2MatchData(code);
        var ref = new WeakReference<>(matchData);
        matchData = null;
        awaitGc(ref, MAX_GC_ATTEMPTS);

        assertDoesNotThrow(() -> {
            var newMatchData = new Pcre2MatchData(code);
            var result = code.match("hello", 0, EnumSet.noneOf(Pcre2MatchOption.class), newMatchData, null);
            assert result >= 0 : "Match should succeed";
        }, "Pcre2Code should remain usable after its associated Pcre2MatchData is cleaned up");
    }

    /**
     * Helper that awaits GC and returns the (now expected null) referent for assertion.
     */
    private static Object awaitGcAndReturn(WeakReference<?> ref) {
        awaitGc(ref, MAX_GC_ATTEMPTS);
        return ref.get();
    }
}
