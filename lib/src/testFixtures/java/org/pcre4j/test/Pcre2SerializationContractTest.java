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
import org.pcre4j.api.INativeMemoryAccess;
import org.pcre4j.api.IPcre2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract tests for PCRE2 serialization operations.
 *
 * @param <T> the PCRE2 API implementation type
 */
public interface Pcre2SerializationContractTest<T extends IPcre2> {

    /**
     * Returns the PCRE2 API implementation to test.
     *
     * @return the PCRE2 API implementation
     */
    T getApi();

    @Test
    default void testSerializeEncode() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Test 1: Basic serialization of a single pattern
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Failed to compile pattern");

        final var serializedBytes = new long[1];
        final var serializedSize = new long[1];

        final var result = api.serializeEncode(new long[]{code}, 1, serializedBytes, serializedSize, 0);
        assertEquals(1, result, "serializeEncode should return 1 for single pattern");
        assertTrue(serializedBytes[0] != 0, "serializedBytes should be non-null");
        assertTrue(serializedSize[0] > 0, "serializedSize should be positive");

        // Read the serialized data to verify it's accessible
        final var bytes = ((INativeMemoryAccess) api).readBytes(serializedBytes[0], (int) serializedSize[0]);
        assertEquals(serializedSize[0], bytes.length, "Read bytes should match serialized size");

        api.serializeFree(serializedBytes[0]);
        api.codeFree(code);

        // Test 2: Serialization of multiple patterns
        final var code1 = api.compile("pattern1", 0, errorcode, erroroffset, 0);
        assertTrue(code1 != 0, "Failed to compile pattern1");

        final var code2 = api.compile("pattern2", 0, errorcode, erroroffset, 0);
        assertTrue(code2 != 0, "Failed to compile pattern2");

        final var serializedBytes2 = new long[1];
        final var serializedSize2 = new long[1];

        final var result2 = api.serializeEncode(new long[]{code1, code2}, 2, serializedBytes2, serializedSize2, 0);
        assertEquals(2, result2, "serializeEncode should return 2 for two patterns");
        assertTrue(serializedBytes2[0] != 0, "serializedBytes should be non-null for multiple patterns");
        assertTrue(serializedSize2[0] > serializedSize[0], "Multiple patterns should have larger serialized size");

        api.serializeFree(serializedBytes2[0]);
        api.codeFree(code1);
        api.codeFree(code2);

        // Test 3: Pattern with capturing groups
        final var code3 = api.compile("(\\w+)@(\\w+\\.\\w+)", 0, errorcode, erroroffset, 0);
        assertTrue(code3 != 0, "Failed to compile pattern with capturing groups");

        final var serializedBytes3 = new long[1];
        final var serializedSize3 = new long[1];

        final var result3 = api.serializeEncode(new long[]{code3}, 1, serializedBytes3, serializedSize3, 0);
        assertEquals(1, result3, "serializeEncode should return 1 for pattern with capturing groups");
        assertTrue(serializedBytes3[0] != 0, "serializedBytes should be non-null");
        assertTrue(serializedSize3[0] > 0, "serializedSize should be positive");

        api.serializeFree(serializedBytes3[0]);
        api.codeFree(code3);

