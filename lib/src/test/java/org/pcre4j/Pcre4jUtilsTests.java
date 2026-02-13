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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link Pcre4jUtils} tests.
 */
public class Pcre4jUtilsTests {

    @Test
    void convertOvectorToStringIndices() {
        final var subject = "0123456789";

        assertArrayEquals(
                new int[]{-1, -1},
                Pcre4jUtils.convertOvectorToStringIndices(subject, new long[]{-1, -1})
        );
        assertArrayEquals(
                new int[]{-1, -1, 0, 0},
                Pcre4jUtils.convertOvectorToStringIndices(subject, new long[]{-1, -1, 0, 0})
        );
        assertArrayEquals(
                new int[]{-1, -1, 0, 10},
                Pcre4jUtils.convertOvectorToStringIndices(subject, new long[]{-1, -1, 0, 10})
        );
    }

    @Test
    void convertCharacterIndexToByteOffsetEmptyString() {
        // Empty string with index 0 should return 0
        assertEquals(0, Pcre4jUtils.convertCharacterIndexToByteOffset("", 0));
    }

    @Test
    void convertCharacterIndexToByteOffsetAtEnd() {
        // Index at end of string should return byte length
        assertEquals(10, Pcre4jUtils.convertCharacterIndexToByteOffset("0123456789", 10));
    }

    @Test
    void convertCharacterIndexToByteOffsetOutOfBounds() {
        // Index past end should throw
        assertThrows(IllegalArgumentException.class, () -> {
            Pcre4jUtils.convertCharacterIndexToByteOffset("abc", 4);
        });
    }

    @Test
    void convertCharacterIndexToByteOffsetEmptyStringOutOfBounds() {
        // Index 1 on empty string should throw
        assertThrows(IllegalArgumentException.class, () -> {
            Pcre4jUtils.convertCharacterIndexToByteOffset("", 1);
        });
    }

    @Test
    void convertCharacterIndexToByteOffsetMixedAsciiAndEmoji() {
        // "a😀b" — emoji U+1F600 is surrogate pair (2 Java chars), 4 bytes in UTF-8
        var subject = "a\uD83D\uDE00b";
        assertEquals(4, subject.length()); // a + high surrogate + low surrogate + b
        assertEquals(0, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 0)); // 'a'
        assertEquals(1, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 1)); // high surrogate
        assertEquals(3, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 2)); // low surrogate
        assertEquals(5, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 3)); // 'b'
        assertEquals(6, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 4)); // end
    }

    @Test
    void convertCharacterIndexToByteOffsetConsecutiveEmoji() {
        // "😀😀" — two emoji, each 4 bytes in UTF-8
        var subject = "\uD83D\uDE00\uD83D\uDE00";
        assertEquals(4, subject.length()); // 2 surrogate pairs
        assertEquals(0, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 0));
        assertEquals(2, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 1));
        assertEquals(4, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 2));
        assertEquals(6, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 3));
        assertEquals(8, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 4));
    }

    @Test
    void convertCharacterIndexToByteOffsetSupplementaryCjk() {
        // U+20000 (CJK Unified Ideographs Extension B) — surrogate pair, 4 bytes in UTF-8
        var subject = "a\uD840\uDC00b";
        assertEquals(0, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 0)); // 'a'
        assertEquals(1, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 1)); // high surrogate
        assertEquals(3, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 2)); // low surrogate
        assertEquals(5, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 3)); // 'b'
    }

    @Test
    void convertCharacterIndexToByteOffsetMixedMultiByteWidths() {
        // Mix of 1-byte, 2-byte, 3-byte, and 4-byte characters
        // "aé日😀" — a(1) + é(2) + 日(3) + 😀(4) = 10 bytes
        var subject = "a\u00e9\u65e5\uD83D\uDE00";
        assertEquals(0, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 0)); // 'a'
        assertEquals(1, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 1)); // 'é'
        assertEquals(3, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 2)); // '日'
        assertEquals(6, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 3)); // high surrogate of 😀
        assertEquals(8, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 4)); // low surrogate of 😀
        assertEquals(10, Pcre4jUtils.convertCharacterIndexToByteOffset(subject, 5)); // end
    }

    @Test
    void convertOvectorToStringIndicesSurrogatePairs() {
        // "a😀b" — emoji is 4 bytes in UTF-8
        // byte offsets: a=0, 😀=1..4, b=5
        var subject = "a\uD83D\uDE00b";
        // Match "😀" at byte 1-5
        var ovector = new long[]{1, 5};
        var result = Pcre4jUtils.convertOvectorToStringIndices(subject, ovector);
        assertEquals(1, result[0]); // char index of emoji start
        assertEquals(3, result[1]); // char index of 'b'
    }

    @Test
    void convertOvectorToStringIndicesMixedMultiByte() {
        // "aé日😀z" — byte offsets: a=0(1b), é=1(2b), 日=3(3b), 😀=6(4b), z=10(1b)
        // Java string length = 6 (a + é + 日 + high_surr + low_surr + z)
        var subject = "a\u00e9\u65e5\uD83D\uDE00z";
        assertEquals(6, subject.length());
        // Match the whole string: byte range 0..11
        var ovector = new long[]{0, 11};
        var result = Pcre4jUtils.convertOvectorToStringIndices(subject, ovector);
        assertEquals(0, result[0]);
        assertEquals(6, result[1]); // Java string length = 6
    }

    @Test
    void convertOvectorToStringIndicesMixedWithUnmatched() {
        // "a😀b" with unmatched group mixed with matched group
        var subject = "a\uD83D\uDE00b";
        // Full match + unmatched group + matched group for 'b'
        var ovector = new long[]{0, 6, -1, -1, 5, 6};
        var result = Pcre4jUtils.convertOvectorToStringIndices(subject, ovector);
        assertArrayEquals(new int[]{0, 4, -1, -1, 3, 4}, result);
    }

}
