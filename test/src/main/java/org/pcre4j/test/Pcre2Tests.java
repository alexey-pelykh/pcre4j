/*
 * Copyright (C) 2024 Oleksii PELYKH
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
import org.pcre4j.Pcre2MatchData;
import org.pcre4j.Pcre2MatchOption;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class Pcre2Tests {

    @Test
    public void plainStringMatch() {
        final var code = new Pcre2Code(
                "42",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "42",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(1, result);

        final var ovector = matchData.ovector();
        assertArrayEquals(new Pcre2MatchData.OffsetPair[]{
                new Pcre2MatchData.OffsetPair(0, 2),
        }, ovector);
    }

    @Test
    public void plainStringMatchNoCapture() {
        final var code = new Pcre2Code(
                "(?:42)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "42",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(1, result);

        final var ovector = matchData.ovector();
        assertArrayEquals(new Pcre2MatchData.OffsetPair[]{
                new Pcre2MatchData.OffsetPair(0, 2),
        }, ovector);
    }

    @Test
    public void plainStringMatchCapture() {
        final var code = new Pcre2Code(
                "(42)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "42",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(2, result);

        final var ovector = matchData.ovector();
        assertArrayEquals(new Pcre2MatchData.OffsetPair[]{
                new Pcre2MatchData.OffsetPair(0, 2),
                new Pcre2MatchData.OffsetPair(0, 2),
        }, ovector);
    }

    @Test
    public void plainStringMatchNamedCapture() {
        final var code = new Pcre2Code(
                "(?P<group>42)",
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
        final var matchData = new Pcre2MatchData(code);

        final var result = code.match(
                "42",
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        assertEquals(2, result);

        final var ovector = matchData.ovector();
        assertArrayEquals(new Pcre2MatchData.OffsetPair[]{
                new Pcre2MatchData.OffsetPair(0, 2),
                new Pcre2MatchData.OffsetPair(0, 2),
        }, ovector);
    }

}
