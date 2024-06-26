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

import org.pcre4j.*;
import org.pcre4j.api.IPcre2;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

/**
 * Performs match operations on a character sequence by interpreting a {@link Pattern} using the PCRE library yet aims
 * to have a {@link java.util.regex.Matcher}-alike API
 */
public class Matcher implements java.util.regex.MatchResult {

    private final static long JIT_STACK_START_SIZE = 32 * 1024;
    private final static long JIT_STACK_MAX_SIZE = 512 * 1024;

    /**
     * The pattern that this matcher used to match the input against
     */
    private Pattern pattern;

    /**
     * The match context that this matcher uses to match against the pattern
     */
    private Pcre2MatchContext matchContext;

    /**
     * The JIT stack that this matcher uses to match against the pattern
     */
    private Pcre2JitStack jitStack;

    /**
     * A map of group names to group indices
     */
    private Map<String, Integer> groupNameToIndex;

    /**
     * The input string that this matcher uses to match against the pattern
     */
    private String input;

    /**
     * The input character sequence encoded in UTF-8
     */
    private byte[] inputBytes;

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
     * The current match string index pairs
     */
    private int[] lastMatchIndices;

    /* package-private */ Matcher(Pattern pattern, CharSequence input) {
        this.pattern = pattern;
        this.matchContext = new Pcre2MatchContext(pattern.code.api(), null);
        if (pattern.code.jitSize() > 0) {
            this.jitStack = new Pcre2JitStack(pattern.code.api(), JIT_STACK_START_SIZE, JIT_STACK_MAX_SIZE, null);
            this.matchContext.assignJitStack(jitStack);
        } else {
            this.jitStack = null;
        }
        this.groupNameToIndex = pattern.namedGroups();

        this.input = input.toString();
        this.inputBytes = this.input.getBytes(StandardCharsets.UTF_8);

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

        return lastMatchIndices[1];
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

        return lastMatchIndices[group * 2 + 1];
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

        return lastMatchIndices[group * 2 + 1];
    }

    /**
     * Find the next pattern match in the input.
     *
     * @return {@code true} if a match is found, otherwise {@code false}
     */
    public boolean find() {
        var start = 0;

        if (lastMatchIndices != null) {
            start = lastMatchIndices[1];

            if (start == lastMatchIndices[0]) {
                start += 1;
            }
        }

        if (start < regionStart) {
            start = regionStart;
        }

        if (start >= regionEnd) {
            lastMatchData = null;
            lastMatchIndices = null;
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

        return input.substring(lastMatchIndices[0], lastMatchIndices[1]);
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

        final var since = lastMatchIndices[group * 2];
        final var until = lastMatchIndices[group * 2 + 1];
        if (since == -1 && until == -1) {
            return null;
        }
        return input.substring(since, until);
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

        final var since = lastMatchIndices[group * 2];
        final var until = lastMatchIndices[group * 2 + 1];
        if (since == -1 && until == -1) {
            return null;
        }
        return input.substring(since, until);
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
        return lastMatchData != null;
    }

    // TODO: hasTransparentBounds()

    // TODO: hitEnd()

    /**
     * Attempts to match the input sequence, starting at the beginning of the region, against the pattern
     *
     * @return {@code true} if the input sequence region starts with the pattern, otherwise {@code false}
     */
    public boolean lookingAt() {
        final EnumSet<Pcre2MatchOption> matchOptions;
        final Pcre2Code lookingAtCode;
        if (pattern.lookingAtCode != null) {
            lookingAtCode = pattern.lookingAtCode;
            matchOptions = EnumSet.noneOf(Pcre2MatchOption.class);
        } else {
            lookingAtCode = pattern.code;
            matchOptions = EnumSet.of(Pcre2MatchOption.ANCHORED);
        }

        final var matchData = new Pcre2MatchData(lookingAtCode);
        final var result = lookingAtCode.match(
                input.subSequence(0, regionEnd).toString(),
                regionStart,
                matchOptions,
                matchData,
                matchContext
        );
        if (result < 1) {
            if (result == IPcre2.ERROR_NOMATCH) {
                return false;
            }

            final var errorMessage = Pcre4jUtils.getErrorMessage(pattern.lookingAtCode.api(), result);
            throw new RuntimeException("Failed to find an anchored match", new IllegalStateException(errorMessage));
        }

        lastMatchData = matchData;
        lastMatchIndices = Pcre4jUtils.convertOvectorToStringIndices(input, inputBytes, matchData.ovector());

        return true;
    }

    /**
     * Attempts to match the input sequence, from the start of the region till its end, against the pattern
     *
     * @return {@code true} if the entire input sequence region matches the pattern, otherwise {@code false}
     */
    public boolean matches() {
        final Pcre2Code matchingCode;
        final EnumSet<Pcre2MatchOption> matchOptions;
        if (pattern.matchingCode != null) {
            matchingCode = pattern.matchingCode;
            matchOptions = EnumSet.noneOf(Pcre2MatchOption.class);
        } else {
            matchingCode = pattern.code;
            matchOptions = EnumSet.of(Pcre2MatchOption.ANCHORED, Pcre2MatchOption.ENDANCHORED);
        }

        final var matchData = new Pcre2MatchData(matchingCode);
        final var result = matchingCode.match(
                input.subSequence(0, regionEnd).toString(),
                regionStart,
                matchOptions,
                matchData,
                matchContext
        );
        if (result < 1) {
            if (result == IPcre2.ERROR_NOMATCH) {
                return false;
            }

            final var errorMessage = Pcre4jUtils.getErrorMessage(pattern.matchingCode.api(), result);
            throw new RuntimeException("Failed to find an anchored match", new IllegalStateException(errorMessage));
        }

        lastMatchData = matchData;
        lastMatchIndices = Pcre4jUtils.convertOvectorToStringIndices(input, inputBytes, matchData.ovector());

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
        lastMatchIndices = null;
        return this;
    }

    /**
     * Resets this matcher with a new input character sequence
     *
     * @param input the new input character sequence
     * @return this matcher
     */
    public Matcher reset(CharSequence input) {
        this.input = input.toString();
        this.inputBytes = this.input.getBytes(StandardCharsets.UTF_8);
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

        return lastMatchIndices[0];
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

        return lastMatchIndices[group * 2];
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

        return lastMatchIndices[group * 2];
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
                input.substring(lastMatchIndices[0], lastMatchIndices[1]),
                Arrays.copyOf(lastMatchIndices, lastMatchIndices.length),
                groupNameToIndex
        );
    }

