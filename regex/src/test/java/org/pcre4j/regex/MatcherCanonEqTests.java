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
package org.pcre4j.regex;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@code CANON_EQ} (canonical equivalence) behavior in {@link Matcher}.
 */
public class MatcherCanonEqTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqMatchesWithDecomposedInput(IPcre2 api) {
        // Test matches() with CANON_EQ exercises the NFD normalization setup path
        // and the index conversion back from NFD space to original space
        var regex = "\u00F1";  // ñ (precomposed)
        var input = "n\u0303";  // n + combining tilde (decomposed)

        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CANON_EQ
        ).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqFindStartEndIndicesWithExpansion(IPcre2 api) {
        // Input has decomposed characters that expand in NFD
        // This exercises convertNfdEndIndexToOriginal with the decomposed sequence path
        var regex = "\u00FC";  // ü (precomposed)
        var input = "au\u0308b";  // a + ü (decomposed: u + combining diaeresis) + b

        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CANON_EQ
        ).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertTrue(pcre4jMatcher.hasMatch());
        // ü in the original string starts at index 1 and ends at index 3 (u + combining diaeresis)
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(1, pcre4jMatcher.start());
        assertEquals(3, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqFindDecomposedAtEndOfString(IPcre2 api) {
        // Match a decomposed character at the end of the string
        // This exercises the path in convertNfdEndIndexToOriginal where
        // the loop reaches the end of the normalized string (nextOrigIdx == origIdx)
        var regex = "\u00E9";  // é (precomposed)
        var input = "cafe\u0301";  // caf + e + combining acute (é decomposed at end)

        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CANON_EQ
        ).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertTrue(pcre4jMatcher.hasMatch());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        // The match should span original indices 3..5 (e + combining acute at end)
        assertEquals(3, pcre4jMatcher.start());
        assertEquals(input.length(), pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqMultipleFindWithMixedForms(IPcre2 api) {
        // Multiple find() calls with mixed composed and decomposed forms
        // exercises the NFD index conversion across multiple match positions
        var regex = "\u00E9";  // é
        // Input: a + é(decomposed) + b + é(precomposed) + c + é(decomposed)
        var input = "ae\u0301b\u00E9ce\u0301";

        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CANON_EQ
        ).matcher(input);

        // First match: decomposed at index 1..3
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());

        // Second match: precomposed at index 4..5
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());

        // Third match: decomposed at index 6..8
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());

        // No more matches
        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqGroupIndicesWithDecomposedChars(IPcre2 api) {
        // Test that group start/end indices are correctly mapped back from NFD space
        // when the match contains decomposed characters
        var regex = "(caf\u00E9)(\\s+)(\\w+)";  // (café)(\s+)(\w+)
        var input = "cafe\u0301 latte";  // café (decomposed) + space + latte

        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CANON_EQ
        ).matcher(input);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        // Group 0 (whole match)
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());

        // Group 1 (café)
        assertEquals(javaMatcher.start(1), pcre4jMatcher.start(1));
        assertEquals(javaMatcher.end(1), pcre4jMatcher.end(1));
        assertEquals(javaMatcher.group(1), pcre4jMatcher.group(1));

        // Group 2 (space)
        assertEquals(javaMatcher.start(2), pcre4jMatcher.start(2));
        assertEquals(javaMatcher.end(2), pcre4jMatcher.end(2));

        // Group 3 (latte)
        assertEquals(javaMatcher.start(3), pcre4jMatcher.start(3));
        assertEquals(javaMatcher.end(3), pcre4jMatcher.end(3));
        assertEquals(javaMatcher.group(3), pcre4jMatcher.group(3));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqWithSurrogatePairsAndDecomposition(IPcre2 api) {
        // Test CANON_EQ with surrogate pairs to exercise the buildIndexMappings surrogate path
        // 𝄞 (U+1D11E, Musical Symbol G Clef) is a surrogate pair in UTF-16
        // Combined with a decomposable character to test both paths
        var regex = "\uD834\uDD1E\u00E9";  // G clef + é (precomposed)
        var input = "\uD834\uDD1Ee\u0301";  // G clef + é (decomposed)

        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CANON_EQ
        ).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqFindAfterSurrogatePair(IPcre2 api) {
        // Test that index mapping is correct when a decomposed char follows a surrogate pair
        // The surrogate pair takes 2 chars in UTF-16 but 1 code point
        var regex = "\u00F1";  // ñ (precomposed)
        var input = "\uD834\uDD1En\u0303x";  // G clef + ñ (decomposed) + x

        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CANON_EQ
        ).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertTrue(pcre4jMatcher.hasMatch());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        // ñ starts at index 2 (after surrogate pair) and ends at index 4 (n + combining tilde)
        assertEquals(2, pcre4jMatcher.start());
        assertEquals(4, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqConsecutiveDecomposedChars(IPcre2 api) {
        // Test with consecutive decomposable characters to exercise
        // the normalizedToOriginalIndex mapping with multiple expansions
        var regex = "\u00E9\u00F1";  // éñ (both precomposed)
        var input = "e\u0301n\u0303";  // é + ñ (both decomposed)

        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CANON_EQ
        ).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(0, pcre4jMatcher.start());
        assertEquals(4, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqMatchResultPreservesNfdIndices(IPcre2 api) {
        // Test that toMatchResult() returns correct indices after NFD conversion
        var regex = "(\\w+)\u00E9";  // word + é
        var input = "cafe\u0301";  // caf + é (decomposed)

        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CANON_EQ
        ).matcher(input);

        assertTrue(pcre4jMatcher.matches());
        var result = pcre4jMatcher.toMatchResult();

        assertEquals(pcre4jMatcher.start(), result.start());
        assertEquals(pcre4jMatcher.end(), result.end());
        assertEquals(pcre4jMatcher.start(1), result.start(1));
        assertEquals(pcre4jMatcher.end(1), result.end(1));
        assertEquals(pcre4jMatcher.group(), result.group());
        assertEquals(pcre4jMatcher.group(1), result.group(1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqResetPreservesNfdMapping(IPcre2 api) {
        // Test that reset(CharSequence) reinitializes NFD mappings correctly
        var regex = "\u00FC";  // ü (precomposed)

        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CANON_EQ
        ).matcher("no match here");

        assertFalse(pcre4jMatcher.find());

        // Reset with decomposed input
        pcre4jMatcher.reset("u\u0308");  // ü decomposed
        assertTrue(pcre4jMatcher.matches());
        assertEquals(0, pcre4jMatcher.start());
        assertEquals(2, pcre4jMatcher.end());

        // Reset with precomposed input
        pcre4jMatcher.reset("\u00FC");  // ü precomposed
        assertTrue(pcre4jMatcher.matches());
        assertEquals(0, pcre4jMatcher.start());
        assertEquals(1, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqMultipleCombiningMarksIndices(IPcre2 api) {
        // Test with characters that decompose to more than 2 code points
        // ế (U+1EBF) = e with circumflex and acute
        // NFD: e + combining circumflex + combining acute (3 code points)
        var regex = "\u1EBF";  // ế precomposed
        var input = "xe\u0302\u0301y";  // x + ế (decomposed to 3 code points) + y

        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CANON_EQ
        ).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertTrue(pcre4jMatcher.hasMatch());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        // The decomposed ế starts at index 1 (after x) and ends at 4 (e + 2 combining marks)
        assertEquals(1, pcre4jMatcher.start());
        assertEquals(4, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqNonDecomposableAsciiPreservesIndices(IPcre2 api) {
        // When CANON_EQ is set but input is pure ASCII (no decomposition needed),
        // indices should be identical to non-CANON_EQ mode
        var regex = "(hello) (world)";
        var input = "hello world";

        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CANON_EQ
        ).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(1), pcre4jMatcher.start(1));
        assertEquals(javaMatcher.end(1), pcre4jMatcher.end(1));
        assertEquals(javaMatcher.start(2), pcre4jMatcher.start(2));
        assertEquals(javaMatcher.end(2), pcre4jMatcher.end(2));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqRegionWithDecomposedChars(IPcre2 api) {
        // Test region boundaries are correctly converted to NFD space
        var regex = "\u00E9";  // é
        // Input: x + é(decomposed) + y + é(decomposed) + z
        var input = "xe\u0301ye\u0301z";

        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        javaMatcher.region(0, 3);  // Region covers "xé" (decomposed) only

        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CANON_EQ
        ).matcher(input);
        pcre4jMatcher.region(0, 3);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertTrue(pcre4jMatcher.hasMatch());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());

        // Should not find a second match within the region
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertFalse(pcre4jMatcher.find());
    }

}
