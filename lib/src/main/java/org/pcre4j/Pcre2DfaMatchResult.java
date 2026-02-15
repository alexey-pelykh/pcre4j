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
package org.pcre4j;

/**
 * Result of a DFA (Deterministic Finite Automaton) match.
 * <p>
 * DFA matching returns all possible match lengths at the same starting position. The matches are ordered from longest
 * to shortest. Unlike standard NFA matching, DFA matching does not support capturing groups — only the overall match
 * boundaries are available.
 * <p>
 * For example, matching the pattern {@code ab(c(d)?)?} against the subject {@code "abcd"} at position 0 could return
 * matches ending at positions 4, 3, and 2 (for "abcd", "abc", and "ab" respectively).
 *
 * @param subject   the subject string that was matched
 * @param start     the start index (character offset) of the match in the subject string
 * @param ends      the end indices (character offsets, exclusive) of all possible matches, ordered from longest to
 *                  shortest
 * @param isPartial {@code true} if this is a partial match result
 */
public record Pcre2DfaMatchResult(String subject, int start, int[] ends, boolean isPartial) {

    /**
     * Get the number of alternative match lengths found.
     *
     * @return the number of matches
     */
    public int count() {
        return ends.length;
    }

    /**
     * Get the end index of the longest match (exclusive character offset).
     *
     * @return the end index of the longest match
     */
    public int longestEnd() {
        return ends[0];
    }

    /**
     * Get the end index of the shortest match (exclusive character offset).
     *
     * @return the end index of the shortest match
     */
    public int shortestEnd() {
        return ends[ends.length - 1];
    }

    /**
     * Get the longest matched substring.
     *
     * @return the longest matched substring
     */
    public String longestMatch() {
        return subject.substring(start, ends[0]);
    }

    /**
     * Get the shortest matched substring.
     *
     * @return the shortest matched substring
     */
    public String shortestMatch() {
        return subject.substring(start, ends[ends.length - 1]);
    }

    /**
     * Get the matched substring for the given match index.
     *
     * @param index the match index (0 = longest, count-1 = shortest)
     * @return the matched substring
     * @throws ArrayIndexOutOfBoundsException if the index is out of bounds
     */
    public String match(int index) {
        return subject.substring(start, ends[index]);
    }
}
