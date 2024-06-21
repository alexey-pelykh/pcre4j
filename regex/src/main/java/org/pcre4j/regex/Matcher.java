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

import org.pcre4j.Pcre2MatchData;
import org.pcre4j.Pcre2MatchOption;
import org.pcre4j.Pcre4jUtils;
import org.pcre4j.api.IPcre2;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

/**
 * Performs match operations on a character sequence by interpreting a {@link Pattern} using the PCRE library yet aims
 * to have a {@link java.util.regex.Matcher}-alike API
 */
public class Matcher implements java.util.regex.MatchResult {

    /**
     * The pattern that this matcher used to match the input against
     */
    private Pattern pattern;

    /**
     * A map of group names to group indices
     */
    private Map<String, Integer> groupNameToIndex;

    /**
     * The input character sequence that this matcher uses to match against the pattern
     */
    private CharSequence input;

    /**
     * The start index of the region (inclusive) that this matcher uses to match against the pattern
     */
    private int regionStart;

    /**
     * The end index of the region (exclusive) that this matcher uses to match against the pattern
     */
    private int regionEnd;

    /**
     * The current match data
     */
    private Pcre2MatchData lastMatchData;

    /**
     * The current match verctor
     */
    private Pcre2MatchData.OffsetPair lastMatch[];

    /* package-private */ Matcher(Pattern pattern, CharSequence input) {
        this.pattern = pattern;
        this.groupNameToIndex = pattern.namedGroups();
        this.input = input;
        reset();
    }

    // TODO: appendReplacement(StringBuffer sb, String replacement)

    // TODO: appendReplacement(StringBuilder sb, String replacement)

    // TODO: appendTail(StringBuffer sb)

    // TODO: appendTail(StringBuilder sb)

    /**
     * Returns the end index of the most recent match
     *
     * @return the end index of the most recent match
     */
    @Override
    public int end() {
        if (!hasMatch()) {
            throw new IllegalStateException("No match found");
        }

        return lastMatch[0].end();
    }

    /**
     * Returns the end index of the specified capturing group in the most recent match
     *
     * @param group the capturing group index
     * @return the end index of the specified capturing group in the most recent match
     */
    @Override
    public int end(int group) {
        if (!hasMatch()) {
            throw new IllegalStateException("No match found");
        }
        if (group < 0 || group > groupCount()) {
            throw new IndexOutOfBoundsException("No such group: " + group);
        }

        return lastMatch[group].end();
    }

    /**
     * Returns the end index of the specified capturing group in the most recent match
     *
     * @param name the capturing group name
     * @return the end index of the specified capturing group in the most recent match
     */
    @Override
    public int end(String name) {
        if (!hasMatch()) {
            throw new IllegalStateException("No match found");
        }
        final var group = groupNameToIndex.get(name);
        if (group == null) {
            throw new IllegalArgumentException("No group with name <" + name + ">");
        }

        return lastMatch[group].end();
    }

    /**
     * Find the next pattern match in the input.
     *
     * @return {@code true} if a match is found, otherwise {@code false}
     */
    public boolean find() {
        var start = 0;

        if (lastMatch != null) {
            start = lastMatch[0].end();

            if (start == lastMatch[0].start()) {
                start += 1;
            }
        }

        if (start < regionStart) {
            start = regionStart;
        }

        if (start >= regionEnd) {
            lastMatchData = null;
            lastMatch = null;
            return false;
        }

        return search(start);
    }

    /**
     * Resets this matcher and attempts to find the next pattern match in the input, starting at the specified index.
     *
     * @param start the index to start searching from
     * @return {@code true} if a match is found, otherwise {@code false}
     */
    public boolean find(int start) {
        int limit = input.length();
        if ((start < 0) || (start > limit))
            throw new IndexOutOfBoundsException("Illegal start index");
        reset();
        return search(start);
    }

