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
package org.pcre4j.test;

import org.junit.jupiter.api.Test;
import org.pcre4j.Pcre2Code;
import org.pcre4j.Pcre2CompileOption;
import org.pcre4j.Pcre2MatchContext;
import org.pcre4j.Pcre2MatchData;
import org.pcre4j.Pcre2MatchOption;
import org.pcre4j.api.IPcre2;
import org.pcre4j.api.Pcre2CalloutBlock;
import org.pcre4j.api.Pcre2CalloutEnumerateBlock;

import java.util.ArrayList;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract tests for PCRE2 callout operations.
 *
 * @param <T> the PCRE2 API implementation type
 */
public interface Pcre2CalloutContractTest<T extends IPcre2> {

    /**
     * Returns the PCRE2 API implementation to test.
     *
     * @return the PCRE2 API implementation
     */
    T getApi();

    @Test
    default void numberedCalloutInvokesHandler() {
        final var code = new Pcre2Code(
                getApi(),
                "a(?C1)b",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(getApi(), null);

        final var callouts = new ArrayList<Pcre2CalloutBlock>();
        matchContext.setCallout(block -> {
            callouts.add(block);
            return 0;
        });

        final var result = code.match(
                "ab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        assertTrue(result > 0, "Match should succeed");
        assertFalse(callouts.isEmpty(), "Callout handler should have been invoked");
        assertEquals(1, callouts.get(0).calloutNumber());
    }

    @Test
    default void defaultCalloutInvokesHandlerWithZero() {
        final var code = new Pcre2Code(
                getApi(),
                "a(?C)b",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(getApi(), null);

        final var callouts = new ArrayList<Pcre2CalloutBlock>();
        matchContext.setCallout(block -> {
            callouts.add(block);
            return 0;
        });

        final var result = code.match(
                "ab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        assertTrue(result > 0, "Match should succeed");
        assertFalse(callouts.isEmpty(), "Callout handler should have been invoked");
        assertEquals(0, callouts.get(0).calloutNumber());
    }

    @Test
    default void stringCalloutPassesString() {
        final var code = new Pcre2Code(
                getApi(),
                "a(?C\"test\")b",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(getApi(), null);

        final var callouts = new ArrayList<Pcre2CalloutBlock>();
        matchContext.setCallout(block -> {
            callouts.add(block);
            return 0;
        });

        final var result = code.match(
                "ab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        assertTrue(result > 0, "Match should succeed");
        assertFalse(callouts.isEmpty(), "Callout handler should have been invoked");
        assertEquals(0, callouts.get(0).calloutNumber(), "String callouts should have callout number 0");
        assertNotNull(callouts.get(0).calloutString(), "String callout should have a string");
        assertEquals("test", callouts.get(0).calloutString());
    }

    @Test
    default void autoCalloutInvokesHandlerMultipleTimes() {
        final var code = new Pcre2Code(
                getApi(),
                "abc",
                EnumSet.of(Pcre2CompileOption.AUTO_CALLOUT),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(getApi(), null);

        final var callouts = new ArrayList<Pcre2CalloutBlock>();
        matchContext.setCallout(block -> {
            callouts.add(block);
            return 0;
        });

        final var result = code.match(
                "abc",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        assertTrue(result > 0, "Match should succeed");
        // Auto-callout places a callout before each item and one at the end
        assertTrue(callouts.size() > 1, "Auto-callout should invoke handler multiple times, got " + callouts.size());
        // All auto-callouts have number 255
        for (var callout : callouts) {
            assertEquals(255, callout.calloutNumber(), "Auto-callouts should have number 255");
        }
    }

    @Test
    default void calloutAbortCausesNoMatch() {
        final var code = new Pcre2Code(
                getApi(),
                "a(?C1)b",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(getApi(), null);

        // Return 1 to force a backtrack/failure at the callout point
        matchContext.setCallout(block -> 1);

        final var result = code.match(
                "ab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        assertEquals(IPcre2.ERROR_NOMATCH, result, "Match should fail when callout returns non-zero");
    }

    @Test
    default void calloutAbortWithNegativeReturnsCalloutError() {
        final var code = new Pcre2Code(
                getApi(),
                "a(?C1)b",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(getApi(), null);

        // Return ERROR_CALLOUT to abort matching entirely
        matchContext.setCallout(block -> IPcre2.ERROR_CALLOUT);

        final var result = code.match(
                "ab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        assertEquals(IPcre2.ERROR_CALLOUT, result, "Match should return ERROR_CALLOUT when handler returns it");
    }

    @Test
    default void setCalloutNullDisablesCallout() {
        final var code = new Pcre2Code(
                getApi(),
                "a(?C1)b",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(getApi(), null);

        // First set a callout that aborts
        matchContext.setCallout(block -> 1);

        // Then disable it
        matchContext.setCallout(null);

        final var result = code.match(
                "ab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        assertTrue(result > 0, "Match should succeed after callout is disabled");
    }

    @Test
    default void replaceCalloutHandler() {
        final var code = new Pcre2Code(
                getApi(),
                "a(?C1)b",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(getApi(), null);

        // Set a callout that aborts
        matchContext.setCallout(block -> 1);

        // Replace with one that allows matching
        final var callouts = new ArrayList<Pcre2CalloutBlock>();
        matchContext.setCallout(block -> {
            callouts.add(block);
            return 0;
        });

        final var result = code.match(
                "ab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        assertTrue(result > 0, "Match should succeed with replacement handler");
        assertFalse(callouts.isEmpty(), "Replacement handler should have been invoked");
    }

    @Test
    default void calloutBlockCurrentPosition() {
        final var code = new Pcre2Code(
                getApi(),
                "a(?C1)b",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(getApi(), null);

        final var callouts = new ArrayList<Pcre2CalloutBlock>();
        matchContext.setCallout(block -> {
            callouts.add(block);
            return 0;
        });

        // Match "ab" at offset 2 in "XXab"
        final var result = code.match(
                "XXab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        assertTrue(result > 0, "Match should succeed");
        assertFalse(callouts.isEmpty(), "Callout handler should have been invoked");
        // After matching 'a', current position should be at 'b' (offset 3 in "XXab")
        assertEquals(3, callouts.get(0).currentPosition(), "Current position should be after 'a'");
        assertEquals(2, callouts.get(0).startMatch(), "Start match should be at position of 'a'");
    }

    @Test
    default void enumerateCalloutsNullHandlerThrows() {
        final var code = new Pcre2Code(
                getApi(),
                "a(?C1)b",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        assertThrows(IllegalArgumentException.class, () -> code.enumerateCallouts(null));
    }

    @Test
    default void enumerateCalloutsFindsNumberedCallout() {
        final var code = new Pcre2Code(
                getApi(),
                "a(?C1)b(?C2)c",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var callouts = new ArrayList<Pcre2CalloutEnumerateBlock>();
        code.enumerateCallouts(block -> {
            callouts.add(block);
            return 0;
        });

        assertEquals(2, callouts.size(), "Should find 2 callout points");
        assertEquals(1, callouts.get(0).calloutNumber());
        assertEquals(2, callouts.get(1).calloutNumber());
    }

    @Test
    default void enumerateCalloutsFindsStringCallout() {
        final var code = new Pcre2Code(
                getApi(),
                "a(?C\"hello\")b",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var callouts = new ArrayList<Pcre2CalloutEnumerateBlock>();
        code.enumerateCallouts(block -> {
            callouts.add(block);
            return 0;
        });

        assertEquals(1, callouts.size(), "Should find 1 callout point");
        assertEquals(0, callouts.get(0).calloutNumber(), "String callouts should have callout number 0");
        assertNotNull(callouts.get(0).calloutString(), "Should have callout string");
        assertEquals("hello", callouts.get(0).calloutString());
    }

    @Test
    default void enumerateCalloutsPatternWithNoCallouts() {
        final var code = new Pcre2Code(
                getApi(),
                "abc",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var callouts = new ArrayList<Pcre2CalloutEnumerateBlock>();
        code.enumerateCallouts(block -> {
            callouts.add(block);
            return 0;
        });

        assertTrue(callouts.isEmpty(), "Should find no callout points in pattern without callouts");
    }

    @Test
    default void enumerateCalloutsAutoCallout() {
        final var code = new Pcre2Code(
                getApi(),
                "abc",
                EnumSet.of(Pcre2CompileOption.AUTO_CALLOUT),
                null
        );

        final var callouts = new ArrayList<Pcre2CalloutEnumerateBlock>();
        code.enumerateCallouts(block -> {
            callouts.add(block);
            return 0;
        });

        // Auto-callout should insert callout points before each item and at end
        assertTrue(callouts.size() > 1, "Auto-callout should create multiple callout points, got " + callouts.size());
    }

    @Test
    default void enumerateCalloutsStopEarly() {
        final var code = new Pcre2Code(
                getApi(),
                "a(?C1)b(?C2)c(?C3)d",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var callouts = new ArrayList<Pcre2CalloutEnumerateBlock>();
        code.enumerateCallouts(block -> {
            callouts.add(block);
            // Stop after first callout
            return 1;
        });

        assertEquals(1, callouts.size(), "Enumeration should stop after handler returns non-zero");
        assertEquals(1, callouts.get(0).calloutNumber());
    }

    @Test
    default void calloutWithoutHandlerDoesNothing() {
        // A match context without a callout handler should not interfere with matching
        final var code = new Pcre2Code(
                getApi(),
                "a(?C1)b",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(getApi(), null);

        // Don't set any callout handler
        final var result = code.match(
                "ab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        assertTrue(result > 0, "Match should succeed without a callout handler set");
    }

    @Test
    default void calloutPatternPosition() {
        final var code = new Pcre2Code(
                getApi(),
                "a(?C1)b",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var callouts = new ArrayList<Pcre2CalloutEnumerateBlock>();
        code.enumerateCallouts(block -> {
            callouts.add(block);
            return 0;
        });

        assertEquals(1, callouts.size());
        // The pattern position should point to 'b' (the next item after the callout)
        assertTrue(callouts.get(0).patternPosition() > 0, "Pattern position should be > 0");
        assertTrue(callouts.get(0).nextItemLength() > 0, "Next item length should be > 0");
    }

    @Test
    default void multipleCalloutsWithDifferentNumbers() {
        final var code = new Pcre2Code(
                getApi(),
                "(?C1)a(?C5)b(?C10)c",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(getApi(), null);

        final var numbers = new ArrayList<Integer>();
        matchContext.setCallout(block -> {
            numbers.add(block.calloutNumber());
            return 0;
        });

        final var result = code.match(
                "abc",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        assertTrue(result > 0, "Match should succeed");
        assertEquals(3, numbers.size(), "Should have 3 callouts");
        assertEquals(1, numbers.get(0));
        assertEquals(5, numbers.get(1));
        assertEquals(10, numbers.get(2));
    }
}
