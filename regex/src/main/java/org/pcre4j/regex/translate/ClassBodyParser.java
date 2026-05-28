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

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the body of a Java character class (the text between the outer {@code [} and its
 * matching {@code ]}) and produces a {@link ClassNode} AST.
 *
 * <p>The parser is called with the index pointing to the character immediately <em>after</em>
 * the opening {@code [}.  On return the position will be pointing to the character immediately
 * <em>after</em> the closing {@code ]}.
 *
 * <h3>Grammar summary</h3>
 * <pre>
 *   class      ::= '[' '^'? intersection ']'
 *   intersection ::= union ( '&&' union )*
 *   union      ::= item*
 *   item       ::= range | atom
 *   range      ::= singleAtom '-' singleAtom     (only when neither atom is a property)
 *   atom       ::= literal | escape | nested-class | posix-class | '\Q' ... '\E'
 * </pre>
 *
 * <p>The {@code pos[0]} int-array is used as a by-reference position cursor so that nested
 * recursive calls advance the shared index correctly.
 */
public final class ClassBodyParser {

    private ClassBodyParser() {
    }

    /**
     * Parses a complete Java character class starting at the {@code [}.
     *
     * @param s   the full pattern string
     * @param pos a one-element int array containing the current parse index, initially pointing
     *            at the {@code [}; on return it will be just past the matching {@code ]}
     * @return the parsed {@link ClassNode}
     * @throws IllegalArgumentException if the class body is syntactically invalid
     */
    public static ClassNode parseClass(final String s, final int[] pos) {
        expect(s, pos, '[');
        return parseClassBody(s, pos);
    }

    /**
     * Parses the class body starting just past an already-consumed {@code [}.
     * Reads up to and including the matching {@code ]}.
     */
    static ClassNode parseClassBody(final String s, final int[] pos) {
        final int len = s.length();

        // Optional leading caret for negation
        final boolean negated = pos[0] < len && s.charAt(pos[0]) == '^';
        if (negated) {
            pos[0]++;
        }

        // Parse the intersection (which itself contains union operands)
        final ClassNode body = parseIntersection(s, pos);

        // Consume the closing ]; absence is a parse error
        if (pos[0] >= len || s.charAt(pos[0]) != ']') {
            throw new IllegalArgumentException(
                    "Unterminated character class starting at " + s);
        }
        pos[0]++;

        if (negated) {
            return new ClassNode.Negated(body);
        }
        return body;
    }

    // -----------------------------------------------------------------------
    // Intersection: union ('&&' union)*
    // -----------------------------------------------------------------------

    private static ClassNode parseIntersection(final String s, final int[] pos) {
        final ClassNode first = parseUnion(s, pos);
        final int len = s.length();

        // Check if there is at least one '&&'
        if (pos[0] + 1 < len
                && s.charAt(pos[0]) == '&'
                && s.charAt(pos[0] + 1) == '&') {
            final List<ClassNode> operands = new ArrayList<>();
            operands.add(first);
            while (pos[0] + 1 < len
                    && s.charAt(pos[0]) == '&'
                    && s.charAt(pos[0] + 1) == '&') {
                pos[0] += 2; // consume '&&'
                operands.add(parseUnion(s, pos));
            }
            return new ClassNode.Intersection(operands);
        }
        return first;
    }

    // -----------------------------------------------------------------------
    // Union: item*  (until ']' or '&&')
    // -----------------------------------------------------------------------

    private static ClassNode parseUnion(final String s, final int[] pos) {
        final List<ClassNode> items = new ArrayList<>();
        final int len = s.length();

        while (pos[0] < len) {
            final char ch = s.charAt(pos[0]);

            // Stop at ] (end of class) or && (intersection separator)
            if (ch == ']') {
                break;
            }
            if (ch == '&' && pos[0] + 1 < len && s.charAt(pos[0] + 1) == '&') {
                break;
            }

            // Parse one item (may produce a Literal, Range, PropertyLeaf, or nested Union)
            final ClassNode item = parseItem(s, pos);
            items.add(item);
        }

        if (items.isEmpty()) {
            return new ClassNode.Union(List.of());
        }
        if (items.size() == 1) {
            // Unwrap single-element unions for cleaner ASTs
            return items.get(0);
        }
        return new ClassNode.Union(items);
    }

    // -----------------------------------------------------------------------
    // Item: try to build a range "a-b"; fall back to single atom
    // -----------------------------------------------------------------------

