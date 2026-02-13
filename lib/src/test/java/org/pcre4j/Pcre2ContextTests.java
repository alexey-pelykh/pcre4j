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

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for context classes: {@link Pcre2GeneralContext}, {@link Pcre2CompileContext},
 * {@link Pcre2MatchContext}, and {@link Pcre2JitStack}.
 */
public class Pcre2ContextTests {

    // === Pcre2GeneralContext ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void generalContextCreation(IPcre2 api) {
        var ctx = new Pcre2GeneralContext(api);
        assertNotNull(ctx);
        assertNotNull(ctx.api());
        assertEquals(api, ctx.api());
        assertTrue(ctx.handle() != 0);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void generalContextNullApiThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> new Pcre2GeneralContext(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void generalContextUsableForCompileContext(IPcre2 api) {
        var generalCtx = new Pcre2GeneralContext(api);
        assertDoesNotThrow(() -> new Pcre2CompileContext(api, generalCtx));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void generalContextUsableForMatchContext(IPcre2 api) {
        var generalCtx = new Pcre2GeneralContext(api);
        assertDoesNotThrow(() -> new Pcre2MatchContext(api, generalCtx));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void generalContextUsableForJitStack(IPcre2 api) {
        var generalCtx = new Pcre2GeneralContext(api);
        var stack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, generalCtx);
        assertNotNull(stack);
        assertTrue(stack.handle() != 0);
    }

    // === Pcre2CompileContext ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextCreation(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertNotNull(ctx);
        assertNotNull(ctx.api());
        assertEquals(api, ctx.api());
        assertTrue(ctx.handle() != 0);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextSetNewline(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertDoesNotThrow(() -> ctx.setNewline(Pcre2Newline.LF));
        assertDoesNotThrow(() -> ctx.setNewline(Pcre2Newline.CR));
        assertDoesNotThrow(() -> ctx.setNewline(Pcre2Newline.CRLF));
        assertDoesNotThrow(() -> ctx.setNewline(Pcre2Newline.ANY));
        assertDoesNotThrow(() -> ctx.setNewline(Pcre2Newline.ANYCRLF));
        assertDoesNotThrow(() -> ctx.setNewline(Pcre2Newline.NUL));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextSetNewlineNullThrows(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> ctx.setNewline(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextSetBsr(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertDoesNotThrow(() -> ctx.setBsr(Pcre2Bsr.UNICODE));
        assertDoesNotThrow(() -> ctx.setBsr(Pcre2Bsr.ANYCRLF));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextSetBsrNullThrows(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> ctx.setBsr(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextSetParensNestLimit(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertDoesNotThrow(() -> ctx.setParensNestLimit(100));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextSetParensNestLimitBoundaryValues(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertDoesNotThrow(() -> ctx.setParensNestLimit(0));
        assertDoesNotThrow(() -> ctx.setParensNestLimit(Integer.MAX_VALUE));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextSetMaxPatternLength(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertDoesNotThrow(() -> ctx.setMaxPatternLength(1024));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextSetMaxPatternLengthBoundaryValues(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertDoesNotThrow(() -> ctx.setMaxPatternLength(0));
        assertDoesNotThrow(() -> ctx.setMaxPatternLength(Long.MAX_VALUE));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextSetCompileExtraOptions(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertDoesNotThrow(() ->
                ctx.setCompileExtraOptions(EnumSet.of(Pcre2CompileExtraOption.BAD_ESCAPE_IS_LITERAL)));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextSetCompileExtraOptionsMultiple(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertDoesNotThrow(() -> ctx.setCompileExtraOptions(
                EnumSet.of(Pcre2CompileExtraOption.BAD_ESCAPE_IS_LITERAL, Pcre2CompileExtraOption.MATCH_WORD)));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextSetCompileExtraOptionsEmpty(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertDoesNotThrow(() -> ctx.setCompileExtraOptions(EnumSet.noneOf(Pcre2CompileExtraOption.class)));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextSetCompileExtraOptionsNullThrows(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertThrows(IllegalArgumentException.class, () ->
                ctx.setCompileExtraOptions(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextUsedInCompilation(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setNewline(Pcre2Newline.CR);

        // Compile a pattern using the context
        var code = new Pcre2Code(api, "test", null, ctx);
        assertNotNull(code);

        // Verify the newline setting was applied
        assertEquals(Pcre2Newline.CR, code.newline());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextParensLimitEnforced(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setParensNestLimit(2);

        // A pattern with deeply nested parentheses should fail with strict limit
        assertThrows(Pcre2CompileException.class, () ->
                new Pcre2Code(api, "((((a))))", null, ctx));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextMaxPatternLengthEnforced(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setMaxPatternLength(3);

        // A pattern longer than 3 characters should fail
        assertThrows(Pcre2CompileException.class, () ->
                new Pcre2Code(api, "test", null, ctx));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextBsrApplied(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setBsr(Pcre2Bsr.ANYCRLF);

        var code = new Pcre2Code(api, "test", null, ctx);
        assertEquals(Pcre2Bsr.ANYCRLF, code.bsr());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void compileContextExtraOptionsApplied(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setCompileExtraOptions(EnumSet.of(Pcre2CompileExtraOption.BAD_ESCAPE_IS_LITERAL));

        // With BAD_ESCAPE_IS_LITERAL, an unrecognized escape like \j should be treated as literal 'j'
        var code = new Pcre2Code(api, "\\j", null, ctx);
        var matchData = new Pcre2MatchData(code);
        var result = code.match("j", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
        assertTrue(result > 0, "\\j should match 'j' with BAD_ESCAPE_IS_LITERAL");
    }

    // === Pcre2MatchContext ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextCreation(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertNotNull(ctx);
        assertNotNull(ctx.api());
        assertEquals(api, ctx.api());
        assertTrue(ctx.handle() != 0);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextSetMatchLimit(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertDoesNotThrow(() -> ctx.setMatchLimit(1000));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextSetMatchLimitBoundaryValues(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertDoesNotThrow(() -> ctx.setMatchLimit(0));
        assertDoesNotThrow(() -> ctx.setMatchLimit(Integer.MAX_VALUE));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextSetMatchLimitNegativeThrows(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> ctx.setMatchLimit(-1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextSetDepthLimit(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertDoesNotThrow(() -> ctx.setDepthLimit(500));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextSetDepthLimitBoundaryValues(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertDoesNotThrow(() -> ctx.setDepthLimit(0));
        assertDoesNotThrow(() -> ctx.setDepthLimit(Integer.MAX_VALUE));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextSetDepthLimitNegativeThrows(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> ctx.setDepthLimit(-1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextSetHeapLimit(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertDoesNotThrow(() -> ctx.setHeapLimit(1024));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextSetHeapLimitBoundaryValues(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertDoesNotThrow(() -> ctx.setHeapLimit(0));
        assertDoesNotThrow(() -> ctx.setHeapLimit(Integer.MAX_VALUE));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextSetHeapLimitNegativeThrows(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> ctx.setHeapLimit(-1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextSetOffsetLimit(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertDoesNotThrow(() -> ctx.setOffsetLimit(100));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextSetOffsetLimitBoundaryValues(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertDoesNotThrow(() -> ctx.setOffsetLimit(0));
        assertDoesNotThrow(() -> ctx.setOffsetLimit(Long.MAX_VALUE));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextSetOffsetLimitNegativeThrows(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> ctx.setOffsetLimit(-1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextAssignJitStack(IPcre2 api) {
        var matchCtx = new Pcre2MatchContext(api, null);
        var jitStack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, null);
        assertDoesNotThrow(() -> matchCtx.assignJitStack(jitStack));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextAssignJitStackNullThrows(IPcre2 api) {
        var matchCtx = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> matchCtx.assignJitStack(null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextLowMatchLimitCausesError(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        ctx.setMatchLimit(1); // Extremely low limit

        var code = new Pcre2Code(api, "(a+)+b");
        var matchData = new Pcre2MatchData(code);

        // With match limit of 1, a complex backtracking pattern should fail
        var result = code.match("aaaaac", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, ctx);
        assertTrue(result < 0, "Very low match limit should cause match failure");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextLowDepthLimitCausesError(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        ctx.setDepthLimit(1); // Extremely low depth limit

        var code = new Pcre2Code(api, "(a+)+b");
        var matchData = new Pcre2MatchData(code);

        // With depth limit of 1, a pattern requiring recursion should fail
        var result = code.match("aaaaac", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, ctx);
        assertTrue(result < 0, "Very low depth limit should cause match failure");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchContextOffsetLimitEnforced(IPcre2 api) {
        // Compile with USE_OFFSET_LIMIT so the offset limit takes effect
        var code = new Pcre2Code(api, "test", EnumSet.of(Pcre2CompileOption.USE_OFFSET_LIMIT));
        var matchData = new Pcre2MatchData(code);

        var ctx = new Pcre2MatchContext(api, null);
        ctx.setOffsetLimit(3);

        // "XXXXtest" - the match for "test" starts at offset 4, which exceeds the limit of 3
        var result = code.match("XXXXtest", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, ctx);
        assertTrue(result < 0, "Match starting beyond offset limit should fail");

        // "XXtest" - the match for "test" starts at offset 2, which is within the limit of 3
        ctx.setOffsetLimit(5);
        var result2 = code.match("XXtest", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, ctx);
        assertTrue(result2 > 0, "Match starting within offset limit should succeed");
    }

    // === Pcre2JitStack ===

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitStackCreation(IPcre2 api) {
        var stack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, null);
        assertNotNull(stack);
        assertNotNull(stack.api());
        assertEquals(api, stack.api());
        assertTrue(stack.handle() != 0);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitStackNullApiThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class,
                () -> new Pcre2JitStack(null, 32 * 1024, 512 * 1024, null));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitStackWithGeneralContext(IPcre2 api) {
        var generalCtx = new Pcre2GeneralContext(api);
        var stack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, generalCtx);
        assertNotNull(stack);
        assertTrue(stack.handle() != 0);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitStackWithSmallSizes(IPcre2 api) {
        var stack = new Pcre2JitStack(api, 1024, 8 * 1024, null);
        assertNotNull(stack);
        assertTrue(stack.handle() != 0);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitStackWithLargeSizes(IPcre2 api) {
        var stack = new Pcre2JitStack(api, 64 * 1024, 1024 * 1024, null);
        assertNotNull(stack);
        assertTrue(stack.handle() != 0);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitStackUsedInMatch(IPcre2 api) {
        Assumptions.assumeTrue(Pcre4jUtils.isJitSupported(api), "JIT is not supported on this platform");

        var matchCtx = new Pcre2MatchContext(api, null);
        var jitStack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, null);
        matchCtx.assignJitStack(jitStack);

        var code = new Pcre2JitCode(api, "(hello)", null, null, null);
        var matchData = new Pcre2MatchData(code);
        var result = code.match("hello", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, matchCtx);
        assertTrue(result > 0, "JIT match with custom stack should succeed");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void jitStackWithGeneralContextUsedInMatch(IPcre2 api) {
        Assumptions.assumeTrue(Pcre4jUtils.isJitSupported(api), "JIT is not supported on this platform");

        var generalCtx = new Pcre2GeneralContext(api);
        var matchCtx = new Pcre2MatchContext(api, generalCtx);
        var jitStack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, generalCtx);
        matchCtx.assignJitStack(jitStack);

        var code = new Pcre2JitCode(api, "(world)", null, null, null);
        var matchData = new Pcre2MatchData(code);
        var result = code.match("world", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, matchCtx);
        assertTrue(result > 0, "JIT match with custom stack and general context should succeed");
    }
}
