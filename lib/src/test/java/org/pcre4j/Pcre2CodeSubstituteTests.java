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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for {@link Pcre2Code#substitute} method.
 */
public class Pcre2CodeSubstituteTests {

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

    @ParameterizedTest
    @MethodSource("parameters")
    void substituteSimple(IPcre2 api) {
        var code = new Pcre2Code(api, "world");
        var result = code.substitute("hello world", 0, null, null, null, "earth");
        assertEquals("hello earth", result);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void substituteGlobal(IPcre2 api) {
        var code = new Pcre2Code(api, "o");
        var result = code.substitute("foo boo", 0,
                EnumSet.of(Pcre2SubstituteOption.GLOBAL), null, null, "0");
        assertEquals("f00 b00", result);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void substituteWithBackreference(IPcre2 api) {
        var code = new Pcre2Code(api, "(\\w+) (\\w+)");
        var result = code.substitute("hello world", 0, null, null, null, "$2 $1");
        assertEquals("world hello", result);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void substituteWithNamedGroup(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<first>\\w+) (?<second>\\w+)");
        var result = code.substitute("hello world", 0, null, null, null, "${second} ${first}");
        assertEquals("world hello", result);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void substituteNoMatch(IPcre2 api) {
        var code = new Pcre2Code(api, "xyz");
        var result = code.substitute("hello world", 0, null, null, null, "abc");
        assertEquals("hello world", result);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void substituteEmptyReplacement(IPcre2 api) {
        var code = new Pcre2Code(api, "world");
        var result = code.substitute("hello world", 0, null, null, null, "");
        assertEquals("hello ", result);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void substituteWithStartOffset(IPcre2 api) {
        var code = new Pcre2Code(api, "o");
        var result = code.substitute("foo boo", 2, null, null, null, "0");
        // Should only substitute the first "o" at or after offset 2
        assertEquals("fo0 boo", result);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void substituteUnicode(IPcre2 api) {
        var code = new Pcre2Code(api, "caf\u00e9");
        var result = code.substitute("I love caf\u00e9", 0, null, null, null, "coffee");
        assertEquals("I love coffee", result);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void substituteNullOptionsUsesEmpty(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var result = code.substitute("test", 0, null, null, null, "replaced");
        assertNotNull(result);
        assertEquals("replaced", result);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void substituteLargeReplacementTriggersReallocation(IPcre2 api) {
        // Create a replacement much larger than the subject to exercise buffer reallocation
        var code = new Pcre2Code(api, "a");
        var largeReplacement = "X".repeat(1000);
        var result = code.substitute("a", 0, null, null, null, largeReplacement);
        assertEquals(largeReplacement, result);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void substituteGlobalWithLargeOutput(IPcre2 api) {
        // Many substitutions that expand the output significantly
        var code = new Pcre2Code(api, ".");
        var subject = "abcdef";
        var result = code.substitute(subject, 0,
                EnumSet.of(Pcre2SubstituteOption.GLOBAL), null, null, "XX");
        assertEquals("XXXXXXXXXXXX", result); // Each char replaced with "XX"
    }
}
