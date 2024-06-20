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

import org.pcre4j.Pcre2Code;
import org.pcre4j.Pcre2CompileError;
import org.pcre4j.Pcre2CompileOption;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.PatternSyntaxException;

/**
 * A compiled representation of a regular expression that uses the PCRE library yet aims to have a
 * {@link java.util.regex.Pattern}-alike API.
 */
public class Pattern {

    /**
     * A {@link java.util.regex.Pattern#CASE_INSENSITIVE}-compatible flag implemented via
     * {@link org.pcre4j.Pcre2CompileOption#CASELESS}
     */
    public static final int CASE_INSENSITIVE = java.util.regex.Pattern.CASE_INSENSITIVE;

    /**
     * A {@link java.util.regex.Pattern#DOTALL}-compatible flag implemented via
     * {@link org.pcre4j.Pcre2CompileOption#DOTALL}
     */
    public static final int DOTALL = java.util.regex.Pattern.DOTALL;

    /**
     * A {@link java.util.regex.Pattern#LITERAL}-compatible flag implemented via
     * {@link org.pcre4j.Pcre2CompileOption#LITERAL}
     */
    public static final int LITERAL = java.util.regex.Pattern.LITERAL;

    /**
     * A {@link java.util.regex.Pattern#MULTILINE}-compatible flag implemented via
     * {@link org.pcre4j.Pcre2CompileOption#MULTILINE}
     */
    public static final int MULTILINE = java.util.regex.Pattern.MULTILINE;

    // TODO: public static final int CANON_EQ = java.util.regex.Pattern.CANON_EQ;
    // TODO: public static final int COMMENTS = java.util.regex.Pattern.COMMENTS;
    // TODO: public static final int UNICODE_CASE = java.util.regex.Pattern.UNICODE_CASE;
    // TODO: public static final int UNICODE_CHARACTER_CLASS = java.util.regex.Pattern.UNICODE_CHARACTER_CLASS;
    // TODO: public static final int UNIX_LINES = java.util.regex.Pattern.UNIX_LINES;

    /* package-private */ final Pcre2Code code;
    private final Map<String, Integer> namedGroups;
    private final String regex;
    private final int flags;

    private Pattern(String regex, int flags) {
        this.regex = regex;
        this.flags = flags;

        final var options = EnumSet.noneOf(Pcre2CompileOption.class);
        if ((flags & CASE_INSENSITIVE) != 0) {
            options.add(Pcre2CompileOption.CASELESS);
        }
        if ((flags & DOTALL) != 0) {
            options.add(Pcre2CompileOption.DOTALL);
        }
        if ((flags & LITERAL) != 0) {
            options.add(Pcre2CompileOption.LITERAL);
        }
        if ((flags & MULTILINE) != 0) {
            options.add(Pcre2CompileOption.MULTILINE);
        }

        try {
            this.code = new Pcre2Code(regex, options, null);
        } catch (Pcre2CompileError e) {
            throw new PatternSyntaxException(e.message(), e.pattern(), (int) e.offset());
        }

        namedGroups = new HashMap<>();
        for (var nameTableEntry : code.nameTable()) {
            namedGroups.put(nameTableEntry.name(), nameTableEntry.group());
        }
    }

    /**
     * Compiles the given regular expression into a PCRE pattern.
     *
     * @param regex the regular expression to compile
     * @return the compiled pattern
     */
    public static Pattern compile(String regex) {
        return new Pattern(regex, 0);
    }

    /**
     * Compiles the given regular expression into a PCRE pattern using the given flags.
     *
     * @param regex the regular expression to compile
     * @param flags the flags to use when compiling the pattern
     * @return the compiled pattern
     */
    public static Pattern compile(String regex, int flags) {
        return new Pattern(regex, flags);
    }

    /**
     * Compiles the given regular expression and matches the given input against it.
     *
     * @param regex the regular expression to compile
     * @param input the input to match against the compiled pattern
     * @return {@code true} if the input matches the pattern, otherwise {@code false}
     */
    public static boolean matches(String regex, CharSequence input) {
        return Pattern.compile(regex).matcher(input).matches();
    }

