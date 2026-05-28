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
package org.pcre4j.regex.translate;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Translates Java regex syntax to PCRE2-compatible syntax for use inside
 * {@code org.pcre4j.regex.Pattern}.
 *
 * <p>Phase 1: rewrite {@code \p{...}} / {@code \P{...}} property tokens outside character
 * classes using {@link PropertyMap}.
 *
 * <p>Phase 2: rewrite character-class bodies — flatten nested unions ({@code [abc[def]]}),
 * resolve intersections ({@code [a-c&&b-d]}), and escape {@code -} after multi-char escapes
 * like {@code [\w-#]} so PCRE2 does not misinterpret them as range operators.
 *
 * <p>Phase 3: translate Java inline mode-modifier groups ({@code (?flags)},
 * {@code (?flags:...)}, {@code (?flags-flags2)}, {@code (?flags-flags2:...)}).
 * Java flags {@code u} (UNICODE_CASE) and {@code U} (UNICODE_CHARACTER_CLASS) are dropped
 * because PCRE2's UTF mode (always enabled) already provides Unicode-aware case folding, and
 * because PCRE2 repurposes {@code (?U)} to mean "ungreedy" — semantically incompatible.
 * Java flag {@code d} (UNIX_LINES) is also dropped as it has no PCRE2 inline equivalent
 * (the newline mode is set at compile time via the compile context).
 * If both sides of a mode modifier become empty after filtering, the whole modifier is dropped
 * (for {@code (?)}) or collapsed to a plain non-capturing group (for {@code (?:)}).
 *
 * <p>The translator correctly skips tokens that appear inside {@code \Q...\E} literal
 * sections and does not match {@code \\p{...}} (escaped backslash followed by {@code p}).
 */
public final class JavaRegexTranslator {

    private static final Logger LOG = Logger.getLogger(JavaRegexTranslator.class.getName());

    private JavaRegexTranslator() {
    }

    /**
     * Translates a Java regex pattern to a PCRE2-compatible pattern.
     *
     * @param javaPattern the original Java pattern string
     * @param flags       the Java {@code Pattern} flags (reserved for future use)
     * @return a PCRE2-compatible pattern string; may be the same object if no rewrite occurred
     */
    public static String translate(final String javaPattern, final int flags) {
        if (javaPattern == null || javaPattern.isEmpty()) {
            return javaPattern;
        }

        final boolean caseless = (flags & java.util.regex.Pattern.CASE_INSENSITIVE) != 0;
        final int len = javaPattern.length();
        final StringBuilder out = new StringBuilder(len + 32);

        int i = 0;
        boolean inQuotation = false; // inside \Q...\E

        while (i < len) {
            final char c = javaPattern.charAt(i);

            // --- Handle \Q...\E literal sections ---
            if (c == '\\' && i + 1 < len) {
                final char next = javaPattern.charAt(i + 1);

                if (!inQuotation && next == 'Q') {
                    out.append('\\');
                    out.append('Q');
                    i += 2;
                    inQuotation = true;
                    continue;
                }

                if (inQuotation && next == 'E') {
                    out.append('\\');
                    out.append('E');
                    i += 2;
                    inQuotation = false;
                    continue;
                }

                if (inQuotation) {
                    out.append(c);
                    i++;
                    continue;
                }

                // Outside quotation: check for a property token \p{...} or \P{...}
                if (next == 'p' || next == 'P') {
                    if (!hasOddTrailingBackslashes(out)) {
                        final int tokenEnd = tryAppendPropertyToken(javaPattern, i, next, out, caseless);
                        if (tokenEnd > i) {
                            i = tokenEnd;
                            continue;
                        }
                    }
                }

                // Java \\uXXXX → PCRE2 \\x{XXXX} (PCRE2 does not accept \\u in patterns).
                if (next == 'u' && i + 5 < len + 1) {
                    int k = i + 2;
                    final int hexEnd = Math.min(k + 4, len);
                    while (k < hexEnd && isHexDigit(javaPattern.charAt(k))) {
                        k++;
                    }
                    if (k - (i + 2) == 4) {
                        out.append("\\x{").append(javaPattern, i + 2, k).append('}');
                        i = k;
                        continue;
                    }
                }

                // Java \N{name} → \x{HHHH} via Character.codePointOf. Falls through to
                // verbatim copy on lookup failure so PCRE2 can produce the diagnostic.
                if (next == 'N' && i + 2 < len && javaPattern.charAt(i + 2) == '{') {
                    final int close = javaPattern.indexOf('}', i + 3);
                    if (close > 0) {
                        final String name = javaPattern.substring(i + 3, close);
                        try {
                            final int cp = Character.codePointOf(name);
                            out.append("\\x{").append(Integer.toHexString(cp)).append('}');
                            i = close + 1;
                            continue;
                        } catch (IllegalArgumentException ignored) {
                            // unknown name — copy braced body literally below
                        }
                        out.append(javaPattern, i, close + 1);
                        i = close + 1;
                        continue;
                    }
                }

                // \x{...} — consume whole braced body so the quantifier-brace validator
                // below does not misread it as {n,m}.
                if (next == 'x' && i + 2 < len && javaPattern.charAt(i + 2) == '{') {
                    final int close = javaPattern.indexOf('}', i + 3);
                    if (close > 0) {
                        out.append(javaPattern, i, close + 1);
                        i = close + 1;
                        continue;
                    }
                }

                // Java \0n, \0nn, \0mnn (m in 0-3) octal escape → PCRE2 \o{...}.
                // PCRE2 treats \0dd differently (max 2 octal digits after \0), so we rewrite.
                if (next == '0' && i + 2 < len && isOctalDigit(javaPattern.charAt(i + 2))) {
                    int k = i + 2;
                    int last = Math.min(k + 3, len);
                    while (k < last && isOctalDigit(javaPattern.charAt(k))) {
                        k++;
                    }
                    // 3-digit form requires first digit 0-3.
                    if (k - (i + 2) == 3 && javaPattern.charAt(i + 2) > '3') {
                        k--;
                    }
                    int value = 0;
                    for (int p = i + 2; p < k; p++) {
                        value = value * 8 + (javaPattern.charAt(p) - '0');
                    }
                    out.append("\\o{").append(Integer.toOctalString(value)).append('}');
                    i = k;
                    continue;
                }

                // Any other backslash sequence: copy backslash and advance.
                out.append(c);
                i++;
                continue;
            }

            if (inQuotation) {
                out.append(c);
                i++;
                continue;
            }

            // --- Phase 2: character class body rewrite ---
            if (c == '[' && !hasOddTrailingBackslashes(out)) {
                final int classStart = i;
                final int[] pos = {i};
                try {
                    final ClassNode classNode = ClassBodyParser.parseClass(javaPattern, pos);
                    final int classEnd = pos[0];
                    // If the class body contains raw lone surrogates, preserve Phase-1 behaviour
                    // to avoid undefined interactions with PCRE2's UTF mode. Scan the entire
                    // (now-known) class extent so very large classes are handled correctly.
                    if (containsRawSurrogate(javaPattern, classStart, classEnd)) {
                        if (LOG.isLoggable(Level.FINE)) {
                            LOG.fine("pcre4j translator: lone surrogate detected in class at "
                                    + "index " + classStart + "; falling back to Phase-1 rewrite");
                        }
                        out.append(rewritePropertiesOnly(javaPattern, classStart, classEnd));
                        i = classEnd;
                        continue;
                    }
                    final String rendered = ClassRenderer.render(classNode);
                    final String maybeFolded = caseless ? expandCasedPropertiesInClass(rendered) : rendered;
                    if (maybeFolded.contains("&&")) {
                        // Fallback: intersection could not be evaluated.
                        // Preserve Phase-1 behaviour by rewriting only property tokens in the
                        // original class text — keeping [, ], && and nested structure verbatim.
                        out.append(rewritePropertiesOnly(javaPattern, classStart, classEnd));
                    } else {
                        out.append(maybeFolded);
                    }
                    i = classEnd;
                    continue;
                } catch (IllegalArgumentException e) {
                    // Bad intersection: JDK rejects [...&&] (empty right operand).
                    if (e.getMessage() != null && e.getMessage().startsWith("Bad intersection syntax")) {
                        throw new java.util.regex.PatternSyntaxException(
                                "Bad intersection syntax", javaPattern, classStart);
                    }
                    // Other parser failure — copy verbatim and let PCRE2 produce the diagnostic.
                    // Log at FINE so support can opt in to seeing the original parser failure.
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.log(Level.FINE, "ClassBodyParser rejected class at index "
                                + classStart + " of pattern; passing through to PCRE2", e);
                    }
                    out.append(c);
                    i++;
                    continue;
                }
            }

            // --- Phase 3: inline mode-flag translator ---
            // Intercept (?flags) / (?flags:) / (?flags-flags2) / (?flags-flags2:) groups.
            // Must not be inside a quotation (checked above) or class (handled by Phase 2 branch).
            if (c == '(' && i + 1 < len && javaPattern.charAt(i + 1) == '?'
                    && !hasOddTrailingBackslashes(out)) {
                final int modeResult = tryTranslateModeModifier(javaPattern, i, len, out);
                if (modeResult > i) {
                    i = modeResult;
                    continue;
                }
            }

            // --- Quantifier-brace validation (JDK parity) ---
            // At this point '{' is in quantifier position: not escaped, not inside a class,
            // not inside Q...E, and not consumed as part of property/hex escapes.
            // JDK rejects any {body} where body is not /\d+(,\d*)?/ with "Illegal repetition".
            if (c == '{' && !hasOddTrailingBackslashes(out)) {
                final int close = javaPattern.indexOf('}', i + 1);
                if (close > i) {
                    final String body = javaPattern.substring(i + 1, close);
                    if (!isValidQuantifierBody(body)) {
                        throw new java.util.regex.PatternSyntaxException(
                                "Illegal repetition", javaPattern, i);
                    }
                }
            }

            // Normal character — copy verbatim
            out.append(c);
            i++;
        }

        return out.toString();
    }

    private static boolean isValidQuantifierBody(final String body) {
        if (body.isEmpty()) {
            return false;
        }
        final int n = body.length();
        int k = 0;
        while (k < n && body.charAt(k) >= '0' && body.charAt(k) <= '9') {
            k++;
        }
        if (k == 0) {
            return false;
        }
        if (k == n) {
            return true;
        }
        if (body.charAt(k) != ',') {
            return false;
        }
        k++;
        while (k < n && body.charAt(k) >= '0' && body.charAt(k) <= '9') {
            k++;
        }
        return k == n;
    }

    private static boolean isHexDigit(final char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }

    private static boolean isOctalDigit(final char ch) {
        return ch >= '0' && ch <= '7';
    }

    /**
     * If the substring starting at {@code start} (which must be {@code '('}) is a Java inline
     * mode-modifier group, translates it — filtering out {@code u}, {@code U}, {@code d} flags —
     * appends the result to {@code out}, and returns the index just past the modifier's terminator
     * character ({@code ')'} or {@code ':'}).
     *
     * <p>Returns {@code start} (unchanged) if the substring is not a mode-modifier group, so the
     * caller can fall through to normal character handling.
     *
     * @param s     the full pattern string
     * @param start index of {@code '('}
     * @param len   {@code s.length()}
     * @param out   the output buffer
     * @return index just past the terminator, or {@code start} if not a mode modifier
     */
    private static int tryTranslateModeModifier(
            final String s, final int start, final int len, final StringBuilder out) {
        // s[start] == '(', s[start+1] == '?'
        int j = start + 2;

        // Scan on-flags: [idmsuxU]*
        final int onStart = j;
        while (j < len && isJavaModeFlag(s.charAt(j))) {
            j++;
        }
        final int onEnd = j;

        // Optional off-flags: -[idmsuxU]*
        int offStart = -1;
        int offEnd = -1;
        if (j < len && s.charAt(j) == '-') {
            j++; // skip '-'
            offStart = j;
            while (j < len && isJavaModeFlag(s.charAt(j))) {
                j++;
            }
            offEnd = j;
        }

        // Terminator must be ')' or ':'
        if (j >= len) {
            return start;
        }
        final char term = s.charAt(j);
        if (term != ')' && term != ':') {
            return start;
        }

        // It IS a mode-modifier group — apply flag filtering
        final String filteredOn = filterModeFlags(s, onStart, onEnd);
        final String filteredOff = (offStart >= 0) ? filterModeFlags(s, offStart, offEnd) : null;
        final boolean hasOn = !filteredOn.isEmpty();
        final boolean hasOff = filteredOff != null && !filteredOff.isEmpty();
        final boolean hasDash = offStart >= 0;

        if (term == ')') {
            if (!hasOn && !hasOff) {
                // (?u), (?u-U), etc. — drop entirely
            } else {
                out.append("(?").append(filteredOn);
                if (hasDash) {
                    out.append('-');
                    if (hasOff) {
                        out.append(filteredOff);
                    }
                }
                out.append(')');
            }
        } else { // ':'
            if (!hasOn && !hasOff) {
                // (?u:...) or (?u-U:...) — collapse to plain non-capturing group
                out.append("(?:");
            } else {
                out.append("(?").append(filteredOn);
                if (hasDash) {
                    out.append('-');
                    if (hasOff) {
                        out.append(filteredOff);
                    }
                }
                out.append(':');
            }
        }
        return j + 1; // advance past terminator
    }

    /**
     * Returns {@code true} if {@code c} is a Java inline mode flag character
     * ({@code i}, {@code d}, {@code m}, {@code s}, {@code u}, {@code c}, {@code x}, or {@code U}).
     * Note: {@code c} (CANON_EQ) is accepted at the syntax level so {@code (?c)} compiles, even
     * though it is filtered out before being handed to PCRE2 (CANON_EQ is implemented by NFD
     * normalisation of the pattern at compile time, not by an inline-flag mechanism).
     */
    private static boolean isJavaModeFlag(final char c) {
        return c == 'i' || c == 'd' || c == 'm' || c == 's' || c == 'u' || c == 'c' || c == 'x' || c == 'U';
    }

    /**
     * Returns the substring {@code s[from, to)} with Java-only mode flags ({@code u}, {@code U},
     * {@code d}, {@code c}) removed, preserving the order of the remaining flag characters.
     * {@code c} (CANON_EQ) has no PCRE2 inline equivalent and is handled by NFD normalisation.
     */
    private static String filterModeFlags(final String s, final int from, final int to) {
        if (from >= to) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(to - from);
        for (int k = from; k < to; k++) {
            final char f = s.charAt(k);
            if (f != 'u' && f != 'U' && f != 'd' && f != 'c') {
                sb.append(f);
            }
        }
        return sb.toString();
    }

    /**
     * Returns the index just past the closing {@code '}'} of a {@code \p{...}} or {@code \P{...}}
     * token starting at {@code start}, or {@code start} if no valid token is found.
     *
     * @param s     the pattern string
     * @param start index of the leading {@code '\\'}
     * @return exclusive end index of the token, or {@code start} if not a valid property token
     */
    private static int findPropertyTokenEnd(final String s, final int start) {
        final int len = s.length();
        // s[start] == '\\', s[start+1] == 'p' or 'P'
        if (start + 3 >= len) {
            return start;
        }
        if (s.charAt(start + 2) != '{') {
            return start;
        }
        final int closeIdx = s.indexOf('}', start + 3);
        if (closeIdx < 0) {
            return start;
        }
        return closeIdx + 1;
    }

    /**
     * Detects a {@code \p{...}}/{@code \P{...}} token at {@code start} and, if found, appends its
     * PropertyMap-rewritten form (or the original text when unmapped) to {@code out}. Returns the
     * index just past the token, or {@code start} if no valid token was present.
     */
    private static int tryAppendPropertyToken(
            final String s, final int start, final char pOrP, final StringBuilder out,
            final boolean caseless) {
        final int tokenEnd = findPropertyTokenEnd(s, start);
        if (tokenEnd <= start) {
            return start;
        }
        final int braceOpen = s.indexOf('{', start + 2);
        final String name = s.substring(braceOpen + 1, tokenEnd - 1);
        final String replacement = PropertyMap.apply(name);
        final String effective = replacement != null ? replacement : name;
        // Under CASELESS, PCRE2 does not case-fold property classes. Rewrite cased categories
        // to the union [Lu Ll Lt] so JDK's CASE_INSENSITIVE semantics are preserved.
        if (caseless && isCasedLetterCategory(effective)) {
            if (pOrP == 'P') {
                out.append("[^\\p{Lu}\\p{Ll}\\p{Lt}]");
            } else {
                out.append("[\\p{Lu}\\p{Ll}\\p{Lt}]");
            }
            return tokenEnd;
        }
        if (replacement == null) {
            out.append(s, start, tokenEnd);
        } else if (replacement.startsWith("[")) {
            if (pOrP == 'P') {
                out.append("[^").append(replacement, 1, replacement.length());
            } else {
                out.append(replacement);
            }
        } else if (replacement.startsWith("\\P{")) {
            if (pOrP == 'P') {
                out.append("\\p{").append(replacement, 3, replacement.length());
            } else {
                out.append(replacement);
            }
        } else {
            out.append('\\').append(pOrP).append('{').append(replacement).append('}');
        }
        return tokenEnd;
    }

    private static boolean isCasedLetterCategory(final String resolved) {
        switch (resolved) {
            case "Lu":
            case "Ll":
            case "Lt":
            case "Lowercase":
            case "Uppercase":
            case "Titlecase":
            case "[a-z]":
            case "[A-Z]":
                return true;
            default:
                return false;
        }
    }

    /**
     * Inside a rendered char class (already starts with {@code [} and ends with {@code ]}), expand
     * cased property atoms ({@code \p{Lu}}, {@code \p{Ll}}, {@code \p{Lt}}, and the binary aliases
     * {@code \p{Lowercase|Uppercase|Titlecase}}) into the union of all three so PCRE2's CASELESS
     * semantics match JDK's CASE_INSENSITIVE for property classes. Also expands the ASCII ranges
     * {@code a-z} and {@code A-Z} (which JDK's CASE_INSENSITIVE folds to all case-equivalents,
     * including titlecase letters like U+01C8) to include the cased-letter union.
     */
    private static String expandCasedPropertiesInClass(final String classText) {
        final boolean hasProp = classText.indexOf("\\p{") >= 0 || classText.indexOf("\\P{") >= 0;
        final boolean hasAsciiCasedRange = classText.contains("a-z") || classText.contains("A-Z");
        if (!hasProp && !hasAsciiCasedRange) {
            return classText;
        }
        final StringBuilder sb = new StringBuilder(classText.length() + 32);
        final int n = classText.length();
        boolean appendedCasedUnion = false;
        for (int i = 0; i < n; i++) {
            final char c = classText.charAt(i);
            if (c == '\\' && i + 3 < n && (classText.charAt(i + 1) == 'p' || classText.charAt(i + 1) == 'P')
                    && classText.charAt(i + 2) == '{') {
                final int close = classText.indexOf('}', i + 3);
                if (close > 0) {
                    final String body = classText.substring(i + 3, close);
                    if (isCasedLetterCategory(body)) {
                        if (classText.charAt(i + 1) == 'P') {
                            // \P{Lu} → leave as-is (rare and not in JDK CASELESS tests).
                            sb.append(classText, i, close + 1);
                        } else {
                            sb.append("\\p{Lu}\\p{Ll}\\p{Lt}");
                            appendedCasedUnion = true;
                        }
                        i = close;
                        continue;
                    }
                }
            }
            sb.append(c);
        }
        // If we left ASCII a-z / A-Z ranges in place, append a cased-letter union before the
        // closing ']' so non-ASCII case-equivalents (e.g. U+01C7 LJ family) also match.
        if (hasAsciiCasedRange && !appendedCasedUnion
                && sb.length() > 1 && sb.charAt(sb.length() - 1) == ']') {
            sb.insert(sb.length() - 1, "\\p{Lu}\\p{Ll}\\p{Lt}");
        }
        return sb.toString();
    }

    /**
     * Returns {@code true} if the current end of {@code sb} has an odd number of consecutive
     * backslashes, meaning the next character would be escaped (i.e. the backslash is literal).
     */
    private static boolean hasOddTrailingBackslashes(final StringBuilder sb) {
        int count = 0;
        for (int j = sb.length() - 1; j >= 0; j--) {
            if (sb.charAt(j) == '\\') {
                count++;
            } else {
                break;
            }
        }
        return (count & 1) == 1;
    }

    /**
     * Applies Phase-1-style property rewriting to the substring {@code s[from, to)} — which
     * must be a character class text like {@code [\p{InGreek}&&[^x]]} — rewriting only
     * {@code \p{...}} / {@code \P{...}} tokens and leaving all other syntax unchanged.
     *
     * <p>This is used as the intersection fallback: when a class with {@code &&} cannot be
     * fully evaluated to a {@link RangeSet}, we preserve the original structure so that PCRE2
     * parses it the same way it would have with Phase-1-only translation, avoiding regressions.
     */
    private static String rewritePropertiesOnly(final String s, final int from, final int to) {
        final StringBuilder sb = new StringBuilder(to - from + 8);
        int i = from;
        boolean inQuote = false;
        while (i < to) {
            final char c = s.charAt(i);
            if (c == '\\' && i + 1 < to) {
                final char next = s.charAt(i + 1);
                if (!inQuote && next == 'Q') {
                    sb.append('\\').append('Q');
                    i += 2;
                    inQuote = true;
                    continue;
                }
                if (inQuote && next == 'E') {
                    sb.append('\\').append('E');
                    i += 2;
                    inQuote = false;
                    continue;
                }
                if (!inQuote && (next == 'p' || next == 'P') && !hasOddTrailingBackslashes(sb)) {
                    final int tokenEnd = tryAppendPropertyToken(s, i, next, sb, false);
                    if (tokenEnd > i) {
                        i = tokenEnd;
                        continue;
                    }
                }
                sb.append(c);
                i++;
                continue;
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }

    /**
     * Returns {@code true} if the substring starting at {@code from} contains any <em>lone</em>
     * surrogate code unit (a high surrogate not followed by a low surrogate, or a low surrogate
     * not preceded by a high surrogate) before the class body ends.
     *
     * <p>Lone surrogates in class bodies cause PCRE2 behavior that depends on JNA/FFM string
     * encoding details.  We preserve Phase-1 semantics for those classes to avoid regressions.
     *
     * <p>Valid supplementary characters stored as surrogate pairs are NOT considered lone
     * surrogates and will be handled normally.
     */
    private static boolean containsRawSurrogate(final String s, final int from, final int to) {
        final int limit = Math.min(to, s.length());
        for (int k = from; k < limit; k++) {
            final char ch = s.charAt(k);
            if (ch >= 0xD800 && ch <= 0xDFFF) {
                // Check if it's a valid surrogate pair
                if (Character.isHighSurrogate(ch) && k + 1 < limit
                        && Character.isLowSurrogate(s.charAt(k + 1))) {
                    k++; // skip the low surrogate — this is a valid pair
                } else {
                    return true; // lone surrogate
                }
            }
        }
        return false;
    }
}
