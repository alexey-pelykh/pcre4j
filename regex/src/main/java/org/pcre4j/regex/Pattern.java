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

import org.pcre4j.Pcre2Code;
import org.pcre4j.Pcre2CompileContext;
import org.pcre4j.Pcre2JitCode;
import org.pcre4j.Pcre4j;
import org.pcre4j.Pcre4jUtils;
import org.pcre4j.api.IPcre2;
import org.pcre4j.exception.Pcre2CompileException;
import org.pcre4j.option.Pcre2CompileOption;
import org.pcre4j.option.Pcre2JitOption;
import org.pcre4j.option.Pcre2Newline;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

/**
 * A compiled representation of a regular expression that uses the PCRE library yet aims to have a
 * {@link java.util.regex.Pattern}-alike API.
 *
 * <h2>Resource Lifecycle</h2>
 *
 * <p>Each {@code Pattern} instance holds one or more native PCRE2 compiled pattern handles
 * (via {@link Pcre2Code}). These native resources are <strong>not</strong> managed through
 * {@link AutoCloseable}; instead, they are automatically released by a {@link java.lang.ref.Cleaner}
 * when the {@code Pattern} becomes unreachable. This mirrors the lifecycle model of
 * {@link java.util.regex.Pattern}, which also does not implement {@link AutoCloseable}.</p>
 *
 * <p>Patterns are designed to be compiled once and reused across many {@link Matcher} instances.
 * There is no {@code close()} method and no need for try-with-resources.</p>
 *
 * <p><strong>Note:</strong> Because native memory is reclaimed during garbage collection rather
 * than deterministically, applications that compile a very large number of short-lived patterns
 * may observe higher native memory usage until the garbage collector runs.</p>
 */
public class Pattern {

    /**
     * A {@link java.util.regex.Pattern#CASE_INSENSITIVE}-compatible flag implemented via
     * {@link org.pcre4j.option.Pcre2CompileOption#CASELESS}
     */
    public static final int CASE_INSENSITIVE = java.util.regex.Pattern.CASE_INSENSITIVE;

    /**
     * A {@link java.util.regex.Pattern#DOTALL}-compatible flag implemented via
     * {@link org.pcre4j.option.Pcre2CompileOption#DOTALL}
     */
    public static final int DOTALL = java.util.regex.Pattern.DOTALL;

    /**
     * A {@link java.util.regex.Pattern#LITERAL}-compatible flag implemented via
     * {@link org.pcre4j.option.Pcre2CompileOption#LITERAL}
     */
    public static final int LITERAL = java.util.regex.Pattern.LITERAL;

    /**
     * A {@link java.util.regex.Pattern#MULTILINE}-compatible flag implemented via
     * {@link org.pcre4j.option.Pcre2CompileOption#MULTILINE}
     */
    public static final int MULTILINE = java.util.regex.Pattern.MULTILINE;

    /**
     * A {@link java.util.regex.Pattern#UNICODE_CHARACTER_CLASS}-compatible flag implemented via
     * {@link org.pcre4j.option.Pcre2CompileOption#UCP}
     */
    public static final int UNICODE_CHARACTER_CLASS = java.util.regex.Pattern.UNICODE_CHARACTER_CLASS;

    /**
     * A {@link java.util.regex.Pattern#UNIX_LINES}-compatible flag implemented via
     * {@link org.pcre4j.option.Pcre2Newline#LF}
     */
    public static final int UNIX_LINES = java.util.regex.Pattern.UNIX_LINES;

