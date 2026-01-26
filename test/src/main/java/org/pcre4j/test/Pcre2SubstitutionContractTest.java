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
import org.pcre4j.Pcre2SubstituteOption;
import org.pcre4j.api.IPcre2;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Contract tests for PCRE2 substitution operations.
 *
 * @param <T> the PCRE2 API implementation type
 */
public interface Pcre2SubstitutionContractTest<T extends IPcre2> {

    /**
     * Returns the PCRE2 API implementation to test.
     *
     * @return the PCRE2 API implementation
     */
    T getApi();

    @Test
    default void substituteBasic() {
        final var code = new Pcre2Code(
                getApi(),
                "world",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.noneOf(Pcre2SubstituteOption.class),
                null,
                null,
                "universe"
        );
        assertEquals("hello universe", result);
    }

    @Test
    default void substituteGlobal() {
        final var code = new Pcre2Code(
                getApi(),
                "o",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.of(Pcre2SubstituteOption.GLOBAL),
                null,
                null,
                "0"
        );
        assertEquals("hell0 w0rld", result);
    }

    @Test
    default void substituteWithCapture() {
        final var code = new Pcre2Code(
                getApi(),
                "(\\w+) (\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.of(Pcre2SubstituteOption.EXTENDED),
                null,
                null,
                "$2 $1"
        );
        assertEquals("world hello", result);
    }

    @Test
    default void substituteWithNamedCapture() {
        final var code = new Pcre2Code(
                getApi(),
                "(?<first>\\w+) (?<second>\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.of(Pcre2SubstituteOption.EXTENDED),
                null,
                null,
                "${second} ${first}"
        );
        assertEquals("world hello", result);
    }

    @Test
    default void substituteUnicode() {
        final var code = new Pcre2Code(
                getApi(),
                "🌐",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );

        final var result = code.substitute(
                "hello 🌐 world",
                0,
                EnumSet.noneOf(Pcre2SubstituteOption.class),
                null,
                null,
                "🌍"
        );
        assertEquals("hello 🌍 world", result);
    }

    @Test
    default void substituteUnicodeGlobal() {
        final var code = new Pcre2Code(
                getApi(),
                "🌐",
                EnumSet.of(Pcre2CompileOption.UTF),
                null
        );

        final var result = code.substitute(
                "🌐 hello 🌐 world 🌐",
                0,
                EnumSet.of(Pcre2SubstituteOption.GLOBAL),
                null,
                null,
                "🌍"
        );
        assertEquals("🌍 hello 🌍 world 🌍", result);
    }

    @Test
    default void substituteReplacementOnly() {
        final var code = new Pcre2Code(
                getApi(),
                "world",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.of(Pcre2SubstituteOption.REPLACEMENT_ONLY),
                null,
                null,
                "universe"
        );
        assertEquals("universe", result);
    }

    @Test
    default void substituteLiteral() {
        final var code = new Pcre2Code(
                getApi(),
                "(\\w+)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello",
                0,
                EnumSet.of(Pcre2SubstituteOption.LITERAL),
                null,
                null,
                "$1"
        );
        assertEquals("$1", result);
    }

    @Test
    default void substituteNoMatch() {
        final var code = new Pcre2Code(
                getApi(),
                "xyz",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.noneOf(Pcre2SubstituteOption.class),
                null,
                null,
                "replacement"
        );
        assertEquals("hello world", result);
    }

    @Test
    default void substituteEmptySubject() {
        final var code = new Pcre2Code(
                getApi(),
                "world",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "",
                0,
                EnumSet.noneOf(Pcre2SubstituteOption.class),
                null,
                null,
                "universe"
        );
        assertEquals("", result);
    }

    @Test
    default void substituteEmptyReplacement() {
        final var code = new Pcre2Code(
                getApi(),
                "world",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                0,
                EnumSet.noneOf(Pcre2SubstituteOption.class),
                null,
                null,
                ""
        );
        assertEquals("hello ", result);
    }

    @Test
    default void substituteWithStartOffset() {
        final var code = new Pcre2Code(
                getApi(),
                "o",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                5,
                EnumSet.noneOf(Pcre2SubstituteOption.class),
                null,
                null,
                "0"
        );
        assertEquals("hello w0rld", result);
    }

    @Test
    default void substituteGlobalWithStartOffset() {
        final var code = new Pcre2Code(
                getApi(),
                "o",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );

        final var result = code.substitute(
                "hello world",
                5,
                EnumSet.of(Pcre2SubstituteOption.GLOBAL),
                null,
                null,
                "0"
        );
        assertEquals("hello w0rld", result);
    }
}
