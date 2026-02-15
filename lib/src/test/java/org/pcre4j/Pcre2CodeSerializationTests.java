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
package org.pcre4j;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;
import org.pcre4j.exception.Pcre2Exception;
import org.pcre4j.option.Pcre2CompileOption;
import org.pcre4j.option.Pcre2MatchOption;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Pcre2CodeSerializationTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void serializeSinglePattern(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        var serialized = Pcre2Code.serialize(code);
        assertNotNull(serialized);
        assertTrue(serialized.length > 0);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void serializeMultiplePatterns(IPcre2 api) {
        var code1 = new Pcre2Code(api, "pattern1");
        var code2 = new Pcre2Code(api, "pattern2");
        var serialized = Pcre2Code.serialize(code1, code2);
        assertNotNull(serialized);
        assertTrue(serialized.length > 0);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void serializeNullThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> {
            Pcre2Code.serialize((Pcre2Code[]) null);
        });
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void serializeEmptyThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> {
            Pcre2Code.serialize();
        });
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void deserializeSinglePattern(IPcre2 api) {
        var code = new Pcre2Code(api, "hello");
        var serialized = Pcre2Code.serialize(code);

        var deserialized = Pcre2Code.deserialize(api, serialized);
        assertNotNull(deserialized);
        assertEquals(1, deserialized.length);

        var matchData = new Pcre2MatchData(deserialized[0]);
        var result = deserialized[0].match(
                "hello world", 0,
                EnumSet.noneOf(Pcre2MatchOption.class), matchData, null
        );
        assertTrue(result > 0, "Deserialized pattern should match");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void deserializeMultiplePatterns(IPcre2 api) {
        var code1 = new Pcre2Code(api, "pattern1");
        var code2 = new Pcre2Code(api, "pattern2");
        var serialized = Pcre2Code.serialize(code1, code2);

        var deserialized = Pcre2Code.deserialize(api, serialized);
        assertNotNull(deserialized);
        assertEquals(2, deserialized.length);

        var matchData1 = new Pcre2MatchData(deserialized[0]);
        var result1 = deserialized[0].match(
                "pattern1 test", 0,
                EnumSet.noneOf(Pcre2MatchOption.class), matchData1, null
        );
        assertTrue(result1 > 0, "First deserialized pattern should match");

        var matchData2 = new Pcre2MatchData(deserialized[1]);
        var result2 = deserialized[1].match(
                "pattern2 test", 0,
                EnumSet.noneOf(Pcre2MatchOption.class), matchData2, null
        );
        assertTrue(result2 > 0, "Second deserialized pattern should match");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void roundTripPreservesCapturingGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "(\\w+)@(\\w+\\.\\w+)");
        var serialized = Pcre2Code.serialize(code);
        var deserialized = Pcre2Code.deserialize(api, serialized);
        assertEquals(1, deserialized.length);

        var matchData = new Pcre2MatchData(deserialized[0]);
        var result = deserialized[0].match(
                "user@example.com", 0,
                EnumSet.noneOf(Pcre2MatchOption.class), matchData, null
        );
        assertTrue(result > 0, "Deserialized pattern with groups should match");
        assertEquals(
                code.captureCount(),
                deserialized[0].captureCount(),
                "Capture count should be preserved"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void roundTripPreservesOptions(IPcre2 api) {
        var options = EnumSet.of(
                Pcre2CompileOption.CASELESS, Pcre2CompileOption.DOTALL
        );
        var code = new Pcre2Code(api, "test", options);
        var serialized = Pcre2Code.serialize(code);
        var deserialized = Pcre2Code.deserialize(api, serialized);
        assertEquals(1, deserialized.length);

        var originalOptions = code.allOptions();
        var deserializedOptions = deserialized[0].allOptions();
        assertEquals(
                originalOptions, deserializedOptions,
                "Options should be preserved through serialization"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void deserializeNullApiThrows(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var serialized = Pcre2Code.serialize(code);

        assertThrows(IllegalArgumentException.class, () -> {
            Pcre2Code.deserialize(null, serialized);
        });
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void deserializeNullDataThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> {
            Pcre2Code.deserialize(api, null);
        });
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void deserializeEmptyDataThrows(IPcre2 api) {
        assertThrows(IllegalArgumentException.class, () -> {
            Pcre2Code.deserialize(api, new byte[0]);
        });
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void deserializeInvalidDataThrows(IPcre2 api) {
        assertThrows(Pcre2Exception.class, () -> {
            Pcre2Code.deserialize(
                    api, new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
            );
        });
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void deserializeWithGlobalBackend(IPcre2 api) {
        Pcre4j.setup(api);
        var code = new Pcre2Code(api, "hello");
        var serialized = Pcre2Code.serialize(code);

        var deserialized = Pcre2Code.deserialize(serialized);
        assertNotNull(deserialized);
        assertEquals(1, deserialized.length);

        var matchData = new Pcre2MatchData(deserialized[0]);
        var result = deserialized[0].match(
                "hello world", 0,
                EnumSet.noneOf(Pcre2MatchOption.class), matchData, null
        );
        assertTrue(result > 0, "Deserialized pattern should match");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void roundTripPreservesNamedGroups(IPcre2 api) {
        var code = new Pcre2Code(api, "(?<user>\\w+)@(?<host>\\w+\\.\\w+)");
        var serialized = Pcre2Code.serialize(code);
        var deserialized = Pcre2Code.deserialize(api, serialized);
        assertEquals(1, deserialized.length);

        assertArrayEquals(
                code.nameTable(), deserialized[0].nameTable(),
                "Name table should be preserved through serialization"
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void deserializedPatternApiMatchesProvided(IPcre2 api) {
        var code = new Pcre2Code(api, "test");
        var serialized = Pcre2Code.serialize(code);
        var deserialized = Pcre2Code.deserialize(api, serialized);

        assertEquals(
                api, deserialized[0].api(),
                "Deserialized pattern should use the provided API"
        );
    }
}