    /**
     * A {@link java.util.regex.Pattern#COMMENTS}-compatible flag implemented via
     * {@link org.pcre4j.option.Pcre2CompileOption#EXTENDED}
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
    private final IPcre2 api;
    private final String regex;
    private final int flags;
    private final int matchLimit;
    private final int depthLimit;
    private final int heapLimit;
    private final Map<String, Integer> namedGroups;
    private final String compiledRegex;
    private final EnumSet<Pcre2CompileOption> compileOptions;
    private final Pcre2CompileContext compileContext;
    private final boolean jitEnabled;
    private volatile Pcre2Code matchingCode;
    private volatile Pcre2Code lookingAtCode;

    /**
     * Create a new {@link Pattern} using the given regular expression, flags, and match limits.
     *
     * @param api        the PCRE API to use
     * @param regex      the regular expression to compile
     * @param flags      the flags to use when compiling the pattern
     * @param matchLimit the match limit (0 = use default)
     * @param depthLimit the depth limit (0 = use default)
     * @param heapLimit  the heap limit in kibibytes (0 = use default)
     */
    private Pattern(IPcre2 api, String regex, int flags, int matchLimit, int depthLimit, int heapLimit) {
        if (api == null) {
            throw new IllegalArgumentException("api cannot be null");
        }
        if (regex == null) {
            throw new IllegalArgumentException("regex cannot be null");
        }

        this.api = api;
        this.regex = regex;
        this.flags = flags;
        this.matchLimit = matchLimit;
        this.depthLimit = depthLimit;
        this.heapLimit = heapLimit;

        // When CANON_EQ is set, normalize the pattern to NFD form for compilation
        // The original regex is preserved in this.regex for pattern() method
        if ((flags & CANON_EQ) != 0) {
            this.compiledRegex = Normalizer.normalize(regex, Normalizer.Form.NFD);
        } else {
            this.compiledRegex = regex;
        }

        this.compileOptions = EnumSet.of(Pcre2CompileOption.UTF);
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

        this.compileContext = new Pcre2CompileContext(api, null);
        if ((flags & UNIX_LINES) != 0) {
            compileContext.setNewline(Pcre2Newline.LF);
        } else {
            compileContext.setNewline(Pcre2Newline.ANY);
        }

        try {
            final var isJitAllowed = Boolean.parseBoolean(System.getProperty("pcre2.regex.jit", "true"));
            this.jitEnabled = Pcre4jUtils.isJitSupported(api) && isJitAllowed;
            if (jitEnabled) {
                this.code = new Pcre2JitCode(
                        api,
                        compiledRegex,
                        compileOptions,
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
            }
        } catch (Pcre2CompileException e) {
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
        return new Pattern(api, regex, flags, 0, 0, 0);
    }

    /**
     * Creates a new builder for constructing a {@link Pattern} with custom match limits.
     * <p>
     * The builder allows configuring per-pattern match limits for ReDoS protection,
     * in addition to the standard compile flags.
     * <p>
     * Example usage:
     * <pre>{@code
     * Pattern pattern = Pattern.builder("complex.*regex")
     *     .flags(Pattern.CASE_INSENSITIVE)
     *     .matchLimit(10_000)
     *     .depthLimit(5_000)
     *     .heapLimit(1024)
     *     .compile();
     * }</pre>
     *
     * @param regex the regular expression to compile
     * @return a new builder
     */
    public static Builder builder(String regex) {
        return builder(Pcre4j.api(), regex);
    }

    /**
     * Creates a new builder for constructing a {@link Pattern} with custom match limits.
     * <p>
     * The builder allows configuring per-pattern match limits for ReDoS protection,
     * in addition to the standard compile flags.
     *
     * @param api   the PCRE API to use
     * @param regex the regular expression to compile
     * @return a new builder
     */
    public static Builder builder(IPcre2 api, String regex) {
        return new Builder(api, regex);
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

    /**
     * Creates a stream from the given input sequence around matches of this pattern.
     *
     * @param input the character sequence to be split
     * @return a stream of strings computed by splitting the input around matches of this pattern
     */
    public Stream<String> splitAsStream(CharSequence input) {
        return Arrays.stream(split(input, 0));
    }

    /**
     * Returns the pre-compiled JIT code with {@link Pcre2CompileOption#ANCHORED} and
     * {@link Pcre2CompileOption#ENDANCHORED} baked in, or {@code null} if JIT is not enabled.
     * <p>
     * The code is compiled lazily on first access to avoid memory overhead for patterns that
     * are never used with {@link Matcher#matches()}.
     *
     * @return the matching code, or {@code null} if JIT is not enabled
     */
    /* package-private */ Pcre2Code matchingCode() {
        if (!jitEnabled) {
            return null;
        }
        var result = matchingCode;
        if (result == null) {
            synchronized (this) {
                result = matchingCode;
                if (result == null) {
                    final var options = EnumSet.copyOf(compileOptions);
                    options.add(Pcre2CompileOption.ANCHORED);
                    options.add(Pcre2CompileOption.ENDANCHORED);
                    result = new Pcre2JitCode(
                            api,
                            compiledRegex,
                            options,
                            EnumSet.of(Pcre2JitOption.COMPLETE),
                            compileContext
                    );
                    matchingCode = result;
                }
            }
        }
        return result;
    }

    /**
     * Returns the pre-compiled JIT code with {@link Pcre2CompileOption#ANCHORED} baked in,
     * or {@code null} if JIT is not enabled.
     * <p>
     * The code is compiled lazily on first access to avoid memory overhead for patterns that
     * are never used with {@link Matcher#lookingAt()}.
     *
     * @return the looking-at code, or {@code null} if JIT is not enabled
     */
    /* package-private */ Pcre2Code lookingAtCode() {
        if (!jitEnabled) {
            return null;
        }
        var result = lookingAtCode;
        if (result == null) {
            synchronized (this) {
                result = lookingAtCode;
                if (result == null) {
                    final var options = EnumSet.copyOf(compileOptions);
                    options.add(Pcre2CompileOption.ANCHORED);
                    result = new Pcre2JitCode(
                            api,
                            compiledRegex,
                            options,
                            EnumSet.of(Pcre2JitOption.COMPLETE),
                            compileContext
                    );
                    lookingAtCode = result;
                }
            }
        }
        return result;
    }

    /**
     * Returns the match limit configured for this pattern, or 0 if using the default.
     *
     * @return the match limit, or 0 for default
     */
    /* package-private */ int matchLimit() {
        return matchLimit;
    }

    /**
     * Returns the depth limit configured for this pattern, or 0 if using the default.
     *
     * @return the depth limit, or 0 for default
     */
    /* package-private */ int depthLimit() {
        return depthLimit;
    }

    /**
     * Returns the heap limit configured for this pattern, or 0 if using the default.
     *
     * @return the heap limit in kibibytes, or 0 for default
     */
    /* package-private */ int heapLimit() {
        return heapLimit;
    }

    /**
     * A builder for constructing {@link Pattern} instances with custom match limits.
     * <p>
     * Match limits provide per-pattern ReDoS protection by capping the resources a match
     * operation can consume. When a limit is exceeded during matching, a {@link MatchLimitException}
     * is thrown.
     * <p>
     * Limits set via the builder take precedence over system property defaults
     * ({@link Matcher#MATCH_LIMIT_PROPERTY}, {@link Matcher#DEPTH_LIMIT_PROPERTY},
     * {@link Matcher#HEAP_LIMIT_PROPERTY}). A limit value of 0 (the default) means the
     * system property value is used, or the PCRE2 compiled-in default if no system property
     * is set.
     * <p>
     * Example:
     * <pre>{@code
     * Pattern pattern = Pattern.builder("complex.*regex")
     *     .flags(Pattern.CASE_INSENSITIVE)
     *     .matchLimit(10_000)
     *     .depthLimit(5_000)
     *     .heapLimit(1024)  // KiB
     *     .compile();
     * }</pre>
     */
    public static class Builder {

        private final IPcre2 api;
        private final String regex;
        private int flags;
        private int matchLimit;
        private int depthLimit;
        private int heapLimit;

        private Builder(IPcre2 api, String regex) {
            if (api == null) {
                throw new IllegalArgumentException("api cannot be null");
            }
            if (regex == null) {
                throw new IllegalArgumentException("regex cannot be null");
            }
            this.api = api;
            this.regex = regex;
        }

        /**
         * Sets the compile flags for this pattern.
         *
         * @param flags the flags to use when compiling the pattern
         * @return this builder
         * @see Pattern#CASE_INSENSITIVE
         * @see Pattern#DOTALL
         * @see Pattern#MULTILINE
         * @see Pattern#LITERAL
         * @see Pattern#UNICODE_CHARACTER_CLASS
         * @see Pattern#UNIX_LINES
         * @see Pattern#COMMENTS
         * @see Pattern#UNICODE_CASE
         * @see Pattern#CANON_EQ
         */
        public Builder flags(int flags) {
            this.flags = flags;
            return this;
        }

        /**
         * Sets the match limit for this pattern.
         * <p>
         * The match limit caps the number of times the internal {@code match()} function can
         * be called during a single match operation. This provides protection against
         * catastrophic backtracking (ReDoS).
         * <p>
         * A value of 0 means the system property ({@link Matcher#MATCH_LIMIT_PROPERTY})
         * or PCRE2's compiled-in default is used.
         *
         * @param matchLimit the match limit (must be non-negative)
         * @return this builder
         * @throws IllegalArgumentException if the match limit is negative
         * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_match_limit.html">
         *     pcre2_set_match_limit</a>
         */
        public Builder matchLimit(int matchLimit) {
            if (matchLimit < 0) {
                throw new IllegalArgumentException("matchLimit must be non-negative, got: " + matchLimit);
            }
            this.matchLimit = matchLimit;
            return this;
        }

        /**
         * Sets the backtracking depth limit for this pattern.
         * <p>
         * The depth limit caps the depth of nested backtracking during a single match
         * operation. This provides protection against patterns that cause deep recursion.
         * <p>
         * A value of 0 means the system property ({@link Matcher#DEPTH_LIMIT_PROPERTY})
         * or PCRE2's compiled-in default is used.
         *
         * @param depthLimit the depth limit (must be non-negative)
         * @return this builder
         * @throws IllegalArgumentException if the depth limit is negative
         * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_depth_limit.html">
         *     pcre2_set_depth_limit</a>
         */
        public Builder depthLimit(int depthLimit) {
            if (depthLimit < 0) {
                throw new IllegalArgumentException("depthLimit must be non-negative, got: " + depthLimit);
            }
            this.depthLimit = depthLimit;
            return this;
        }

        /**
         * Sets the heap memory limit for this pattern.
         * <p>
         * The heap limit caps the amount of heap memory (in kibibytes) that can be used
         * during a single match operation. This provides protection against patterns that
         * consume excessive memory.
         * <p>
         * A value of 0 means the system property ({@link Matcher#HEAP_LIMIT_PROPERTY})
         * or PCRE2's compiled-in default is used.
         *
         * @param heapLimit the heap limit in kibibytes (must be non-negative)
         * @return this builder
         * @throws IllegalArgumentException if the heap limit is negative
         * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_heap_limit.html">
         *     pcre2_set_heap_limit</a>
         */
        public Builder heapLimit(int heapLimit) {
            if (heapLimit < 0) {
                throw new IllegalArgumentException("heapLimit must be non-negative, got: " + heapLimit);
            }
            this.heapLimit = heapLimit;
            return this;
        }

        /**
         * Compiles the pattern with the configured flags and match limits.
         *
         * @return the compiled pattern
         * @throws java.util.regex.PatternSyntaxException if the regex syntax is invalid
         */
        public Pattern compile() {
            return new Pattern(api, regex, flags, matchLimit, depthLimit, heapLimit);
        }
    }
}
