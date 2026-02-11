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
package org.pcre4j.test;

import org.junit.jupiter.api.Test;
import org.pcre4j.Pcre2Code;
import org.pcre4j.Pcre2CompileOption;
import org.pcre4j.Pcre2MatchContext;
import org.pcre4j.Pcre2MatchData;
import org.pcre4j.Pcre2MatchOption;
import org.pcre4j.api.IPcre2;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract tests for PCRE2 match context operations.
 *
 * @param <T> the PCRE2 API implementation type
 */
public interface Pcre2MatchContextContractTest<T extends IPcre2> {

    /**
     * Returns the PCRE2 API implementation to test.
     *
     * @return the PCRE2 API implementation
     */
    T getApi();

    @Test
    default void setMatchLimitNegativeThrows() {
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        assertThrows(IllegalArgumentException.class, () -> matchContext.setMatchLimit(-1));
    }

    @Test
    default void setMatchLimitZeroAllowed() {
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        // Should not throw
        matchContext.setMatchLimit(0);
    }

    @Test
    default void setMatchLimitPositiveAllowed() {
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        // Should not throw
        matchContext.setMatchLimit(1000);
    }

    @Test
    default void matchWithEmbeddedLimitCausesMatchLimitError() {
        // Use a pattern with embedded match limit and disable PCRE2 optimizations
        // that would otherwise prevent catastrophic backtracking.
        // (*NO_AUTO_POSSESS) and (*NO_START_OPT) disable optimizations that
        // would normally make the match efficient.
        final var code = new Pcre2Code(
                getApi(),
                "(*LIMIT_MATCH=100)(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // Match against a string that cannot match (ends with 'b' not matching '$' after 'a's)
        // This forces exponential backtracking as PCRE2 tries all possible groupings
        final var result = code.match(
                "aaaaaaaaaaaaaaaaaaaaaaaab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );

        // The match should fail with MATCHLIMIT error due to excessive backtracking
        assertEquals(IPcre2.ERROR_MATCHLIMIT, result);
    }

    @Test
    default void matchWithContextLimitCausesMatchLimitError() {
        // A pattern that requires extensive backtracking - disable PCRE2 optimizations
        // that would otherwise prevent catastrophic backtracking.
        final var code = new Pcre2Code(
                getApi(),
                "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(getApi(), null);

        // Set a match limit via the match context
        matchContext.setMatchLimit(100);

        // Match against a string that cannot match (ends with 'b' not matching '$' after 'a's)
        // This forces exponential backtracking as PCRE2 tries all possible groupings
        final var result = code.match(
                "aaaaaaaaaaaaaaaaaaaaaaaab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        // The match should fail with MATCHLIMIT error due to excessive backtracking
        assertEquals(IPcre2.ERROR_MATCHLIMIT, result);
    }

    @Test
    default void matchLimitFromPattern() {
        // A pattern with embedded match limit
        final var code = new Pcre2Code(
                getApi(),
                "(*LIMIT_MATCH=5000)test",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        // The match limit should be readable from the compiled pattern
        assertEquals(5000, code.matchLimit());
    }

    @Test
    default void setDepthLimitNegativeThrows() {
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        assertThrows(IllegalArgumentException.class, () -> matchContext.setDepthLimit(-1));
    }

    @Test
    default void setDepthLimitZeroAllowed() {
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        // Should not throw
        matchContext.setDepthLimit(0);
    }

    @Test
    default void setDepthLimitPositiveAllowed() {
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        // Should not throw
        matchContext.setDepthLimit(1000);
    }

    @Test
    default void matchWithEmbeddedDepthLimitCausesDepthLimitError() {
        // Use a pattern with embedded depth limit and disable PCRE2 optimizations
        // that would otherwise prevent catastrophic backtracking.
        // (*NO_AUTO_POSSESS) and (*NO_START_OPT) disable optimizations that
        // would normally make the match efficient.
        final var code = new Pcre2Code(
                getApi(),
                "(*LIMIT_DEPTH=10)(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // Match against a string that cannot match (ends with 'b' not matching '$' after 'a's)
        // This forces recursive backtracking as PCRE2 tries all possible groupings
        final var result = code.match(
                "aaaaaaaaaaaaaaaaaaaaaaaab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );

        // The match should fail with DEPTHLIMIT error due to deep recursion
        assertEquals(IPcre2.ERROR_DEPTHLIMIT, result);
    }

    @Test
    default void matchWithContextDepthLimitCausesDepthLimitError() {
        // A pattern that requires extensive backtracking - disable PCRE2 optimizations
        // that would otherwise prevent catastrophic backtracking.
        final var code = new Pcre2Code(
                getApi(),
                "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);
        final var matchContext = new Pcre2MatchContext(getApi(), null);

        // Set a depth limit via the match context
        matchContext.setDepthLimit(10);

        // Match against a string that cannot match (ends with 'b' not matching '$' after 'a's)
        // This forces recursive backtracking as PCRE2 tries all possible groupings
        final var result = code.match(
                "aaaaaaaaaaaaaaaaaaaaaaaab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        // The match should fail with DEPTHLIMIT error due to deep recursion
        assertEquals(IPcre2.ERROR_DEPTHLIMIT, result);
    }

    @Test
    default void depthLimitFromPattern() {
        // A pattern with embedded depth limit
        final var code = new Pcre2Code(
                getApi(),
                "(*LIMIT_DEPTH=5000)test",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        // The depth limit should be readable from the compiled pattern
        assertEquals(5000, code.depthLimit());
    }

    @Test
    default void setHeapLimitNegativeThrows() {
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        assertThrows(IllegalArgumentException.class, () -> matchContext.setHeapLimit(-1));
    }

    @Test
    default void setHeapLimitZeroAllowed() {
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        // Should not throw
        matchContext.setHeapLimit(0);
    }

    @Test
    default void setHeapLimitPositiveAllowed() {
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        // Should not throw
        matchContext.setHeapLimit(1000);
    }

    @Test
    default void heapLimitFromPattern() {
        // A pattern with embedded heap limit
        final var code = new Pcre2Code(
                getApi(),
                "(*LIMIT_HEAP=5000)test",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        // The heap limit should be readable from the compiled pattern
        assertEquals(5000, code.heapLimit());
    }

    @Test
    default void matchWithEmbeddedHeapLimitCausesHeapLimitError() {
        // Use a pattern with embedded heap limit and disable PCRE2 optimizations
        // that would otherwise prevent catastrophic backtracking.
        // (*NO_AUTO_POSSESS) and (*NO_START_OPT) disable optimizations that
        // would normally make the match efficient.
        final var code = new Pcre2Code(
                getApi(),
                "(*LIMIT_HEAP=1)(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // Match against a string that cannot match (ends with 'b' not matching '$' after 'a's)
        // This forces extensive backtracking that requires heap memory
        final var result = code.match(
                "aaaaaaaaaaaaaaaaaaaaaaaab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );

        // The match should fail with HEAPLIMIT error due to heap memory exhaustion
        assertEquals(IPcre2.ERROR_HEAPLIMIT, result);
    }

    @Test
    default void matchWithContextHeapLimitCausesHeapLimitError() {
        // A pattern that requires extensive backtracking - disable PCRE2 optimizations
        // that would otherwise prevent catastrophic backtracking.
        final var code = new Pcre2Code(
                getApi(),
                "(*NO_AUTO_POSSESS)(*NO_START_OPT)(a+)+$",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // Create a match context with a very low heap limit
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        matchContext.setHeapLimit(1); // 1 KiB - very low limit

        // Match against a string that cannot match (ends with 'b' not matching '$' after 'a's)
        // This forces extensive backtracking that requires heap memory
        final var result = code.match(
                "aaaaaaaaaaaaaaaaaaaaaaaab",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );

        // The match should fail with HEAPLIMIT error due to heap memory exhaustion
        assertEquals(IPcre2.ERROR_HEAPLIMIT, result);
    }

    @Test
    default void setOffsetLimitNegativeThrows() {
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        assertThrows(IllegalArgumentException.class, () -> matchContext.setOffsetLimit(-1));
    }

    @Test
    default void setOffsetLimitZeroAllowed() {
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        // Should not throw
        matchContext.setOffsetLimit(0);
    }

    @Test
    default void setOffsetLimitPositiveAllowed() {
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        // Should not throw
        matchContext.setOffsetLimit(1000);
    }

    @Test
    default void offsetLimitEnforcedWhenPatternCompiledWithOption() {
        // Compile pattern with USE_OFFSET_LIMIT option
        final var code = new Pcre2Code(
                getApi(),
                "test",
                EnumSet.of(Pcre2CompileOption.USE_OFFSET_LIMIT),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // Offset limit semantics: match can start at position <= limit (inclusive)
        // To prevent a match at position P, set limit to P-1

        // Test 1: "test" starts at position 0
        // With limit 0, position 0 IS allowed (0 <= 0), so match succeeds
        final var matchContext1 = new Pcre2MatchContext(getApi(), null);
        matchContext1.setOffsetLimit(0);

        final var result1 = code.match(
                "test",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext1
        );
        assertTrue(result1 > 0, "Limit 0, pos 0: should match (0<=0), result=" + result1);

        // Test 2: In "XXXXtest", "test" starts at position 4
        // With limit 3, positions 0-3 are allowed, position 4 is NOT (4 > 3)
        final var matchContext2 = new Pcre2MatchContext(getApi(), null);
        matchContext2.setOffsetLimit(3);

        final var result2 = code.match(
                "XXXXtest",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext2
        );
        assertEquals(IPcre2.ERROR_NOMATCH, result2, "Limit 3, pos 4: should fail (4>3), result=" + result2);

        // Test 3: With limit 4, position 4 IS allowed (4 <= 4)
        final var matchContext3 = new Pcre2MatchContext(getApi(), null);
        matchContext3.setOffsetLimit(4);

        final var result3 = code.match(
                "XXXXtest",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext3
        );
        assertTrue(result3 > 0, "Match should succeed: test at pos 4, limit 4 (4 <= 4) (result=" + result3 + ")");

        // Test 4: With limit 5, position 4 is also allowed (4 <= 5)
        final var matchContext4 = new Pcre2MatchContext(getApi(), null);
        matchContext4.setOffsetLimit(5);

        final var result4 = code.match(
                "XXXXtest",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext4
        );
        assertTrue(result4 > 0, "Match should succeed: test at pos 4, limit 5 (4 <= 5) (result=" + result4 + ")");
    }

    @Test
    default void offsetLimitCausesErrorWithoutCompileOption() {
        // Compile pattern WITHOUT USE_OFFSET_LIMIT option
        final var code = new Pcre2Code(
                getApi(),
                "test",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        // First verify the pattern matches without any match context
        final var resultNoContext = code.match(
                "01234test",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertTrue(resultNoContext > 0, "Match should succeed without match context");

        // Create a match context with offset limit of 5
        final var matchContext = new Pcre2MatchContext(getApi(), null);
        matchContext.setOffsetLimit(5);

        // According to PCRE2, using offset limit on a pattern not compiled with USE_OFFSET_LIMIT
        // causes PCRE2_ERROR_BADOFFSETLIMIT error
        final var result = code.match(
                "01234test",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                matchContext
        );
        assertEquals(
                IPcre2.ERROR_BADOFFSETLIMIT,
                result,
                "Offset limit without USE_OFFSET_LIMIT should cause BADOFFSETLIMIT"
        );
    }

    @Test
    default void offsetLimitRawApiTest() {
        var api = getApi();

        // Test using raw API to verify low-level behavior
        // Test with "Xtest" where "test" starts at position 1
        String pattern = "test";
        int options = IPcre2.USE_OFFSET_LIMIT;
        int[] errorcode = new int[1];
        long[] erroroffset = new long[1];

        // Compile with USE_OFFSET_LIMIT
        long code = api.compile(pattern, options, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Compile should succeed (code=" + code + ", error=" + errorcode[0] + ")");

        // Create match data
        long matchData = api.matchDataCreateFromPattern(code, 0);
        assertTrue(matchData != 0, "Match data creation should succeed");

        // Test 1: Match without offset limit (should succeed)
        int result = api.match(code, "Xtest", 0, 0, matchData, 0);
        assertTrue(result > 0, "Match without offset limit should succeed (result=" + result + ")");

        // Create match context
        long matchCtx = api.matchContextCreate(0);
        assertTrue(matchCtx != 0, "Match context creation should succeed");

        // Offset limit semantics: match can start at position <= limit (inclusive)

        // Test 2: Set offset limit to 0 - "test" starts at position 1, which is > 0, so should fail
        api.setOffsetLimit(matchCtx, 0);
        result = api.match(code, "Xtest", 0, 0, matchData, matchCtx);
        assertEquals(IPcre2.ERROR_NOMATCH, result, "Limit 0, pos 1: should fail (1>0), result=" + result);

        // Test 3: Set offset limit to 1 - "test" starts at position 1, which IS <= 1, so should succeed
        api.setOffsetLimit(matchCtx, 1);
        result = api.match(code, "Xtest", 0, 0, matchData, matchCtx);
        assertTrue(result > 0, "Limit 1, pos 1: should match (1<=1), result=" + result);

        // Test 4: Set offset limit to 2 - "test" starts at position 1, which IS <= 2, so should succeed
        api.setOffsetLimit(matchCtx, 2);
        result = api.match(code, "Xtest", 0, 0, matchData, matchCtx);
        assertTrue(result > 0, "Limit 2, pos 1: should match (1<=2), result=" + result);

        // Clean up
        api.matchContextFree(matchCtx);
        api.matchDataFree(matchData);
        api.codeFree(code);
    }
}