    private static ClassNode parseItem(final String s, final int[] pos) {
        final ClassNode atom = parseAtom(s, pos);
        final int len = s.length();

        // Attempt a range: "atom-atom" only if atom is a single code point (Literal)
        // and the next char is '-' and is not at end-of-class
        if (atom instanceof ClassNode.Literal litLo
                && pos[0] < len
                && s.charAt(pos[0]) == '-'
                && pos[0] + 1 < len
                && s.charAt(pos[0] + 1) != ']') {
            pos[0]++; // consume '-'
            final ClassNode atomHi = parseAtom(s, pos);
            if (atomHi instanceof ClassNode.Literal litHi) {
                return new ClassNode.Range(litLo.cp(), litHi.cp());
            }
            // RHS is not a single code point (e.g. nested class) — treat '-' as literal
            final List<ClassNode> parts = new ArrayList<>(3);
            parts.add(litLo);
            parts.add(new ClassNode.Literal('-'));
            parts.add(atomHi);
            return new ClassNode.Union(parts);
        }

        // If atom is a PropertyLeaf followed by '-', treat '-' as literal (Java semantics)
        if (atom instanceof ClassNode.PropertyLeaf
                && pos[0] < len
                && s.charAt(pos[0]) == '-'
                && pos[0] + 1 < len
                && s.charAt(pos[0] + 1) != ']') {
            pos[0]++; // consume '-'
            final ClassNode next = parseAtom(s, pos);
            final List<ClassNode> parts = new ArrayList<>(3);
            parts.add(atom);
            parts.add(new ClassNode.Literal('-'));
            parts.add(next);
            return new ClassNode.Union(parts);
        }

        return atom;
    }

    // -----------------------------------------------------------------------
    // Atom: single element — literal, escape, nested class, \Q...\E
    // -----------------------------------------------------------------------

    private static ClassNode parseAtom(final String s, final int[] pos) {
        final int len = s.length();
        if (pos[0] >= len) {
            throw new IllegalArgumentException("Unexpected end of pattern inside character class");
        }
        final char ch = s.charAt(pos[0]);

        if (ch == '[') {
            // Nested character class — parse recursively and return as-is
            return parseClass(s, pos);
        }

        if (ch == '\\') {
            return parseEscape(s, pos);
        }

        // Plain character — read as a full Unicode code point (handles supplementary chars)
        final int cp = s.codePointAt(pos[0]);
        pos[0] += Character.charCount(cp);
        return new ClassNode.Literal(cp);
    }