    /**
     * Returns the input subsequence captured by the given group in the most recent match.
     *
     * @return the input subsequence captured by the given group in the most recent match
     */
    @Override
    public String group() {
        if (!hasMatch()) {
            throw new IllegalStateException("No match found");
        }

        return input.subSequence(lastMatch[0].start(), lastMatch[0].end()).toString();
    }

    /**
     * Returns the input subsequence captured by the given group in the most recent match.
     *
     * @param group the capturing group index
     * @return the input subsequence captured by the given group in the most recent match
     */
    @Override
    public String group(int group) {
        if (!hasMatch()) {
            throw new IllegalStateException("No match found");
        }
        if (group < 0 || group > groupCount()) {
            throw new IndexOutOfBoundsException("No such group: " + group);
        }

        return input.subSequence(lastMatch[group].start(), lastMatch[group].end()).toString();
    }

    /**
     * Returns the input subsequence captured by the given group in the most recent match.
     *
     * @param name the capturing group name
     * @return the input subsequence captured by the given group in the most recent match
     */
    @Override
    public String group(String name) {
        if (!hasMatch()) {
            throw new IllegalStateException("No match found");
        }
        final var group = groupNameToIndex.get(name);
        if (group == null) {
            throw new IllegalArgumentException("No group with name <" + name + ">");
        }

        return input.subSequence(lastMatch[group].start(), lastMatch[group].end()).toString();
    }

    /**
     * Returns the number of capturing groups in this matcher's pattern.
     *
     * @return the number of capturing groups in this matcher's pattern
     */
    @Override
    public int groupCount() {
        return pattern.code.captureCount();
    }

    // TODO: hasAnchoringBounds()

    /**
     * Returns {@code true} if the matcher has found a match, otherwise {@code false}
     *
     * @return {@code true} if the matcher has found a match, otherwise {@code false}
     */
    @Override
    public boolean hasMatch() {
        return lastMatch != null;
    }

    // TODO: hasTransparentBounds()

    // TODO: hitEnd()

    /**
     * Attempts to match the input sequence, starting at the beginning of the region, against the pattern
     *
     * @return {@code true} if the input sequence region starts with the pattern, otherwise {@code false}
     */
    public boolean lookingAt() {
        final var matchData = new Pcre2MatchData(pattern.code);
        final var result = pattern.code.match(
                input.subSequence(0, regionEnd).toString(),
                regionStart,
                EnumSet.of(Pcre2MatchOption.ANCHORED),
                matchData,
                null
        );
        if (result < 1) {
            if (result == IPcre2.ERROR_NOMATCH) {
                return false;
            }

            final var errorMessage = Pcre4jUtils.getErrorMessage(pattern.code.api(), result);
            throw new RuntimeException("Failed to find an anchored match", new IllegalStateException(errorMessage));
        }

        this.lastMatchData = matchData;
        this.lastMatch = matchData.ovector();

        return true;
    }

    /**
     * Attempts to match the input sequence, from the start of the region till its end, against the pattern
     *
     * @return {@code true} if the entire input sequence region matches the pattern, otherwise {@code false}
     */
    public boolean matches() {
        final var matchData = new Pcre2MatchData(pattern.code);
        final var result = pattern.code.match(
                input.subSequence(0, regionEnd).toString(),
                regionStart,
                EnumSet.of(Pcre2MatchOption.ANCHORED, Pcre2MatchOption.ENDANCHORED),
                matchData,
                null
        );
        if (result < 1) {
            if (result == IPcre2.ERROR_NOMATCH) {
                return false;
            }

            final var errorMessage = Pcre4jUtils.getErrorMessage(pattern.code.api(), result);
            throw new RuntimeException("Failed to find an anchored match", new IllegalStateException(errorMessage));
        }

        this.lastMatchData = matchData;
        this.lastMatch = matchData.ovector();

        return true;
    }

    /**
     * Returns a map of named groups in the pattern.
     *
     * @return the map of named groups in the pattern
     */
    @Override
    public Map<String, Integer> namedGroups() {
        return groupNameToIndex;
    }

