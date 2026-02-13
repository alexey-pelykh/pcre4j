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
package org.pcre4j.regex;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.pcre4j.regex.MatcherTestUtils.assertAppendReplacement;

/**
 * Tests for replacement operations ({@code replaceAll()}, {@code replaceFirst()}, {@code appendReplacement()})
 * in {@link Matcher}.
 */
public class MatcherReplacementTests {

    // ========================================================================
    // Replacement method tests
    // ========================================================================

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void quoteReplacement(IPcre2 api) {
        // Test basic string without special characters
        assertEquals(
                java.util.regex.Matcher.quoteReplacement("hello"),
                Matcher.quoteReplacement("hello")
        );

        // Test string with backslash
        assertEquals(
                java.util.regex.Matcher.quoteReplacement("hello\\world"),
                Matcher.quoteReplacement("hello\\world")
        );

        // Test string with dollar sign
        assertEquals(
                java.util.regex.Matcher.quoteReplacement("price: $100"),
                Matcher.quoteReplacement("price: $100")
        );

        // Test string with both special characters
        assertEquals(
                java.util.regex.Matcher.quoteReplacement("$100 \\ $200"),
                Matcher.quoteReplacement("$100 \\ $200")
        );

        // Test empty string
        assertEquals(
                java.util.regex.Matcher.quoteReplacement(""),
                Matcher.quoteReplacement("")
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceAllBasic(IPcre2 api) {
        var regex = "world";
        var input = "hello world";
        var replacement = "universe";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceAllMultiple(IPcre2 api) {
        var regex = "o";
        var input = "hello world";
        var replacement = "0";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceAllWithGroupReference(IPcre2 api) {
        var regex = "(\\w+) (\\w+)";
        var input = "hello world";
        var replacement = "$2 $1";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceAllWithNamedGroupReference(IPcre2 api) {
        var regex = "(?<first>\\w+) (?<second>\\w+)";
        var input = "hello world";
        var replacement = "${second} ${first}";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceAllNoMatch(IPcre2 api) {
        var regex = "xyz";
        var input = "hello world";
        var replacement = "abc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceAllEmptyReplacement(IPcre2 api) {
        var regex = "world";
        var input = "hello world";
        var replacement = "";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceAllUnicode(IPcre2 api) {
        var regex = "\uD83C\uDF10";
        var input = "hello \uD83C\uDF10 world \uD83C\uDF10";
        var replacement = "\uD83C\uDF0D";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceFirstBasic(IPcre2 api) {
        var regex = "o";
        var input = "hello world";
        var replacement = "0";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceFirst(replacement), pcre4jMatcher.replaceFirst(replacement));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceFirstWithGroupReference(IPcre2 api) {
        var regex = "(\\w+) (\\w+)";
        var input = "hello world, foo bar";
        var replacement = "$2-$1";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceFirst(replacement), pcre4jMatcher.replaceFirst(replacement));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceFirstNoMatch(IPcre2 api) {
        var regex = "xyz";
        var input = "hello world";
        var replacement = "abc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceFirst(replacement), pcre4jMatcher.replaceFirst(replacement));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceAllWithFunction(IPcre2 api) {
        var regex = "\\d+";
        var input = "a1b22c333";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Replace each number with its length
        assertEquals(
                javaMatcher.replaceAll(mr -> "[" + mr.group().length() + "]"),
                pcre4jMatcher.replaceAll(mr -> "[" + mr.group().length() + "]")
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceFirstWithFunction(IPcre2 api) {
        var regex = "\\d+";
        var input = "a1b22c333";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Replace first number with its length
        assertEquals(
                javaMatcher.replaceFirst(mr -> "[" + mr.group().length() + "]"),
                pcre4jMatcher.replaceFirst(mr -> "[" + mr.group().length() + "]")
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceFirstWithFunctionNoMatch(IPcre2 api) {
        var regex = "xyz";
        var input = "hello world";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(
                javaMatcher.replaceFirst(mr -> "replaced"),
                pcre4jMatcher.replaceFirst(mr -> "replaced")
        );
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementStringBuffer(IPcre2 api) {
        var regex = "(\\w+)";
        var input = "one two three";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuffer();
        var pcre4jSb = new StringBuffer();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, "[$1]");
            pcre4jMatcher.appendReplacement(pcre4jSb, "[$1]");
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementStringBuilder(IPcre2 api) {
        var regex = "(\\w+)";
        var input = "one two three";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertAppendReplacement(javaMatcher, pcre4jMatcher, "[$1]");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementWithNamedGroup(IPcre2 api) {
        var regex = "(?<word>\\w+)";
        var input = "one two three";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertAppendReplacement(javaMatcher, pcre4jMatcher, "${word}!");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementEscapedCharacters(IPcre2 api) {
        var regex = "\\d+";
        var input = "test123value";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Test escaping $ and \ in replacement
        assertAppendReplacement(javaMatcher, pcre4jMatcher, "\\$\\\\");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementLiteralText(IPcre2 api) {
        var regex = "world";
        var input = "hello world!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertAppendReplacement(javaMatcher, pcre4jMatcher, "universe");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendTailOnly(IPcre2 api) {
        var regex = "xyz";
        var input = "hello world";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        // No matches, just call appendTail
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementNoMatch(IPcre2 api) {
        var regex = "\\d+";
        var input = "hello world";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var sb = new StringBuilder();

        // Calling appendReplacement without a match should throw
        assertThrows(IllegalStateException.class, () -> pcre4jMatcher.appendReplacement(sb, "test"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementMultipleGroups(IPcre2 api) {
        var regex = "(\\w)(\\w)(\\w)";
        var input = "abc def ghi";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertAppendReplacement(javaMatcher, pcre4jMatcher, "$3$2$1");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementGroupZero(IPcre2 api) {
        var regex = "\\w+";
        var input = "hello world";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertAppendReplacement(javaMatcher, pcre4jMatcher, "[$0]");
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementUnicode(IPcre2 api) {
        var regex = "\uD83C\uDF10";
        var input = "hello \uD83C\uDF10 world \uD83C\uDF10!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertAppendReplacement(javaMatcher, pcre4jMatcher, "\uD83C\uDF0D");
    }

    // ========================================================================
    // Complex replacement pattern tests
    // ========================================================================

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceAllWithFullMatchReference(IPcre2 api) {
        var regex = "\\w+";
        var input = "hello world";
        var replacement = "[$0]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceAllWithNamedGroupReferenceYearMonth(IPcre2 api) {
        var regex = "(?<year>\\d{4})-(?<month>\\d{2})";
        var input = "date: 2024-01, also 2025-12";
        var replacement = "${month}/${year}";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementWithEscapedDollarSign(IPcre2 api) {
        var regex = "\\d+";
        var input = "price: 100";
        var replacement = "\\$5";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, replacement);
            pcre4jMatcher.appendReplacement(pcre4jSb, replacement);
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementDollarAtEndThrows(IPcre2 api) {
        var regex = "x";
        var input = "x";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        assertTrue(pcre4jMatcher.find());

        var sb = new StringBuilder();
        assertThrows(IllegalArgumentException.class, () -> pcre4jMatcher.appendReplacement(sb, "cost$"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementDollarFollowedByInvalidCharThrows(IPcre2 api) {
        var regex = "x";
        var input = "x";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        assertTrue(pcre4jMatcher.find());

        var sb = new StringBuilder();
        assertThrows(IllegalArgumentException.class, () -> pcre4jMatcher.appendReplacement(sb, "$x"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementBackslashAtEndThrows(IPcre2 api) {
        var regex = "x";
        var input = "x";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        assertTrue(pcre4jMatcher.find());

        var sb = new StringBuilder();
        assertThrows(IllegalArgumentException.class, () -> pcre4jMatcher.appendReplacement(sb, "test\\"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementBackslashEscapesNextChar(IPcre2 api) {
        var regex = "x";
        var input = "x";
        var replacement = "\\a\\b\\c";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, replacement);
            pcre4jMatcher.appendReplacement(pcre4jSb, replacement);
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementWithUnicodeReplacement(IPcre2 api) {
        var regex = "(\\w+)";
        var input = "hello world";
        var replacement = "\u00e9$1\u00e9";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, replacement);
            pcre4jMatcher.appendReplacement(pcre4jSb, replacement);
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementWithSurrogatePairReplacement(IPcre2 api) {
        var regex = "\\w+";
        var input = "hello world";
        var replacement = "\uD83D\uDE00";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, replacement);
            pcre4jMatcher.appendReplacement(pcre4jSb, replacement);
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementUnclosedGroupReferenceThrows(IPcre2 api) {
        var regex = "x";
        var input = "x";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        assertTrue(pcre4jMatcher.find());

        var sb = new StringBuilder();
        assertThrows(IllegalArgumentException.class, () -> pcre4jMatcher.appendReplacement(sb, "${name"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementEmptyGroupReferenceThrows(IPcre2 api) {
        var regex = "x";
        var input = "x";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        assertTrue(pcre4jMatcher.find());

        var sb = new StringBuilder();
        assertThrows(IllegalArgumentException.class, () -> pcre4jMatcher.appendReplacement(sb, "${}"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementWithNumberedGroupInBraces(IPcre2 api) {
        var regex = "(\\w+) (\\w+)";
        var input = "hello world";
        var replacement = "${2} ${1}";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var pcre4jSb = new StringBuilder();

        while (pcre4jMatcher.find()) {
            pcre4jMatcher.appendReplacement(pcre4jSb, replacement);
        }
        pcre4jMatcher.appendTail(pcre4jSb);

        // PCRE4J supports numbered groups in braces as an extension
        assertEquals("world hello", pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementInvalidGroupNumberThrows(IPcre2 api) {
        var regex = "(x)";
        var input = "x";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        assertTrue(pcre4jMatcher.find());

        var sb = new StringBuilder();
        assertThrows(IndexOutOfBoundsException.class, () -> pcre4jMatcher.appendReplacement(sb, "$5"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementInvalidGroupNumberInBracesThrows(IPcre2 api) {
        var regex = "(x)";
        var input = "x";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        assertTrue(pcre4jMatcher.find());

        var sb = new StringBuilder();
        assertThrows(IndexOutOfBoundsException.class, () -> pcre4jMatcher.appendReplacement(sb, "${5}"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementInvalidNamedGroupThrows(IPcre2 api) {
        var regex = "(x)";
        var input = "x";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        assertTrue(pcre4jMatcher.find());

        var sb = new StringBuilder();
        assertThrows(IllegalArgumentException.class, () -> pcre4jMatcher.appendReplacement(sb, "${nonexistent}"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementWithMixedGroupReferences(IPcre2 api) {
        var regex = "(?<first>\\w+) (\\w+) (?<third>\\w+)";
        var input = "one two three";
        var replacement = "${first}-$2-${third}";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, replacement);
            pcre4jMatcher.appendReplacement(pcre4jSb, replacement);
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void replaceFirstWithFullMatchReference(IPcre2 api) {
        var regex = "\\w+";
        var input = "hello world";
        var replacement = "($0)";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceFirst(replacement), pcre4jMatcher.replaceFirst(replacement));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementWithNullGroup(IPcre2 api) {
        var regex = "(a)|(b)";
        var input = "ab";
        var replacement = "[$1$2]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, replacement);
            pcre4jMatcher.appendReplacement(pcre4jSb, replacement);
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void appendReplacementMultiDigitGroupNumber(IPcre2 api) {
        var regex = "(a)(b)(c)(d)(e)(f)(g)(h)(i)(j)(k)(l)";
        var input = "abcdefghijkl";
        var replacement = "$12$1";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, replacement);
            pcre4jMatcher.appendReplacement(pcre4jSb, replacement);
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

}
