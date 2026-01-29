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

import java.text.Normalizer;
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

    /**
     * A {@link java.util.regex.Pattern#UNICODE_CHARACTER_CLASS}-compatible flag implemented via
     * {@link org.pcre4j.Pcre2CompileOption#UCP}
     */
    public static final int UNICODE_CHARACTER_CLASS = java.util.regex.Pattern.UNICODE_CHARACTER_CLASS;

    /**
     * A {@link java.util.regex.Pattern#UNIX_LINES}-compatible flag implemented via {@link org.pcre4j.Pcre2Newline#LF}
     */
    public static final int UNIX_LINES = java.util.regex.Pattern.UNIX_LINES;

    /**
     * A {@link java.util.regex.Pattern#COMMENTS}-compatible flag implemented via
     * {@link org.pcre4j.Pcre2CompileOption#EXTENDED}
     * <p>
     * Permits whitespace and comments in the pattern. In this mode, whitespace is ignored except when escaped or
     * inside a character class, and comments starting with {@code #} are ignored until end of line.
     * </p>
     * <p>
     * Comments mode can also be enabled via the embedded flag expression {@code (?x)}.
     * </p>
     */
    public static final int COMMENTS = java.util.regex.Pattern.COMMENTS;

    /**
     * A {@link java.util.regex.Pattern#UNICODE_CASE}-compatible flag that enables Unicode-aware case folding.
     * <p>
     * When this flag is specified, case-insensitive matching (when enabled by the {@link #CASE_INSENSITIVE} flag)
     * is done in a manner consistent with the Unicode Standard. By default in {@link java.util.regex.Pattern},
     * case-insensitive matching assumes that only characters in the US-ASCII charset are being matched.
     * </p>
     * <p>
     * <strong>Implementation Note:</strong> PCRE2 with UTF mode (which PCRE4J always enables) already performs
     * Unicode-aware case folding when {@link #CASE_INSENSITIVE} is used. This flag is provided for API
     * compatibility with {@link java.util.regex.Pattern} and has no additional effect on PCRE2's behavior
     * since Unicode case folding is already enabled by default.
     * </p>
     *
     * @see #CASE_INSENSITIVE
     */
    public static final int UNICODE_CASE = java.util.regex.Pattern.UNICODE_CASE;

    /**
     * A {@link java.util.regex.Pattern#CANON_EQ}-compatible flag that enables canonical equivalence matching.
     * <p>
     * When this flag is specified, two characters will be considered to match if, and only if, their full
     * canonical decompositions match. For example, the expression {@code "a\u030A"} (a + combining ring above)
     * will match the string {@code "\u00E5"} (å, precomposed) when this flag is specified.
     * </p>
     * <p>
     * By default, matching does not take canonical equivalence into account.
     * </p>
     * <p>
     * There is no embedded flag character for enabling canonical equivalence.
     * </p>
     * <p>
     * <strong>Implementation Note:</strong> This flag is implemented by normalizing both the pattern and input
     * to NFD (Canonical Decomposition) form using {@link java.text.Normalizer} before matching. Match indices
     * are mapped back to the original input string positions. This approach may impose a performance penalty.
     * </p>
     * <p>
     * <strong>Limitations:</strong> The NFD normalization approach provides correct behavior for most common
     * canonical equivalence cases. However, there are known differences from {@link java.util.regex.Pattern}'s
     * implementation:
     * </p>
     * <ul>
     *   <li><strong>Character classes:</strong> Character classes containing precomposed characters (e.g.,
     *       {@code [éè]}) will not correctly match the decomposed forms of those characters. The NFD
     *       normalization transforms the character class contents, changing the regex semantics. For
     *       reliable canonical equivalence with character classes, use decomposed forms in the pattern.</li>
     *   <li><strong>Complex patterns:</strong> Patterns with alternations or backreferences where
     *       canonically equivalent forms have different lengths may behave differently than
     *       {@link java.util.regex.Pattern}.</li>
     * </ul>
     *
     * @see java.text.Normalizer
     */
    public static final int CANON_EQ = java.util.regex.Pattern.CANON_EQ;
    /* package-private */ final Pcre2Code code;
    /* package-private */ final Pcre2Code matchingCode;
    /* package-private */ final Pcre2Code lookingAtCode;
    private final IPcre2 api;
    private final String regex;
    private final int flags;
    private final Map<String, Integer> namedGroups;

    /**
     * Create a new {@link Pattern} using the given regular expression and flags.
     *
     * @param api   the PCRE API to use
     * @param regex the regular expression to compile
     * @param flags the flags to use when compiling the pattern
     */
    private Pattern(IPcre2 api, String regex, int flags) {
        if (api == null) {
            throw new IllegalArgumentException("api cannot be null");
        }
        if (regex == null) {
            throw new IllegalArgumentException("regex cannot be null");
        }

        this.api = api;
        this.regex = regex;
        this.flags = flags;

        // When CANON_EQ is set, normalize the pattern to NFD form for compilation
        // The original regex is preserved in this.regex for pattern() method
        final String compiledRegex;
        if ((flags & CANON_EQ) != 0) {
            compiledRegex = Normalizer.normalize(regex, Normalizer.Form.NFD);
        } else {
            compiledRegex = regex;
        }

        final var compileOptions = EnumSet.of(Pcre2CompileOption.UTF);
        if ((flags & CASE_INSENSITIVE) != 0) {
            compileOptions.add(Pcre2CompileOption.CASELESS);
        }
        if ((flags & DOTALL) != 0) {
            compileOptions.add(Pcre2CompileOption.DOTALL);
        }
        if ((flags & LITERAL) != 0) {
            compileOptions.add(Pcre2CompileOption.LITERAL);
        }
        if ((flags & MULTILINE) != 0) {
            compileOptions.add(Pcre2CompileOption.MULTILINE);
        }
        if ((flags & UNICODE_CHARACTER_CLASS) != 0) {
            compileOptions.add(Pcre2CompileOption.UCP);
        }
        if ((flags & COMMENTS) != 0) {
            compileOptions.add(Pcre2CompileOption.EXTENDED);
        }
        // Note: UNICODE_CASE flag is recognized for API compatibility but has no additional effect
        // since PCRE2 with UTF mode (always enabled) already performs Unicode-aware case folding.
        // Note: CANON_EQ flag is handled above by normalizing the pattern to NFD form.

        final var compileContext = new Pcre2CompileContext(api, null);
        if ((flags & UNIX_LINES) != 0) {
            compileContext.setNewline(Pcre2Newline.LF);
        } else {
            compileContext.setNewline(Pcre2Newline.ANY);
        }

        try {
            final var isJitAllowed = Boolean.parseBoolean(System.getProperty("pcre2.regex.jit", "true"));
            if (Pcre4jUtils.isJitSupported(api) && isJitAllowed) {
                this.code = new Pcre2JitCode(
                        api,
                        compiledRegex,
                        compileOptions,
                        EnumSet.of(Pcre2JitOption.COMPLETE),
                        compileContext
                );

                final var matchingCompileOptions = EnumSet.copyOf(compileOptions);
                matchingCompileOptions.add(Pcre2CompileOption.ANCHORED);
                matchingCompileOptions.add(Pcre2CompileOption.ENDANCHORED);
                this.matchingCode = new Pcre2JitCode(
                        api,
                        compiledRegex,
                        matchingCompileOptions,
                        EnumSet.of(Pcre2JitOption.COMPLETE),
                        compileContext
                );

                final var lookingAtCompileOptions = EnumSet.copyOf(compileOptions);
                lookingAtCompileOptions.add(Pcre2CompileOption.ANCHORED);
                this.lookingAtCode = new Pcre2JitCode(
                        api,
                        compiledRegex,
                        lookingAtCompileOptions,
                        EnumSet.of(Pcre2JitOption.COMPLETE),
                        compileContext
                );
            } else {
                this.code = new Pcre2Code(
                        api,
                        compiledRegex,
                        compileOptions,
                        compileContext
                );
                this.matchingCode = null;
                this.lookingAtCode = null;
            }
        } catch (Pcre2CompileError e) {
            throw new PatternSyntaxException(e.message(), e.pattern(), (int) e.offset());
        }

        namedGroups = new HashMap<>();
        for (var nameTableEntry : this.code.nameTable()) {
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
        return compile(Pcre4j.api(), regex);
    }

    /**
     * Compiles the given regular expression into a PCRE pattern.
     *
     * @param api   the PCRE API to use
     * @param regex the regular expression to compile
     * @return the compiled pattern
     */
    public static Pattern compile(IPcre2 api, String regex) {
        return compile(api, regex, 0);
    }

    /**
     * Compiles the given regular expression into a PCRE pattern using the given flags.
     *
     * @param regex the regular expression to compile
     * @param flags the flags to use when compiling the pattern
     * @return the compiled pattern
     */
    public static Pattern compile(String regex, int flags) {
        return compile(Pcre4j.api(), regex, flags);
    }

    /**
     * Compiles the given regular expression into a PCRE pattern using the given flags.
     *
     * @param api   the PCRE API to use
     * @param regex the regular expression to compile
     * @param flags the flags to use when compiling the pattern
     * @return the compiled pattern
     */
    public static Pattern compile(IPcre2 api, String regex, int flags) {
        return new Pattern(api, regex, flags);
    }

    /**
     * Compiles the given regular expression and matches the given input against it.
     *
     * @param regex the regular expression to compile
     * @param input the input to match against the compiled pattern
     * @return {@code true} if the input matches the pattern, otherwise {@code false}
     */
    public static boolean matches(String regex, CharSequence input) {
        return matches(Pcre4j.api(), regex, input);
    }

    /**
     * Compiles the given regular expression and matches the given input against it.
     *
     * @param api   the PCRE API to use
     * @param regex the regular expression to compile
     * @param input the input to match against the compiled pattern
     * @return {@code true} if the input matches the pattern, otherwise {@code false}
     */
    public static boolean matches(IPcre2 api, String regex, CharSequence input) {
        return Pattern.compile(api, regex).matcher(input).matches();
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

    /**
     * Returns a literal pattern String for the specified String.
     *
     * <p>This method returns a String that can be used to create a Pattern that would
     * match the string {@code s} as if it were a literal pattern.</p>
     *
     * <p>Metacharacters or escape sequences in the input String will be given no special meaning.</p>
     *
     * @param s The string to be literalized
     * @return A literal pattern String
     */
    public static String quote(String s) {
        int slashEIndex = s.indexOf("\\E");
        if (slashEIndex == -1) {
            return "\\Q" + s + "\\E";
        }
        return "\\Q" + s.replace("\\E", "\\E\\\\E\\Q") + "\\E";
    }

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
     * @param input             the input to split
     * @param limit             the maximum number of items to return
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