    @Override
    public String toString() {
        return Matcher.class.getName() +
                "[pattern=" + pattern +
                " region=" + regionStart + ',' + regionEnd +
                " lastMatchIndices=" + Arrays.toString(lastMatchIndices) +
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
        this.matchContext = new Pcre2MatchContext(pattern.code.api(), null);
        if (pattern.code.jitSize() > 0) {
            this.jitStack = new Pcre2JitStack(pattern.code.api(), JIT_STACK_START_SIZE, JIT_STACK_MAX_SIZE, null);
            this.matchContext.assignJitStack(jitStack);
        } else {
            this.jitStack = null;
        }
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
                matchContext
        );
        if (result < 1) {
            if (result == IPcre2.ERROR_NOMATCH) {
                return false;
            }

            final var errorMessage = Pcre4jUtils.getErrorMessage(pattern.code.api(), result);
            throw new RuntimeException("Failed to find a match", new IllegalStateException(errorMessage));
        }

        lastMatchData = matchData;
        lastMatchIndices = Pcre4jUtils.convertOvectorToStringIndices(input, inputBytes, matchData.ovector());

        return true;
    }

    /**
     * An immutable match result
     */
    public static class MatchResult implements java.util.regex.MatchResult {

        private final String substring;
        private final int[] matchIndices;
        private final Map<String, Integer> groupNameToIndex;

        /* package-private */ MatchResult(String substring, int[] matchIndices, Map<String, Integer> groupNameToIndex) {
            this.substring = substring;
            this.matchIndices = matchIndices;
            this.groupNameToIndex = groupNameToIndex;
        }

        @Override
        public int start() {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }

            return matchIndices[0];
        }

        @Override
        public int start(int group) {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }
            if (group < 0 || group > groupCount()) {
                throw new IndexOutOfBoundsException("No such group: " + group);
            }

            return matchIndices[group * 2];
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

            return matchIndices[group * 2];
        }

        @Override
        public int end() {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }

            return matchIndices[1];
        }

        @Override
        public int end(int group) {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }
            if (group < 0 || group > groupCount()) {
                throw new IndexOutOfBoundsException("No such group: " + group);
            }

            return matchIndices[group * 2 + 1];
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

            return matchIndices[group * 2 + 1];
        }

        @Override
        public String group() {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }

            return substring;
        }

        @Override
        public String group(int group) {
            if (!hasMatch()) {
                throw new IllegalStateException("No match found");
            }
            if (group < 0 || group > groupCount()) {
                throw new IndexOutOfBoundsException("No such group: " + group);
            }

            return substring.substring(
                    matchIndices[group * 2] - matchIndices[0],
                    matchIndices[group * 2 + 1] - matchIndices[0]
            );
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

            return substring.substring(
                    matchIndices[group * 2] - matchIndices[0],
                    matchIndices[group * 2 + 1] - matchIndices[0]
            );
        }

        @Override
        public int groupCount() {
            if (matchIndices == null) {
                return 0;
            }
            return matchIndices.length / 2 - 1;
        }

        @Override
        public Map<String, Integer> namedGroups() {
            return Map.copyOf(groupNameToIndex);
        }

        @Override
        public boolean hasMatch() {
            return matchIndices != null;
        }
    }
}
