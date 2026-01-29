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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure API likeness of the {@link Pattern} to the {@link java.util.regex.Pattern}.
 */
public class PatternTests {

    private static final IPcre2 JNA_PCRE2 = new org.pcre4j.jna.Pcre2();
    private static final IPcre2 FFM_PCRE2 = new org.pcre4j.ffm.Pcre2();

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(JNA_PCRE2),
                Arguments.of(FFM_PCRE2)
        );
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void namedGroups(IPcre2 api) {
        var regex = "(?<number>42)";
        var javaPattern = java.util.regex.Pattern.compile(regex);
        var pcre4jPattern = Pattern.compile(api, regex);

        assertEquals(javaPattern.namedGroups(), pcre4jPattern.namedGroups());
    }

    @ParameterizedTest
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
    void withoutUnicodeCharacterClass(IPcre2 api) {
        var regex = "\\w";
        var input = "Ǎ";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
    void unicodeCaseFlagValue(IPcre2 api) {
        // Verify UNICODE_CASE flag has the correct value (0x40)
        assertEquals(java.util.regex.Pattern.UNICODE_CASE, Pattern.UNICODE_CASE);
        assertEquals(0x40, Pattern.UNICODE_CASE);
    }

    @ParameterizedTest
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
    void unicodeCaseFlagsMethod(IPcre2 api) {
        // Verify flags() method returns UNICODE_CASE when set
        var regex = "test";
        int flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

        var pattern = Pattern.compile(api, regex, flags);
        assertEquals(flags, pattern.flags());
        assertTrue((pattern.flags() & Pattern.UNICODE_CASE) != 0);
    }

    @ParameterizedTest
    @MethodSource("parameters")
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

}
