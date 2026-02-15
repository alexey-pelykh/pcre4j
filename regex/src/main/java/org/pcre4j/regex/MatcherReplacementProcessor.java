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

import java.util.Map;
import java.util.regex.MatchResult;

/**
 * Processes replacement strings for {@link Matcher}'s replacement operations.
 * <p>
 * Handles group references ({@code $1}, {@code ${1}}, {@code ${name}}) and escape
 * sequences ({@code \\}, {@code \$}) in replacement strings, as specified by
 * {@link java.util.regex.Matcher#appendReplacement}.
 */
/* package-private */ final class MatcherReplacementProcessor {

    private MatcherReplacementProcessor() {
        // Utility class
    }

    /**
     * Process the replacement string and append to the given Appendable.
     * Handles group references: $1, ${1}, ${name}
     *
     * @param sb the target appendable
     * @param replacement the replacement string containing group references and escape sequences
     * @param matchResult the current match result for resolving group references
     * @param groupNameToIndex mapping of group names to group indices
     */
    static void appendReplacement(Appendable sb, String replacement, MatchResult matchResult,
            Map<String, Integer> groupNameToIndex) {
        int cursor = 0;
        final int len = replacement.length();

        try {
            while (cursor < len) {
                char c = replacement.charAt(cursor);
                if (c == '\\') {
                    cursor++;
                    if (cursor >= len) {
                        throw new IllegalArgumentException(
                                "Illegal escape sequence at end of replacement string"
                        );
                    }
                    sb.append(replacement.charAt(cursor));
                    cursor++;
                } else if (c == '$') {
                    cursor++;
                    if (cursor >= len) {
                        throw new IllegalArgumentException(
                                "Illegal group reference at end of replacement string"
                        );
                    }
                    c = replacement.charAt(cursor);
                    if (c == '{') {
                        // Named or numbered group reference: ${name} or ${number}
                        cursor++;
                        int start = cursor;
                        while (cursor < len && replacement.charAt(cursor) != '}') {
                            cursor++;
                        }
                        if (cursor >= len) {
                            throw new IllegalArgumentException("Unclosed group reference");
                        }
                        final String groupRef = replacement.substring(start, cursor);
                        cursor++; // skip '}'
                        if (groupRef.isEmpty()) {
                            throw new IllegalArgumentException("Empty group reference");
                        }
                        // Try to parse as number first
                        String groupValue;
                        if (Character.isDigit(groupRef.charAt(0))) {
                            int groupNum = Integer.parseInt(groupRef);
                            if (groupNum > matchResult.groupCount()) {
                                throw new IndexOutOfBoundsException("No group " + groupNum);
                            }
                            groupValue = matchResult.group(groupNum);
                        } else {
                            final var groupIndex = groupNameToIndex.get(groupRef);
                            if (groupIndex == null) {
                                throw new IllegalArgumentException(
                                        "No group with name <" + groupRef + ">"
                                );
                            }
                            groupValue = matchResult.group(groupIndex);
                        }
                        if (groupValue != null) {
                            sb.append(groupValue);
                        }
                    } else if (Character.isDigit(c)) {
                        // Numbered group reference: $1, $12, etc.
                        int groupNum = c - '0';
                        cursor++;
                        // Greedily consume more digits to get the full group number
                        // but only if the resulting number is a valid group
                        while (cursor < len) {
                            char nextChar = replacement.charAt(cursor);
                            if (!Character.isDigit(nextChar)) {
                                break;
                            }
                            int nextGroupNum = groupNum * 10 + (nextChar - '0');
                            if (nextGroupNum > matchResult.groupCount()) {
                                break;
                            }
                            groupNum = nextGroupNum;
                            cursor++;
                        }
                        if (groupNum > matchResult.groupCount()) {
                            throw new IndexOutOfBoundsException("No group " + groupNum);
                        }
                        String groupValue = matchResult.group(groupNum);
                        if (groupValue != null) {
                            sb.append(groupValue);
                        }
                    } else {
                        throw new IllegalArgumentException(
                                "Illegal group reference: character '" + c + "' after '$'"
                        );
                    }
                } else {
                    sb.append(c);
                    cursor++;
                }
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException("IOException during append", e);
        }
    }

    /**
     * Returns a literal replacement string for the specified string.
     * <p>
     * This method produces a string that can be used as a literal replacement in methods like
     * {@link Matcher#appendReplacement} and {@link Matcher#replaceAll}. The string produced will match the
     * original string if treated as a literal sequence.
     * <p>
     * Special characters {@code \} and {@code $} will be escaped by prepending a {@code \}.
     *
     * @param s the string to be literalized
     * @return a literal string replacement
     */
    static String quoteReplacement(String s) {
        if (s.indexOf('\\') == -1 && s.indexOf('$') == -1) {
            return s;
        }
        final var sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            final var c = s.charAt(i);
            if (c == '\\' || c == '$') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
