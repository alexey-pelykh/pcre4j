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
 * <p>Phase 1 scope: rewrite {@code \p{...}} / {@code \P{...}} property tokens using
 * {@link PropertyMap}. All other syntax is passed through unchanged.
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
                    // Enter \Q...\E region — copy both chars and flip state
                    out.append('\\');
                    out.append('Q');
                    i += 2;
                    inQuotation = true;
                    continue;
                }

                if (inQuotation && next == 'E') {
                    // Leave \Q...\E region
                    out.append('\\');
                    out.append('E');
                    i += 2;
                    inQuotation = false;
                    continue;
                }

                if (inQuotation) {
                    // Inside quotation: copy verbatim
                    out.append(c);
                    i++;
                    continue;
                }

                // Outside quotation: check for a property token \p{...} or \P{...}
                if (next == 'p' || next == 'P') {
                    // Peek ahead: is the character before this backslash also a backslash?
                    // We track "number of consecutive backslashes before this position" in the
                    // output to decide whether this backslash is itself escaped.
                    // However, since we build `out` on the fly and have already copied all prior
                    // chars, we count trailing backslashes in `out`.
                    if (!hasOddTrailingBackslashes(out)) {
                        // Try to consume a property token starting at i
                        final int tokenEnd = findPropertyTokenEnd(javaPattern, i);
                        if (tokenEnd > i) {
                            // Extract the name between { and }
                            final int braceOpen = javaPattern.indexOf('{', i + 2);
                            final String name = javaPattern.substring(braceOpen + 1, tokenEnd - 1);
                            final String replacement = PropertyMap.apply(name);

                            if (replacement != null) {
                                if (replacement.startsWith("[")) {
                                    // Whole-token replacement (expanded range / multi-class)
                                    // For \P{...} (negated) we cannot simply wrap in [^...] because
                                    // the replacement may already be a full class. We handle the
                                    // common cases:
                                    //   \p{...} → replacement (already a [...])
                                    //   \P{...} → negate: we prepend [^ and strip outer [ from replacement
                                    if (next == 'P') {
                                        // Negate the expanded class: [stuff] → [^stuff]
                                        out.append("[^");
                                        out.append(replacement, 1, replacement.length());
                                    } else {
                                        out.append(replacement);
                                    }
                                } else if (replacement.startsWith("\\P{")) {
                                    // Special case: javaDefined → \P{Cn}  (a whole negated property)
                                    if (next == 'P') {
                                        // \P{javaDefined} → \p{Cn}  (double negation → positive)
                                        out.append("\\p{");
                                        out.append(replacement, 3, replacement.length());
                                    } else {
                                        out.append(replacement);
                                    }
                                } else {
                                    // Plain name replacement
                                    out.append('\\');
                                    out.append(next); // keep p or P
                                    out.append('{');
                                    out.append(replacement);
                                    out.append('}');
                                }
                                i = tokenEnd;
                                continue;
                            }
                            // No rewrite: copy the original token verbatim
                            out.append(javaPattern, i, tokenEnd);
                            i = tokenEnd;
                            continue;
                        }
                    }
                }

                // Any other backslash sequence: copy backslash and advance; the next char
                // will be picked up in the next iteration (single-char copy path).
                out.append(c);
                i++;
                continue;
            }

            if (inQuotation) {
                out.append(c);
                i++;
                continue;
            }

            // Normal character — copy verbatim
            out.append(c);
            i++;
        }

        return out.toString();
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
}
