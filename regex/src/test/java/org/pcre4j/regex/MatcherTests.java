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

import org.junit.jupiter.api.Test;
import org.pcre4j.Pcre4j;
import org.pcre4j.jna.Pcre2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests to ensure API likeness of the {@link Matcher} to the {@link java.util.regex.Matcher}.
 */
public class MatcherTests {

    static {
        Pcre4j.setup(new Pcre2());
    }

    @Test
    void unicodeOneByte() {
        var regex = "Å";
        var input = "Å";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void unicodeTwoBytes() {
        var regex = "Ǎ";
        var input = "Ǎ";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void unicodeThreeBytes() {
        var regex = "•";
        var input = "•";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void unicodeFourBytes() {
        var regex = "\uD83C\uDF0D";
        var input = "\uD83C\uDF0D";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void unicode() {
        var regex = "ÅǍ•\uD83C\uDF0D!";
        var input = "ÅǍ•\uD83C\uDF0D!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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
    @Test
    void matchesTrue() {
        var regex = "42";
        var input = "42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void matchesFalse() {
        var regex = "42";
        var input = "42!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void matchesTrueInRegion() {
        var regex = "42";
        var input = "[42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void matchesFalseRegion() {
        var regex = "42";
        var input = "[42!]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void lookingAtTrue() {
        var regex = "42";
        var input = "42!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void lookingAtFalse() {
        var regex = "42";
        var input = "!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void lookingAtTrueInRegion() {
        var regex = "42";
        var input = "[42!]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void lookingAtFalseRegion() {
        var regex = "42";
        var input = "[!42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void findTrue() {
        var regex = "42";
        var input = "42!";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void findFalse() {
        var regex = "42!";
        var input = "42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void findTrueInRegion() {
        var regex = "42";
        var input = "[42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void findFalseInRegion() {
        var regex = "42!";
        var input = "[42]";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void findTrueAtOffset() {
        var regex = "42";
        var input = "!!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void findFalseAtOffset() {
        var regex = "42";
        var input = "!!test";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void findMultiple() {
        var regex = "42";
        var input = "42!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void findMultipleWithinRegion() {
        var regex = "42";
        var input = "42!42!42!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void findMultipleOutsideRegion() {
        var regex = "42";
        var input = "42!__!__!42";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

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

    @Test
    void captureGroups() {
        var regex = "(?<four>4)(.*)(?<two>2)";
        var input = "4test2";
        var javaMatcher = java.util.regex.Pattern.compile(regex).matcher(input);
        var pcre4jMatcher = Pattern.compile(regex).matcher(input);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());

        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.group(0), pcre4jMatcher.group(0));
        assertEquals(javaMatcher.group(1), pcre4jMatcher.group(1));
        assertEquals(javaMatcher.group(2), pcre4jMatcher.group(2));
        assertEquals(javaMatcher.group(3), pcre4jMatcher.group(3));
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.start(0), pcre4jMatcher.start(0));
        assertEquals(javaMatcher.start(1), pcre4jMatcher.start(1));
        assertEquals(javaMatcher.start(2), pcre4jMatcher.start(2));
        assertEquals(javaMatcher.start(3), pcre4jMatcher.start(3));
        assertEquals(javaMatcher.start("four"), pcre4jMatcher.start("four"));
        assertEquals(javaMatcher.start("two"), pcre4jMatcher.start("two"));
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.end(0), pcre4jMatcher.end(0));
        assertEquals(javaMatcher.end(1), pcre4jMatcher.end(1));
        assertEquals(javaMatcher.end(2), pcre4jMatcher.end(2));
        assertEquals(javaMatcher.end(3), pcre4jMatcher.end(3));
        assertEquals(javaMatcher.end("four"), pcre4jMatcher.end("four"));
        assertEquals(javaMatcher.end("two"), pcre4jMatcher.end("two"));
    }

}
