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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.stream.Stream;

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

    private static IPcre2 loadBackend(String className) {
        try {
            return (IPcre2) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Backend " + className + " not found on classpath", e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException
                 | NoSuchMethodException e) {
            throw new RuntimeException("Failed to instantiate backend " + className, e);
        }
    }

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(loadBackend("org.pcre4j.jna.Pcre2")),
                Arguments.of(loadBackend("org.pcre4j.ffm.Pcre2"))
        );
    }

    // === Pcre2GeneralContext ===

    @ParameterizedTest
    @MethodSource("parameters")
    void generalContextCreation(IPcre2 api) {
        var ctx = new Pcre2GeneralContext(api);
        assertNotNull(ctx);
        assertNotNull(ctx.api());
        assertEquals(api, ctx.api());
        assertTrue(ctx.handle() != 0);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void generalContextUsableForCompileContext(IPcre2 api) {
        var generalCtx = new Pcre2GeneralContext(api);
        assertDoesNotThrow(() -> new Pcre2CompileContext(api, generalCtx));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void generalContextUsableForMatchContext(IPcre2 api) {
        var generalCtx = new Pcre2GeneralContext(api);
        assertDoesNotThrow(() -> new Pcre2MatchContext(api, generalCtx));
    }

    // === Pcre2CompileContext ===

    @ParameterizedTest
    @MethodSource("parameters")
    void compileContextCreation(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertNotNull(ctx);
        assertNotNull(ctx.api());
        assertEquals(api, ctx.api());
        assertTrue(ctx.handle() != 0);
    }

    @ParameterizedTest
    @MethodSource("parameters")
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
    @MethodSource("parameters")
    void compileContextSetNewlineNullThrows(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> ctx.setNewline(null));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void compileContextSetBsr(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertDoesNotThrow(() -> ctx.setBsr(Pcre2Bsr.UNICODE));
        assertDoesNotThrow(() -> ctx.setBsr(Pcre2Bsr.ANYCRLF));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void compileContextSetBsrNullThrows(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> ctx.setBsr(null));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void compileContextSetParensNestLimit(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertDoesNotThrow(() -> ctx.setParensNestLimit(100));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void compileContextSetMaxPatternLength(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertDoesNotThrow(() -> ctx.setMaxPatternLength(1024));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void compileContextSetCompileExtraOptions(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertDoesNotThrow(() -> ctx.setCompileExtraOptions(Pcre2CompileExtraOption.BAD_ESCAPE_IS_LITERAL));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void compileContextSetCompileExtraOptionsNullThrows(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertThrows(IllegalArgumentException.class, () ->
                ctx.setCompileExtraOptions((Pcre2CompileExtraOption[]) null));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void compileContextSetCompileExtraOptionsNullElementThrows(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        assertThrows(IllegalArgumentException.class, () ->
                ctx.setCompileExtraOptions(Pcre2CompileExtraOption.BAD_ESCAPE_IS_LITERAL, null));
    }

    @ParameterizedTest
    @MethodSource("parameters")
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
    @MethodSource("parameters")
    void compileContextParensLimitEnforced(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setParensNestLimit(2);

        // A pattern with deeply nested parentheses should fail with strict limit
        assertThrows(Pcre2CompileError.class, () ->
                new Pcre2Code(api, "((((a))))", null, ctx));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void compileContextMaxPatternLengthEnforced(IPcre2 api) {
        var ctx = new Pcre2CompileContext(api, null);
        ctx.setMaxPatternLength(3);

        // A pattern longer than 3 characters should fail
        assertThrows(Pcre2CompileError.class, () ->
                new Pcre2Code(api, "test", null, ctx));
    }

    // === Pcre2MatchContext ===

    @ParameterizedTest
    @MethodSource("parameters")
    void matchContextCreation(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertNotNull(ctx);
        assertNotNull(ctx.api());
        assertEquals(api, ctx.api());
        assertTrue(ctx.handle() != 0);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchContextSetMatchLimit(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertDoesNotThrow(() -> ctx.setMatchLimit(1000));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchContextSetMatchLimitNegativeThrows(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> ctx.setMatchLimit(-1));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchContextSetDepthLimit(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertDoesNotThrow(() -> ctx.setDepthLimit(500));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchContextSetDepthLimitNegativeThrows(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> ctx.setDepthLimit(-1));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchContextSetHeapLimit(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertDoesNotThrow(() -> ctx.setHeapLimit(1024));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchContextSetHeapLimitNegativeThrows(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> ctx.setHeapLimit(-1));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchContextSetOffsetLimit(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertDoesNotThrow(() -> ctx.setOffsetLimit(100));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchContextSetOffsetLimitNegativeThrows(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> ctx.setOffsetLimit(-1));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchContextAssignJitStack(IPcre2 api) {
        var matchCtx = new Pcre2MatchContext(api, null);
        var jitStack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, null);
        assertDoesNotThrow(() -> matchCtx.assignJitStack(jitStack));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchContextAssignJitStackNullThrows(IPcre2 api) {
        var matchCtx = new Pcre2MatchContext(api, null);
        assertThrows(IllegalArgumentException.class, () -> matchCtx.assignJitStack(null));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchContextLowMatchLimitCausesError(IPcre2 api) {
        var ctx = new Pcre2MatchContext(api, null);
        ctx.setMatchLimit(1); // Extremely low limit

        var code = new Pcre2Code(api, "(a+)+b");
        var matchData = new Pcre2MatchData(code);

        // With match limit of 1, a complex backtracking pattern should fail
        var result = code.match("aaaaac", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, ctx);
        assertTrue(result < 0, "Very low match limit should cause match failure");
    }

    // === Pcre2JitStack ===

    @ParameterizedTest
    @MethodSource("parameters")
    void jitStackCreation(IPcre2 api) {
        var stack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, null);
        assertNotNull(stack);
        assertNotNull(stack.api());
        assertEquals(api, stack.api());
        assertTrue(stack.handle() != 0);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void jitStackWithGeneralContext(IPcre2 api) {
        var generalCtx = new Pcre2GeneralContext(api);
        var stack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, generalCtx);
        assertNotNull(stack);
        assertTrue(stack.handle() != 0);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void jitStackUsedInMatch(IPcre2 api) {
        var matchCtx = new Pcre2MatchContext(api, null);
        var jitStack = new Pcre2JitStack(api, 32 * 1024, 512 * 1024, null);
        matchCtx.assignJitStack(jitStack);

        var code = new Pcre2JitCode(api, "(hello)", null, null, null);
        var matchData = new Pcre2MatchData(code);
        var result = code.match("hello", 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, matchCtx);
        assertTrue(result > 0, "JIT match with custom stack should succeed");
    }
}