    // -----------------------------------------------------------------------
    // Escape sequences inside a character class
    // -----------------------------------------------------------------------

    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    private static ClassNode parseEscape(final String s, final int[] pos) {
        final int len = s.length();
        expect(s, pos, '\\');

        if (pos[0] >= len) {
            throw new IllegalArgumentException("Trailing backslash inside character class");
        }
        final char esc = s.charAt(pos[0]);
        pos[0]++;

        switch (esc) {
            // Simple single-char escapes
            case 'n': return new ClassNode.Literal('\n');
            case 't': return new ClassNode.Literal('\t');
            case 'r': return new ClassNode.Literal('\r');
            case 'f': return new ClassNode.Literal('\f');
            case 'a': return new ClassNode.Literal(0x07);  // BEL
            case 'e': return new ClassNode.Literal(0x1B);  // ESC
            case '0': {
                // Java octal escape: \0n, \0nn, \0mnn where m∈[0-3], n∈[0-7] (max value 0xFF)
                int val = 0;
                int count = 0;
                while (pos[0] < len && count < 3) {
                    final char d = s.charAt(pos[0]);
                    if (d < '0' || d > '7') {
                        break;
                    }
                    final int next = val * 8 + (d - '0');
                    if (next > 0xFF) {
                        break;
                    }
                    val = next;
                    pos[0]++;
                    count++;
                }
                return new ClassNode.Literal(val);
            }

            // \cX — control character
            case 'c': {
                if (pos[0] >= len) {
                    throw new IllegalArgumentException("Incomplete \\c escape");
                }
                final int ctrl = s.charAt(pos[0]) & 0x1F;
                pos[0]++;
                return new ClassNode.Literal(ctrl);
            }

            // \xHH — hex escape (2 digits), or \x{HH...} form
            case 'x': {
                if (pos[0] < len && s.charAt(pos[0]) == '{') {
                    // \x{HH...} form — requires closing '}'
                    pos[0]++; // skip '{'
                    int val = 0;
                    boolean any = false;
                    while (pos[0] < len && s.charAt(pos[0]) != '}') {
                        val = val * 16 + hexDigit(s.charAt(pos[0]));
                        pos[0]++;
                        any = true;
                    }
                    if (pos[0] >= len || s.charAt(pos[0]) != '}') {
                        throw new IllegalArgumentException("Unterminated \\x{...} escape");
                    }
                    if (!any) {
                        throw new IllegalArgumentException("Empty \\x{} escape");
                    }
                    pos[0]++; // skip '}'
                    return new ClassNode.Literal(val);
                }
                // Plain \xHH — requires exactly 2 hex digits
                if (pos[0] + 1 >= len) {
                    throw new IllegalArgumentException("Incomplete \\x escape (need 2 hex digits)");
                }
                final int hi = hexDigit(s.charAt(pos[0]++));
                final int lo = hexDigit(s.charAt(pos[0]++));
                return new ClassNode.Literal(hi * 16 + lo);
            }

            // \\uHHHH — Unicode escape (exactly 4 hex digits)
            case 'u': {
                if (pos[0] + 3 >= len) {
                    throw new IllegalArgumentException("Incomplete \\u escape (need 4 hex digits)");
                }
                int val = 0;
                for (int i = 0; i < 4; i++) {
                    val = val * 16 + hexDigit(s.charAt(pos[0]++));
                }
                return new ClassNode.Literal(val);
            }

            // \Q...\E — literal section inside class
            case 'Q': {
                final List<ClassNode> literals = new ArrayList<>();
                while (pos[0] < len) {
                    if (s.charAt(pos[0]) == '\\' && pos[0] + 1 < len
                            && s.charAt(pos[0] + 1) == 'E') {
                        pos[0] += 2; // consume \E
                        break;
                    }
                    final int cp = s.codePointAt(pos[0]);
                    literals.add(new ClassNode.Literal(cp));
                    pos[0] += Character.charCount(cp);
                }
                if (literals.isEmpty()) {
                    return new ClassNode.Union(List.of());
                }
                if (literals.size() == 1) {
                    return literals.get(0);
                }
                return new ClassNode.Union(literals);
            }

            // Shorthand classes \d \D \w \W \s \S
            case 'd': return new ClassNode.PropertyLeaf("\\d", false);
            case 'D': return new ClassNode.PropertyLeaf("\\D", true);
            case 'w': return new ClassNode.PropertyLeaf("\\w", false);
            case 'W': return new ClassNode.PropertyLeaf("\\W", true);
            case 's': return new ClassNode.PropertyLeaf("\\s", false);
            case 'S': return new ClassNode.PropertyLeaf("\\S", true);

            // \h \H horizontal whitespace (PCRE2 extension accepted in classes)
            case 'h': return new ClassNode.PropertyLeaf("\\h", false);
            case 'H': return new ClassNode.PropertyLeaf("\\H", true);

            // \v \V vertical whitespace
            case 'v': return new ClassNode.PropertyLeaf("\\v", false);
            case 'V': return new ClassNode.PropertyLeaf("\\V", true);

            // \p{...} or \P{...} — Unicode property
            case 'p':
            case 'P': {
                final boolean neg = (esc == 'P');
                if (pos[0] < len && s.charAt(pos[0]) == '{') {
                    pos[0]++; // skip '{'
                    final int start = pos[0];
                    while (pos[0] < len && s.charAt(pos[0]) != '}') {
                        pos[0]++;
                    }
                    final String propName = s.substring(start, pos[0]);
                    if (pos[0] < len) pos[0]++; // skip '}'
                    // Apply PropertyMap rewrite (same as outer translator Phase 1)
                    final String rewritten = PropertyMap.apply(propName);
                    final String token;
                    if (rewritten == null) {
                        token = "\\" + esc + "{" + propName + "}";
                    } else if (rewritten.startsWith("[")) {
                        // Expanded to a range string — keep as opaque PCRE2 token;
                        // for evaluation purposes we mark as unknown so Evaluator falls back
                        token = "\\" + esc + "{" + propName + "}";
                    } else if (rewritten.startsWith("\\P{")) {
                        // Double-negation case (javaDefined)
                        token = neg ? "\\p{" + rewritten.substring(3) : rewritten;
                    } else {
                        token = "\\" + esc + "{" + rewritten + "}";
                    }
                    return new ClassNode.PropertyLeaf(token, neg);
                }
                // \p or \P without braces — emit as-is
                return new ClassNode.PropertyLeaf("\\" + esc, neg);
            }

            // \N{name} (named character) — pass through
            case 'N': {
                if (pos[0] < len && s.charAt(pos[0]) == '{') {
                    final int start = pos[0];
                    while (pos[0] < len && s.charAt(pos[0]) != '}') {
                        pos[0]++;
                    }
                    if (pos[0] < len) pos[0]++;
                    return new ClassNode.PropertyLeaf("\\N" + s.substring(start, pos[0]), false);
                }
                return new ClassNode.Literal('N');
            }

            // Any other escaped character — treat as literal of that character
            default:
                return new ClassNode.Literal(esc);
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static void expect(final String s, final int[] pos, final char expected) {
        if (pos[0] >= s.length() || s.charAt(pos[0]) != expected) {
            throw new IllegalArgumentException(
                    "Expected '" + expected + "' at index " + pos[0]
                    + " in: " + s.substring(0, Math.min(pos[0] + 1, s.length())));
        }
        pos[0]++;
    }

    private static int hexDigit(final char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        throw new IllegalArgumentException("Invalid hex digit: " + c);
    }
}