    /**
     * Returns the pattern that this matcher uses to match against the input
     *
     * @return the pattern that this matcher uses to match against the input
     */
    public Pattern pattern() {
        return pattern;
    }

    // TODO: quoteReplacement(String s)

    /**
     * Sets the region of the input that this matcher uses to match against the pattern
     *
     * @param start the start index of the region (inclusive)
     * @param end   the end index of the region (exclusive)
     * @return this matcher
     */
    public Matcher region(int start, int end) {
        if ((start < 0) || (start > input.length()))
            throw new IndexOutOfBoundsException("start");
        if ((end < 0) || (end > input.length()))
            throw new IndexOutOfBoundsException("end");
        if (start > end)
            throw new IndexOutOfBoundsException("start > end");
        reset();
        regionStart = start;
        regionEnd = end;
        return this;
    }

    /**
     * Returns the end index of the region (exclusive) that this matcher uses to match against the pattern
     *
     * @return the end index of the region (exclusive) that this matcher uses to match against the pattern
     */
    public int regionEnd() {
        return regionEnd;
    }

    /**
     * Returns the start index of the region (inclusive) that this matcher uses to match against the pattern
     *
     * @return the start index of the region (inclusive) that this matcher uses to match against the pattern
     */
    public int regionStart() {
        return regionStart;
    }

    // TODO: replaceAll(String replacement)

    // TODO: replaceAll(Function<MatchResult,String> replacer)

    // TODO: replaceFirst(String replacement)

    // TODO: replaceFirst(Function<MatchResult,String> replacer)

    // TODO: requireEnd()

    /**
     * Resets this matcher
     *
     * @return this matcher
     */
    public Matcher reset() {
        regionStart = 0;
        regionEnd = input.length();
        lastMatchData = null;
        lastMatch = null;
        return this;
    }

    /**
     * Resets this matcher with a new input character sequence
     *
     * @param input the new input character sequence
     * @return this matcher
     */
    public Matcher reset(CharSequence input) {
        this.input = input;
        return reset();
    }

    // TODO: results()

    /**
     * Returns the start index of the most recent match
     *
     * @return the start index of the most recent match
     */
    @Override
    public int start() {
        if (!hasMatch()) {
            throw new IllegalStateException("No match found");
        }

        return lastMatch[0].start();
    }

    /**
     * Returns the start index of the specified capturing group in the most recent match
     *
     * @param group the capturing group index
     * @return the start index of the specified capturing group in the most recent match
     */
    @Override
    public int start(int group) {
        if (!hasMatch()) {
            throw new IllegalStateException("No match found");
        }
        if (group < 0 || group > groupCount()) {
            throw new IndexOutOfBoundsException("No such group: " + group);
        }

        return lastMatch[group].start();
    }

    /**
     * Returns the start index of the specified capturing group in the most recent match
     *
     * @param name the capturing group name
     * @return the start index of the specified capturing group in the most recent match
     */
    @Override
    public int start(String name) {
        if (!hasMatch()) {
            throw new IllegalStateException("No match found");
        }
        final var group = groupNameToIndex.get(name);
        if (group == null) {
            throw new IllegalArgumentException("No group with name <" + name + ">");
        }

        return lastMatch[group].start();
    }

    /**
     * Returns a {@link MatchResult} with the frozen current state of the matcher will be detached from the matcher
     *
     * @return an immutable {@link MatchResult} with the frozen current state of the matcher
     */
    public MatchResult toMatchResult() {
        if (!hasMatch()) {
            return new MatchResult(
                    null,
                    null,
                    groupNameToIndex
            );
        }

        return new MatchResult(
                input.subSequence(lastMatch[0].start(), lastMatch[0].end()).toString(),
                Arrays.copyOf(lastMatch, lastMatch.length),
                groupNameToIndex
        );
    }

