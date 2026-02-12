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
package org.pcre4j.regex;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure API likeness of the {@link Pattern} to the {@link java.util.regex.Pattern}.
 */
public class PatternTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void toStringReturnsPattern(IPcre2 api) {
        var regex = "\\d+";
        var javaPattern = java.util.regex.Pattern.compile(regex);
        var pcre4jPattern = Pattern.compile(api, regex);

        assertEquals(javaPattern.toString(), pcre4jPattern.toString());
        assertEquals(regex, pcre4jPattern.toString());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void namedGroups(IPcre2 api) {
        var regex = "(?<number>42)";
        var javaPattern = java.util.regex.Pattern.compile(regex);
        var pcre4jPattern = Pattern.compile(api, regex);

        assertEquals(javaPattern.namedGroups(), pcre4jPattern.namedGroups());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void split(IPcre2 api) {
        var regex = "\\D+";
        var input = "0, 1, 1, 2, 3, 5, 8, ..., 144, ...";
        var javaPattern = java.util.regex.Pattern.compile(regex);
        var pcre4jPattern = Pattern.compile(api, regex);

        assertArrayEquals(javaPattern.split(input), pcre4jPattern.split(input));
        assertArrayEquals(javaPattern.split(input, 2), pcre4jPattern.split(input, 2));
        assertArrayEquals(javaPattern.splitWithDelimiters(input, 0), pcre4jPattern.splitWithDelimiters(input, 0));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicodeSplit(IPcre2 api) {
        var regex = "\\D+";
        var input = "0 ⇾ 1 ⇾ 1 ⇾ 2 ⇾ 3 ⇾ 5 ⇾ 8 ⇾ … ⇾ 144 ⇾ …";
        var javaPattern = java.util.regex.Pattern.compile(regex);
        var pcre4jPattern = Pattern.compile(api, regex);

        assertArrayEquals(javaPattern.split(input), pcre4jPattern.split(input));
        assertArrayEquals(javaPattern.split(input, 2), pcre4jPattern.split(input, 2));
        assertArrayEquals(javaPattern.splitWithDelimiters(input, 0), pcre4jPattern.splitWithDelimiters(input, 0));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void withoutUnicodeCharacterClass(IPcre2 api) {
        var regex = "\\w";
        var input = "Ǎ";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void withUnicodeCharacterClass(IPcre2 api) {
        var regex = "\\w";
        var input = "Ǎ";
        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.UNICODE_CHARACTER_CLASS
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.UNICODE_CHARACTER_CLASS
        ).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void withoutUnixNewline(IPcre2 api) {
        var regex = "^A$";
        var input = "A\u0085B";
        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.MULTILINE
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.MULTILINE
        ).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void withUnixNewline(IPcre2 api) {
        var regex = "^A$";
        var input = "A\u0085B";
        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.MULTILINE | java.util.regex.Pattern.UNIX_LINES
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.MULTILINE | Pattern.UNIX_LINES
        ).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertThrows(IllegalStateException.class, javaMatcher::group);
        assertThrows(IllegalStateException.class, pcre4jMatcher::group);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void quote(IPcre2 api) {
        var input = ".*+?^$|()[]\\{}";
        var inputWithSlashE = "abc\\Edef";
        var inputWithEmptyString = "";

        assertEquals(java.util.regex.Pattern.quote(inputWithEmptyString), Pattern.quote(inputWithEmptyString));
        assertEquals(java.util.regex.Pattern.quote(input), Pattern.quote(input));
        assertEquals(java.util.regex.Pattern.quote(inputWithSlashE), Pattern.quote(inputWithSlashE));
        assertTrue(Pattern.compile(api, Pattern.quote(inputWithEmptyString)).matcher(inputWithEmptyString).matches());
        assertTrue(Pattern.compile(api, Pattern.quote(input)).matcher(input).matches());
        assertTrue(Pattern.compile(api, Pattern.quote(inputWithSlashE)).matcher(inputWithSlashE).matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void commentsWhitespaceIgnored(IPcre2 api) {
        // Whitespace in pattern should be ignored with COMMENTS flag
        var regex = "a b c";
        var input = "abc";
        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.COMMENTS
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.COMMENTS
        ).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void commentsHashComments(IPcre2 api) {
        // Comments starting with # should be ignored until end of line
        var regex = "abc # this is a comment\ndef";
        var input = "abcdef";
        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.COMMENTS
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.COMMENTS
        ).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void commentsEscapedWhitespace(IPcre2 api) {
        // Escaped whitespace should be matched literally
        var regex = "a\\ b";
        var input = "a b";
        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.COMMENTS
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.COMMENTS
        ).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void commentsWhitespaceInCharacterClass(IPcre2 api) {
        // Escaped whitespace inside character class should be matched literally
        // Note: In PCRE2's EXTENDED mode, whitespace inside character classes is preserved,
        // but in Java's COMMENTS mode, whitespace inside character classes is also ignored.
        // Using escaped space [\\ ] works consistently in both.
        var regex = "[\\ ]";
        var input = " ";
        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.COMMENTS
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.COMMENTS
        ).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void commentsEmbeddedFlag(IPcre2 api) {
        // Embedded (?x) flag should enable comments mode
        var regex = "(?x)a b c";
        var input = "abc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicodeCaseFlagValue(IPcre2 api) {
        // Verify UNICODE_CASE flag has the correct value (0x40)
        assertEquals(java.util.regex.Pattern.UNICODE_CASE, Pattern.UNICODE_CASE);
        assertEquals(0x40, Pattern.UNICODE_CASE);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicodeCaseKelvinSign(IPcre2 api) {
        // Test Unicode case folding with Kelvin sign (U+212A)
        // In Unicode case folding, Kelvin sign matches k/K
        var regex = "k";
        var input = "\u212A"; // Kelvin sign

        // Java requires UNICODE_CASE for this to match
        var javaMatcherWithoutUnicodeCase = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CASE_INSENSITIVE
        ).matcher(input);
        var javaMatcherWithUnicodeCase = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.UNICODE_CASE
        ).matcher(input);

        // PCRE4J with UTF mode always does Unicode case folding
        var pcre4jMatcherWithoutUnicodeCase = Pattern.compile(
                api,
                regex,
                Pattern.CASE_INSENSITIVE
        ).matcher(input);
        var pcre4jMatcherWithUnicodeCase = Pattern.compile(
                api,
                regex,
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        ).matcher(input);

        // Java without UNICODE_CASE: no match
        assertFalse(javaMatcherWithoutUnicodeCase.matches());
        // Java with UNICODE_CASE: match
        assertTrue(javaMatcherWithUnicodeCase.matches());

        // PCRE4J always matches (UTF mode enables Unicode case folding by default)
        // Note: This is a documented behavioral difference from java.util.regex
        assertTrue(pcre4jMatcherWithoutUnicodeCase.matches());
        assertTrue(pcre4jMatcherWithUnicodeCase.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicodeCaseLongS(IPcre2 api) {
        // Test Unicode case folding with Long S (U+017F)
        // In Unicode case folding, Long S (ſ) matches s/S
        var regex = "s";
        var input = "\u017F"; // Long S

        // Java requires UNICODE_CASE for this to match
        var javaMatcherWithoutUnicodeCase = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CASE_INSENSITIVE
        ).matcher(input);
        var javaMatcherWithUnicodeCase = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.UNICODE_CASE
        ).matcher(input);

        // PCRE4J with UTF mode always does Unicode case folding
        var pcre4jMatcherWithoutUnicodeCase = Pattern.compile(
                api,
                regex,
                Pattern.CASE_INSENSITIVE
        ).matcher(input);
        var pcre4jMatcherWithUnicodeCase = Pattern.compile(
                api,
                regex,
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        ).matcher(input);

        // Java without UNICODE_CASE: no match
        assertFalse(javaMatcherWithoutUnicodeCase.matches());
        // Java with UNICODE_CASE: match
        assertTrue(javaMatcherWithUnicodeCase.matches());

        // PCRE4J always matches (UTF mode enables Unicode case folding by default)
        // Note: This is a documented behavioral difference from java.util.regex
        assertTrue(pcre4jMatcherWithoutUnicodeCase.matches());
        assertTrue(pcre4jMatcherWithUnicodeCase.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicodeCaseWithEmbeddedCaseInsensitiveFlag(IPcre2 api) {
        // Test that embedded (?i) flag with UTF mode provides Unicode case folding
        // In PCRE4J with UTF mode, Unicode case folding is always enabled
        var regex = "(?i)k"; // case-insensitive via embedded flag
        var input = "\u212A"; // Kelvin sign

        // PCRE4J with UTF always does Unicode case folding
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicodeCaseBasicCaseInsensitive(IPcre2 api) {
        // Test basic case-insensitive matching still works
        var regex = "hello";
        var input = "HELLO";

        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.UNICODE_CASE
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        ).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicodeCaseFlagsMethod(IPcre2 api) {
        // Verify flags() method returns UNICODE_CASE when set
        var regex = "test";
        int flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

        var pattern = Pattern.compile(api, regex, flags);
        assertEquals(flags, pattern.flags());
        assertTrue((pattern.flags() & Pattern.UNICODE_CASE) != 0);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void unicodeCaseAloneHasNoEffect(IPcre2 api) {
        // UNICODE_CASE without CASE_INSENSITIVE should not enable case-insensitive matching
        var regex = "k";
        var input = "K";

        var javaMatcher = java.util.regex.Pattern.compile(
                regex,
                java.util.regex.Pattern.UNICODE_CASE
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                regex,
                Pattern.UNICODE_CASE
        ).matcher(input);

        // Both should NOT match - UNICODE_CASE alone doesn't enable case-insensitive matching
        assertFalse(javaMatcher.matches());
        assertFalse(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqFlagValue(IPcre2 api) {
        // Verify CANON_EQ flag has the correct value (0x80)
        assertEquals(java.util.regex.Pattern.CANON_EQ, Pattern.CANON_EQ);
        assertEquals(0x80, Pattern.CANON_EQ);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqPrecomposedPatternMatchesDecomposedInput(IPcre2 api) {
        // Test canonical equivalence: precomposed pattern matches decomposed input
        // Pattern: é (U+00E9, precomposed)
        // Input: e + combining acute accent (U+0065 U+0301, decomposed)
        var precomposedPattern = "\u00E9";  // é
        var decomposedInput = "e\u0301";     // e + combining acute accent

        var javaMatcher = java.util.regex.Pattern.compile(
                precomposedPattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(decomposedInput);
        var pcre4jMatcher = Pattern.compile(
                api,
                precomposedPattern,
                Pattern.CANON_EQ
        ).matcher(decomposedInput);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqDecomposedPatternMatchesPrecomposedInput(IPcre2 api) {
        // Test canonical equivalence: decomposed pattern matches precomposed input
        // Pattern: a + combining ring above (U+0061 U+030A, decomposed)
        // Input: å (U+00E5, precomposed)
        var decomposedPattern = "a\u030A";  // a + combining ring above
        var precomposedInput = "\u00E5";    // å

        var javaMatcher = java.util.regex.Pattern.compile(
                decomposedPattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(precomposedInput);
        var pcre4jMatcher = Pattern.compile(
                api,
                decomposedPattern,
                Pattern.CANON_EQ
        ).matcher(precomposedInput);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqWithoutFlagNoMatch(IPcre2 api) {
        // Without CANON_EQ, precomposed and decomposed should NOT match
        var precomposedPattern = "\u00E9";  // é
        var decomposedInput = "e\u0301";     // e + combining acute accent

        var javaMatcher = java.util.regex.Pattern.compile(precomposedPattern).matcher(decomposedInput);
        var pcre4jMatcher = Pattern.compile(api, precomposedPattern).matcher(decomposedInput);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertFalse(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqFindInMiddleOfString(IPcre2 api) {
        // Test find() with canonical equivalence
        var precomposedPattern = "caf\u00E9";  // café with precomposed é
        var decomposedInput = "I love cafe\u0301!";  // café with decomposed é

        var javaMatcher = java.util.regex.Pattern.compile(
                precomposedPattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(decomposedInput);
        var pcre4jMatcher = Pattern.compile(
                api,
                precomposedPattern,
                Pattern.CANON_EQ
        ).matcher(decomposedInput);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertTrue(pcre4jMatcher.hasMatch());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqReplaceAll(IPcre2 api) {
        // Test replaceAll() with canonical equivalence
        var precomposedPattern = "caf\u00E9";
        // Input: café with decomposed é AND café with precomposed é
        var mixedInput = "cafe\u0301 and caf\u00E9";

        var javaMatcher = java.util.regex.Pattern.compile(
                precomposedPattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(mixedInput);
        var pcre4jMatcher = Pattern.compile(
                api,
                precomposedPattern,
                Pattern.CANON_EQ
        ).matcher(mixedInput);

        assertEquals(javaMatcher.replaceAll("tea"), pcre4jMatcher.replaceAll("tea"));
        assertEquals("tea and tea", pcre4jMatcher.reset().replaceAll("tea"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqReplaceFirst(IPcre2 api) {
        // Test replaceFirst() with canonical equivalence
        var precomposedPattern = "caf\u00E9";
        var mixedInput = "cafe\u0301 and caf\u00E9";

        var javaMatcher = java.util.regex.Pattern.compile(
                precomposedPattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(mixedInput);
        var pcre4jMatcher = Pattern.compile(
                api,
                precomposedPattern,
                Pattern.CANON_EQ
        ).matcher(mixedInput);

        assertEquals(javaMatcher.replaceFirst("tea"), pcre4jMatcher.replaceFirst("tea"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqSplit(IPcre2 api) {
        // Test split() with canonical equivalence
        var precomposedPattern = "\u00E9";  // é
        // Input: a + é (decomposed) + b + é (precomposed) + c
        var mixedInput = "ae\u0301b\u00E9c";

        var javaPattern = java.util.regex.Pattern.compile(
                precomposedPattern,
                java.util.regex.Pattern.CANON_EQ
        );
        var pcre4jPattern = Pattern.compile(
                api,
                precomposedPattern,
                Pattern.CANON_EQ
        );

        assertArrayEquals(javaPattern.split(mixedInput), pcre4jPattern.split(mixedInput));
        assertArrayEquals(new String[]{"a", "b", "c"}, pcre4jPattern.split(mixedInput));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqWithCaseInsensitive(IPcre2 api) {
        // Test CANON_EQ combined with CASE_INSENSITIVE
        var upperPrecomposed = "\u00C9";  // É (uppercase)
        var lowerDecomposed = "e\u0301";  // é (lowercase, decomposed)

        var javaMatcher = java.util.regex.Pattern.compile(
                upperPrecomposed,
                java.util.regex.Pattern.CANON_EQ | java.util.regex.Pattern.CASE_INSENSITIVE
        ).matcher(lowerDecomposed);
        var pcre4jMatcher = Pattern.compile(
                api,
                upperPrecomposed,
                Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE
        ).matcher(lowerDecomposed);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqGroupCapture(IPcre2 api) {
        // Test that captured groups return correct text
        var groupPattern = "(caf\u00E9)";  // café in a capture group
        var decomposedInput = "cafe\u0301";

        var javaMatcher = java.util.regex.Pattern.compile(
                groupPattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(decomposedInput);
        var pcre4jMatcher = Pattern.compile(
                api,
                groupPattern,
                Pattern.CANON_EQ
        ).matcher(decomposedInput);

        assertTrue(javaMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
        // Group 1 should return the actual matched text from input (decomposed form)
        assertEquals(javaMatcher.group(1), pcre4jMatcher.group(1));
        assertEquals(decomposedInput, pcre4jMatcher.group(1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqFlagsMethod(IPcre2 api) {
        // Verify flags() method returns CANON_EQ when set
        var regex = "test";
        int flags = Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE;

        var pattern = Pattern.compile(api, regex, flags);
        assertEquals(flags, pattern.flags());
        assertTrue((pattern.flags() & Pattern.CANON_EQ) != 0);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqAlternationPattern(IPcre2 api) {
        // Test CANON_EQ with alternation pattern (instead of character class)
        // Alternation works correctly with NFD normalization
        var alternationPattern = "\u00E9|\u00E8";  // é|è - alternation
        var precomposedInput = "\u00E9";  // é precomposed

        var javaMatcher = java.util.regex.Pattern.compile(
                alternationPattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(precomposedInput);
        var pcre4jMatcher = Pattern.compile(
                api,
                alternationPattern,
                Pattern.CANON_EQ
        ).matcher(precomposedInput);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());

        // Also test with decomposed input
        var decomposedInput = "e\u0301";  // é decomposed
        javaMatcher = java.util.regex.Pattern.compile(
                alternationPattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(decomposedInput);
        pcre4jMatcher = Pattern.compile(
                api,
                alternationPattern,
                Pattern.CANON_EQ
        ).matcher(decomposedInput);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqMultipleCombiningMarks(IPcre2 api) {
        // Test with multiple combining marks
        // ế (U+1EBF) = e with circumflex and acute
        var precomposed = "\u1EBF";
        var decomposed = "e\u0302\u0301";  // e + combining circumflex + combining acute

        var javaMatcher = java.util.regex.Pattern.compile(
                precomposed,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(decomposed);
        var pcre4jMatcher = Pattern.compile(
                api,
                precomposed,
                Pattern.CANON_EQ
        ).matcher(decomposed);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqWithRegion(IPcre2 api) {
        // Test CANON_EQ with region (non-zero regionStart)
        var pattern = "caf\u00E9";  // café with precomposed é
        var input = "XXXcafe\u0301YYY";  // café with decomposed é surrounded by other chars

        var javaMatcher = java.util.regex.Pattern.compile(
                pattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        javaMatcher.region(3, 8);  // Region covers "café" (decomposed)

        var pcre4jMatcher = Pattern.compile(
                api,
                pattern,
                Pattern.CANON_EQ
        ).matcher(input);
        pcre4jMatcher.region(3, 8);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqWithRegionFind(IPcre2 api) {
        // Test CANON_EQ with region using find()
        var pattern = "\u00E9";  // é precomposed
        var input = "ae\u0301b\u00E9c";  // a + é(decomposed) + b + é(precomposed) + c

        var javaMatcher = java.util.regex.Pattern.compile(
                pattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        javaMatcher.region(0, 3);  // Region covers "aé" (decomposed)

        var pcre4jMatcher = Pattern.compile(
                api,
                pattern,
                Pattern.CANON_EQ
        ).matcher(input);
        pcre4jMatcher.region(0, 3);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertTrue(pcre4jMatcher.hasMatch());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqWithTransparentBounds(IPcre2 api) {
        // Test CANON_EQ with transparent bounds
        var pattern = "(?<=a)e\u0301";  // lookbehind for 'a' + decomposed é
        var input = "ae\u0301b";

        var javaMatcher = java.util.regex.Pattern.compile(
                pattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        javaMatcher.region(1, 3);
        javaMatcher.useTransparentBounds(true);

        var pcre4jMatcher = Pattern.compile(
                api,
                pattern,
                Pattern.CANON_EQ
        ).matcher(input);
        pcre4jMatcher.region(1, 3);
        pcre4jMatcher.useTransparentBounds(true);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqResetWithNewInput(IPcre2 api) {
        // Test that reset(CharSequence) properly reinitializes CANON_EQ support
        var pattern = "\u00E9";  // é precomposed

        var pcre4jMatcher = Pattern.compile(
                api,
                pattern,
                Pattern.CANON_EQ
        ).matcher("test");

        // Reset with decomposed input
        pcre4jMatcher.reset("e\u0301");
        assertTrue(pcre4jMatcher.matches());

        // Reset with precomposed input
        pcre4jMatcher.reset("\u00E9");
        assertTrue(pcre4jMatcher.matches());

        // Reset with non-matching input
        pcre4jMatcher.reset("a");
        assertFalse(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqMultipleFinds(IPcre2 api) {
        // Test multiple find() calls with CANON_EQ
        var pattern = "\u00E9";  // é
        // Three é characters: decomposed, precomposed, decomposed
        var input = "e\u0301 \u00E9 e\u0301";

        var javaMatcher = java.util.regex.Pattern.compile(
                pattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                pattern,
                Pattern.CANON_EQ
        ).matcher(input);

        int javaCount = 0;
        while (javaMatcher.find()) javaCount++;

        int pcre4jCount = 0;
        while (pcre4jMatcher.find()) pcre4jCount++;

        assertEquals(javaCount, pcre4jCount);
        assertEquals(3, pcre4jCount);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqLookingAt(IPcre2 api) {
        // Test lookingAt() with CANON_EQ
        var pattern = "caf\u00E9";  // café
        var input = "cafe\u0301 is good";  // café (decomposed) + rest

        var javaMatcher = java.util.regex.Pattern.compile(
                pattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                pattern,
                Pattern.CANON_EQ
        ).matcher(input);

        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
        assertTrue(pcre4jMatcher.lookingAt());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqAtEndOfString(IPcre2 api) {
        // Test CANON_EQ when match is at end of string
        var pattern = "\u00E9$";  // é at end
        var input = "cafe\u0301";  // café with decomposed é at end

        var javaMatcher = java.util.regex.Pattern.compile(
                pattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                pattern,
                Pattern.CANON_EQ
        ).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertTrue(pcre4jMatcher.hasMatch());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqToMatchResult(IPcre2 api) {
        // Test toMatchResult() preserves correct indices with CANON_EQ
        var pattern = "caf\u00E9";
        var input = "I love cafe\u0301!";

        var pcre4jMatcher = Pattern.compile(
                api,
                pattern,
                Pattern.CANON_EQ
        ).matcher(input);

        assertTrue(pcre4jMatcher.find());
        var result = pcre4jMatcher.toMatchResult();

        assertEquals(pcre4jMatcher.start(), result.start());
        assertEquals(pcre4jMatcher.end(), result.end());
        assertEquals(pcre4jMatcher.group(), result.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqAppendReplacement(IPcre2 api) {
        // Test appendReplacement with CANON_EQ
        var pattern = "\u00E9";  // é
        var input = "cafe\u0301 caf\u00E9";  // two cafés

        var pcre4jMatcher = Pattern.compile(
                api,
                pattern,
                Pattern.CANON_EQ
        ).matcher(input);

        var sb = new StringBuilder();
        while (pcre4jMatcher.find()) {
            pcre4jMatcher.appendReplacement(sb, "E");
        }
        pcre4jMatcher.appendTail(sb);

        assertEquals("cafE cafE", sb.toString());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqWithSurrogatePairs(IPcre2 api) {
        // Test CANON_EQ with surrogate pairs (characters outside BMP)
        // 𝄞 (U+1D11E, Musical Symbol G Clef) - doesn't decompose but tests surrogate handling
        var pattern = "test\uD834\uDD1Eend";  // test + G clef + end
        var input = "test\uD834\uDD1Eend";

        var pcre4jMatcher = Pattern.compile(
                api,
                pattern,
                Pattern.CANON_EQ
        ).matcher(input);

        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void canonEqMixedSurrogatesAndCombining(IPcre2 api) {
        // Test with both surrogate pairs and combining characters
        var pattern = "\uD834\uDD1E\u00E9";  // G clef + é (precomposed)
        var input = "\uD834\uDD1Ee\u0301";   // G clef + é (decomposed)

        var javaMatcher = java.util.regex.Pattern.compile(
                pattern,
                java.util.regex.Pattern.CANON_EQ
        ).matcher(input);
        var pcre4jMatcher = Pattern.compile(
                api,
                pattern,
                Pattern.CANON_EQ
        ).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void splitAsStream(IPcre2 api) {
        // Test splitAsStream against Java Pattern for multiple inputs and edge cases
        Object[][] cases = new Object[][]{
                {",", "a,b,c"},
                {",", "a,b,c,"},
                {",", ""},
                {",", "abc"},
                {"\\d", "a1b2c"}
        };

        for (Object[] c : cases) {
            String regex = (String) c[0];
            String input = (String) c[1];

            var javaPattern = java.util.regex.Pattern.compile(regex);
            var pcre4jPattern = Pattern.compile(api, regex);

            assertArrayEquals(
                    javaPattern.splitAsStream(input).toArray(),
                    pcre4jPattern.splitAsStream(input).toArray(),
                    "Mismatch for regex=" + regex + ", input=" + input
            );
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchesStaticFullMatch(IPcre2 api) {
        var regex = "\\d+";

        assertEquals(
                java.util.regex.Pattern.matches(regex, "12345"),
                Pattern.matches(api, regex, "12345")
        );
        assertTrue(Pattern.matches(api, regex, "12345"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchesStaticNoMatch(IPcre2 api) {
        var regex = "\\d+";

        assertEquals(
                java.util.regex.Pattern.matches(regex, "abc"),
                Pattern.matches(api, regex, "abc")
        );
        assertFalse(Pattern.matches(api, regex, "abc"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchesStaticPartialInputDoesNotMatch(IPcre2 api) {
        var regex = "\\d+";

        assertEquals(
                java.util.regex.Pattern.matches(regex, "123abc"),
                Pattern.matches(api, regex, "123abc")
        );
        assertFalse(Pattern.matches(api, regex, "123abc"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchesStaticEmptyInput(IPcre2 api) {
        var regex = ".*";

        assertEquals(
                java.util.regex.Pattern.matches(regex, ""),
                Pattern.matches(api, regex, "")
        );
        assertTrue(Pattern.matches(api, regex, ""));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void asPredicateFindsPartialMatch(IPcre2 api) {
        var regex = "\\d+";
        var javaPredicate = java.util.regex.Pattern.compile(regex).asPredicate();
        var pcre4jPredicate = Pattern.compile(api, regex).asPredicate();

        assertEquals(javaPredicate.test("abc123def"), pcre4jPredicate.test("abc123def"));
        assertTrue(pcre4jPredicate.test("abc123def"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void asPredicateFullMatch(IPcre2 api) {
        var regex = "\\d+";
        var javaPredicate = java.util.regex.Pattern.compile(regex).asPredicate();
        var pcre4jPredicate = Pattern.compile(api, regex).asPredicate();

        assertEquals(javaPredicate.test("12345"), pcre4jPredicate.test("12345"));
        assertTrue(pcre4jPredicate.test("12345"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void asPredicateNoMatch(IPcre2 api) {
        var regex = "\\d+";
        var javaPredicate = java.util.regex.Pattern.compile(regex).asPredicate();
        var pcre4jPredicate = Pattern.compile(api, regex).asPredicate();

        assertEquals(javaPredicate.test("abc"), pcre4jPredicate.test("abc"));
        assertFalse(pcre4jPredicate.test("abc"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void asPredicateEmptyInput(IPcre2 api) {
        var regex = "\\d+";
        var javaPredicate = java.util.regex.Pattern.compile(regex).asPredicate();
        var pcre4jPredicate = Pattern.compile(api, regex).asPredicate();

        assertEquals(javaPredicate.test(""), pcre4jPredicate.test(""));
        assertFalse(pcre4jPredicate.test(""));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void asMatchPredicateFullMatch(IPcre2 api) {
        var regex = "\\d+";
        var javaPredicate = java.util.regex.Pattern.compile(regex).asMatchPredicate();
        var pcre4jPredicate = Pattern.compile(api, regex).asMatchPredicate();

        assertEquals(javaPredicate.test("12345"), pcre4jPredicate.test("12345"));
        assertTrue(pcre4jPredicate.test("12345"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void asMatchPredicatePartialInputDoesNotMatch(IPcre2 api) {
        var regex = "\\d+";
        var javaPredicate = java.util.regex.Pattern.compile(regex).asMatchPredicate();
        var pcre4jPredicate = Pattern.compile(api, regex).asMatchPredicate();

        assertEquals(javaPredicate.test("abc123def"), pcre4jPredicate.test("abc123def"));
        assertFalse(pcre4jPredicate.test("abc123def"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void asMatchPredicateNoMatch(IPcre2 api) {
        var regex = "\\d+";
        var javaPredicate = java.util.regex.Pattern.compile(regex).asMatchPredicate();
        var pcre4jPredicate = Pattern.compile(api, regex).asMatchPredicate();

        assertEquals(javaPredicate.test("abc"), pcre4jPredicate.test("abc"));
        assertFalse(pcre4jPredicate.test("abc"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void asMatchPredicateEmptyInput(IPcre2 api) {
        var regex = "\\d+";
        var javaPredicate = java.util.regex.Pattern.compile(regex).asMatchPredicate();
        var pcre4jPredicate = Pattern.compile(api, regex).asMatchPredicate();

        assertEquals(javaPredicate.test(""), pcre4jPredicate.test(""));
        assertFalse(pcre4jPredicate.test(""));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void asPredicateVsAsMatchPredicate(IPcre2 api) {
        var regex = "\\d+";
        var asPredicate = Pattern.compile(api, regex).asPredicate();
        var asMatchPredicate = Pattern.compile(api, regex).asMatchPredicate();

        // asPredicate uses find() - matches partial input
        assertTrue(asPredicate.test("abc123def"));
        // asMatchPredicate uses matches() - requires full match
        assertFalse(asMatchPredicate.test("abc123def"));

        // Both match when input fully matches the pattern
        assertTrue(asPredicate.test("12345"));
        assertTrue(asMatchPredicate.test("12345"));

        // Neither matches when pattern not found at all
        assertFalse(asPredicate.test("abc"));
        assertFalse(asMatchPredicate.test("abc"));
    }

}
