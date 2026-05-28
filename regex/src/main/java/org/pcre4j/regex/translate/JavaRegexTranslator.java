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
                        final int tokenEnd = findPropertyTokenEnd(javaPattern, i);
                        if (tokenEnd > i) {
                            final int braceOpen = javaPattern.indexOf('{', i + 2);
                            final String name = javaPattern.substring(braceOpen + 1, tokenEnd - 1);
                            final String replacement = PropertyMap.apply(name);

                            if (replacement != null) {
                                if (replacement.startsWith("[")) {
                                    if (next == 'P') {
                                        out.append("[^");
                                        out.append(replacement, 1, replacement.length());
                                    } else {
                                        out.append(replacement);
                                    }
                                } else if (replacement.startsWith("\\P{")) {
                                    if (next == 'P') {
                                        out.append("\\p{");
                                        out.append(replacement, 3, replacement.length());
                                    } else {
                                        out.append(replacement);
                                    }
                                } else {
                                    out.append('\\');
                                    out.append(next);
                                    out.append('{');
                                    out.append(replacement);
                                    out.append('}');
                                }
                                i = tokenEnd;
                                continue;
                            }
                            out.append(javaPattern, i, tokenEnd);
                            i = tokenEnd;
                            continue;
                        }
                    }
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
                    // If the class body contains raw surrogate code units, preserve Phase-1
                    // behaviour to avoid undefined interactions with PCRE2's UTF mode.
                    if (containsRawSurrogate(javaPattern, classStart)) {
                        // Scan ahead to find the end of this class (Phase 1 pass handles nesting)
                        final ClassNode classNode = ClassBodyParser.parseClass(javaPattern, pos);
                        final int classEnd = pos[0];
                        out.append(rewritePropertiesOnly(javaPattern, classStart, classEnd));
                        i = classEnd;
                        continue;
                    }
                    final ClassNode classNode = ClassBodyParser.parseClass(javaPattern, pos);
                    final int classEnd = pos[0];
                    final String rendered = ClassRenderer.render(classNode);
                    if (rendered.contains("&&")) {
                        // Fallback: intersection could not be evaluated.
                        // Preserve Phase-1 behaviour by rewriting only property tokens in the
                        // original class text — keeping [, ], && and nested structure verbatim.
                        out.append(rewritePropertiesOnly(javaPattern, classStart, classEnd));
                    } else {
                        out.append(rendered);
                    }
                    i = classEnd;
                    continue;
                } catch (IllegalArgumentException e) {
                    // Parser failed — copy verbatim and let PCRE2 handle (or reject) it
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

            // Normal character — copy verbatim
            out.append(c);
            i++;
        }

        return out.toString();
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
     * ({@code i}, {@code d}, {@code m}, {@code s}, {@code u}, {@code x}, or {@code U}).
     */
    private static boolean isJavaModeFlag(final char c) {
        return c == 'i' || c == 'd' || c == 'm' || c == 's' || c == 'u' || c == 'x' || c == 'U';
    }

    /**
     * Returns the substring {@code s[from, to)} with Java-only mode flags ({@code u}, {@code U},
     * {@code d}) removed, preserving the order of the remaining flag characters.
     */
    private static String filterModeFlags(final String s, final int from, final int to) {
        if (from >= to) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(to - from);
        for (int k = from; k < to; k++) {
            final char f = s.charAt(k);
            if (f != 'u' && f != 'U' && f != 'd') {
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
                    final int tokenEnd = findPropertyTokenEnd(s, i);
                    if (tokenEnd > i) {
                        final int braceOpen = s.indexOf('{', i + 2);
                        final String name = s.substring(braceOpen + 1, tokenEnd - 1);
                        final String replacement = PropertyMap.apply(name);
                        if (replacement != null) {
                            if (replacement.startsWith("[")) {
                                if (next == 'P') {
                                    sb.append("[^");
                                    sb.append(replacement, 1, replacement.length());
                                } else {
                                    sb.append(replacement);
                                }
                            } else if (replacement.startsWith("\\P{")) {
                                if (next == 'P') {
                                    sb.append("\\p{");
                                    sb.append(replacement, 3, replacement.length());
                                } else {
                                    sb.append(replacement);
                                }
                            } else {
                                sb.append('\\').append(next).append('{')
                                  .append(replacement).append('}');
                            }
                            i = tokenEnd;
                            continue;
                        }
                        sb.append(s, i, tokenEnd);
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
    private static boolean containsRawSurrogate(final String s, final int from) {
        final int limit = Math.min(from + 4096, s.length());
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