    @Override
    public String toString() {
        return Matcher.class.getName() +
                "[pattern=" + pattern +
                " region=" + regionStart + ',' + regionEnd +
                " lastMatch=" + Arrays.toString(lastMatch) +
                "]";
    }

    // TODO: useAnchoringBounds(boolean b)

    /**
     * Changes the pattern that this matcher uses to against the input
     *
     * @param newPattern the new pattern to use
     * @return this matcher
     */
    public Matcher usePattern(Pattern newPattern) {
        if (newPattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null");
        }
        this.pattern = newPattern;
        this.groupNameToIndex = newPattern.namedGroups();
        reset();
        return this;
    }

    // TODO: useTransparentBounds(boolean b)

    /**
     * Find next match of the pattern in the input starting from the specified index
     *
     * @param start the index to start searching from in the input
     * @return {@code true} if a match is found, otherwise {@code false}
     */
    private boolean search(int start) {
        final var matchData = new Pcre2MatchData(pattern.code);
        final var result = pattern.code.match(
                input.subSequence(0, regionEnd).toString(),
                start,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        if (result < 1) {
            if (result == IPcre2.ERROR_NOMATCH) {
                return false;
            }

            final var errorMessage = Pcre4jUtils.getErrorMessage(pattern.code.api(), result);
            throw new RuntimeException("Failed to find a match", new IllegalStateException(errorMessage));
        }

        lastMatchData = matchData;
        lastMatch = matchData.ovector();

        return true;
    }

    /**
     * An immutable match result
     */
    public static class MatchResult implements java.util.regex.MatchResult {

        private final CharSequence subsequence;
        private final Pcre2MatchData.OffsetPair match[];
        private final Map<String, Integer> groupNameToIndex;

        /* package-private */ MatchResult(
                CharSequence subsequence,
                Pcre2MatchData.OffsetPair match[],
                Map<String, Integer> groupNameToIndex
        ) {
            this.subsequence = subsequence;
            this.match = match;
            this.groupNameToIndex = groupNameToIndex;
        }

        @Override
        public int start() {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }

            return match[0].start();
        }

        @Override
        public int start(int group) {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }
            if (group < 0 || group > groupCount()) {
                throw new IndexOutOfBoundsException("No such group: " + group);
            }

            return match[group].start();
        }

        @Override
        public int start(String name) {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }
            final var group = groupNameToIndex.get(name);
            if (group == null) {
                throw new IllegalArgumentException("No group with name <" + name + ">");
            }

            return match[group].start();
        }

        @Override
        public int end() {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }

            return match[0].end();
        }

        @Override
        public int end(int group) {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }
            if (group < 0 || group > groupCount()) {
                throw new IndexOutOfBoundsException("No such group: " + group);
            }

            return match[group].end();
        }

        @Override
        public int end(String name) {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }
            final var group = groupNameToIndex.get(name);
            if (group == null) {
                throw new IllegalArgumentException("No group with name <" + name + ">");
            }

            return match[group].end();
        }

        @Override
        public String group() {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }

            return subsequence.toString();
        }

        @Override
        public String group(int group) {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }
            if (group < 0 || group > groupCount()) {
                throw new IndexOutOfBoundsException("No such group: " + group);
            }

            final var offset = match[0].start();
            return subsequence.subSequence(match[group].start() - offset, match[group].end() - offset).toString();
        }

        @Override
        public String group(String name) {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }
            final var group = groupNameToIndex.get(name);
            if (group == null) {
                throw new IllegalArgumentException("No group with name <" + name + ">");
            }

            final var offset = match[0].start();
            return subsequence.subSequence(match[group].start() - offset, match[group].end() - offset).toString();
        }

        @Override
        public int groupCount() {
            if (match == null) {
                return 0;
            }
            return match.length - 1;
        }

        @Override
        public Map<String, Integer> namedGroups() {
            return Map.copyOf(groupNameToIndex);
        }

        @Override
        public boolean hasMatch() {
            return match != null;
        }
    }
}