    /**
     * Creates a predicate that tests if this pattern is found in a given input.
     *
     * @return the predicate
     */
    public Predicate<CharSequence> asPredicate() {
        return input -> matcher(input).find();
    }

    /**
     * Creates a predicate that tests if this pattern matches a given input.
     *
     * @return the predicate
     */
    public Predicate<CharSequence> asMatchPredicate() {
        return input -> matcher(input).matches();
    }

    /**
     * Returns the flags used to compile this pattern.
     *
     * @return the flags used to compile this pattern
     */
    public int flags() {
        return flags;
    }

    /**
     * Creates a matcher that will match the given input against this pattern.
     *
     * @param input the input to match against this pattern
     * @return the matcher
     */
    public Matcher matcher(CharSequence input) {
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");
        }
        return new Matcher(this, input);
    }

    /**
     * Returns the regular expression that was compiled.
     *
     * @return the regular expression that was compiled
     */
    public String pattern() {
        return regex;
    }

    // TODO: quote(String s)

    /**
     * Splits the given input around matches of this pattern.
     *
     * @param input the input to split
     * @return the array of strings computed by splitting the input around matches of this pattern
     */
    public String[] split(CharSequence input) {
        return split(input, 0, false);
    }

    /**
     * Splits the given input around matches of this pattern.
     *
     * @param input the input to split
     * @param limit the maximum number of items to return
     * @return the array of strings computed by splitting the input around matches of this pattern
     */
    public String[] split(CharSequence input, int limit) {
        return split(input, limit, false);
    }

    /**
     * Splits the given input around matches of this pattern and returns both the strings and the matching delimiters.
     *
     * @param input the input to split
     * @param limit the maximum number of items to return
     * @return the array of strings and matching delimiters computed by splitting the input around matches of this
     * pattern
     */
    public String[] splitWithDelimiters(CharSequence input, int limit) {
        return split(input, limit, true);
    }

    /**
     * Splits the given input around matches of this pattern and returns either just the strings or both the strings
     * and the matching delimiters.
     *
     * @param input the input to split
     * @param limit the maximum number of items to return
     * @param includeDelimiters whether to include the matching delimiters in the result
     * @return the array of strings and optionally matching delimiters computed by splitting the input around matches
     * of this pattern
     */
    public String[] split(CharSequence input, int limit, boolean includeDelimiters) {
        final var matcher = matcher(input);
        final var result = new ArrayList<String>();
        var numMatches = 0;
        var offset = 0;
        while (matcher.find()) {
            if (limit <= 0 || numMatches < limit - 1) {
                if (offset == 0 && offset == matcher.start() && matcher.start() == matcher.end()) {
                    continue;
                }
                final var match = input.subSequence(offset, matcher.start()).toString();
                result.add(match);
                offset = matcher.end();
                if (includeDelimiters) {
                    result.add(input.subSequence(matcher.start(), offset).toString());
                }
                numMatches += 1;
            } else if (numMatches == limit - 1) {
                final var match = input.subSequence(offset, input.length()).toString();
                result.add(match);
                offset = matcher.end();
                numMatches += 1;
            }
        }

        if (result.isEmpty()) {
            return new String[]{input.toString()};
        }

        if (limit <= 0 || numMatches < limit) {
            result.add(input.subSequence(offset, input.length()).toString());
        }

        var resultSize = result.size();
        if (limit <= 0) {
            while (resultSize > 0 && result.get(resultSize - 1).isEmpty()) {
                resultSize--;
            }
        }
        return result.subList(0, resultSize).toArray(new String[resultSize]);
    }

    // TODO: splitAsStream(CharSequence input)

    /**
     * Returns a map of named groups in this pattern.
     *
     * @return the map of named groups in this pattern
     */
    public Map<String, Integer> namedGroups() {
        return Map.copyOf(namedGroups);
    }

    @Override
    public String toString() {
        return regex;
    }
}
