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
package org.pcre4j.compat.report;

import java.util.List;
import java.util.regex.Pattern;

public final class Classifier {

    private record Rule(Pattern p, String label) {}

    private static final List<Rule> RULES = List.of(
            new Rule(Pattern.compile("\\\\p\\{In\\w+\\}"), "block-property \\p{InXxx}"),
            new Rule(Pattern.compile("\\\\p\\{Is\\w+\\}"), "script-property \\p{IsXxx}"),
            new Rule(Pattern.compile("\\\\p\\{java\\w+\\}"), "\\p{javaXxx}"),
            new Rule(Pattern.compile("\\[[^\\]]*&&\\[[^\\]]*\\]\\]"), "character-class intersection [...&&[...]]"),
            new Rule(Pattern.compile("\\(\\?U\\)"), "(?U) inline UNICODE_CHARACTER_CLASS"),
            new Rule(Pattern.compile("\\\\R"), "\\R linebreak"),
            new Rule(Pattern.compile("\\\\X"), "\\X grapheme cluster"),
            new Rule(Pattern.compile("\\\\h|\\\\H|\\\\v|\\\\V"), "\\h \\H \\v \\V"),
            new Rule(Pattern.compile("\\(\\?<\\w+>"), "named group syntax"),
            new Rule(Pattern.compile("\\\\b\\{\\w+\\}"), "\\b{...} word boundary type")
    );

    private Classifier() {}

    public static String classify(String pattern) {
        if (pattern == null) return "unclassified";
        for (Rule r : RULES) {
            if (r.p().matcher(pattern).find()) return r.label();
        }
        return "unclassified";
    }
}
