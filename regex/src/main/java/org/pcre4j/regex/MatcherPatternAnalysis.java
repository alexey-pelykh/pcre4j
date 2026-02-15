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

/**
 * Static utility methods for analyzing and transforming regex patterns.
 * <p>
 * These methods support the {@link Matcher}'s anchoring bounds, hit-end, and require-end
 * semantics by analyzing pattern structure (anchors, quantifiers, character classes).
 * <p>
 * All methods are stateless and operate purely on pattern strings.
 */
/* package-private */ final class MatcherPatternAnalysis {

    private MatcherPatternAnalysis() {
        // Utility class
    }

    /**
     * Transforms a regex pattern for anchoring bounds mode.
     * <p>
     * Replaces {@code ^} with {@code \G} and removes {@code $} (outside character classes).
     * The {@code \G} assertion matches at startOffset (where matching begins), which is
     * what Java's {@code ^} does with anchoring bounds enabled. The {@code $} removal
     * is compensated by post-hoc verification that the match ends at regionEnd.
     * <p>
     * Handles POSIX character classes like {@code [[:alpha:]]} correctly by tracking
     * nested bracket depth.
     *
     * @param pattern the original pattern
     * @return the transformed pattern
     */
    static String transformPatternForAnchoringBounds(String pattern) {
        final var sb = new StringBuilder(pattern.length() + 10);
        int charClassDepth = 0;  // Track nested character class depth for POSIX classes
        boolean escaped = false;

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);

            if (escaped) {
                sb.append(c);
                escaped = false;
                continue;
            }

            if (c == '\\') {
                sb.append(c);
                escaped = true;
                continue;
            }

            // Handle POSIX character classes like [[:alpha:]] - they contain nested brackets
            // Also handles regular character classes
            if (c == '[') {
                charClassDepth++;
                sb.append(c);
                continue;
            }

            if (c == ']' && charClassDepth > 0) {
                charClassDepth--;
                sb.append(c);
                continue;
            }

            if (charClassDepth == 0) {
                if (c == '^') {
                    // Replace ^ with \G (matches at startOffset)
                    sb.append("\\G");
                    continue;
                }
                if (c == '$') {
                    // Remove $ (will verify match end position post-hoc)
                    continue;
                }
            }

            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * Checks if a regex pattern contains a $ anchor outside of character classes.
     * This is used to determine if the match end position must be verified post-hoc
     * when using the transformed pattern for anchoring bounds.
     *
     * @param pattern the pattern to check
     * @return true if the pattern contains a $ anchor outside character classes
     */
    static boolean patternContainsDollarAnchor(String pattern) {
        int charClassDepth = 0;
        boolean escaped = false;

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '[') {
                charClassDepth++;
                continue;
            }

            if (c == ']' && charClassDepth > 0) {
                charClassDepth--;
                continue;
            }

            if (charClassDepth == 0 && c == '$') {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a pattern can consume more input at the end of a match.
     * <p>
     * This returns true if the pattern ends with constructs that could consume
     * more input, such as:
     * <ul>
     *   <li>{@code +}, {@code *}, {@code ?} quantifiers</li>
     *   <li>{@code {n,}} or {@code {n,m}} quantifiers where the upper bound isn't reached</li>
     *   <li>Character classes {@code [...]}</li>
     *   <li>Dot {@code .}</li>
     *   <li>{@code \w}, {@code \d}, {@code \s} and similar character type escapes</li>
     * </ul>
     *
     * @param pattern the pattern to check
     * @return true if the pattern could consume more input at the end
     */
    static boolean patternCanConsumeMoreAtEnd(String pattern) {
        if (pattern.isEmpty()) {
            return false;
        }

        // Check the last character of the pattern to determine if it could consume more
        int i = pattern.length() - 1;

        while (i >= 0) {
            char c = pattern.charAt(i);

            // Check for quantifiers at the end
            if (c == '+' || c == '*' || c == '?' || c == '}') {
                return true; // Pattern has a quantifier that could consume more
            }

            // Check for character class end
            if (c == ']') {
                // Found end of character class without quantifier - matches exactly one character
                // A character class like [a-z] matches one char just like . or \w
                return false;
            }

            // Check for escape sequences
            if (i > 0 && pattern.charAt(i - 1) == '\\') {
                // Escape sequence at end
                // Character type escapes (\w, \d, \s, \W, \D, \S, etc.) can match multiple chars
                // when followed by a quantifier, but at the pattern end they match exactly one
                // However, \w matches one character and if input ends with matching char,
                // more matching chars could extend
                if (c == 'w' || c == 'W' || c == 'd' || c == 'D' || c == 's' || c == 'S') {
                    // These match a class of characters - similar to character class
                    // But without a quantifier, they match exactly one character
                    // Java seems to return hitEnd=false for these at end
                    return false;
                }
                return false;
            }

            // Check for dot (matches any character)
            if (c == '.') {
                // Dot at end without quantifier matches exactly one char
                return false;
            }

            // If we reach here with a normal character, the pattern ends with a literal
            // Literals match exactly, so no more input could extend the match
            return false;
        }

        return false;
    }

    /**
     * Checks if a regex pattern contains a "soft" end anchor ($ or \Z) outside of character classes.
     * These anchors can match before a final newline, meaning more input could invalidate a match.
     * <p>
     * Note: \z (lowercase) is not included because it only matches at the absolute end,
     * so more input cannot invalidate a match that used \z.
     *
     * @param pattern the pattern to check
     * @return true if the pattern contains $ or \Z outside character classes
     */
    static boolean patternContainsSoftEndAnchor(String pattern) {
        int charClassDepth = 0;
        boolean escaped = false;

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);

            if (escaped) {
                // Check for \Z (uppercase only - \z is absolute end, not soft)
                if (charClassDepth == 0 && c == 'Z') {
                    return true;
                }
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '[') {
                charClassDepth++;
                continue;
            }

            if (c == ']' && charClassDepth > 0) {
                charClassDepth--;
                continue;
            }

            if (charClassDepth == 0 && c == '$') {
                return true;
            }
        }

        return false;
    }
}
