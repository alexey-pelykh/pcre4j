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

import org.pcre4j.*;
import org.pcre4j.api.IPcre2;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

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

    /**
     * The position in the input from which to start the next append operation.
     * Used by {@link #appendReplacement} and {@link #appendTail}.
     */
    private int appendPos;

    /**
     * Whether the boundaries of this matcher's region are treated as anchors.
     * When true (default), {@code ^} and {@code $} match at region boundaries.
     * When false, they only match at the true start/end of input.
     */
    private boolean anchoringBounds = true;

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

    /**
     * Implements a non-terminal append-and-replace step.
     * <p>
     * This method performs the following actions:
     * <ol>
     * <li>It reads characters from the input sequence, starting at the append position, and appends them to the
     *     given string buffer. It stops after reading the last character preceding the previous match.</li>
     * <li>It appends the given replacement string to the string buffer.</li>
     * <li>It sets the append position of this matcher to the index of the last character matched, plus one.</li>
     * </ol>
     * <p>
     * The replacement string may contain references to captured subsequences: {@code $g} or {@code ${g}} refers to
     * capturing group {@code g}; {@code ${name}} refers to a named-capturing group. Use {@code \\} to include a
     * literal backslash and {@code \$} to include a literal dollar sign.
     *
     * @param sb          the target string buffer
     * @param replacement the replacement string
     * @return this matcher
     * @throws IllegalStateException if no match has yet been attempted, or if the previous match operation failed
     */
    public Matcher appendReplacement(StringBuffer sb, String replacement) {
        if (!hasMatch()) {
            throw new IllegalStateException("No match available");
        }
        // Append text between last append position and start of match
        sb.append(input, appendPos, start());
        // Process and append replacement string
        appendReplacementInternal(sb, replacement);
        // Update append position to end of current match
        appendPos = end();
        return this;
    }

    /**
     * Implements a non-terminal append-and-replace step.
     * <p>
     * This method performs the following actions:
     * <ol>
     * <li>It reads characters from the input sequence, starting at the append position, and appends them to the
     *     given string builder. It stops after reading the last character preceding the previous match.</li>
     * <li>It appends the given replacement string to the string builder.</li>
     * <li>It sets the append position of this matcher to the index of the last character matched, plus one.</li>
     * </ol>
     * <p>
     * The replacement string may contain references to captured subsequences: {@code $g} or {@code ${g}} refers to
     * capturing group {@code g}; {@code ${name}} refers to a named-capturing group. Use {@code \\} to include a
     * literal backslash and {@code \$} to include a literal dollar sign.
     *
     * @param sb          the target string builder
     * @param replacement the replacement string
     * @return this matcher
     * @throws IllegalStateException if no match has yet been attempted, or if the previous match operation failed
     */
    public Matcher appendReplacement(StringBuilder sb, String replacement) {
        if (!hasMatch()) {
            throw new IllegalStateException("No match available");
        }
        // Append text between last append position and start of match
        sb.append(input, appendPos, start());
        // Process and append replacement string
        appendReplacementInternal(sb, replacement);
        // Update append position to end of current match
        appendPos = end();
        return this;
    }

    /**
     * Process the replacement string and append to the given Appendable.
     * Handles group references: $1, ${1}, ${name}
     */
    private void appendReplacementInternal(Appendable sb, String replacement) {
        int cursor = 0;
        final int len = replacement.length();

        try {
            while (cursor < len) {
                char c = replacement.charAt(cursor);
                if (c == '\\') {
                    cursor++;
                    if (cursor >= len) {
                        throw new IllegalArgumentException("Illegal escape sequence at end of replacement string");
                    }
                    sb.append(replacement.charAt(cursor));
                    cursor++;
                } else if (c == '$') {
                    cursor++;
                    if (cursor >= len) {
                        throw new IllegalArgumentException("Illegal group reference at end of replacement string");
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
                            if (groupNum > groupCount()) {
                                throw new IndexOutOfBoundsException("No group " + groupNum);
                            }
                            groupValue = group(groupNum);
                        } else {
                            groupValue = group(groupRef);
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
                            if (nextGroupNum > groupCount()) {
                                break;
                            }
                            groupNum = nextGroupNum;
                            cursor++;
                        }
                        if (groupNum > groupCount()) {
                            throw new IndexOutOfBoundsException("No group " + groupNum);
                        }
                        String groupValue = group(groupNum);
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
     * Implements a terminal append-and-replace step.
     * <p>
     * This method reads characters from the input sequence, starting at the append position, and appends them
     * to the given string buffer. It is intended to be invoked after one or more invocations of the
     * {@link #appendReplacement(StringBuffer, String)} method to copy the remainder of the input sequence.
     *
     * @param sb the target string buffer
     * @return the target string buffer
     */
    public StringBuffer appendTail(StringBuffer sb) {
        sb.append(input, appendPos, regionEnd);
        return sb;
    }

    /**
     * Implements a terminal append-and-replace step.
     * <p>
     * This method reads characters from the input sequence, starting at the append position, and appends them
     * to the given string builder. It is intended to be invoked after one or more invocations of the
     * {@link #appendReplacement(StringBuilder, String)} method to copy the remainder of the input sequence.
     *
     * @param sb the target string builder
     * @return the target string builder
     */
    public StringBuilder appendTail(StringBuilder sb) {
        sb.append(input, appendPos, regionEnd);
        return sb;
    }

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

        if (start > regionEnd) {
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

    /**
     * Queries the anchoring of region bounds for this matcher.
     * <p>
     * This method returns {@code true} if this matcher uses <i>anchoring</i> bounds, {@code false} otherwise.
     * <p>
     * By default, a matcher uses anchoring bounds.
     *
     * @return {@code true} if this matcher is using anchoring bounds, {@code false} otherwise
     * @see #useAnchoringBounds(boolean)
     */
    public boolean hasAnchoringBounds() {
        return anchoringBounds;
    }

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

        // Apply anchoring bounds options
        matchOptions.addAll(getMatchOptions());

        final var regionSubject = getRegionSubject(regionStart);
        final var matchData = new Pcre2MatchData(lookingAtCode);
        final var result = lookingAtCode.match(
                regionSubject.subject(),
                regionSubject.startOffset(),
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

        processMatchResult(matchData, regionSubject);
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

        // Apply anchoring bounds options
        matchOptions.addAll(getMatchOptions());

        final var regionSubject = getRegionSubject(regionStart);
        final var matchData = new Pcre2MatchData(matchingCode);
        final var result = matchingCode.match(
                regionSubject.subject(),
                regionSubject.startOffset(),
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

        processMatchResult(matchData, regionSubject);
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

    /**
     * Returns a literal replacement string for the specified string.
     * <p>
     * This method produces a string that can be used as a literal replacement in methods like
     * {@link #appendReplacement} and {@link #replaceAll}. The string produced will match the
     * original string if treated as a literal sequence.
     * <p>
     * Special characters {@code \} and {@code $} will be escaped by prepending a {@code \}.
     *
     * @param s the string to be literalized
     * @return a literal string replacement
     */
    public static String quoteReplacement(String s) {
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

    /**
     * Replaces every subsequence of the input sequence that matches the pattern with the given replacement string.
     * <p>
     * This method first resets the matcher, then scans through the input sequence looking for matches. Characters
     * that are not part of any match are left unchanged; each match is replaced with the replacement string.
     * <p>
     * The replacement string may contain references to captured subsequences: {@code $g} or {@code ${g}} refers to
     * capturing group {@code g}; {@code ${name}} refers to a named-capturing group. Use {@code \\} to include a
     * literal backslash and {@code \$} to include a literal dollar sign.
     *
     * @param replacement the replacement string
     * @return the string resulting from replacing every match with the replacement string
     */
    public String replaceAll(String replacement) {
        reset();
        return pattern.code.substitute(
                input.substring(regionStart, regionEnd),
                0,
                EnumSet.of(Pcre2SubstituteOption.GLOBAL, Pcre2SubstituteOption.EXTENDED),
                null,
                matchContext,
                replacement
        );
    }

    /**
     * Replaces every subsequence of the input sequence that matches the pattern with the result of applying
     * the given replacer function to the match result.
     * <p>
     * This method first resets the matcher. Each match is replaced with the result of calling
     * {@code replacer.apply(this)} with the matcher positioned at the match.
     *
     * @param replacer the function to apply to each match
     * @return the string resulting from replacing every match with the replacer function's result
     * @throws NullPointerException if replacer is null
     */
    public String replaceAll(Function<java.util.regex.MatchResult, String> replacer) {
        if (replacer == null) {
            throw new NullPointerException("replacer");
        }
        reset();
        final var sb = new StringBuilder();
        while (find()) {
            appendReplacement(sb, replacer.apply(this));
        }
        appendTail(sb);
        return sb.toString();
    }

    /**
     * Replaces the first subsequence of the input sequence that matches the pattern with the given replacement string.
     * <p>
     * This method first resets the matcher. If the pattern matches, the first match is replaced with the
     * replacement string. Characters that are not part of the match are left unchanged.
     * <p>
     * The replacement string may contain references to captured subsequences: {@code $g} or {@code ${g}} refers to
     * capturing group {@code g}; {@code ${name}} refers to a named-capturing group. Use {@code \\} to include a
     * literal backslash and {@code \$} to include a literal dollar sign.
     *
     * @param replacement the replacement string
     * @return the string resulting from replacing the first match with the replacement string
     */
    public String replaceFirst(String replacement) {
        reset();
        return pattern.code.substitute(
                input.substring(regionStart, regionEnd),
                0,
                EnumSet.of(Pcre2SubstituteOption.EXTENDED),
                null,
                matchContext,
                replacement
        );
    }

    /**
     * Replaces the first subsequence of the input sequence that matches the pattern with the result of applying
     * the given replacer function to the match result.
     * <p>
     * This method first resets the matcher. If the pattern matches, the first match is replaced with the result
     * of calling {@code replacer.apply(this)} with the matcher positioned at the match.
     *
     * @param replacer the function to apply to the match
     * @return the string resulting from replacing the first match with the replacer function's result
     * @throws NullPointerException if replacer is null
     */
    public String replaceFirst(Function<java.util.regex.MatchResult, String> replacer) {
        if (replacer == null) {
            throw new NullPointerException("replacer");
        }
        reset();
        if (!find()) {
            return input.substring(regionStart, regionEnd);
        }
        final var sb = new StringBuilder();
        appendReplacement(sb, replacer.apply(this));
        appendTail(sb);
        return sb.toString();
    }

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
        appendPos = 0;
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

    /**
     * Returns a stream of match results for each subsequence of the input sequence that matches the pattern.
     * <p>
     * The match results occur in the same order as the matching subsequences in the input sequence.
     * <p>
     * Each match result is produced as if by {@link #toMatchResult()}.
     * <p>
     * This method does not reset this matcher. Matching starts on initiation of the terminal stream operation
     * either at the beginning of this matcher's region, or, if the matcher has not since been reset, at the
     * first character not matched by a previous match.
     * <p>
     * If the match results are used after the matcher has been modified (via {@link #find()}, {@link #reset()},
     * etc.), the behavior is undefined.
     *
     * @return a sequential stream of match results
     * @since 9
     */
    public Stream<java.util.regex.MatchResult> results() {
        return Stream.iterate(
                find() ? toMatchResult() : null,
                Objects::nonNull,
                mr -> find() ? toMatchResult() : null
        );
    }

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

    /**
     * Sets the anchoring of region bounds for this matcher.
     * <p>
     * Invoking this method with an argument of {@code true} will set this matcher to use <i>anchoring</i> bounds.
     * If the boolean argument is {@code false}, then <i>non-anchoring</i> bounds will be used.
     * <p>
     * Using anchoring bounds, the boundaries of this matcher's region match anchors such as {@code ^} and {@code $}.
     * <p>
     * Without anchoring bounds, the boundaries of this matcher's region will not match anchors such as {@code ^}
     * and {@code $}.
     * <p>
     * By default, a matcher uses anchoring bounds.
     *
     * @param b a boolean indicating whether or not to use anchoring bounds
     * @return this matcher
     * @see #hasAnchoringBounds()
     */
    public Matcher useAnchoringBounds(boolean b) {
        anchoringBounds = b;
        return this;
    }

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
        final var regionSubject = getRegionSubject(start);
        final var matchData = new Pcre2MatchData(pattern.code);
        final var result = pattern.code.match(
                regionSubject.subject(),
                regionSubject.startOffset(),
                getMatchOptions(),
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

        processMatchResult(matchData, regionSubject);
        return true;
    }

    /**
     * Get the match options based on the current anchoring bounds setting.
     *
     * @return the set of match options to use
     */
    private EnumSet<Pcre2MatchOption> getMatchOptions() {
        final var options = EnumSet.noneOf(Pcre2MatchOption.class);
        if (!anchoringBounds) {
            if (regionStart > 0) {
                options.add(Pcre2MatchOption.NOTBOL);
            }
            if (regionEnd < input.length()) {
                options.add(Pcre2MatchOption.NOTEOL);
            }
        }
        return options;
    }

    /**
     * Holds the subject string and coordinate mapping for region-aware matching.
     * <p>
     * When matching with a region, PCRE2 needs to receive only the region substring
     * so that word boundaries (\b) don't see outside the region. This matches Java's
     * default behavior where transparent bounds are disabled. The indexAdjustment is
     * used to convert match indices back to the full input coordinate space.
     *
     * @param subject the subject string to match against
     * @param startOffset the offset within subject to start matching
     * @param indexAdjustment the value to add to match indices to convert to full input coordinates
     */
    private record RegionSubject(String subject, int startOffset, int indexAdjustment) {}

    /**
     * Creates a RegionSubject for matching, handling region boundaries.
     * <p>
     * Always uses only the region substring so that \b (word boundary) doesn't see outside the region.
     * This matches Java's default behavior where transparent bounds are disabled.
     * The difference between anchoring enabled/disabled is handled by NOTBOL/NOTEOL flags.
     *
     * @param matchStartInInput the start position for matching in input coordinates
     * @return the RegionSubject containing the subject string and coordinate mapping
     */
    private RegionSubject getRegionSubject(int matchStartInInput) {
        if (regionStart > 0) {
            return new RegionSubject(
                    input.substring(regionStart, regionEnd),
                    matchStartInInput - regionStart,
                    regionStart
            );
        } else {
            return new RegionSubject(
                    input.subSequence(0, regionEnd).toString(),
                    matchStartInInput,
                    0
            );
        }
    }

    /**
     * Process match results: convert ovector to string indices and adjust for region offset.
     *
     * @param matchData the match data containing the ovector
     * @param regionSubject the region subject used for matching
     */
    private void processMatchResult(Pcre2MatchData matchData, RegionSubject regionSubject) {
        lastMatchData = matchData;
        final var subjectBytes = regionSubject.subject().getBytes(StandardCharsets.UTF_8);
        lastMatchIndices = Pcre4jUtils.convertOvectorToStringIndices(
                regionSubject.subject(), subjectBytes, matchData.ovector()
        );

        // Adjust indices back to full input coordinate space
        if (regionSubject.indexAdjustment() > 0) {
            for (int i = 0; i < lastMatchIndices.length; i++) {
                if (lastMatchIndices[i] >= 0) {
                    lastMatchIndices[i] += regionSubject.indexAdjustment();
                }
            }
        }
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
