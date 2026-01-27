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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests to ensure API likeness of the {@link Matcher} to the {@link java.util.regex.Matcher}.
 */
public class MatcherTests {

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
    void unicodeOneByte(IPcre2 api) {
        var regex = "Å";
        var input = "Å";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void unicodeTwoBytes(IPcre2 api) {
        var regex = "Ǎ";
        var input = "Ǎ";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void unicodeThreeBytes(IPcre2 api) {
        var regex = "•";
        var input = "•";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void unicodeFourBytes(IPcre2 api) {
        var regex = "\uD83C\uDF0D";
        var input = "\uD83C\uDF0D";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void unicode(IPcre2 api) {
        var regex = "ÅǍ•\uD83C\uDF0D!";
        var input = "ÅǍ•\uD83C\uDF0D!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void unicodeRegion(IPcre2 api) {
        var regex = "\uD83C\uDF0D";
        var input = "ÅǍ•\uD83C\uDF0D!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 5);
        pcre4jMatcher.region(3, 5);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchesTrue(IPcre2 api) {
        var regex = "42";
        var input = "42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchesFalse(IPcre2 api) {
        var regex = "42";
        var input = "42!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
        assertThrows(IllegalStateException.class, javaMatcher::group);
        assertThrows(IllegalStateException.class, pcre4jMatcher::group);
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertThrows(IllegalStateException.class, javaMatchResult::start);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::start);
        assertThrows(IllegalStateException.class, javaMatchResult::end);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::end);
        assertThrows(IllegalStateException.class, javaMatchResult::group);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::group);
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchesTrueInRegion(IPcre2 api) {
        var regex = "42";
        var input = "[42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(1, 3);
        pcre4jMatcher.region(1, 3);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void matchesFalseRegion(IPcre2 api) {
        var regex = "42";
        var input = "[42!]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(1, 4);
        pcre4jMatcher.region(1, 4);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
        assertThrows(IllegalStateException.class, javaMatcher::group);
        assertThrows(IllegalStateException.class, pcre4jMatcher::group);
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertThrows(IllegalStateException.class, javaMatchResult::start);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::start);
        assertThrows(IllegalStateException.class, javaMatchResult::end);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::end);
        assertThrows(IllegalStateException.class, javaMatchResult::group);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::group);
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void lookingAtTrue(IPcre2 api) {
        var regex = "42";
        var input = "42!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void lookingAtFalse(IPcre2 api) {
        var regex = "42";
        var input = "!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
        assertThrows(IllegalStateException.class, javaMatcher::group);
        assertThrows(IllegalStateException.class, pcre4jMatcher::group);
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertThrows(IllegalStateException.class, javaMatchResult::start);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::start);
        assertThrows(IllegalStateException.class, javaMatchResult::end);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::end);
        assertThrows(IllegalStateException.class, javaMatchResult::group);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::group);
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void lookingAtTrueInRegion(IPcre2 api) {
        var regex = "42";
        var input = "[42!]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(1, 4);
        pcre4jMatcher.region(1, 4);

        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void lookingAtFalseRegion(IPcre2 api) {
        var regex = "42";
        var input = "[!42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(1, 4);
        pcre4jMatcher.region(1, 4);

        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
        assertThrows(IllegalStateException.class, javaMatcher::group);
        assertThrows(IllegalStateException.class, pcre4jMatcher::group);
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertThrows(IllegalStateException.class, javaMatchResult::start);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::start);
        assertThrows(IllegalStateException.class, javaMatchResult::end);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::end);
        assertThrows(IllegalStateException.class, javaMatchResult::group);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::group);
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void findTrue(IPcre2 api) {
        var regex = "42";
        var input = "42!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void findFalse(IPcre2 api) {
        var regex = "42!";
        var input = "42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
        assertThrows(IllegalStateException.class, javaMatcher::group);
        assertThrows(IllegalStateException.class, pcre4jMatcher::group);
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertThrows(IllegalStateException.class, javaMatchResult::start);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::start);
        assertThrows(IllegalStateException.class, javaMatchResult::end);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::end);
        assertThrows(IllegalStateException.class, javaMatchResult::group);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::group);
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void findTrueInRegion(IPcre2 api) {
        var regex = "42";
        var input = "[42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(1, 3);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void findFalseInRegion(IPcre2 api) {
        var regex = "42!";
        var input = "[42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(1, 3);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
        assertThrows(IllegalStateException.class, javaMatcher::group);
        assertThrows(IllegalStateException.class, pcre4jMatcher::group);
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertThrows(IllegalStateException.class, javaMatchResult::start);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::start);
        assertThrows(IllegalStateException.class, javaMatchResult::end);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::end);
        assertThrows(IllegalStateException.class, javaMatchResult::group);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::group);
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void findTrueAtOffset(IPcre2 api) {
        var regex = "42";
        var input = "!!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(2), pcre4jMatcher.find(2));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void findFalseAtOffset(IPcre2 api) {
        var regex = "42";
        var input = "!!test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(2), pcre4jMatcher.find(2));
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
        assertThrows(IllegalStateException.class, javaMatcher::group);
        assertThrows(IllegalStateException.class, pcre4jMatcher::group);
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertThrows(IllegalStateException.class, javaMatchResult::start);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::start);
        assertThrows(IllegalStateException.class, javaMatchResult::end);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::end);
        assertThrows(IllegalStateException.class, javaMatchResult::group);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::group);
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void findMultiple(IPcre2 api) {
        var regex = "42";
        var input = "42!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult0 = javaMatcher.toMatchResult();
        var pcre4jMatchResult0 = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult0.start(), pcre4jMatchResult0.start());
        assertEquals(javaMatchResult0.end(), pcre4jMatchResult0.end());
        assertEquals(javaMatchResult0.group(), pcre4jMatchResult0.group());
        assertEquals(javaMatchResult0.groupCount(), pcre4jMatchResult0.groupCount());

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult1 = javaMatcher.toMatchResult();
        var pcre4jMatchResult1 = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult1.start(), pcre4jMatchResult1.start());
        assertEquals(javaMatchResult1.end(), pcre4jMatchResult1.end());
        assertEquals(javaMatchResult1.group(), pcre4jMatchResult1.group());
        assertEquals(javaMatchResult1.groupCount(), pcre4jMatchResult1.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void findMultipleWithinRegion(IPcre2 api) {
        var regex = "42";
        var input = "42!42!42!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(2, 8);
        pcre4jMatcher.region(2, 8);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult0 = javaMatcher.toMatchResult();
        var pcre4jMatchResult0 = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult0.start(), pcre4jMatchResult0.start());
        assertEquals(javaMatchResult0.end(), pcre4jMatchResult0.end());
        assertEquals(javaMatchResult0.group(), pcre4jMatchResult0.group());
        assertEquals(javaMatchResult0.groupCount(), pcre4jMatchResult0.groupCount());

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult1 = javaMatcher.toMatchResult();
        var pcre4jMatchResult1 = pcre4jMatcher.toMatchResult();
        assertEquals(javaMatchResult1.start(), pcre4jMatchResult1.start());
        assertEquals(javaMatchResult1.end(), pcre4jMatchResult1.end());
        assertEquals(javaMatchResult1.group(), pcre4jMatchResult1.group());
        assertEquals(javaMatchResult1.groupCount(), pcre4jMatchResult1.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void findMultipleOutsideRegion(IPcre2 api) {
        var regex = "42";
        var input = "42!__!__!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(2, 8);
        pcre4jMatcher.region(2, 8);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
        assertThrows(IllegalStateException.class, javaMatcher::group);
        assertThrows(IllegalStateException.class, pcre4jMatcher::group);
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());

        var javaMatchResult = javaMatcher.toMatchResult();
        var pcre4jMatchResult = pcre4jMatcher.toMatchResult();
        assertThrows(IllegalStateException.class, javaMatchResult::start);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::start);
        assertThrows(IllegalStateException.class, javaMatchResult::end);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::end);
        assertThrows(IllegalStateException.class, javaMatchResult::group);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::group);
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void captureGroups(IPcre2 api) {
        var regex = "(?<four>4)(.*)(?<two>2)";
        var input = "4test2";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
        for (var group = 1; group <= javaMatcher.groupCount(); group++) {
            assertEquals(javaMatcher.group(group), pcre4jMatcher.group(group));
            assertEquals(javaMatcher.start(group), pcre4jMatcher.start(group));
            assertEquals(javaMatcher.end(group), pcre4jMatcher.end(group));
        }
        assertEquals(javaMatcher.start("four"), pcre4jMatcher.start("four"));
        assertEquals(javaMatcher.start("two"), pcre4jMatcher.start("two"));
        assertEquals(javaMatcher.end("four"), pcre4jMatcher.end("four"));
        assertEquals(javaMatcher.end("two"), pcre4jMatcher.end("two"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void emptyGroup(IPcre2 api) {
        var regex = "!*";
        var input = "42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
        for (var group = 1; group <= javaMatcher.groupCount(); group++) {
            assertEquals(javaMatcher.group(group), pcre4jMatcher.group(group));
            assertEquals(javaMatcher.start(group), pcre4jMatcher.start(group));
            assertEquals(javaMatcher.end(group), pcre4jMatcher.end(group));
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void singleUnmatchedGroup(IPcre2 api) {
        var regex = "(42)?";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
        for (var group = 1; group <= javaMatcher.groupCount(); group++) {
            assertEquals(javaMatcher.group(group), pcre4jMatcher.group(group));
            assertEquals(javaMatcher.start(group), pcre4jMatcher.start(group));
            assertEquals(javaMatcher.end(group), pcre4jMatcher.end(group));
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void unmatchedGroups(IPcre2 api) {
        var regex = "42((?<exclamation>!)|(?<question>\\?))";
        var input = "42!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
        for (var group = 1; group <= javaMatcher.groupCount(); group++) {
            assertEquals(javaMatcher.group(group), pcre4jMatcher.group(group));
            assertEquals(javaMatcher.start(group), pcre4jMatcher.start(group));
            assertEquals(javaMatcher.end(group), pcre4jMatcher.end(group));
        }
        assertEquals(javaMatcher.start("exclamation"), pcre4jMatcher.start("exclamation"));
        assertEquals(javaMatcher.start("question"), pcre4jMatcher.start("question"));
        assertEquals(javaMatcher.end("exclamation"), pcre4jMatcher.end("exclamation"));
        assertEquals(javaMatcher.end("question"), pcre4jMatcher.end("question"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void positiveLookaround(IPcre2 api) {
        var regex = "(?<=(?<lWrapper>\\W))?(\\d+)(?=(?<rWrapper>\\W))?";
        var input = "(42)";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
        for (var group = 1; group <= javaMatcher.groupCount(); group++) {
            assertEquals(javaMatcher.group(group), pcre4jMatcher.group(group));
            assertEquals(javaMatcher.start(group), pcre4jMatcher.start(group));
            assertEquals(javaMatcher.end(group), pcre4jMatcher.end(group));
        }
        assertEquals(javaMatcher.start("lWrapper"), pcre4jMatcher.start("lWrapper"));
        assertEquals(javaMatcher.start("rWrapper"), pcre4jMatcher.start("rWrapper"));
        assertEquals(javaMatcher.end("lWrapper"), pcre4jMatcher.end("lWrapper"));
        assertEquals(javaMatcher.end("rWrapper"), pcre4jMatcher.end("rWrapper"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void positiveUnmatchedLookaround(IPcre2 api) {
        var regex = "(?<=(?<lWrapper>\\W))?(\\d+)(?=(?<rWrapper>\\W))?";
        var input = "42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
        for (var group = 1; group <= javaMatcher.groupCount(); group++) {
            assertEquals(javaMatcher.group(group), pcre4jMatcher.group(group));
            assertEquals(javaMatcher.start(group), pcre4jMatcher.start(group));
            assertEquals(javaMatcher.end(group), pcre4jMatcher.end(group));
        }
        assertEquals(javaMatcher.start("lWrapper"), pcre4jMatcher.start("lWrapper"));
        assertEquals(javaMatcher.start("rWrapper"), pcre4jMatcher.start("rWrapper"));
        assertEquals(javaMatcher.end("lWrapper"), pcre4jMatcher.end("lWrapper"));
        assertEquals(javaMatcher.end("rWrapper"), pcre4jMatcher.end("rWrapper"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void emptyStringMatches(IPcre2 api) {
        var regex = "^$";
        var input = "";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void emptyStringFind(IPcre2 api) {
        var regex = "^$";
        var input = "";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void findAtEndOfString(IPcre2 api) {
        var regex = "$";
        var input = "abc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // find(start) at end of string should work for $ pattern
        assertEquals(javaMatcher.find(input.length()), pcre4jMatcher.find(input.length()));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void findExhaustedInRegion(IPcre2 api) {
        // Test find() behavior when iterating through all matches in a region
        var regex = "a";
        var input = "aaa";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to first two characters only
        javaMatcher.region(0, 2);
        pcre4jMatcher.region(0, 2);

        // First find() should succeed (matches 'a' at position 0)
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        // Second find() should succeed (matches 'a' at position 1)
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        // Third find() should fail - no more matches in region
        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void findWithZeroWidthMatchExhaustsRegion(IPcre2 api) {
        // Test that after a zero-width match at regionEnd, the next find() returns false
        // This exercises the start > regionEnd branch in find():
        // After zero-width match at [1,1], start becomes 1, but since start == lastMatchIndices[0],
        // it's incremented to 2. If regionEnd is 1, then start (2) > regionEnd (1) is true.
        var regex = "$";  // Zero-width match at end
        var input = "ab";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to just "a" (positions 0-1)
        javaMatcher.region(0, 1);
        pcre4jMatcher.region(0, 1);

        // First find() matches $ at position 1 (end of region) - zero-width match at [1,1]
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(1, javaMatcher.start());
        assertEquals(1, pcre4jMatcher.start());
        assertEquals(1, javaMatcher.end());
        assertEquals(1, pcre4jMatcher.end());

        // Second find(): lastMatchIndices = [1,1], so start = 1
        // Since start (1) == lastMatchIndices[0] (1), start is incremented to 2
        // Now start (2) > regionEnd (1) is TRUE, triggering the early return
        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());
    }

    // ========================================================================
    // Replacement method tests
    // ========================================================================

    @ParameterizedTest
    @MethodSource("parameters")
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
    @MethodSource("parameters")
    void replaceAllBasic(IPcre2 api) {
        var regex = "world";
        var input = "hello world";
        var replacement = "universe";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void replaceAllMultiple(IPcre2 api) {
        var regex = "o";
        var input = "hello world";
        var replacement = "0";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void replaceAllWithGroupReference(IPcre2 api) {
        var regex = "(\\w+) (\\w+)";
        var input = "hello world";
        var replacement = "$2 $1";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void replaceAllWithNamedGroupReference(IPcre2 api) {
        var regex = "(?<first>\\w+) (?<second>\\w+)";
        var input = "hello world";
        var replacement = "${second} ${first}";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void replaceAllNoMatch(IPcre2 api) {
        var regex = "xyz";
        var input = "hello world";
        var replacement = "abc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void replaceAllEmptyReplacement(IPcre2 api) {
        var regex = "world";
        var input = "hello world";
        var replacement = "";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void replaceAllUnicode(IPcre2 api) {
        var regex = "🌐";
        var input = "hello 🌐 world 🌐";
        var replacement = "🌍";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceAll(replacement), pcre4jMatcher.replaceAll(replacement));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void replaceFirstBasic(IPcre2 api) {
        var regex = "o";
        var input = "hello world";
        var replacement = "0";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceFirst(replacement), pcre4jMatcher.replaceFirst(replacement));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void replaceFirstWithGroupReference(IPcre2 api) {
        var regex = "(\\w+) (\\w+)";
        var input = "hello world, foo bar";
        var replacement = "$2-$1";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceFirst(replacement), pcre4jMatcher.replaceFirst(replacement));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void replaceFirstNoMatch(IPcre2 api) {
        var regex = "xyz";
        var input = "hello world";
        var replacement = "abc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.replaceFirst(replacement), pcre4jMatcher.replaceFirst(replacement));
    }

    @ParameterizedTest
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
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
    @MethodSource("parameters")
    void appendReplacementStringBuilder(IPcre2 api) {
        var regex = "(\\w+)";
        var input = "one two three";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, "[$1]");
            pcre4jMatcher.appendReplacement(pcre4jSb, "[$1]");
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void appendReplacementWithNamedGroup(IPcre2 api) {
        var regex = "(?<word>\\w+)";
        var input = "one two three";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, "${word}!");
            pcre4jMatcher.appendReplacement(pcre4jSb, "${word}!");
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void appendReplacementEscapedCharacters(IPcre2 api) {
        var regex = "\\d+";
        var input = "test123value";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            // Test escaping $ and \ in replacement
            javaMatcher.appendReplacement(javaSb, "\\$\\\\");
            pcre4jMatcher.appendReplacement(pcre4jSb, "\\$\\\\");
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void appendReplacementLiteralText(IPcre2 api) {
        var regex = "world";
        var input = "hello world!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, "universe");
            pcre4jMatcher.appendReplacement(pcre4jSb, "universe");
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("parameters")
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
    @MethodSource("parameters")
    void appendReplacementNoMatch(IPcre2 api) {
        var regex = "\\d+";
        var input = "hello world";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var sb = new StringBuilder();

        // Calling appendReplacement without a match should throw
        assertThrows(IllegalStateException.class, () -> pcre4jMatcher.appendReplacement(sb, "test"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void appendReplacementMultipleGroups(IPcre2 api) {
        var regex = "(\\w)(\\w)(\\w)";
        var input = "abc def ghi";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, "$3$2$1");
            pcre4jMatcher.appendReplacement(pcre4jSb, "$3$2$1");
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void appendReplacementGroupZero(IPcre2 api) {
        var regex = "\\w+";
        var input = "hello world";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, "[$0]");
            pcre4jMatcher.appendReplacement(pcre4jSb, "[$0]");
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void appendReplacementUnicode(IPcre2 api) {
        var regex = "🌐";
        var input = "hello 🌐 world 🌐!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaSb = new StringBuilder();
        var pcre4jSb = new StringBuilder();

        while (javaMatcher.find() && pcre4jMatcher.find()) {
            javaMatcher.appendReplacement(javaSb, "🌍");
            pcre4jMatcher.appendReplacement(pcre4jSb, "🌍");
        }
        javaMatcher.appendTail(javaSb);
        pcre4jMatcher.appendTail(pcre4jSb);

        assertEquals(javaSb.toString(), pcre4jSb.toString());
    }

    // ========================================================================
    // results() method tests
    // ========================================================================

    @ParameterizedTest
    @MethodSource("parameters")
    void resultsBasic(IPcre2 api) {
        var regex = "\\d+";
        var input = "a1b22c333d";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        for (int i = 0; i < javaResults.size(); i++) {
            assertEquals(javaResults.get(i).group(), pcre4jResults.get(i).group());
            assertEquals(javaResults.get(i).start(), pcre4jResults.get(i).start());
            assertEquals(javaResults.get(i).end(), pcre4jResults.get(i).end());
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void resultsNoMatches(IPcre2 api) {
        var regex = "xyz";
        var input = "hello world";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        assertTrue(pcre4jResults.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void resultsSingleMatch(IPcre2 api) {
        var regex = "world";
        var input = "hello world!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        assertEquals(1, pcre4jResults.size());
        assertEquals(javaResults.get(0).group(), pcre4jResults.get(0).group());
        assertEquals(javaResults.get(0).start(), pcre4jResults.get(0).start());
        assertEquals(javaResults.get(0).end(), pcre4jResults.get(0).end());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void resultsWithGroups(IPcre2 api) {
        var regex = "(\\w)(\\d)";
        var input = "a1 b2 c3";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        for (int i = 0; i < javaResults.size(); i++) {
            assertEquals(javaResults.get(i).groupCount(), pcre4jResults.get(i).groupCount());
            assertEquals(javaResults.get(i).group(), pcre4jResults.get(i).group());
            assertEquals(javaResults.get(i).group(1), pcre4jResults.get(i).group(1));
            assertEquals(javaResults.get(i).group(2), pcre4jResults.get(i).group(2));
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void resultsImmutableSnapshots(IPcre2 api) {
        var regex = "\\w+";
        var input = "one two three";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Collect all results first
        var results = pcre4jMatcher.results().toList();

        // Results should be independent snapshots
        assertEquals(3, results.size());
        assertEquals("one", results.get(0).group());
        assertEquals("two", results.get(1).group());
        assertEquals("three", results.get(2).group());

        // Each should have correct positions
        assertEquals(0, results.get(0).start());
        assertEquals(3, results.get(0).end());
        assertEquals(4, results.get(1).start());
        assertEquals(7, results.get(1).end());
        assertEquals(8, results.get(2).start());
        assertEquals(13, results.get(2).end());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void resultsDoesNotReset(IPcre2 api) {
        var regex = "\\w+";
        var input = "one two three";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Call find() first to advance the matcher
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals("one", javaMatcher.group());
        assertEquals("one", pcre4jMatcher.group());

        // results() should NOT reset and should continue from current position
        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        assertEquals(2, pcre4jResults.size());  // "two" and "three" only
        assertEquals("two", pcre4jResults.get(0).group());
        assertEquals("three", pcre4jResults.get(1).group());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void resultsZeroWidthMatches(IPcre2 api) {
        var regex = "(?=\\d)";  // Zero-width positive lookahead for digit
        var input = "a1b2c3";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        for (int i = 0; i < javaResults.size(); i++) {
            assertEquals(javaResults.get(i).start(), pcre4jResults.get(i).start());
            assertEquals(javaResults.get(i).end(), pcre4jResults.get(i).end());
            // Zero-width matches have start == end
            assertEquals(pcre4jResults.get(i).start(), pcre4jResults.get(i).end());
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void resultsEmptyString(IPcre2 api) {
        var regex = ".*";
        var input = "";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void resultsUnicode(IPcre2 api) {
        var regex = "\\p{L}+";
        var input = "hello мир 世界";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var javaResults = javaMatcher.results().toList();
        var pcre4jResults = pcre4jMatcher.results().toList();

        assertEquals(javaResults.size(), pcre4jResults.size());
        for (int i = 0; i < javaResults.size(); i++) {
            assertEquals(javaResults.get(i).group(), pcre4jResults.get(i).group());
            assertEquals(javaResults.get(i).start(), pcre4jResults.get(i).start());
            assertEquals(javaResults.get(i).end(), pcre4jResults.get(i).end());
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void resultsStreamOperations(IPcre2 api) {
        var regex = "\\d+";
        var input = "a1b22c333d4444";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Test that stream operations work correctly
        var javaSum = javaMatcher.results()
                .mapToInt(mr -> Integer.parseInt(mr.group()))
                .sum();
        var pcre4jSum = pcre4jMatcher.results()
                .mapToInt(mr -> Integer.parseInt(mr.group()))
                .sum();

        assertEquals(javaSum, pcre4jSum);
        assertEquals(1 + 22 + 333 + 4444, pcre4jSum);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void resultsCount(IPcre2 api) {
        var regex = "a";
        var input = "abracadabra";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.results().count(), pcre4jMatcher.results().count());
        // Need to create new matchers since results() consumes the matcher state
        javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        assertEquals(5, pcre4jMatcher.results().count());
    }

}
