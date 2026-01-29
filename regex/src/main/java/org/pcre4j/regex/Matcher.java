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

    /**
     * Whether the boundaries of this matcher's region are transparent.
     * When true, lookahead and lookbehind can see beyond region boundaries.
     * When false (default), lookaround cannot see outside the region.
     */
    private boolean transparentBounds = false;

    /**
     * Whether the end of input was hit by the search engine in the last match operation.
     * When true, it is possible that more input would have changed the result of the last search.
     */
    private boolean hitEnd = false;

    /**
     * Whether more input could change a positive match into a negative one.
     * When true, and a match was found, more input could cause the match to be lost.
     */
    private boolean requireEnd = false;

    /**
     * Lazily compiled code for anchoring bounds mode that transforms the pattern
     * to use \G instead of ^ and removes $ for post-hoc verification.
     * This is needed because PCRE2's ^ always matches at position 0, not at startOffset.
     */
    private Pcre2Code anchoringBoundsCode = null;

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

    /**
     * Queries the transparency of region bounds for this matcher.
     * <p>
     * This method returns {@code true} if this matcher uses <i>transparent</i> bounds, {@code false} otherwise.
     * <p>
     * By default, a matcher uses opaque region boundaries.
     *
     * @return {@code true} if this matcher is using transparent bounds, {@code false} otherwise
     * @see #useTransparentBounds(boolean)
     */
    public boolean hasTransparentBounds() {
        return transparentBounds;
    }

    /**
     * Returns true if the end of input was hit by the search engine in the last match operation
     * performed by this matcher.
     * <p>
     * When this method returns true, then it is possible that more input would have changed the
     * result of the last search.
     *
     * @return true if the end of input was hit in the last match; false otherwise
     */
    public boolean hitEnd() {
        return hitEnd;
    }

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
                updateHitEndRequireEnd(regionSubject, false, matchOptions);
                return false;
            }

            final var errorMessage = Pcre4jUtils.getErrorMessage(pattern.lookingAtCode.api(), result);
            throw new RuntimeException("Failed to find an anchored match", new IllegalStateException(errorMessage));
        }

        processMatchResult(matchData, regionSubject);
        updateHitEndRequireEnd(regionSubject, true, matchOptions);
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
        if (pattern.matchingCode != null && !transparentBounds) {
            // Use the pre-compiled JIT code with ANCHORED and ENDANCHORED baked in
            // but only when transparent bounds is disabled, because ENDANCHORED
            // would anchor to end of full input rather than regionEnd
            matchingCode = pattern.matchingCode;
            matchOptions = EnumSet.noneOf(Pcre2MatchOption.class);
        } else {
            matchingCode = pattern.code;
            // For transparent bounds, we can't use ENDANCHORED with full input because it would
            // anchor to end of input, not regionEnd. We'll manually verify match end instead.
            if (transparentBounds) {
                matchOptions = EnumSet.of(Pcre2MatchOption.ANCHORED);
            } else {
                matchOptions = EnumSet.of(Pcre2MatchOption.ANCHORED, Pcre2MatchOption.ENDANCHORED);
            }
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
                updateHitEndRequireEnd(regionSubject, false, matchOptions);
                return false;
            }

            final var errorMessage = Pcre4jUtils.getErrorMessage(
                    pattern.matchingCode != null ? pattern.matchingCode.api() : pattern.code.api(), result);
            throw new RuntimeException("Failed to find an anchored match", new IllegalStateException(errorMessage));
        }

        processMatchResult(matchData, regionSubject);

        // For transparent bounds, manually verify the match covers exactly the region
        if (transparentBounds) {
            if (lastMatchIndices[0] != regionStart || lastMatchIndices[1] != regionEnd) {
                // Match doesn't span exactly the region, try with constrained subject
                final var constrainedSubject = getConstrainedRegionSubject(regionStart);
                final var constrainedMatchData = new Pcre2MatchData(matchingCode);
                // Use ENDANCHORED for constrained subject since it ends at regionEnd
                final var constrainedOptions = EnumSet.copyOf(matchOptions);
                constrainedOptions.add(Pcre2MatchOption.ENDANCHORED);
                final var constrainedResult = matchingCode.match(
                        constrainedSubject.subject(),
                        constrainedSubject.startOffset(),
                        constrainedOptions,
                        constrainedMatchData,
                        matchContext
                );
                if (constrainedResult < 1) {
                    lastMatchData = null;
                    lastMatchIndices = null;
                    updateHitEndRequireEnd(constrainedSubject, false, constrainedOptions);
                    return false;
                }
                processMatchResult(constrainedMatchData, constrainedSubject);
                // Verify constrained match spans the region
                if (lastMatchIndices[0] != regionStart || lastMatchIndices[1] != regionEnd) {
                    lastMatchData = null;
                    lastMatchIndices = null;
                    updateHitEndRequireEnd(constrainedSubject, false, constrainedOptions);
                    return false;
                }
                updateHitEndRequireEnd(constrainedSubject, true, constrainedOptions);
                return true;
            }
        }

        updateHitEndRequireEnd(regionSubject, true, matchOptions);
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

    /**
     * Returns true if more input could change a positive match into a negative one.
     * <p>
     * If this method returns true, and a match was found, then more input could cause the match
     * to be lost. If this method returns false and a match was found, then more input might
     * change the match but the match won't be lost. If a match was not found, then requireEnd
     * has no meaning.
     *
     * @return true if more input could change a positive match into a negative one
     */
    public boolean requireEnd() {
        return requireEnd;
    }

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
        // Note: hitEnd and requireEnd are NOT reset by Java's Matcher.reset()
        // They persist across resets until a new match operation is performed
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

        // Clear cached transformed pattern since the pattern changed
        this.anchoringBoundsCode = null;

        reset();

        return this;
    }

    /**
     * Sets the transparency of region bounds for this matcher.
     * <p>
     * Invoking this method with an argument of {@code true} will set this matcher to use <i>transparent</i> bounds.
     * If the boolean argument is {@code false}, then <i>opaque</i> bounds will be used.
     * <p>
     * Using transparent bounds, the boundaries of this matcher's region are transparent to lookahead, lookbehind,
     * and boundary matching constructs. Those constructs can see beyond the boundaries of the region to see if a
     * match is appropriate.
     * <p>
     * Using opaque bounds, the boundaries of this matcher's region are opaque to lookahead, lookbehind, and
     * boundary matching constructs that may try to see beyond them. Those constructs cannot look past the boundaries
     * so they will fail to match anything outside of the region.
     * <p>
     * By default, a matcher uses opaque bounds.
     *
     * @param b a boolean indicating whether to use opaque or transparent regions
     * @return this matcher
     * @see #hasTransparentBounds()
     */
    public Matcher useTransparentBounds(boolean b) {
        transparentBounds = b;
        return this;
    }

    /**
     * Find next match of the pattern in the input starting from the specified index.
     * <p>
     * <b>Algorithm Overview:</b>
     * <ol>
     *   <li><b>Transparent + Anchoring bounds path</b>: When both are enabled and searching from
     *       regionStart, try a transformed pattern where ^ becomes \G (matches at startOffset)
     *       and $ is removed (verified post-hoc). This handles the case where PCRE2's ^ would
     *       match at position 0, not regionStart. If this path succeeds with match ending at
     *       regionEnd, return success. Otherwise fall through to normal matching.</li>
     *   <li><b>Normal matching</b>: Match using the original pattern against the region subject
     *       (full input for transparent bounds, region substring for opaque bounds).</li>
     *   <li><b>Region boundary enforcement</b>: For transparent bounds, if the match extends
     *       beyond regionEnd, retry with a constrained subject (truncated at regionEnd) to find
     *       a shorter valid match. If no valid match exists, advance searchStart and retry.</li>
     * </ol>
     *
     * @param start the index to start searching from in the input
     * @return {@code true} if a match is found, otherwise {@code false}
     */
    private boolean search(int start) {
        int searchStart = start;
        while (searchStart <= regionEnd) {
            final var regionSubject = getRegionSubject(searchStart);
            final var matchOptions = getMatchOptions();

            // PATH 1: Transparent + Anchoring bounds special handling
            //
            // Problem: PCRE2's ^ always matches at position 0, but Java's ^ with anchoring bounds
            // should match at regionStart. With transparent bounds we pass the full input, so ^
            // would incorrectly match at position 0 instead of regionStart.
            //
            // Solution: Transform pattern (^ -> \G, remove $) and verify $ constraint post-hoc.
            // \G matches at startOffset which is regionStart, achieving the correct behavior.
            //
            // Note: MULTILINE patterns are excluded because ^ should also match after newlines,
            // which \G cannot replicate.
            if (transparentBounds && anchoringBounds && searchStart == regionStart) {
                final var abCode = getOrCreateAnchoringBoundsCode();
                if (abCode != null) {
                    // Use the transformed pattern (^ replaced with \G, $ removed)
                    final var matchData = new Pcre2MatchData(abCode);
                    final var result = abCode.match(
                            input,
                            searchStart,
                            matchOptions,
                            matchData,
                            matchContext
                    );
                    if (result >= 1) {
                        // Process to get match indices
                        processMatchResult(matchData, new RegionSubject(input, searchStart, 0));

                        // Check if the original pattern contained $ anchor (outside character classes)
                        // If so, we must verify the match ends at regionEnd (simulates $ at regionEnd)
                        final boolean originalHadDollar = patternContainsDollarAnchor(pattern.pattern());
                        if (!originalHadDollar) {
                            // No $ in original pattern - the match is valid as long as it ends within region
                            if (lastMatchIndices[1] <= regionEnd) {
                                updateHitEndRequireEnd(new RegionSubject(input, searchStart, 0), true, matchOptions);
                                return true;
                            }
                        } else {
                            // Original had $ which was removed, so verify match ends at regionEnd
                            if (lastMatchIndices[1] == regionEnd) {
                                updateHitEndRequireEnd(new RegionSubject(input, searchStart, 0), true, matchOptions);
                                return true;
                            }
                        }

                        // Match doesn't satisfy anchor constraints. Reset and fall through to
                        // normal matching which may find a match without anchor constraints.
                        lastMatchData = null;
                        lastMatchIndices = null;
                    }
                    // If no match with transformed pattern, fall through to normal matching.
                    // This allows patterns without ^ to still find matches.
                }
            }

            // PATH 2: Normal matching with original pattern
            final var matchData = new Pcre2MatchData(pattern.code);
            final var result = pattern.code.match(
                    regionSubject.subject(),
                    regionSubject.startOffset(),
                    matchOptions,
                    matchData,
                    matchContext
            );
            if (result < 1) {
                if (result == IPcre2.ERROR_NOMATCH) {
                    updateHitEndRequireEnd(regionSubject, false, matchOptions);
                    return false;
                }

                final var errorMessage = Pcre4jUtils.getErrorMessage(pattern.code.api(), result);
                throw new RuntimeException("Failed to find a match", new IllegalStateException(errorMessage));
            }

            processMatchResult(matchData, regionSubject);

            // PATH 3: Region boundary enforcement for transparent bounds
            //
            // When transparent bounds are enabled, the full input is passed to PCRE2,
            // so we need to verify the match doesn't extend beyond regionEnd.
            if (transparentBounds && lastMatchIndices[1] > regionEnd) {
                // Match extends beyond region end. Try matching with constrained subject
                // to see if there's a valid shorter match (e.g., for greedy quantifiers).
                // This preserves lookbehind (which sees before regionStart) while constraining
                // the actual match to end within the region.
                final var constrainedSubject = getConstrainedRegionSubject(searchStart);
                final var constrainedMatchData = new Pcre2MatchData(pattern.code);
                final var constrainedResult = pattern.code.match(
                        constrainedSubject.subject(),
                        constrainedSubject.startOffset(),
                        matchOptions,
                        constrainedMatchData,
                        matchContext
                );

                if (constrainedResult >= 1) {
                    // Found a valid match within the constrained region
                    processMatchResult(constrainedMatchData, constrainedSubject);
                    updateHitEndRequireEnd(constrainedSubject, true, matchOptions);
                    return true;
                }

                // No valid match at this position, try next position
                lastMatchData = null;
                lastMatchIndices = null;
                searchStart = searchStart + 1;
                continue;
            }

            updateHitEndRequireEnd(regionSubject, true, matchOptions);
            return true;
        }
        updateHitEndRequireEnd(getRegionSubject(start), false, getMatchOptions());
        return false;
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
     * When transparent bounds are enabled, the full input string is passed to PCRE2 so that
     * lookahead and lookbehind can see beyond the region. Matches are validated after matching
     * to ensure they don't extend beyond the region end.
     * <p>
     * When transparent bounds are disabled (opaque, the default), only the region substring is
     * passed so that lookbehind and word boundaries (\b) cannot see outside the region.
     * The difference between anchoring enabled/disabled is handled by NOTBOL/NOTEOL flags.
     *
     * @param matchStartInInput the start position for matching in input coordinates
     * @return the RegionSubject containing the subject string and coordinate mapping
     */
    private RegionSubject getRegionSubject(int matchStartInInput) {
        if (transparentBounds) {
            // Transparent bounds: pass the full input string so that lookahead can see beyond
            // regionEnd and lookbehind can see before regionStart
            return new RegionSubject(
                    input,
                    matchStartInInput,
                    0
            );
        } else {
            // Opaque bounds (default): pass only the region substring
            if (regionStart > 0) {
                return new RegionSubject(
                        input.substring(regionStart, regionEnd),
                        matchStartInInput - regionStart,
                        regionStart
                );
            } else {
                return new RegionSubject(
                        input.substring(0, regionEnd),
                        matchStartInInput,
                        0
                );
            }
        }
    }

    /**
     * Get a constrained region subject for transparent bounds matching when the initial match
     * extends beyond regionEnd. This allows lookbehind to see before regionStart while
     * constraining the actual match to end at or before regionEnd.
     *
     * @param matchStartInInput the starting position for matching in input coordinates
     * @return the constrained region subject
     */
    private RegionSubject getConstrainedRegionSubject(int matchStartInInput) {
        // Pass substring from 0 to regionEnd, preserving lookbehind context
        // while constraining the match end position
        return new RegionSubject(
                input.substring(0, regionEnd),
                matchStartInInput,
                0
        );
    }

    /**
     * Gets or creates a compiled pattern variant for anchoring bounds mode.
     * <p>
     * This transforms the pattern to handle Java's anchoring bounds semantics:
     * <ul>
     *   <li>{@code ^} is replaced with {@code \G} (matches at startOffset, not position 0)</li>
     *   <li>{@code $} is removed (match end position is verified post-hoc)</li>
     * </ul>
     * <p>
     * This is necessary because PCRE2's {@code ^} always matches at position 0 of the subject,
     * while Java's {@code ^} with anchoring bounds matches at regionStart.
     *
     * @return the compiled anchoring bounds code, or null if transformation is not needed
     */
    private Pcre2Code getOrCreateAnchoringBoundsCode() {
        if (anchoringBoundsCode != null) {
            return anchoringBoundsCode;
        }

        // In MULTILINE mode, ^ matches at line boundaries (after newlines), not just at start.
        // The \G transformation only matches at startOffset, which breaks multiline semantics.
        // Skip transformation for MULTILINE patterns and fall back to normal matching.
        if ((pattern.flags() & Pattern.MULTILINE) != 0) {
            return null;
        }

        final var originalPattern = pattern.pattern();
        final var transformed = transformPatternForAnchoringBounds(originalPattern);

        // If no transformation needed, return null to indicate using the original pattern
        if (transformed.equals(originalPattern)) {
            return null;
        }

        // Compile the transformed pattern
        try {
            final var compileOptions = EnumSet.of(Pcre2CompileOption.UTF);
            // Copy relevant flags from the original pattern
            final int flags = pattern.flags();
            if ((flags & Pattern.CASE_INSENSITIVE) != 0) {
                compileOptions.add(Pcre2CompileOption.CASELESS);
            }
            if ((flags & Pattern.DOTALL) != 0) {
                compileOptions.add(Pcre2CompileOption.DOTALL);
            }
            if ((flags & Pattern.LITERAL) != 0) {
                compileOptions.add(Pcre2CompileOption.LITERAL);
            }
            // Note: MULTILINE patterns return early (line 1281), so this flag is never set here.
            // The check is kept for completeness in case the early return logic changes.
            if ((flags & Pattern.UNICODE_CHARACTER_CLASS) != 0) {
                compileOptions.add(Pcre2CompileOption.UCP);
            }
            // Note: UNICODE_CASE flag is recognized for API compatibility but has no additional effect
            // since PCRE2 with UTF mode (always enabled) already performs Unicode-aware case folding.
            if ((flags & Pattern.COMMENTS) != 0) {
                compileOptions.add(Pcre2CompileOption.EXTENDED);
            }

            final var compileContext = new Pcre2CompileContext(pattern.code.api(), null);
            if ((flags & Pattern.UNIX_LINES) != 0) {
                compileContext.setNewline(Pcre2Newline.LF);
            } else {
                compileContext.setNewline(Pcre2Newline.ANY);
            }

            anchoringBoundsCode = new Pcre2Code(
                    pattern.code.api(),
                    transformed,
                    compileOptions,
                    compileContext
            );
            return anchoringBoundsCode;
        } catch (Pcre2CompileError e) {
            // If transformation produces invalid pattern, fall back to original
            return null;
        }
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
    private static String transformPatternForAnchoringBounds(String pattern) {
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
    private static boolean patternContainsDollarAnchor(String pattern) {
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
     * Update hitEnd and requireEnd flags based on the match result.
     * <p>
     * hitEnd is set to true if the end of input was hit by the search engine, meaning
     * more input could have changed the result. This is true when:
     * <ul>
     *   <li>The match ended at the end of the subject and the pattern could match more</li>
     *   <li>No match was found and a partial match exists (more input could complete the match)</li>
     *   <li>No match was found and the search needed to examine the end of input</li>
     * </ul>
     * <p>
     * requireEnd is set to true if the match depends on end-of-input anchors ($ or \Z),
     * meaning more input could turn a positive match into a negative one. Note that \z
     * (absolute end) does not set requireEnd because it only matches at the very end.
     *
     * @param regionSubject the region subject used for matching
     * @param matchFound whether a match was found
     * @param matchOptions the match options used
     */
    private void updateHitEndRequireEnd(RegionSubject regionSubject, boolean matchFound,
            EnumSet<Pcre2MatchOption> matchOptions) {
        // Reset flags at the start of evaluation
        hitEnd = false;
        requireEnd = false;

        final String subject = regionSubject.subject();
        final int subjectEnd = subject.length();
        final int effectiveSubjectEnd = subjectEnd + regionSubject.indexAdjustment();

        if (matchFound) {
            // Check if match ended at the effective end of the subject
            final int matchEnd = lastMatchIndices[1];

            if (matchEnd == effectiveSubjectEnd) {
                // Match ended at the end of input.
                // hitEnd should be true if more input could have extended the match.
                // This is true for patterns with open-ended constructs like +, *, character classes.

                // Check for soft end anchors ($ or \Z) first
                if (patternContainsSoftEndAnchor(pattern.pattern())) {
                    hitEnd = true;
                    requireEnd = true;
                } else if (patternCanConsumeMoreAtEnd(pattern.pattern())) {
                    // Pattern has constructs that could consume more input at the end
                    hitEnd = true;
                }
            }
        } else {
            // No match found - hitEnd is true if:
            // 1. A partial match exists (more input could complete the match), OR
            // 2. The search needed to examine the entire input to determine no match
            //
            // For correctness with Java's behavior, we set hitEnd=true when the search
            // reached the end of input. In practice, for most patterns this is the case
            // when no match is found.

            // Check for partial match
            final var partialOptions = EnumSet.copyOf(matchOptions);
            partialOptions.add(Pcre2MatchOption.PARTIAL_SOFT);

            final var partialMatchData = new Pcre2MatchData(pattern.code);
            final var partialResult = pattern.code.match(
                    subject,
                    regionSubject.startOffset(),
                    partialOptions,
                    partialMatchData,
                    matchContext
            );

            if (partialResult == IPcre2.ERROR_PARTIAL) {
                // Partial match exists - more input could lead to a match
                hitEnd = true;
            } else {
                // No partial match - but the search still needed to examine the input.
                // In Java's implementation, hitEnd is typically true when no match is found
                // because the search engine had to look through the entire input.
                // Set hitEnd=true unless we can prove the search ended early.
                //
                // For a pattern like "xyz" against "abc", Java returns hitEnd=true because
                // the search engine had to examine all positions to determine there's no match.
                hitEnd = true;
            }
        }
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
    private static boolean patternCanConsumeMoreAtEnd(String pattern) {
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
    private static boolean patternContainsSoftEndAnchor(String pattern) {
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
