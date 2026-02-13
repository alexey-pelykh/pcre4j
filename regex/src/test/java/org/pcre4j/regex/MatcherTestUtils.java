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

import java.util.regex.MatchResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Shared assertion helpers for {@link Matcher} tests.
 */
class MatcherTestUtils {

    static void assertMatcherState(java.util.regex.Matcher javaMatcher, Matcher pcre4jMatcher) {
        assertEquals(javaMatcher.start(), pcre4jMatcher.start());
        assertEquals(javaMatcher.end(), pcre4jMatcher.end());
        assertEquals(javaMatcher.group(), pcre4jMatcher.group());
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
    }

    static void assertMatchResultState(MatchResult javaMatchResult, MatchResult pcre4jMatchResult) {
        assertEquals(javaMatchResult.start(), pcre4jMatchResult.start());
        assertEquals(javaMatchResult.end(), pcre4jMatchResult.end());
        assertEquals(javaMatchResult.group(), pcre4jMatchResult.group());
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    static void assertNoMatchState(java.util.regex.Matcher javaMatcher, Matcher pcre4jMatcher) {
        assertThrows(IllegalStateException.class, javaMatcher::start);
        assertThrows(IllegalStateException.class, pcre4jMatcher::start);
        assertThrows(IllegalStateException.class, javaMatcher::end);
        assertThrows(IllegalStateException.class, pcre4jMatcher::end);
        assertThrows(IllegalStateException.class, javaMatcher::group);
        assertThrows(IllegalStateException.class, pcre4jMatcher::group);
        assertEquals(javaMatcher.groupCount(), pcre4jMatcher.groupCount());
    }

    static void assertNoMatchResultState(MatchResult javaMatchResult, MatchResult pcre4jMatchResult) {
        assertThrows(IllegalStateException.class, javaMatchResult::start);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::start);
        assertThrows(IllegalStateException.class, javaMatchResult::end);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::end);
        assertThrows(IllegalStateException.class, javaMatchResult::group);
        assertThrows(IllegalStateException.class, pcre4jMatchResult::group);
        assertEquals(javaMatchResult.groupCount(), pcre4jMatchResult.groupCount());
    }

    static void assertGroups(java.util.regex.Matcher javaMatcher, Matcher pcre4jMatcher) {
        for (var group = 1; group <= javaMatcher.groupCount(); group++) {
            assertEquals(javaMatcher.group(group), pcre4jMatcher.group(group));
            assertEquals(javaMatcher.start(group), pcre4jMatcher.start(group));
            assertEquals(javaMatcher.end(group), pcre4jMatcher.end(group));
        }
    }

    static void assertAppendReplacement(
            java.util.regex.Matcher javaMatcher,
            Matcher pcre4jMatcher,
            String replacement
    ) {
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
