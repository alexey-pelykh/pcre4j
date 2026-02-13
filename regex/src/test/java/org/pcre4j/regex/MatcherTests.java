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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests to ensure API likeness of the {@link Matcher} to the {@link java.util.regex.Matcher}.
 */
public class MatcherTests {

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyStringMatches(IPcre2 api) {
        var regex = "^$";
        var input = "";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void findAtEndOfString(IPcre2 api) {
        var regex = "$";
        var input = "abc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // find(start) at end of string should work for $ pattern
        assertEquals(javaMatcher.find(input.length()), pcre4jMatcher.find(input.length()));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
        var regex = "🌐";
        var input = "hello 🌐 world 🌐";
        var replacement = "🌍";
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
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

    // ========================================================================
    // Anchoring bounds tests
    // ========================================================================

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hasAnchoringBoundsDefault(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Default should be true (anchoring enabled)
        assertEquals(javaMatcher.hasAnchoringBounds(), pcre4jMatcher.hasAnchoringBounds());
        assertTrue(pcre4jMatcher.hasAnchoringBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void useAnchoringBoundsReturnsThis(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Should return this for method chaining
        var result = pcre4jMatcher.useAnchoringBounds(false);
        assertEquals(pcre4jMatcher, result);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void useAnchoringBoundsFalse(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.useAnchoringBounds(false);
        pcre4jMatcher.useAnchoringBounds(false);

        assertEquals(javaMatcher.hasAnchoringBounds(), pcre4jMatcher.hasAnchoringBounds());
        assertFalse(pcre4jMatcher.hasAnchoringBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void useAnchoringBoundsTrue(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set to false first, then back to true
        javaMatcher.useAnchoringBounds(false).useAnchoringBounds(true);
        pcre4jMatcher.useAnchoringBounds(false).useAnchoringBounds(true);

        assertEquals(javaMatcher.hasAnchoringBounds(), pcre4jMatcher.hasAnchoringBounds());
        assertTrue(pcre4jMatcher.hasAnchoringBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsCaretWithRegion(IPcre2 api) {
        // Test that ^ matches at region start with anchoring bounds enabled (default)
        var regex = "^test";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        // With anchoring bounds (default), ^ should match at region start
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);  // Java behavior: ^ matches at region start with anchoring bounds
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsCaretWithRegionDisabled(IPcre2 api) {
        // Test that ^ does NOT match at region start with anchoring bounds disabled
        var regex = "^test";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 7).useAnchoringBounds(false);

        // With non-anchoring bounds, ^ should NOT match at region start (only at true input start)
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertFalse(pcre4jMatcher.hasAnchoringBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsDollarWithRegion(IPcre2 api) {
        // Test that $ matches at region end with anchoring bounds enabled (default)
        var regex = "test$";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        // With anchoring bounds (default), $ should match at region end
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsDollarWithRegionDisabled(IPcre2 api) {
        // Test that $ does NOT match at region end with anchoring bounds disabled
        var regex = "test$";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 7).useAnchoringBounds(false);

        // With non-anchoring bounds, $ should NOT match at region end (only at true input end)
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsLookingAtWithRegion(IPcre2 api) {
        // Test lookingAt() with ^ pattern in a region
        var regex = "^test";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        // With anchoring bounds (default), ^ should match at region start in lookingAt
        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsLookingAtWithRegionDisabled(IPcre2 api) {
        // Test lookingAt() with ^ pattern in a region with anchoring bounds disabled
        var regex = "^test";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 7).useAnchoringBounds(false);

        // With non-anchoring bounds, ^ should NOT match at region start in lookingAt
        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsMatchesWithRegion(IPcre2 api) {
        // Test matches() with ^ and $ pattern in a region
        var regex = "^test$";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        // With anchoring bounds (default), ^test$ should match the region content
        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsMatchesWithRegionDisabled(IPcre2 api) {
        // Test matches() with ^ and $ pattern in a region with anchoring bounds disabled
        var regex = "^test$";
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 7).useAnchoringBounds(false);

        // With non-anchoring bounds, ^test$ should NOT match (anchors won't match at region boundaries)
        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsPreservedAfterReset(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.useAnchoringBounds(false);
        pcre4jMatcher.useAnchoringBounds(false);

        javaMatcher.reset();
        pcre4jMatcher.reset();

        // Anchoring bounds setting should be preserved after reset
        assertEquals(javaMatcher.hasAnchoringBounds(), pcre4jMatcher.hasAnchoringBounds());
        assertFalse(pcre4jMatcher.hasAnchoringBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsPreservedAfterResetWithInput(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.useAnchoringBounds(false);
        pcre4jMatcher.useAnchoringBounds(false);

        javaMatcher.reset("newtest");
        pcre4jMatcher.reset("newtest");

        // Anchoring bounds setting should be preserved after reset with new input
        assertEquals(javaMatcher.hasAnchoringBounds(), pcre4jMatcher.hasAnchoringBounds());
        assertFalse(pcre4jMatcher.hasAnchoringBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsWithFullInput(IPcre2 api) {
        // When region covers full input, anchoring bounds should have no effect
        var regex = "^test$";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Test with anchoring bounds enabled (default)
        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());

        // Test with anchoring bounds disabled - should still match since region = full input
        javaMatcher.reset().useAnchoringBounds(false);
        pcre4jMatcher.reset().useAnchoringBounds(false);
        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsWordBoundaryWithRegion(IPcre2 api) {
        // Test that \b (word boundary) behavior with regions
        var regex = "\\bword\\b";
        var input = "XXXword YYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 8);
        pcre4jMatcher.region(3, 8);

        // With anchoring bounds, \b should match at region boundaries
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void anchoringBoundsWordBoundaryWithRegionDisabled(IPcre2 api) {
        // Test that \b (word boundary) behavior with regions and non-anchoring bounds.
        // Note: In Java, useAnchoringBounds(false) only affects ^ and $.
        // Word boundaries (\b) are controlled by useTransparentBounds() which is NOT yet implemented.
        // With transparent bounds disabled (the default), \b treats region boundaries as word boundaries
        // regardless of the anchoringBounds setting.
        var regex = "\\bword\\b";
        var input = "XXXword YYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 8).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 8).useAnchoringBounds(false);

        // With transparent bounds disabled (default), \b sees region boundaries as word boundaries,
        // so \bword\b matches at region start even though "XXX" is adjacent in full input.
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

    // ========================================================================
    // Transparent bounds tests
    // ========================================================================

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hasTransparentBoundsDefault(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Default should be false (opaque bounds)
        assertEquals(javaMatcher.hasTransparentBounds(), pcre4jMatcher.hasTransparentBounds());
        assertFalse(pcre4jMatcher.hasTransparentBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void useTransparentBoundsReturnsThis(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Should return this for method chaining
        var result = pcre4jMatcher.useTransparentBounds(true);
        assertEquals(pcre4jMatcher, result);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void useTransparentBoundsTrue(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.useTransparentBounds(true);
        pcre4jMatcher.useTransparentBounds(true);

        assertEquals(javaMatcher.hasTransparentBounds(), pcre4jMatcher.hasTransparentBounds());
        assertTrue(pcre4jMatcher.hasTransparentBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void useTransparentBoundsFalse(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set to true first, then back to false
        javaMatcher.useTransparentBounds(true).useTransparentBounds(false);
        pcre4jMatcher.useTransparentBounds(true).useTransparentBounds(false);

        assertEquals(javaMatcher.hasTransparentBounds(), pcre4jMatcher.hasTransparentBounds());
        assertFalse(pcre4jMatcher.hasTransparentBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsLookbehindCanSeeBeforeRegion(IPcre2 api) {
        // Test that lookbehind can see text before region start with transparent bounds enabled
        var regex = "(?<=foo)bar";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to start after "foo" (positions 3-9, which is "barXXX")
        javaMatcher.region(3, 9).useTransparentBounds(true);
        pcre4jMatcher.region(3, 9).useTransparentBounds(true);

        // With transparent bounds, lookbehind should see "foo" before the region
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);  // Should find "bar" preceded by "foo"
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsLookbehindCannotSeeBeforeRegionWhenOpaque(IPcre2 api) {
        // Test that lookbehind cannot see text before region start with opaque bounds (default)
        var regex = "(?<=foo)bar";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to start after "foo" (positions 3-9, which is "barXXX")
        // With opaque bounds (default), lookbehind cannot see "foo"
        javaMatcher.region(3, 9);
        pcre4jMatcher.region(3, 9);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(javaResult);  // Should NOT find "bar" because lookbehind can't see "foo"
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsLookaheadCanSeeAfterRegion(IPcre2 api) {
        // Test that lookahead can see text after region end with transparent bounds enabled
        var regex = "bar(?=XXX)";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to "foobar" (positions 0-6), not including "XXX"
        javaMatcher.region(0, 6).useTransparentBounds(true);
        pcre4jMatcher.region(0, 6).useTransparentBounds(true);

        // With transparent bounds, lookahead should see "XXX" after the region
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);  // Should find "bar" followed by "XXX"
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsLookaheadCannotSeeAfterRegionWhenOpaque(IPcre2 api) {
        // Test that lookahead cannot see text after region end with opaque bounds (default)
        var regex = "bar(?=XXX)";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to "foobar" (positions 0-6), not including "XXX"
        // With opaque bounds (default), lookahead cannot see "XXX"
        javaMatcher.region(0, 6);
        pcre4jMatcher.region(0, 6);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(javaResult);  // Should NOT find "bar" because lookahead can't see "XXX"
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWordBoundaryCanSeeBeforeRegion(IPcre2 api) {
        // Test that \b (word boundary) can see text before region with transparent bounds
        var regex = "\\bword";
        var input = "XXXword YYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region starting at "word" (position 3), with "XXX" before it
        javaMatcher.region(3, 8).useTransparentBounds(true);
        pcre4jMatcher.region(3, 8).useTransparentBounds(true);

        // With transparent bounds, \b should see that "X" is before "word" (no word boundary)
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        // "XXXword" - the X before 'w' is a letter, so \b should NOT match at position 3
        assertFalse(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWordBoundaryWithOpaqueRegion(IPcre2 api) {
        // Test that \b (word boundary) treats region start as word boundary with opaque bounds
        var regex = "\\bword";
        var input = "XXXword YYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region starting at "word" (position 3), with "XXX" before it
        // With opaque bounds (default), \b treats region start as word boundary
        javaMatcher.region(3, 8);
        pcre4jMatcher.region(3, 8);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        // With opaque bounds, \b sees region boundary as word boundary, so matches
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsPreservedAfterReset(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.useTransparentBounds(true);
        pcre4jMatcher.useTransparentBounds(true);

        javaMatcher.reset();
        pcre4jMatcher.reset();

        // Transparent bounds setting should be preserved after reset
        assertEquals(javaMatcher.hasTransparentBounds(), pcre4jMatcher.hasTransparentBounds());
        assertTrue(pcre4jMatcher.hasTransparentBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsPreservedAfterResetWithInput(IPcre2 api) {
        var regex = "test";
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.useTransparentBounds(true);
        pcre4jMatcher.useTransparentBounds(true);

        javaMatcher.reset("newtest");
        pcre4jMatcher.reset("newtest");

        // Transparent bounds setting should be preserved after reset with new input
        assertEquals(javaMatcher.hasTransparentBounds(), pcre4jMatcher.hasTransparentBounds());
        assertTrue(pcre4jMatcher.hasTransparentBounds());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsCombinedWithAnchoringBounds(IPcre2 api) {
        // Test that transparent bounds and anchoring bounds can be used together
        var regex = "(?<=foo)^bar$(?=XXX)";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region to "bar" only (positions 3-6)
        // Enable transparent bounds (lookaround can see outside)
        // Enable anchoring bounds (^ and $ match at region boundaries)
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        // Should match: lookbehind sees "foo", ^ matches region start, $ matches region end,
        // lookahead sees "XXX"
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsLookingAtWithLookbehind(IPcre2 api) {
        // Test lookingAt() with lookbehind and transparent bounds
        var regex = "(?<=foo)bar";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 9).useTransparentBounds(true);
        pcre4jMatcher.region(3, 9).useTransparentBounds(true);

        assertEquals(javaMatcher.lookingAt(), pcre4jMatcher.lookingAt());
        assertTrue(pcre4jMatcher.lookingAt());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMatchesWithLookaround(IPcre2 api) {
        // Test matches() with lookaround and transparent bounds
        var regex = "(?<=foo)bar(?=XXX)";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertTrue(pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsNegativeLookbehind(IPcre2 api) {
        // Test negative lookbehind with transparent bounds
        var regex = "(?<!foo)bar";
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region starts at "bar" (position 3)
        javaMatcher.region(3, 9).useTransparentBounds(true);
        pcre4jMatcher.region(3, 9).useTransparentBounds(true);

        // With transparent bounds, negative lookbehind sees "XXX" (not "foo"), so matches
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsNegativeLookahead(IPcre2 api) {
        // Test negative lookahead with transparent bounds
        var regex = "bar(?!XXX)";
        var input = "foobarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "foobar" (positions 0-6), "YYY" is outside
        javaMatcher.region(0, 6).useTransparentBounds(true);
        pcre4jMatcher.region(0, 6).useTransparentBounds(true);

        // With transparent bounds, negative lookahead sees "YYY" (not "XXX"), so matches
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithFind(IPcre2 api) {
        // Test find() with transparent bounds
        var regex = "(?<=\\d)\\w+";
        var input = "123abcXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "abc" (positions 3-6), with "123" before it
        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        // find() should find "abc" preceded by a digit
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
        assertEquals("abc", pcre4jMatcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsUnicodeWithLookbehind(IPcre2 api) {
        // Test transparent bounds with Unicode and lookbehind
        var regex = "(?<=🌐)test";
        var input = "🌐testXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // 🌐 is 2 characters in Java string (surrogate pair), so region starts at position 2
        javaMatcher.region(2, 6).useTransparentBounds(true);
        pcre4jMatcher.region(2, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithUsePattern(IPcre2 api) {
        // Test that usePattern() works correctly with transparent bounds
        // This exercises the anchoringBoundsCode = null path in usePattern()
        var regex1 = "(?<=foo)bar";
        var regex2 = "(?<=XXX)YYY";
        var input = "foobarXXXYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex1).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex1).matcher(input);

        // Set up transparent bounds and match with first pattern
        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertTrue(pcre4jMatcher.hasMatch());

        // Switch to second pattern
        javaMatcher.usePattern(java.util.regex.Pattern.compile(regex2));
        pcre4jMatcher.usePattern(Pattern.compile(api, regex2));
        // usePattern calls reset(), set up new region
        javaMatcher.region(9, 12).useTransparentBounds(true);
        pcre4jMatcher.region(9, 12).useTransparentBounds(true);
        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertTrue(pcre4jMatcher.hasMatch());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithCaseInsensitive(IPcre2 api) {
        // Test transparent bounds with CASE_INSENSITIVE flag
        var regex = "(?<=FOO)bar";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.CASE_INSENSITIVE).matcher(input);

        javaMatcher.region(3, 9).useTransparentBounds(true);
        pcre4jMatcher.region(3, 9).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithDotall(IPcre2 api) {
        // Test transparent bounds with DOTALL flag
        var regex = "(?<=foo).";
        var input = "foo\nXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.DOTALL).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.DOTALL).matcher(input);

        javaMatcher.region(3, 7).useTransparentBounds(true);
        pcre4jMatcher.region(3, 7).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
        assertEquals("\n", pcre4jMatcher.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithMultiline(IPcre2 api) {
        // Test transparent bounds with MULTILINE flag
        // MULTILINE affects ^ behavior - should still work with transparent bounds
        var regex = "^bar";
        var input = "foo\nbarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.MULTILINE).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.MULTILINE).matcher(input);

        // Region starts at "bar" (position 4)
        javaMatcher.region(4, 10).useTransparentBounds(true);
        pcre4jMatcher.region(4, 10).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMatchExtendsBeyondRegion(IPcre2 api) {
        // Test where greedy match would extend beyond region - should find shorter match
        var regex = "(?<=foo)\\w+";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "bar" only (positions 3-6), pattern would match "barXXX" without constraint
        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
        assertEquals("bar", pcre4jMatcher.group());  // Should be constrained to region
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsNoMatchWhenConstrainedToRegion(IPcre2 api) {
        // Test where match only exists beyond region - should not match
        var regex = "(?<=foo)XXX";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "bar" only (positions 3-6), "XXX" is outside
        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(javaResult);  // No match because XXX is outside region
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithAnchoringBoundsAndAnchors(IPcre2 api) {
        // Test transparent + anchoring bounds with ^ and $ in pattern
        var regex = "^bar$";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "bar" only (positions 3-6)
        // With anchoring bounds, ^ should match at region start, $ at region end
        // With transparent bounds, lookaround would see outside (though this pattern has none)
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMatchesConstrainedSubject(IPcre2 api) {
        // Test matches() with transparent bounds where match would extend beyond region
        var regex = "(?<=foo)\\w+(?=YYY)";
        var input = "foobarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "bar" (positions 3-6)
        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.matches();
        var pcre4jResult = pcre4jMatcher.matches();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithComments(IPcre2 api) {
        // Test transparent bounds with COMMENTS flag
        var regex = "(?<=foo) bar  # match bar after foo";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.COMMENTS).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.COMMENTS).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithUnicodeCharacterClass(IPcre2 api) {
        // Test transparent bounds with UNICODE_CHARACTER_CLASS flag
        var regex = "(?<=foo)\\w+";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.UNICODE_CHARACTER_CLASS)
                .matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.UNICODE_CHARACTER_CLASS).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithUnixLines(IPcre2 api) {
        // Test transparent bounds with UNIX_LINES flag
        var regex = "(?<=foo)bar";
        var input = "foobar\nXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.UNIX_LINES).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.UNIX_LINES).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsCachedTransformedPattern(IPcre2 api) {
        // Test that the transformed pattern is cached and reused
        var regex = "^bar$";
        var input = "XXXbarYYY";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // First match - creates the transformed pattern
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        assertTrue(pcre4jMatcher.find());

        // Reset and match again - should use cached transformed pattern
        pcre4jMatcher.reset();
        pcre4jMatcher.region(3, 6);
        assertTrue(pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsNoTransformationNeeded(IPcre2 api) {
        // Test pattern without ^ or $ (no transformation needed)
        var regex = "(?<=foo)bar(?=XXX)";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // This pattern has no ^ or $, so no transformation is needed
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringBoundsWithOnlyDollar(IPcre2 api) {
        // Test pattern with only $ (transformation removes $)
        var regex = "(?<=foo)bar$";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region ends at "bar" (position 6), $ should match at region end
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringBoundsWithOnlyCaret(IPcre2 api) {
        // Test pattern with only ^ (transformation replaces ^ with \G)
        var regex = "^bar";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region starts at "bar" (position 3), ^ should match at region start
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsEscapedAnchors(IPcre2 api) {
        // Test that escaped ^ and $ are not transformed
        var regex = "\\^bar\\$";
        var input = "foo^bar$XXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // \^ and \$ are literal characters, not anchors
        javaMatcher.region(3, 8).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 8).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchorsInCharacterClass(IPcre2 api) {
        // Test that ^ and $ inside character classes are not transformed
        var regex = "[^a]ar[$]";
        var input = "foobar$XXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // [^a] is negated char class, [$] matches literal $
        javaMatcher.region(3, 8).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 8).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMatchDoesNotEndAtRegionEnd(IPcre2 api) {
        // Test where anchored match doesn't end exactly at regionEnd
        var regex = "^ba$";
        var input = "foobarXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region is "bar" (positions 3-6), but ^ba$ only matches "ba" not "bar"
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(javaResult);  // "ba" doesn't match because $ requires end at position 6
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMultipleFlagsCombined(IPcre2 api) {
        // Test transparent bounds with multiple flags combined
        var regex = "(?<=FOO) bar  # comment";
        var input = "foobar\nXXX";
        int javaFlags = java.util.regex.Pattern.CASE_INSENSITIVE
                | java.util.regex.Pattern.COMMENTS
                | java.util.regex.Pattern.DOTALL;
        int pcre4jFlags = Pattern.CASE_INSENSITIVE | Pattern.COMMENTS | Pattern.DOTALL;

        var javaMatcher = java.util.regex.Pattern.compile(regex, javaFlags).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, pcre4jFlags).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsConstrainedMatchFailsAdvancesPosition(IPcre2 api) {
        // Test that when a match extends beyond regionEnd and the constrained match fails,
        // the search continues from the next position and eventually finds a valid match.
        // This exercises the PATH 3 branch where constrainedResult < 1 and we advance searchStart.
        var regex = "b+";  // Greedy quantifier that would extend beyond region
        var input = "XXXbbbYYYbb";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Set region from 3 to 5, input has "bbb" at 3-5 and "bb" at 9-11
        // With transparent bounds, the greedy b+ would want to match all 3 b's but regionEnd=5
        javaMatcher.region(3, 5).useTransparentBounds(true);
        pcre4jMatcher.region(3, 5).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void opaqueBoundsWithRegionStartGreaterThanZero(IPcre2 api) {
        // Test opaque bounds (default) with regionStart > 0 to exercise getRegionSubject() branch
        var regex = "bar";
        var input = "foobarbaz";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region from 3 to 6: "bar"
        javaMatcher.region(3, 6);
        pcre4jMatcher.region(3, 6);

        // Should find match at 3 (start of region)
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(pcre4jResult);
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(3, pcre4jMatcher.start());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void opaqueBoundsLookbehindBlocked(IPcre2 api) {
        // Test that with opaque bounds (default), lookbehind cannot see before region
        // This uses regionStart > 0 to exercise the substring branch in getRegionSubject()
        var regex = "(?<=foo)bar";
        var input = "foobar";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region from 3 to 6: "bar", but lookbehind needs to see "foo" before region
        javaMatcher.region(3, 6);  // Opaque bounds (default)
        pcre4jMatcher.region(3, 6);

        // With opaque bounds, lookbehind cannot see "foo" before region
        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithLiteralFlag(IPcre2 api) {
        // Test transparent bounds with LITERAL flag - pattern treated as literal
        var regex = "^ba";  // With LITERAL, ^ and $ are literal characters, not anchors
        var input = "^ba$test";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.LITERAL).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.LITERAL).matcher(input);

        javaMatcher.useTransparentBounds(true);
        pcre4jMatcher.useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void nonAnchoringBoundsWithMiddleRegion(IPcre2 api) {
        // Test non-anchoring bounds with region in middle of input
        // This exercises getMatchOptions() with both NOTBOL and NOTEOL
        var regex = "test";
        var input = "XXXtestYYY";  // length 10
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 7) means regionStart=3 > 0, regionEnd=7 < 10
        // With non-anchoring bounds, both NOTBOL and NOTEOL should be set
        javaMatcher.region(3, 7).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 7).useAnchoringBounds(false);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(pcre4jResult);
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsSearchStartAdvancesOnConstraintFailure(IPcre2 api) {
        // Test the loop where searchStart advances after constrained match fails
        // Pattern: b+ (greedy) would match multiple b's
        // Input: "aabbbcc" with region (2, 4) meaning "bb" is in region but "bbb" available with transparent bounds
        var regex = "b+";
        var input = "aabbbcc";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (2, 4) covers "bb", but transparent bounds can see "bbb"
        // The greedy b+ with transparent bounds sees 3 b's, tries to match, but extends past regionEnd=4
        // The constrained match at position 2 should find "bb" (2 chars within region)
        javaMatcher.region(2, 4).useTransparentBounds(true);
        pcre4jMatcher.region(2, 4).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
        // Match should be "bb" from position 2 to 4
        assertEquals(2, pcre4jMatcher.start());
        assertEquals(4, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringBoundsWithTransformFailedMatch(IPcre2 api) {
        // Test PATH 1 where transformed pattern matches but doesn't end at regionEnd,
        // then falls through to PATH 2 for normal matching
        var regex = "^ba";  // Only caret anchor, no dollar
        var input = "XXXbarYYY";  // "bar" from 3-6
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 6), pattern ^ba would transform to \Gba
        // \G matches at startOffset (3), so "ba" matches positions 3-5
        // Match ends at 5, not regionEnd=6, so falls through to normal matching
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void nonAnchoringBoundsOnlyNotbol(IPcre2 api) {
        // Test non-anchoring bounds with only NOTBOL (regionStart > 0, regionEnd == input.length())
        var regex = "test";
        var input = "XXXtest";  // length 7
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 7): regionStart=3 > 0 (NOTBOL), regionEnd=7 == input.length() (no NOTEOL)
        javaMatcher.region(3, 7).useAnchoringBounds(false);
        pcre4jMatcher.region(3, 7).useAnchoringBounds(false);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void nonAnchoringBoundsOnlyNoteol(IPcre2 api) {
        // Test non-anchoring bounds with only NOTEOL (regionStart == 0, regionEnd < input.length())
        var regex = "test";
        var input = "testXXX";  // length 7
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (0, 4): regionStart=0 (no NOTBOL), regionEnd=4 < 7 (NOTEOL)
        javaMatcher.region(0, 4).useAnchoringBounds(false);
        pcre4jMatcher.region(0, 4).useAnchoringBounds(false);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void opaqueBoundsRegionStartZero(IPcre2 api) {
        // Test opaque bounds with regionStart == 0 to exercise else branch in getRegionSubject()
        var regex = "test";
        var input = "testXXX";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (0, 4): regionStart=0, uses the else branch in getRegionSubject()
        javaMatcher.region(0, 4);
        pcre4jMatcher.region(0, 4);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(pcre4jResult);
        assertEquals(0, pcre4jMatcher.start());
        assertEquals(4, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithDollarAnchorAtRegionEnd(IPcre2 api) {
        // Test transparent + anchoring bounds with pattern that has $ and match ends at regionEnd
        var regex = "^bar$";
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 6): "bar", pattern ^bar$ should match with anchoring bounds
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(pcre4jResult);
        assertEquals(3, pcre4jMatcher.start());
        assertEquals(6, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsTransformedPatternNoMatch(IPcre2 api) {
        // Test PATH 1 where transformed pattern doesn't match, falls through to PATH 2
        var regex = "^xyz";  // Pattern with ^ that won't match at regionStart
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 6): "bar", pattern ^xyz won't match even with transformation
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMatchExtendsAndConstrainedFails(IPcre2 api) {
        // Test PATH 3 where match extends beyond regionEnd and constrained match also fails
        // Pattern: "bbb" requires 3 b's, but region only has 2 b's
        var regex = "bbb";
        var input = "aabbccc";  // "bb" at position 2-4
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (2, 4): "bb", but pattern needs "bbb"
        // Transparent bounds sees "bbc" but "bbb" doesn't exist
        javaMatcher.region(2, 4).useTransparentBounds(true);
        pcre4jMatcher.region(2, 4).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsSearchLoopExhaustsPositions(IPcre2 api) {
        // Test that search loop correctly terminates when all positions exhausted
        var regex = "xyz";  // Pattern that won't match anywhere in region
        var input = "XXXabcYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithDollarPatternNotEndingAtRegionEnd(IPcre2 api) {
        // Test pattern with $ where the match doesn't end at regionEnd
        // This exercises the path where originalHadDollar is true but lastMatchIndices[1] != regionEnd
        var regex = "^ba$";  // Pattern with $ anchor
        var input = "XXXbarYYY";  // "bar" at 3-6, pattern "^ba$" needs to end after "ba"
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 6): "bar", but ^ba$ would match at 3-5 (not at regionEnd=6)
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsPatternWithOnlyDollarAnchor(IPcre2 api) {
        // Test transparent + anchoring bounds with pattern that has only $ (no ^)
        // This exercises patternContainsDollarAnchor returning true for patterns without ^
        var regex = "bar$";
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 6): "bar", pattern bar$ should match with $ at regionEnd
        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsGreedyQuantifierConstrainedMatch(IPcre2 api) {
        // Test greedy quantifier where transparent bounds sees more but constrained match works
        var regex = "a+";  // Greedy quantifier
        var input = "XXXaaaYYY";  // "aaa" at 3-6
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 5): only "aa" in region, but transparent bounds sees "aaa"
        // Constrained match should find "aa"
        javaMatcher.region(3, 5).useTransparentBounds(true);
        pcre4jMatcher.region(3, 5).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
        assertEquals(3, pcre4jMatcher.start());
        assertEquals(5, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithNestedCharacterClass(IPcre2 api) {
        // Test pattern with POSIX character class (nested brackets) containing $
        // This exercises the charClassDepth tracking in patternContainsDollarAnchor
        var regex = "[[:alpha:]$]+";  // POSIX class with $ inside
        var input = "XXXa$bYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMatchStartsAfterRegionStart(IPcre2 api) {
        // Test transparent bounds where match doesn't start at regionStart
        // This ensures PATH 1 is skipped when searchStart != regionStart
        var regex = "bar";
        var input = "XXXfoobarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 9): "foobar", "bar" starts at position 6
        javaMatcher.region(3, 9).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 9).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
        assertEquals(6, pcre4jMatcher.start());
        assertEquals(9, pcre4jMatcher.end());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void opaqueBoundsWithIndexAdjustment(IPcre2 api) {
        // Test that index adjustment works correctly for opaque bounds with regionStart > 0
        var regex = "(test)";  // Capturing group
        var input = "XXXtestYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 7): "test"
        javaMatcher.region(3, 7);
        pcre4jMatcher.region(3, 7);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        // Check that group indices are properly adjusted to input coordinates
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.start(1), pcre4jMatcher.start(1));
        assertEquals(javaMatcher.end(1), pcre4jMatcher.end(1));
        assertEquals(3, pcre4jMatcher.start(1));
        assertEquals(7, pcre4jMatcher.end(1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsMultipleFinds(IPcre2 api) {
        // Test multiple find() calls with transparent bounds
        var regex = "a";
        var input = "XXXabaYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 6): "aba", should find 'a' at positions 3 and 5
        javaMatcher.region(3, 6).useTransparentBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true);

        // First match
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(3, pcre4jMatcher.start());

        // Second match
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(5, pcre4jMatcher.start());

        // No more matches
        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsEmptyRegion(IPcre2 api) {
        // Test transparent bounds with empty region
        var regex = "";  // Empty pattern matches empty string
        var input = "test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Empty region at position 2
        javaMatcher.region(2, 2).useTransparentBounds(true);
        pcre4jMatcher.region(2, 2).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsFullInputRegion(IPcre2 api) {
        // Test transparent bounds when region covers full input
        var regex = "(?<=X)test(?=Y)";
        var input = "XtestY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region covers full input
        javaMatcher.region(0, input.length()).useTransparentBounds(true);
        pcre4jMatcher.region(0, input.length()).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringWithCaseInsensitiveFlag(IPcre2 api) {
        // Test transparent + anchoring bounds with CASE_INSENSITIVE flag
        // This exercises the CASELESS option in getOrCreateAnchoringBoundsCode()
        var regex = "^BAR$";
        var input = "XXXbarYYY";
        var javaPattern = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.CASE_INSENSITIVE);
        var javaMatcher = javaPattern.matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.CASE_INSENSITIVE).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringWithDotallFlag(IPcre2 api) {
        // Test transparent + anchoring bounds with DOTALL flag
        // This exercises the DOTALL option in getOrCreateAnchoringBoundsCode()
        var regex = "^b.r$";
        var input = "XXXb\nrYYY";  // newline in middle
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.DOTALL).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.DOTALL).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringWithUnicodeCharacterClassFlag(IPcre2 api) {
        // Test transparent + anchoring bounds with UNICODE_CHARACTER_CLASS flag
        // This exercises the UCP option in getOrCreateAnchoringBoundsCode()
        // Using a simpler pattern that works consistently
        var regex = "^test$";
        var input = "XXXtestYYY";
        int javaFlags = java.util.regex.Pattern.UNICODE_CHARACTER_CLASS;
        var javaMatcher = java.util.regex.Pattern.compile(regex, javaFlags).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.UNICODE_CHARACTER_CLASS).matcher(input);

        javaMatcher.region(3, 7).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 7).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringWithCommentsFlag(IPcre2 api) {
        // Test transparent + anchoring bounds with COMMENTS flag
        // This exercises the EXTENDED option in getOrCreateAnchoringBoundsCode()
        var regex = "^bar$  # comment";
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.COMMENTS).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.COMMENTS).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsAnchoringWithUnixLinesFlag(IPcre2 api) {
        // Test transparent + anchoring bounds with UNIX_LINES flag
        // This exercises the LF newline option in getOrCreateAnchoringBoundsCode()
        var regex = "^bar$";
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.UNIX_LINES).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, Pattern.UNIX_LINES).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsConstrainedMatchFailsAndAdvances(IPcre2 api) {
        // Test PATH 3 where match extends beyond regionEnd, constrained fails, and searchStart advances
        // Pattern "bb" would match at position 3 (matching "bb"), extending to position 5
        // But with region (3, 4), only one 'b' is in region, so constrained match fails
        // Then search should advance but find no more matches
        var regex = "bb";
        var input = "aaabbccc";  // "bb" at position 3-5
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (3, 4): only one 'b' in region
        javaMatcher.region(3, 4).useTransparentBounds(true);
        pcre4jMatcher.region(3, 4).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertFalse(pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void patternContainsDollarAnchorEscapedDollar(IPcre2 api) {
        // Test patternContainsDollarAnchor with escaped $ (should return false)
        // This exercises the escaped character handling path
        var regex = "bar\\$";  // Escaped $, not an anchor
        var input = "XXXbar$YYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 7).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 7).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void patternContainsDollarAnchorInCharClass(IPcre2 api) {
        // Test patternContainsDollarAnchor with $ inside character class (should return false)
        // This exercises the character class depth tracking
        var regex = "[a$]+bar";  // $ inside character class, not an anchor
        var input = "XXX$abarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        javaMatcher.region(3, 8).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 8).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsLoopAdvancesMultipleTimes(IPcre2 api) {
        // Test that search loop can advance multiple times before finding a match
        // Pattern needs to fail at multiple positions before succeeding
        var regex = "c+";  // Match one or more c's
        var input = "aabbccdd";  // "cc" at position 4-6
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // Region (2, 5): "bbcc" - first c at position 4
        // Transparent bounds sees beyond but match starts at 4
        javaMatcher.region(2, 5).useTransparentBounds(true);
        pcre4jMatcher.region(2, 5).useTransparentBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        if (javaResult) {
            assertEquals(javaMatcher.start(), pcre4jMatcher.start());
            assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        }
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void transparentBoundsWithAllFlagsAndAnchors(IPcre2 api) {
        // Test with multiple flags combined plus anchors
        int javaFlags = java.util.regex.Pattern.CASE_INSENSITIVE
                | java.util.regex.Pattern.DOTALL
                | java.util.regex.Pattern.COMMENTS;
        int pcre4jFlags = Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.COMMENTS;

        var regex = "^BAR$  # comment";
        var input = "XXXbarYYY";
        var javaMatcher = java.util.regex.Pattern.compile(regex, javaFlags).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex, pcre4jFlags).matcher(input);

        javaMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);
        pcre4jMatcher.region(3, 6).useTransparentBounds(true).useAnchoringBounds(true);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult);
    }

    // hitEnd() and requireEnd() tests

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndBeforeMatchOperation(IPcre2 api) {
        // Before any match operation, hitEnd should return false
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("testing");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("testing");

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertFalse(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndBeforeMatchOperation(IPcre2 api) {
        // Before any match operation, requireEnd should return false
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("testing");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("testing");

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertFalse(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndAfterPartialMatch(IPcre2 api) {
        // Pattern "AAB" against input "AA" - no full match, but partial match exists
        var javaMatcher = java.util.regex.Pattern.compile("AAB").matcher("AA");
        var pcre4jMatcher = Pattern.compile(api, "AAB").matcher("AA");

        assertFalse(javaMatcher.matches());
        assertFalse(pcre4jMatcher.matches());

        // hitEnd should be true because more input could lead to a match
        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertTrue(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndAfterSuccessfulMatchAtEnd(IPcre2 api) {
        // Pattern "test" matches exactly at end of input - hitEnd is false in Java
        // because the literal pattern matched completely
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertFalse(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndAfterSuccessfulMatchNotAtEnd(IPcre2 api) {
        // Pattern matches but not at the end - hitEnd should be false
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("test123");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("test123");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertFalse(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithNoMatchNoPartial(IPcre2 api) {
        // Pattern cannot possibly match - hitEnd is still true in Java because
        // the search engine had to examine the entire input to determine no match
        var javaMatcher = java.util.regex.Pattern.compile("xyz").matcher("abc");
        var pcre4jMatcher = Pattern.compile(api, "xyz").matcher("abc");

        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertTrue(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithDollarAnchor(IPcre2 api) {
        // Pattern with $ anchor matching at end - requireEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("test$").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test$").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertTrue(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithoutAnchor(IPcre2 api) {
        // Pattern without end anchor - requireEnd should be false even if match at end
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertFalse(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithBackslashZ(IPcre2 api) {
        // Pattern with \z anchor (absolute end) - requireEnd is FALSE in Java
        // because \z only matches at the absolute end, so more input cannot
        // invalidate the match (it would just not match the \z anymore)
        var javaMatcher = java.util.regex.Pattern.compile("test\\z").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test\\z").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertFalse(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithBackslashZUppercase(IPcre2 api) {
        // Pattern with \Z anchor (end before final newline) - requireEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("test\\Z").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test\\Z").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertTrue(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithLookingAt(IPcre2 api) {
        // Test hitEnd with lookingAt
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("test");

        assertTrue(javaMatcher.lookingAt());
        assertTrue(pcre4jMatcher.lookingAt());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithMatches(IPcre2 api) {
        // Test hitEnd with matches
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("test");

        assertTrue(javaMatcher.matches());
        assertTrue(pcre4jMatcher.matches());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndPersistsAfterReset(IPcre2 api) {
        // In Java, hitEnd persists after reset() - it is NOT cleared
        var javaMatcher = java.util.regex.Pattern.compile("test$").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test$").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        // Verify hitEnd is true after the match (pattern has $ anchor)
        assertTrue(javaMatcher.hitEnd());
        assertTrue(pcre4jMatcher.hitEnd());

        javaMatcher.reset();
        pcre4jMatcher.reset();

        // hitEnd persists after reset
        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertTrue(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndPersistsAfterReset(IPcre2 api) {
        // In Java, requireEnd persists after reset() - it is NOT cleared
        var javaMatcher = java.util.regex.Pattern.compile("test$").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test$").matcher("test");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        // Verify requireEnd is true after the match (pattern has $ anchor)
        assertTrue(javaMatcher.requireEnd());
        assertTrue(pcre4jMatcher.requireEnd());

        javaMatcher.reset();
        pcre4jMatcher.reset();

        // requireEnd persists after reset
        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertTrue(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithGreedyQuantifier(IPcre2 api) {
        // Greedy quantifier at end - hitEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("a+").matcher("aaa");
        var pcre4jMatcher = Pattern.compile(api, "a+").matcher("aaa");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithCharacterClass(IPcre2 api) {
        // Character class at end - hitEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("[a-z]+").matcher("abc");
        var pcre4jMatcher = Pattern.compile(api, "[a-z]+").matcher("abc");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithDot(IPcre2 api) {
        // Dot at end can match more - hitEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("a.").matcher("ab");
        var pcre4jMatcher = Pattern.compile(api, "a.").matcher("ab");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndAndRequireEndWithDollarNoMatch(IPcre2 api) {
        // Pattern ends with $ but doesn't match - requireEnd meaningless, hitEnd indicates partial
        var javaMatcher = java.util.regex.Pattern.compile("xyz$").matcher("abc");
        var pcre4jMatcher = Pattern.compile(api, "xyz$").matcher("abc");

        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithRegion(IPcre2 api) {
        // Test hitEnd with region
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("XXtestXX");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("XXtestXX");

        javaMatcher.region(2, 6);
        pcre4jMatcher.region(2, 6);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithDollarInCharacterClass(IPcre2 api) {
        // $ inside character class is literal, not anchor - requireEnd should be false
        var javaMatcher = java.util.regex.Pattern.compile("[$]").matcher("$");
        var pcre4jMatcher = Pattern.compile(api, "[$]").matcher("$");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertFalse(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndAfterMultipleFinds(IPcre2 api) {
        // Multiple finds - hitEnd should reflect the last find
        var javaMatcher = java.util.regex.Pattern.compile("a").matcher("aXa");
        var pcre4jMatcher = Pattern.compile(api, "a").matcher("aXa");

        // First find - not at end
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertFalse(pcre4jMatcher.hitEnd());

        // Second find - at end
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());
        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());

        // Third find - no match
        assertFalse(javaMatcher.find());
        assertFalse(pcre4jMatcher.find());
        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithCharacterClassNoQuantifier(IPcre2 api) {
        // Character class without quantifier at end - matches exactly one char, hitEnd should be false
        var javaMatcher = java.util.regex.Pattern.compile("[a-z]").matcher("a");
        var pcre4jMatcher = Pattern.compile(api, "[a-z]").matcher("a");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertFalse(pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithWordEscape(IPcre2 api) {
        // \w at end without quantifier - matches exactly one char
        var javaMatcher = java.util.regex.Pattern.compile("\\w").matcher("a");
        var pcre4jMatcher = Pattern.compile(api, "\\w").matcher("a");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithDigitEscape(IPcre2 api) {
        // \d at end without quantifier
        var javaMatcher = java.util.regex.Pattern.compile("\\d").matcher("5");
        var pcre4jMatcher = Pattern.compile(api, "\\d").matcher("5");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithSpaceEscape(IPcre2 api) {
        // \s at end without quantifier
        var javaMatcher = java.util.regex.Pattern.compile("\\s").matcher(" ");
        var pcre4jMatcher = Pattern.compile(api, "\\s").matcher(" ");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithLookingAtQuantifier(IPcre2 api) {
        // lookingAt with quantifier at end
        var javaMatcher = java.util.regex.Pattern.compile("a+").matcher("aaa");
        var pcre4jMatcher = Pattern.compile(api, "a+").matcher("aaa");

        assertTrue(javaMatcher.lookingAt());
        assertTrue(pcre4jMatcher.lookingAt());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithMatchesQuantifier(IPcre2 api) {
        // matches with quantifier
        var javaMatcher = java.util.regex.Pattern.compile("a+").matcher("aaa");
        var pcre4jMatcher = Pattern.compile(api, "a+").matcher("aaa");

        assertTrue(javaMatcher.matches());
        assertTrue(pcre4jMatcher.matches());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithMatchesDollar(IPcre2 api) {
        // matches() with $ - requireEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("test$").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test$").matcher("test");

        assertTrue(javaMatcher.matches());
        assertTrue(pcre4jMatcher.matches());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
        assertTrue(pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithLookingAtDollar(IPcre2 api) {
        // lookingAt() with $ - requireEnd should be true when matched at end
        var javaMatcher = java.util.regex.Pattern.compile("test$").matcher("test");
        var pcre4jMatcher = Pattern.compile(api, "test$").matcher("test");

        assertTrue(javaMatcher.lookingAt());
        assertTrue(pcre4jMatcher.lookingAt());

        assertEquals(javaMatcher.requireEnd(), pcre4jMatcher.requireEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void requireEndWithBraceQuantifier(IPcre2 api) {
        // Pattern with {n,} quantifier - hitEnd should be true
        var javaMatcher = java.util.regex.Pattern.compile("a{2,}").matcher("aaa");
        var pcre4jMatcher = Pattern.compile(api, "a{2,}").matcher("aaa");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void hitEndWithMatchNotAtEnd(IPcre2 api) {
        // Match found but not at end of input - hitEnd should be false
        var javaMatcher = java.util.regex.Pattern.compile("test").matcher("testXYZ");
        var pcre4jMatcher = Pattern.compile(api, "test").matcher("testXYZ");

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        assertEquals(javaMatcher.hitEnd(), pcre4jMatcher.hitEnd());
        assertFalse(pcre4jMatcher.hitEnd());
    }

    // Empty region anchor semantics tests (regression tests for issue #69)

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyRegionAtEndWithAnchors_find(IPcre2 api) {
        // Regression test for issue #69: ^$ pattern should match empty region at end of input
        var regex = "^$";
        var input = "abc";

        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        javaMatcher.region(3, 3);

        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        pcre4jMatcher.region(3, 3);

        var javaResult = javaMatcher.find();
        var pcre4jResult = pcre4jMatcher.find();
        assertEquals(javaResult, pcre4jResult);
        assertTrue(javaResult); // Java returns true for ^$ matching empty region
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyRegionAtEndWithAnchors_matches(IPcre2 api) {
        // Regression test for issue #69: ^$ pattern should match empty region at end of input
        var regex = "^$";
        var input = "abc";

        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        javaMatcher.region(3, 3);

        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        pcre4jMatcher.region(3, 3);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyRegionAtStartWithAnchors_find(IPcre2 api) {
        // Test ^$ pattern with empty region at start of input
        var regex = "^$";
        var input = "abc";

        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        javaMatcher.region(0, 0);

        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        pcre4jMatcher.region(0, 0);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void emptyRegionInMiddleWithAnchors_find(IPcre2 api) {
        // Test ^$ pattern with empty region in middle of input
        var regex = "^$";
        var input = "abc";

        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        javaMatcher.region(1, 1);

        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        pcre4jMatcher.region(1, 1);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void toStringBeforeMatch(IPcre2 api) {
        var regex = "\\d+";
        var input = "abc123def";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        var result = pcre4jMatcher.toString();

        assertTrue(result.startsWith("org.pcre4j.regex.Matcher["));
        assertTrue(result.contains("pattern=" + regex));
        assertTrue(result.contains("region=0," + input.length()));
        assertTrue(result.contains("lastMatchIndices=null"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void toStringAfterMatch(IPcre2 api) {
        var regex = "\\d+";
        var input = "abc123def";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        pcre4jMatcher.find();

        var result = pcre4jMatcher.toString();

        assertTrue(result.startsWith("org.pcre4j.regex.Matcher["));
        assertTrue(result.contains("pattern=" + regex));
        assertTrue(result.contains("region=0," + input.length()));
        assertTrue(result.contains("lastMatchIndices=[3, 6]"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void toStringWithRegion(IPcre2 api) {
        var regex = "\\d+";
        var input = "abc123def";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);
        pcre4jMatcher.region(3, 6);

        var result = pcre4jMatcher.toString();

        assertTrue(result.contains("region=3,6"));
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

    // ========================================================================
    // MatchResult snapshot tests
    // ========================================================================

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultSnapshotDoesNotChangeWhenMatcherAdvances(IPcre2 api) {
        var regex = "\\w+";
        var input = "hello world";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        var javaSnapshot = javaMatcher.toMatchResult();
        var pcre4jSnapshot = pcre4jMatcher.toMatchResult();

        assertEquals(javaSnapshot.group(), pcre4jSnapshot.group());
        assertEquals(javaSnapshot.start(), pcre4jSnapshot.start());
        assertEquals(javaSnapshot.end(), pcre4jSnapshot.end());

        // Advance the matcher to next match
        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        // Snapshot should still reflect the first match
        assertEquals(javaSnapshot.group(), pcre4jSnapshot.group());
        assertEquals(javaSnapshot.start(), pcre4jSnapshot.start());
        assertEquals(javaSnapshot.end(), pcre4jSnapshot.end());

        // But the matcher itself has moved forward
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals("world", pcre4jMatcher.group());
        assertEquals("hello", pcre4jSnapshot.group());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultNamedGroupAccessors(IPcre2 api) {
        var regex = "(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})";
        var input = "date: 2024-01-15";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        var javaResult = javaMatcher.toMatchResult();
        var pcre4jResult = pcre4jMatcher.toMatchResult();

        // Test group(String)
        assertEquals(javaResult.group("year"), pcre4jResult.group("year"));
        assertEquals(javaResult.group("month"), pcre4jResult.group("month"));
        assertEquals(javaResult.group("day"), pcre4jResult.group("day"));

        // Test start(String)
        assertEquals(javaResult.start("year"), pcre4jResult.start("year"));
        assertEquals(javaResult.start("month"), pcre4jResult.start("month"));
        assertEquals(javaResult.start("day"), pcre4jResult.start("day"));

        // Test end(String)
        assertEquals(javaResult.end("year"), pcre4jResult.end("year"));
        assertEquals(javaResult.end("month"), pcre4jResult.end("month"));
        assertEquals(javaResult.end("day"), pcre4jResult.end("day"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultByGroupNumber(IPcre2 api) {
        var regex = "(\\w+)\\s+(\\w+)\\s+(\\w+)";
        var input = "one two three";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        var javaResult = javaMatcher.toMatchResult();
        var pcre4jResult = pcre4jMatcher.toMatchResult();

        // Test group(int)
        for (int i = 0; i <= javaResult.groupCount(); i++) {
            assertEquals(javaResult.group(i), pcre4jResult.group(i));
            assertEquals(javaResult.start(i), pcre4jResult.start(i));
            assertEquals(javaResult.end(i), pcre4jResult.end(i));
        }

        assertEquals(javaResult.groupCount(), pcre4jResult.groupCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultThrowsIllegalStateExceptionWhenNoMatch(IPcre2 api) {
        var regex = "\\d+";
        var input = "hello";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        // No find() call, so no match has occurred
        var result = pcre4jMatcher.toMatchResult();

        assertThrows(IllegalStateException.class, () -> result.start());
        assertThrows(IllegalStateException.class, () -> result.end());
        assertThrows(IllegalStateException.class, () -> result.group());
        assertThrows(IllegalStateException.class, () -> result.start(0));
        assertThrows(IllegalStateException.class, () -> result.end(0));
        assertThrows(IllegalStateException.class, () -> result.group(0));
        assertThrows(IllegalStateException.class, () -> result.start("name"));
        assertThrows(IllegalStateException.class, () -> result.end("name"));
        assertThrows(IllegalStateException.class, () -> result.group("name"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultThrowsIndexOutOfBoundsForInvalidGroup(IPcre2 api) {
        var regex = "(\\w+)";
        var input = "hello";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(pcre4jMatcher.find());
        var result = pcre4jMatcher.toMatchResult();

        // Group count is 1, so group 2 should throw
        assertThrows(IndexOutOfBoundsException.class, () -> result.start(5));
        assertThrows(IndexOutOfBoundsException.class, () -> result.end(5));
        assertThrows(IndexOutOfBoundsException.class, () -> result.group(5));

        // Negative group
        assertThrows(IndexOutOfBoundsException.class, () -> result.start(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> result.end(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> result.group(-1));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultThrowsForInvalidNamedGroup(IPcre2 api) {
        var regex = "(?<word>\\w+)";
        var input = "hello";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(pcre4jMatcher.find());
        var result = pcre4jMatcher.toMatchResult();

        assertThrows(IllegalArgumentException.class, () -> result.start("nonexistent"));
        assertThrows(IllegalArgumentException.class, () -> result.end("nonexistent"));
        assertThrows(IllegalArgumentException.class, () -> result.group("nonexistent"));
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultNamedGroups(IPcre2 api) {
        var regex = "(?<first>\\w+) (?<second>\\w+)";
        var input = "hello world";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(javaMatcher.find());
        assertTrue(pcre4jMatcher.find());

        var javaResult = javaMatcher.toMatchResult();
        var pcre4jResult = pcre4jMatcher.toMatchResult();

        assertEquals(javaResult.namedGroups(), pcre4jResult.namedGroups());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultHasMatchFalseWhenNoMatch(IPcre2 api) {
        var regex = "xyz";
        var input = "hello";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertFalse(pcre4jMatcher.find());
        var result = pcre4jMatcher.toMatchResult();

        assertFalse(result.hasMatch());
        assertEquals(0, result.groupCount());
    }

    @ParameterizedTest
    @MethodSource("org.pcre4j.test.BackendProvider#parameters")
    void matchResultHasMatchTrueAfterMatch(IPcre2 api) {
        var regex = "\\w+";
        var input = "hello";
        var pcre4jMatcher = Pattern.compile(api, regex).matcher(input);

        assertTrue(pcre4jMatcher.find());
        var result = pcre4jMatcher.toMatchResult();

        assertTrue(result.hasMatch());
    }

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