        // Test 4: Invalid input - null codes array
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeEncode(null, 1, new long[1], new long[1], 0);
        }, "Should throw IllegalArgumentException for null codes array");

        // Test 5: Invalid input - zero numberOfCodes
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeEncode(new long[]{0}, 0, new long[1], new long[1], 0);
        }, "Should throw IllegalArgumentException for zero numberOfCodes");

        // Test 6: Invalid input - negative numberOfCodes
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeEncode(new long[]{0}, -1, new long[1], new long[1], 0);
        }, "Should throw IllegalArgumentException for negative numberOfCodes");

        // Test 7: Invalid input - null serializedBytes
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeEncode(new long[]{0}, 1, null, new long[1], 0);
        }, "Should throw IllegalArgumentException for null serializedBytes");

        // Test 8: Invalid input - null serializedSize
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeEncode(new long[]{0}, 1, new long[1], null, 0);
        }, "Should throw IllegalArgumentException for null serializedSize");
    }

    @Test
    default void testSerializeDecode() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Test 1: Serialize and decode a single pattern
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Failed to compile pattern");

        final var serializedBytes = new long[1];
        final var serializedSize = new long[1];

        final var encodeResult = api.serializeEncode(new long[]{code}, 1, serializedBytes, serializedSize, 0);
        assertEquals(1, encodeResult, "serializeEncode should return 1 for single pattern");

        // Read the serialized data
        final var bytes = ((INativeMemoryAccess) api).readBytes(serializedBytes[0], (int) serializedSize[0]);
        assertEquals(serializedSize[0], bytes.length, "Read bytes should match serialized size");

        // Decode the pattern
        final var decodedCodes = new long[1];
        final var decodeResult = api.serializeDecode(decodedCodes, 1, bytes, 0);
        assertEquals(1, decodeResult, "serializeDecode should return 1 for single pattern");
        assertTrue(decodedCodes[0] != 0, "Decoded code should be non-null");

        // Verify the decoded pattern works by matching
        final var matchData = api.matchDataCreateFromPattern(decodedCodes[0], 0);
        assertTrue(matchData != 0, "Failed to create match data from decoded pattern");

        final var matchResult = api.match(decodedCodes[0], "hello world", 0, 0, matchData, 0);
        assertTrue(matchResult > 0, "Decoded pattern should match");

        api.matchDataFree(matchData);
        api.codeFree(decodedCodes[0]);
        api.serializeFree(serializedBytes[0]);
        api.codeFree(code);

        // Test 2: Serialize and decode multiple patterns
        final var code1 = api.compile("pattern1", 0, errorcode, erroroffset, 0);
        assertTrue(code1 != 0, "Failed to compile pattern1");

        final var code2 = api.compile("pattern2", 0, errorcode, erroroffset, 0);
        assertTrue(code2 != 0, "Failed to compile pattern2");

        final var serializedBytes2 = new long[1];
        final var serializedSize2 = new long[1];

        final var codes12 = new long[]{code1, code2};
        final var encodeResult2 = api.serializeEncode(codes12, 2, serializedBytes2, serializedSize2, 0);
        assertEquals(2, encodeResult2, "serializeEncode should return 2 for two patterns");

        final var bytes2 = ((INativeMemoryAccess) api).readBytes(serializedBytes2[0], (int) serializedSize2[0]);

        final var decodedCodes2 = new long[2];
        final var decodeResult2 = api.serializeDecode(decodedCodes2, 2, bytes2, 0);
        assertEquals(2, decodeResult2, "serializeDecode should return 2 for two patterns");
        assertTrue(decodedCodes2[0] != 0, "First decoded code should be non-null");
        assertTrue(decodedCodes2[1] != 0, "Second decoded code should be non-null");

        // Verify both decoded patterns work
        final var matchData1 = api.matchDataCreateFromPattern(decodedCodes2[0], 0);
        final var match1 = api.match(decodedCodes2[0], "pattern1 test", 0, 0, matchData1, 0);
        assertTrue(match1 > 0, "First decoded pattern should match");
        api.matchDataFree(matchData1);

        final var matchData2 = api.matchDataCreateFromPattern(decodedCodes2[1], 0);
        final var match2 = api.match(decodedCodes2[1], "pattern2 test", 0, 0, matchData2, 0);
        assertTrue(match2 > 0, "Second decoded pattern should match");
        api.matchDataFree(matchData2);

        api.codeFree(decodedCodes2[0]);
        api.codeFree(decodedCodes2[1]);
        api.serializeFree(serializedBytes2[0]);
        api.codeFree(code1);
        api.codeFree(code2);

        // Test 3: Decode with capturing groups preserved
        final var code3 = api.compile("(\\w+)@(\\w+\\.\\w+)", 0, errorcode, erroroffset, 0);
        assertTrue(code3 != 0, "Failed to compile pattern with capturing groups");

        final var serializedBytes3 = new long[1];
        final var serializedSize3 = new long[1];

        final var encodeResult3 = api.serializeEncode(new long[]{code3}, 1, serializedBytes3, serializedSize3, 0);
        assertEquals(1, encodeResult3, "serializeEncode should return 1");

        final var bytes3 = ((INativeMemoryAccess) api).readBytes(serializedBytes3[0], (int) serializedSize3[0]);

        final var decodedCodes3 = new long[1];
        final var decodeResult3 = api.serializeDecode(decodedCodes3, 1, bytes3, 0);
        assertEquals(1, decodeResult3, "serializeDecode should return 1");

        // Verify capturing groups work in decoded pattern
        final var matchData3 = api.matchDataCreateFromPattern(decodedCodes3[0], 0);
        final var match3 = api.match(decodedCodes3[0], "user@example.com", 0, 0, matchData3, 0);
        assertTrue(match3 > 0, "Decoded pattern with groups should match");

        // Check that we got the expected number of groups
        final var ovectorCount = api.getOvectorCount(matchData3);
        assertEquals(3, ovectorCount, "Should have 3 groups (full match + 2 capturing groups)");

        api.matchDataFree(matchData3);
        api.codeFree(decodedCodes3[0]);
        api.serializeFree(serializedBytes3[0]);
        api.codeFree(code3);

        // Test 4: Invalid input - null codes array
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeDecode(null, 1, new byte[10], 0);
        }, "Should throw IllegalArgumentException for null codes array");

        // Test 5: Invalid input - zero numberOfCodes
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeDecode(new long[1], 0, new byte[10], 0);
        }, "Should throw IllegalArgumentException for zero numberOfCodes");

        // Test 6: Invalid input - negative numberOfCodes
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeDecode(new long[1], -1, new byte[10], 0);
        }, "Should throw IllegalArgumentException for negative numberOfCodes");

        // Test 7: Invalid input - null bytes array
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeDecode(new long[1], 1, null, 0);
        }, "Should throw IllegalArgumentException for null bytes array");

        // Test 8: Invalid input - codes array too small
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeDecode(new long[1], 2, new byte[10], 0);
        }, "Should throw IllegalArgumentException when codes array is smaller than numberOfCodes");
    }

    @Test
    default void testSerializeFree() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Test 1: Free serialized data from a single pattern
        final var code = api.compile("test", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Failed to compile pattern");

        final var serializedBytes = new long[1];
        final var serializedSize = new long[1];

        final var result = api.serializeEncode(new long[]{code}, 1, serializedBytes, serializedSize, 0);
        assertEquals(1, result, "serializeEncode should return 1 for single pattern");
        assertTrue(serializedBytes[0] != 0, "serializedBytes should be non-null");

        // Free the serialized data - should not throw
        api.serializeFree(serializedBytes[0]);

        api.codeFree(code);

        // Test 2: Free with null pointer (0) - should not throw
        api.serializeFree(0);

        // Test 3: Free serialized data from multiple patterns
        final var code1 = api.compile("pattern1", 0, errorcode, erroroffset, 0);
        final var code2 = api.compile("pattern2", 0, errorcode, erroroffset, 0);
        assertTrue(code1 != 0, "Failed to compile pattern1");
        assertTrue(code2 != 0, "Failed to compile pattern2");

        final var serializedBytes2 = new long[1];
        final var serializedSize2 = new long[1];

        final var result2 = api.serializeEncode(new long[]{code1, code2}, 2, serializedBytes2, serializedSize2, 0);
        assertEquals(2, result2, "serializeEncode should return 2 for two patterns");

        // Free the serialized data - should not throw
        api.serializeFree(serializedBytes2[0]);

        api.codeFree(code1);
        api.codeFree(code2);
    }

    @Test
    default void testSerializeGetNumberOfCodes() {
        var api = getApi();
        final var errorcode = new int[1];
        final var erroroffset = new long[1];

        // Test 1: Get count from serialized single pattern
        final var code = api.compile("hello", 0, errorcode, erroroffset, 0);
        assertTrue(code != 0, "Failed to compile pattern");

        final var serializedBytes = new long[1];
        final var serializedSize = new long[1];

        final var encodeResult = api.serializeEncode(new long[]{code}, 1, serializedBytes, serializedSize, 0);
        assertEquals(1, encodeResult, "serializeEncode should return 1 for single pattern");

        final var bytes = ((INativeMemoryAccess) api).readBytes(serializedBytes[0], (int) serializedSize[0]);
        final var count = api.serializeGetNumberOfCodes(bytes);
        assertEquals(1, count, "serializeGetNumberOfCodes should return 1 for single pattern");

        api.serializeFree(serializedBytes[0]);
        api.codeFree(code);

        // Test 2: Get count from serialized multiple patterns
        final var code1 = api.compile("pattern1", 0, errorcode, erroroffset, 0);
        assertTrue(code1 != 0, "Failed to compile pattern1");

        final var code2 = api.compile("pattern2", 0, errorcode, erroroffset, 0);
        assertTrue(code2 != 0, "Failed to compile pattern2");

        final var code3 = api.compile("pattern3", 0, errorcode, erroroffset, 0);
        assertTrue(code3 != 0, "Failed to compile pattern3");

        final var serializedBytes2 = new long[1];
        final var serializedSize2 = new long[1];

        final var encodeResult2 = api.serializeEncode(
                new long[]{code1, code2, code3}, 3, serializedBytes2, serializedSize2, 0);
        assertEquals(3, encodeResult2, "serializeEncode should return 3 for three patterns");

        final var bytes2 = ((INativeMemoryAccess) api).readBytes(serializedBytes2[0], (int) serializedSize2[0]);
        final var count2 = api.serializeGetNumberOfCodes(bytes2);
        assertEquals(3, count2, "serializeGetNumberOfCodes should return 3 for three patterns");

        api.serializeFree(serializedBytes2[0]);
        api.codeFree(code1);
        api.codeFree(code2);
        api.codeFree(code3);

        // Test 3: Invalid input - null bytes array
        assertThrows(IllegalArgumentException.class, () -> {
            api.serializeGetNumberOfCodes(null);
        }, "Should throw IllegalArgumentException for null bytes array");

        // Test 4: Invalid input - corrupted/invalid data (should return ERROR_BADMAGIC)
        final var invalidBytes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        final var invalidResult = api.serializeGetNumberOfCodes(invalidBytes);
        assertEquals(IPcre2.ERROR_BADMAGIC, invalidResult,
                "serializeGetNumberOfCodes should return ERROR_BADMAGIC for invalid data");
    }
}
