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
    void matchesTrue() {
        var javaMatcher = java.util.regex.Pattern.compile("42").matcher("42");
        var pcre4jMatcher = Pattern.compile("42").matcher("42");

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
    }

    @Test
    void matchesFalse() {
        var javaMatcher = java.util.regex.Pattern.compile("42").matcher("42!");
        var pcre4jMatcher = Pattern.compile("42").matcher("42!");

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
    }

    @Test
    void matchesTrueInRegion() {
        var javaMatcher = java.util.regex.Pattern.compile("42").matcher("[42]");
        var pcre4jMatcher = Pattern.compile("42").matcher("[42]");

        javaMatcher.region(1, 3);
        pcre4jMatcher.region(1, 3);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
    }

    @Test
    void matchesFalseRegion() {
        var javaMatcher = java.util.regex.Pattern.compile("42").matcher("[42!]");
        var pcre4jMatcher = Pattern.compile("42").matcher("[42!]");

        javaMatcher.region(1, 4);
        pcre4jMatcher.region(1, 4);

        assertEquals(javaMatcher.matches(), pcre4jMatcher.matches());
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
    }

    @Test
    void findTrue() {
        var javaMatcher = java.util.regex.Pattern.compile("42").matcher("42!");
        var pcre4jMatcher = Pattern.compile("42").matcher("42!");

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
    }

    @Test
    void findFalse() {
        var javaMatcher = java.util.regex.Pattern.compile("42!").matcher("42");
        var pcre4jMatcher = Pattern.compile("42!").matcher("42");

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
    }

    @Test
    void findTrueInRegion() {
        var javaMatcher = java.util.regex.Pattern.compile("42").matcher("[42]");
        var pcre4jMatcher = Pattern.compile("42").matcher("[42]");

        javaMatcher.region(1, 3);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
    }

    @Test
    void findFalseInRegion() {
        var javaMatcher = java.util.regex.Pattern.compile("42!").matcher("[42]");
        var pcre4jMatcher = Pattern.compile("42!").matcher("[42]");

        javaMatcher.region(1, 3);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
    }

    @Test
    void findTrueAtOffset() {
        var javaMatcher = java.util.regex.Pattern.compile("42").matcher("!!42");
        var pcre4jMatcher = Pattern.compile("42").matcher("!!42");

        assertEquals(javaMatcher.find(2), pcre4jMatcher.find(2));
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
    }

    @Test
    void findFalseAtOffset() {
        var javaMatcher = java.util.regex.Pattern.compile("42").matcher("!!test");
        var pcre4jMatcher = Pattern.compile("42").matcher("!!test");

        assertEquals(javaMatcher.find(2), pcre4jMatcher.find(2));
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
    }

    @Test
    void findMultiple() {
        var javaMatcher = java.util.regex.Pattern.compile("42").matcher("42!42");
        var pcre4jMatcher = Pattern.compile("42").matcher("42!42");

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
    }

    @Test
    void findMultipleWithinRegion() {
        var javaMatcher = java.util.regex.Pattern.compile("42").matcher("42!42!42!42");
        var pcre4jMatcher = Pattern.compile("42").matcher("42!42!42!42");

        javaMatcher.region(2, 8);
        pcre4jMatcher.region(2, 8);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
    }

    @Test
    void findMultipleOutsideRegion() {
        var javaMatcher = java.util.regex.Pattern.compile("42").matcher("42!__!__!42");
        var pcre4jMatcher = Pattern.compile("42").matcher("42!__!__!42");

        javaMatcher.region(2, 8);
        pcre4jMatcher.region(2, 8);

        assertEquals(javaMatcher.find(), pcre4jMatcher.find());
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
    }

    @Test
    void captureGroups() {
        var javaMatcher = java.util.regex.Pattern.compile("(?<four>4)(.*)(?<two>2)").matcher("4test2");
        var pcre4jMatcher = Pattern.compile("(?<four>4)(.*)(?<two>2)").matcher("4test2");

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
